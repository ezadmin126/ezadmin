package top.ezadmin.service.impl;

import org.apache.commons.lang.StringEscapeUtils;
import top.ezadmin.common.enums.*;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.ListDao;
import top.ezadmin.dao.PluginsDao;

import top.ezadmin.service.ListService;
import top.ezadmin.dao.model.ItemInitData;
import top.ezadmin.common.annotation.EzCacheAnnotation;
import top.ezadmin.common.constants.SelectKVContants;
import top.ezadmin.plugins.cache.Callback;
 import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.MapParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import top.ezadmin.EzClientBootstrap;

import top.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import top.ezadmin.plugins.express.executor.ListExpressExecutor;
import top.ezadmin.dao.Dao;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

public class ListServiceImpl implements ListService {

    Logger LOG = LoggerFactory.getLogger(ListServiceImpl.class);
    Dao dao=Dao.getInstance();
    EzClientBootstrap bootstrap= EzClientBootstrap.instance();

    @Override
    public List<Map<String, Object>> getDataListByListId(DataSource dataSource,Map<String, Object> list,
                   Map<String, Object> request  , Map<String, String> session, Page page) throws Exception {
        //
        try {
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");


            String select_express = getString(coreMap, "select_express");

            ListExpressExecutor listExpressExecutor = ListExpressExecutor.createInstance();
            listExpressExecutor.datasource(dataSource)
                    .express(Utils.trimNull(transSqlToQl(select_express)))
                    .page(page);
            //额外设置listDTO
            listExpressExecutor.getOperatorParam().setListDto(list);

            //计算group by
            String group = excuteGroup(list, request, session);

            listExpressExecutor.addParam("_CHECKD_IDS", Utils.getStringByObject(request, "_CHECKD_IDS"));
            listExpressExecutor.addParam("EZ_SUM_FLAG", Utils.getStringByObject(request, "EZ_SUM_FLAG"));
            listExpressExecutor.addParam("GROUP_BY", group);
            listExpressExecutor.addSessionParam(session);
            listExpressExecutor.addRequestParam(request);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) listExpressExecutor.execute();
            return dataList;
        }catch (Exception e){
            Utils.addLog("获取列表数据异常:", e);
            return null;
        }
    }




    @Override
    public long getDataCountByListId(DataSource dataSource,  Map<String, Object>   list, Map<String, Object> request, Map<String, String> session  ) throws Exception {
        Map<String,Object> coreMap=(Map<String,Object>)list.get("core");


        String count_express=getString(coreMap,"count_express");
        String select_express=getString(coreMap,"select_express");
        ListExpressExecutor listExpressExecutor = ListExpressExecutor.createInstance();
        String countQl="";
        if(StringUtils.isBlank(count_express)||
                StringUtils.equalsIgnoreCase("count(1)",count_express.toLowerCase().trim())){
            countQl=transSqlCountToQl(select_express);
        }else{
            countQl=transSqlCountToQl(count_express);
        }
        listExpressExecutor.datasource(dataSource).express(countQl);
        //计算group by
        String group=excuteGroup(list,request,session);
        listExpressExecutor.listDTOAndSearchParam(list);
        listExpressExecutor.addParam("_CHECKD_IDS",Utils.getStringByObject(request,"_CHECKD_IDS"));
        listExpressExecutor.addParam("EZ_SUM_FLAG",Utils.getStringByObject(request,"EZ_SUM_FLAG"));
        listExpressExecutor.addParam("GROUP_BY",group);

        listExpressExecutor.addSessionParam(session);
        listExpressExecutor.addRequestParam( request);
        Object obj=  listExpressExecutor.executeCount();
        return NumberUtils.toLong(obj+"");
    }

    private String excuteGroup( Map<String, Object> list, Map<String, Object> request, Map<String, String> session  )   {
       try {
           Map<String,Object> coreMap=(Map<String,Object>)list.get("core");

           String groupByExpress=Utils.getStringByObject(coreMap,"groupby_express");
           //兼容老的 group by
           if(StringUtils.startWithTrimAndLower(groupByExpress,"group ")){
                return Utils.trimNull(groupByExpress);
           }
           ListExpressExecutor groupExe = ListExpressExecutor.createInstance();
           groupExe.getOperatorParam().setListDto(list);
           groupExe.express(groupByExpress);
           groupExe.addSessionParam(session);
           groupExe.addRequestParam( request);
           Object obj = groupExe.executeCount();
           return Utils.trimNull(obj);
       }catch (Exception e){
           LOG.error("EZADMIN   group by 配置错误   ,",   e);
           return " ";
       }
    }


    private String transSqlToQl(String ql){
        if(StringUtils.trimEmpty(ql).toLowerCase(Locale.ROOT).startsWith("select")){
            return "list=search(\""+ql+"\");return list";
        }
        return ql;
    }

    private String transSqlCountToQl(String ql){
        if(StringUtils.trimEmpty(ql).toLowerCase(Locale.ROOT).startsWith("select")){
            return "c=search(\""+ql+"\");return c;";
        }
        return ql;
    }



    ItemInitData text(String initData,Map<String,Object> request){
        ItemInitData data=new ItemInitData();
        String sql=initData;
        if (ShortItemValueEnum.status.name().equalsIgnoreCase(sql)) {
            return data.items(SelectKVContants.KV_STATUS());
        }
        if (ShortItemValueEnum.yesno.name().equalsIgnoreCase(sql)) {
            return data.items(SelectKVContants.KV_YESNO());
        }
        if (ShortItemValueEnum.delete.name().equalsIgnoreCase(sql)) {
            return data.items(SelectKVContants.KV_DELETE_FLAG());
        }
        if (ShortItemValueEnum.DATABASE.name() .equalsIgnoreCase(sql)) {
            List<Map<String, Object>> keyvalues = new ArrayList();
            for (String item: EzClientBootstrap.instance().datasourceKeys()) {
                Map<String, Object> map2 = new HashMap<>();
                map2.put(SelectKVContants.KEY, item);
                map2.put(SelectKVContants.VALUE, item);
                keyvalues.add(map2);
            }
            return data.items(keyvalues);
        }
        if (ShortItemValueEnum.FirstColEnum.name().equalsIgnoreCase(sql)) {
            List<Map<String, Object>> keyvalues = new ArrayList();
            for (int i = 0; i < ColTypeEnum.values().length; i++) {
                Map<String, Object> map2 = new HashMap<>();
                map2.put(SelectKVContants.KEY, ColTypeEnum.values()[i].name());
                map2.put(SelectKVContants.VALUE, ColTypeEnum.values()[i].name());
                keyvalues.add(map2);
            }
            return data.items(keyvalues);
        }

        if (ShortItemValueEnum.UrlOpenTypeEnum.name().equalsIgnoreCase(sql)) {
            List<Map<String, Object>> keyvalues = new ArrayList();
            for (int i = 0; i < UrlOpenTypeEnum.values().length; i++) {
                Map<String, Object> map2 = new HashMap<>();
                map2.put(SelectKVContants.KEY, UrlOpenTypeEnum.values()[i].name());
                map2.put(SelectKVContants.VALUE, UrlOpenTypeEnum.values()[i].desc());
                keyvalues.add(map2);
            }
            return data.items(keyvalues);
        }
        if (ShortItemValueEnum.ItemDataSourceType.name().equalsIgnoreCase(sql)) {
            List<Map<String, Object>> keyvalues = new ArrayList();
            for (int i = 0; i < ItemDataSourceType.values().length; i++) {
                Map<String, Object> map2 = new HashMap<>();
                map2.put(SelectKVContants.KEY, ItemDataSourceType.values()[i].name());
                map2.put(SelectKVContants.VALUE, ItemDataSourceType.values()[i].name());
                keyvalues.add(map2);
            }
            return data.items(keyvalues);
        }

        if (ShortItemValueEnum.OperatorEnum.name().equalsIgnoreCase(sql)) {
            List<Map<String, Object>> keyvalues = new ArrayList();
            for (int i = 0; i < OperatorEnum.values().length; i++) {
                Map<String, Object> map2 = new HashMap<>();
                map2.put(SelectKVContants.KEY, OperatorEnum.values()[i].name());
                map2.put(SelectKVContants.VALUE, OperatorEnum.values()[i].getNameC());
                keyvalues.add(map2);
            }
            return data.items(keyvalues);
        }
        if (ShortItemValueEnum.JdbcTypeEnum.name().equalsIgnoreCase(sql)) {
            List<Map<String, Object>> keyvalues = new ArrayList();
            for (int i = 0; i < JdbcTypeEnum.values().length; i++) {
                Map<String, Object> map2 = new HashMap<>();
                map2.put(SelectKVContants.KEY, JdbcTypeEnum.values()[i].name());
                map2.put(SelectKVContants.VALUE, JdbcTypeEnum.values()[i].name());
                keyvalues.add(map2);
            }
            return data.items(keyvalues);
        }
        if ("plugins4list".equalsIgnoreCase(sql)) {
            return data.items(  listPlugin(""));
        }
        if ("plugins4td".equalsIgnoreCase(sql)) {
            return data.items( listPlugin("td"));
        }
        if ("plugins4th".equalsIgnoreCase(sql)) {
            return data.items( listPlugin("th"));
        }
        if ("plugins4search".equalsIgnoreCase(sql)) {
            return data.items( listPlugin("search"));
        }
        if ("listbutton".equalsIgnoreCase(sql)) {
            return data.items( listPlugin("listbutton"));
        }
//        if ("plugins4form".equalsIgnoreCase(sql)) {
//            return data.items( formPlugin());
//        }

        List<Map<String, Object>> keyvalues = new ArrayList();
        String[] items=sql.split("\n");
        if(items!=null&&items.length>0){
            for (int i = 0; i < items.length; i++) {
                Map<String, Object> map2 = new HashMap<>();
                String k=items[i];
                String v=items[i];
                if(items[i].indexOf(":")>0){
                    String kv[]=items[i].split(":");
                    if(kv.length==2){
                        k=kv[0];
                        v=kv[1];
                    }
                }
                map2.put(SelectKVContants.KEY, k);
                map2.put(SelectKVContants.VALUE, v);
                keyvalues.add(map2);
            }
        }
        return data.items(keyvalues);
    }
    public  static List<Map<String, Object>> listPlugin(String pre) {
        return PluginsDao.getInstance().listPlugin(pre);
    }
    ItemInitData kvSqlCache(final String initData,Map<String, Object> params,final DataSource datasource ,String mini){
        ItemInitData data=new ItemInitData();
        String key= StringUtils.filterLetterOrDigit(initData);
        if(LOG.isDebugEnabled()){
            LOG.debug("开始调用缓存{}",key);
        }
        final ResultModel model = CommentsSqlParser.parse(initData, params);
        Callback callback=new Callback() {
            @Override
            public Object call(String key) {
                try {
                    List list= dao.executeQueryString(datasource, model.getResult(), model.getParamsStatic());
                    if(list!=null&&list.size()>10000){
                        LOG.error("ezadmin cache  缓存 元素超过 10000个，数据量过大建议优化 key={} "
                                ,initData);
                    }
                    return list;
                } catch (Exception throwables) {
                    LOG.error("缓存异常{}",initData,throwables);
                }
                return Collections.emptyList();
            }
        };
        if("5".equals(mini)){
            return  data.items(( List<Map<String, Object>>) EzClientBootstrap.instance().getCache().get5("KV_SQL_CACHE_", key
                    , callback));
        }
        if("60".equals(mini)){
            return  data.items(( List<Map<String, Object>>) EzClientBootstrap.instance().getCache().get60("KV_SQL_CACHE_", key
                    , callback));
        }
        return  data.items(( List<Map<String, Object>>) EzClientBootstrap.instance().getCache().get("KV_SQL_CACHE_", key
                , callback));
    }
    ItemInitData qlexpress(String initData,Map<String, Object> params,DataSource datasource){
        String ID=params.get("ID")+"";
        ItemInitData data=new ItemInitData();

        Map<String, Object> result=new HashMap<>();
        if ( StringUtils.isNotJsBlank(ID)) {
            try {
                result = DefaultExpressExecutor.createInstance().datasource(datasource)
                        .express(initData)
                        .addParam("ID", ID)
                        .addRequestParam(params)
                        .addParam(params)
                        .executeAndReturnMap();
            } catch (Exception e) {
                LOG.error("ITEM INIT ID={}",ID,e);
            }
        }
        data.items((List<Map<String, Object>>)result.get("items"));
        data.childItems((List<Map<String, Object>>)result.get("childItems"));
        data.setOtherParams((Map<String, Object>)(result.get("otherParams")==null?new HashMap<>():result.get("otherParams")));
        return data;
    }
    ItemInitData ezlist(String initData,Map<String, Object> params,DataSource datasource){
        Map<String,String> curMap=new HashMap<>();
        ItemInitData data=new ItemInitData();
//        for (Map.Entry<String,Object> entry:params.entrySet())
//        {
//            curMap.put(entry.getKey(),entry.getValue()+"");
//        } ;
//        String str=  MapParser.parse(initData,params).getResult();
//        Map<String,String> configMap=JSONUtils.parseMap(str);
//        curMap.putAll(configMap);
//        EzList list = new DefaultEzList( initData, bootstrap.getOriginDataSource(), curMap ,curMap);
//        list.renderHtml();
//        data.setEzList(list.getEzListDto());
        return data;
    }
    public ItemInitData getSelectItems(DataSource datasource, String initData, String initType, Map<String, Object> params
    ) throws Exception {
        ItemInitData data=new ItemInitData();
        initType=StringUtils.upperCase(StringUtils.isBlank(initType)?ItemDataSourceType.TEXT.name():initType);

        switch (ItemDataSourceType.get(initType)){
            case DATAGROUP:
                data=datagroup(datasource,initData,params);
                break;
            case  TEXT :
                data=text(initData,params);
                break;
            case JSON:
            case KVJSONRESULT:
                data= data.items(JSONUtils.parseListMapString(initData ));
                break;
            case KVSQL:
                ResultModel model = CommentsSqlParser.parse(initData,  params);
                List<Map<String, Object>> keyvalues = dao.executeQuery(datasource, model.getResult(), null);
                data=data.items(keyvalues);
                break;
            case KVSQLCACHEFOREVER:
                data=kvSqlCache(  initData,  params,  datasource,"0");
            case KVSQLCACHE:
                data=kvSqlCache(  initData,  params,  datasource,"60");
                break;
            case KVSQL2TREECACHE:
                data=kvTreeSqlCache(  initData,  params,  datasource,"60");
                break;
            case KVSQLCACHE5:
                data=kvSqlCache(  initData,  params,  datasource,"5");
                break;
            case QLEXPRESS:
                data=qlexpress(  initData,  params,  datasource);break;
            case EZLIST:
                data=ezlist(initData,  params,  datasource);
                break;
            default:break;
        }
        if(Utils.isEmpty(data.getItems())){
            data.items(new ArrayList<Map<String, Object>>());
        }
        return data ;
    }
    //DATA_CODE,DATA_TYPE,DATA_CONTENT f
    private ItemInitData datagroup(DataSource datasource,String initData, Map<String, Object> params) throws Exception {
       String kv[]= initData.split("\\.");
       Map<String,Object> group= (Map<String, Object>)EzClientBootstrap.instance().getCache().get("datagroup_cache", kv[0], new Callback() {
           @Override
           public Object call(String key) {
               try {
                   Map<String, Object> m = dao.executeQueryOne(EzClientBootstrap.instance().getEzDataSource(),
                           "select DATA_CODE,DATA_TYPE,DATA_CONTENT from T_EZADMIN_DATA WHERE DELETE_FLAG=0 AND DATA_CODE=?", new Object[]{kv[0]});
                   return m;
               }catch (Exception e){
                   return Collections.emptyMap();
               }
           }
       });
        if(!Utils.isEmpty(group)){
            if(Utils.trimNull(group.get("DATA_TYPE")).equalsIgnoreCase("datagroup")){
                return new ItemInitData();
            }
            if(kv.length>1){
                params.put("DICT_TYPE",kv[1]);
            }
            return  getSelectItems(  datasource,   Utils.trimNull(group.get("DATA_CONTENT")),   Utils.trimNull(group.get("DATA_TYPE")),  params
            );
        }
        return new ItemInitData();
    }

    private ItemInitData kvTreeSqlCache(String initData, Map<String, Object> params, DataSource datasource, String s) {
       final ItemInitData data=kvSqlCache(initData,params,datasource,s);
        String key= StringUtils.filterLetterOrDigit(initData);
        List<Map<String,Object>> d=(List<Map<String,Object>>) EzClientBootstrap.instance().getCache().get("kvtreesqlcache", key, new Callback() {
            @Override
            public Object call(String key) {
                return Utils.flatTree(data.getItems());
            }
        });
        data.items(d);
        return data;
    }



    /**
     *
     * @param pluginCode  adminstyle+
     * @return map
     * @throws Exception
     */
    @EzCacheAnnotation
    public Map<String, String> loadPlugin(String adminstyle,String fold,String pluginCode) throws Exception {
        try {
            return PluginsDao.getInstance().getPlugin(adminstyle,fold, pluginCode);
        }catch (Exception e){
            LOG.warn("plugin error{} {} {} ",adminstyle,fold,pluginCode,e);
        }
        return Collections.emptyMap();
    }


    @Override
    @EzCacheAnnotation
    public String selectPublishListById(String encodeId) throws Exception {
        try {
            String sql="select id,lower(EZ_CODE) EZ_CODE,DATASOURCE,EZ_NAME,EZ_CONFIG from T_EZADMIN_PUBLISH where   lower(EZ_CODE)=? and EZ_TYPE=1 " +
                    "";
            Map<String, Object> listMap=null;
            //系统list直接集成在jar包
            if(!StringUtils.contains("listmanage,formmanage",encodeId)){
                try {
                    listMap=Dao.getInstance().executeQueryOne(EzClientBootstrap.instance().getEzDataSource(),
                            sql, new Object[]{encodeId.toLowerCase()});
                }catch (Exception e){
                    LOG.error(encodeId,e);
                }
            }
            if(Utils.isEmpty(listMap)){
                Map<String, Object> m=ListDao.getInstance().selectAllListById(encodeId.toLowerCase());
                if(Utils.isEmpty(m)){
                    return null;
                }
                return JSONUtils.toJSONString(m);
            }
            String html=Utils.trimNull(listMap.get("EZ_CONFIG"));
            Map<String,Object>c= ListDao.getInstance().selectAllListByHtml(html);
            c.put("EZ_CONFIG",html);
            c.put("EZ_CODE",Utils.trimNull(listMap.get("EZ_CODE")));
            c.put("EZ_NAME",Utils.trimNull(listMap.get("EZ_NAME")));
            c.put("DATASOURCE",Utils.trimNull(listMap.get("DATASOURCE")));
            return JSONUtils.toJSONString(c);
        }catch (Exception e){
            throw e;
        }
    }

    public String selectDbPublishListById(String encodeId) throws Exception {

        String sql="select id,lower(EZ_CODE) EZ_CODE,DATASOURCE,EZ_NAME,EZ_CONFIG from T_EZADMIN_PUBLISH where   lower(EZ_CODE)=? and EZ_TYPE=1 " +
                "";
        Map<String, Object> listMap=null;
        try {
            listMap=Dao.getInstance().executeQueryOne(EzClientBootstrap.instance().getEzDataSource(),
                    sql, new Object[]{encodeId.toLowerCase()});
        }catch (Exception e){
            LOG.error(encodeId,e);
        }
        return JSONUtils.toJSONString(listMap);
    }
    @EzCacheAnnotation
    public  Map<String, Object> selectAllListMapById(String encodeId) throws Exception {
        try {
            return  ListDao.getInstance().selectAllListById(encodeId);
        }catch (Exception e){
            LOG.error(""+encodeId,e);
            return new HashMap<>();
        }
    }
    @EzCacheAnnotation
    public  Map<String, Object> selectAllListByHtml(String html) throws Exception {
        try {
            return  ListDao.getInstance().selectAllListByHtml(html);
        }catch (Exception e){
            LOG.error(""+html,e);
            return new HashMap<>();
        }
    }



    public Map<String,Object>  selectConfigEditList(String code  ) throws Exception {
        String sql="select id,lower(EZ_CODE) EZ_CODE,DATASOURCE,EZ_NAME,EZ_CONFIG from T_EZADMIN_EDIT where   lower(EZ_CODE)=? and EZ_TYPE=1 " +
                "";
        Map<String, Object> listMap=Dao.getInstance().executeQueryOne(EzClientBootstrap.instance().getEzDataSource(),
                sql,new Object[]{ code.toLowerCase() });
        if(Utils.isEmpty(listMap)){
            return listMap;
        }
        String html=Utils.trimNull(listMap.get("EZ_CONFIG"));
        Map<String,Object>c= ListDao.getInstance().selectAllListByHtml(html);
        c.put("EZ_CONFIG",html);
        c.put("EZ_CODE",Utils.trimNull(listMap.get("EZ_CODE")));
        c.put("EZ_NAME",Utils.trimNull(listMap.get("EZ_NAME")));
        c.put("DATASOURCE",Utils.trimNull(listMap.get("DATASOURCE")));
        return c;
    }





    @Override
    public void fillListById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception {
        try {
            Page pagination = loadingPage(list, requestParamMap);
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            List<Map<String, Object>> searchList = (List<Map<String, Object>>) list.get("search");
            List<Map<String, Object>> tabList = (List<Map<String, Object>>) list.get("tab");
            List<Map<String, Object>> tablebtnList = (List<Map<String, Object>>) list.get("tablebtn");
            List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("col");
            List<Map<String, Object>> rowList = new ArrayList<>();
            List<Map<String, Object>> rowbtnList = (List<Map<String, Object>>) list.get("rowbtn");


            String globalEmptyShow = getString(coreMap, "empty_show");

            String datasourceCore = getString(coreMap, "datasource");

            DataSource dataSourceVO = EzClientBootstrap.instance().getDataSourceByKey(datasourceCore);
            //头部Nav
            filltab(requestParamMap, tabList );
            //搜索项
            fillsearch(requestParamMap, sessionParamMap, coreMap, searchList, datasourceCore);
            //表按钮
            filltablebtn(coreMap,requestParamMap, tablebtnList);
            //表头
            fillcol(coreMap,colList,requestParamMap);
            page(pagination, list, requestParamMap);
            //无需加载数据，比如tree,
            if (StringUtils.equals("0", Utils.trimNull(requestParamMap.get("loadDataFlag")))) {
                return;
            }
            //填充数据
            List<Map<String, Object>> dataList = getDataListByListId(dataSourceVO, list, requestParamMap, sessionParamMap, pagination);
            if (Utils.isEmpty(dataList)) {
                return;
            }
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> dataRow = dataList.get(i);
                List<String> tds = new ArrayList<>();
                dataRow.put("tds", tds);
                dataRow.put("rowjson",JSONUtils.toJSONString(dataRow));

                if (Utils.isEmpty(colList)) {
                    rowList.add(dataRow);
                    continue;
                }
                for (int j = 0; j < colList.size(); j++) {
                    Map<String, Object> th = colList.get(j);
                    String itemName = Utils.getStringByObject(th, JsoupUtil.ITEM_NAME);
                    String bodyPlugin = Utils.getStringByObject(th, JsoupUtil.BODY_PLUGIN_CODE);
                    String jdbcType = Utils.getStringByObject(th, JsoupUtil.JDBCTYPE);
                    String url = Utils.getStringByObject(th, JsoupUtil.URL);
                    String ORGSRC = Utils.getStringByObject(th, JsoupUtil.ORGSRC);
                    String windowname = Utils.getStringByObject(th, JsoupUtil.WINDOW_NAME);
                    String dataInDb = ObjectUtils.toString(dataRow.get(itemName));

                    String columnEmptyShow = Utils.getStringByObject(th, JsoupUtil.EMPTY_SHOW);

                    dataInDb = calulateData(dataInDb, globalEmptyShow, columnEmptyShow, jdbcType);

                    Map<String, String> plugin =  loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list",getString(th, JsoupUtil.BODY_PLUGIN_CODE));
                    if(StringUtils.isBlank(bodyPlugin)){
                        tds.add("<td class='  ezadmin-td ezadmin-td-'" + itemName + ">" + dataInDb + "</td>");
                        continue;
                    }
                    try {


                            Context context = new Context();
                            context.setVariable("_CHECK_ID_VALUE", dataRow.get("ID"));
                            context.setVariable("count", pagination.getStartRecord() + i + 1);
                            context.setVariable("dataRow", dataRow.entrySet());
                            context.setVariables(th);
                            context.setVariable(JsoupUtil.URL, MapParser.parseDefaultEmpty(url, dataRow).getResult());
                            context.setVariable(JsoupUtil.ORGSRC, MapParser.parseDefaultEmpty(ORGSRC, dataRow).getResult());
                            context.setVariable(JsoupUtil.WINDOW_NAME, MapParser.parseDefaultEmpty(windowname, dataRow).getResult());
                            context.setVariables(dataRow);
                            context.setVariable("dataInDb", dataInDb);
                            context.setVariable("uploadUrl", requestParamMap.get("ContextPath") + EzClientBootstrap.instance().getUploadUrl());
                            if (StringUtils.startsWith(EzClientBootstrap.instance().getDownloadUrl(), "http")) {
                                context.setVariable("downloadUrl", EzClientBootstrap.instance().getDownloadUrl());
                            } else {
                                context.setVariable("downloadUrl", requestParamMap.get("ContextPath") + EzClientBootstrap.instance().getDownloadUrl());
                            }
                            if(StringUtils.isBlank(getString(th, JsoupUtil.DATA))){
                                String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
                                String html = ThymeleafUtils.processString(template, context);
                                tds.add(html);
                                continue;
                            }

                            String columnDs = getString(th, JsoupUtil.DATASOURCE);
                            if (StringUtils.isBlank(columnDs)) {
                                columnDs = datasourceCore;
                            }
                            DataSource temp = EzClientBootstrap.instance().getDataSourceByKey(columnDs);

                            try {
                                Map nm = new HashMap();
                                nm.putAll(requestParamMap);
                                nm.putAll(sessionParamMap);//SESSION cover
                                ItemInitData items = getSelectItems(temp, getString(th, JsoupUtil.DATA), getString(th, JsoupUtil.DATATYPE),  nm);
                                if(Utils.isNotEmpty(items.getItems())){
                                    final String dataInDbf=dataInDb;
                                    boolean containsStudent = items.getItems().stream()
                                            .anyMatch(student -> dataInDbf.equals(student.get("K")));
                                    if(containsStudent){
                                        context.setVariable("items", items.getItems());
                                    }
                                }
                                context.setVariable("itemsJson", JSONUtils.toJSONString(items.getItems()));
                            } catch (Exception e) {
                                Utils.addLog("获取列表数据异常:"+JSONUtils.toJSONString(th), e);
                             }

                            String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
                            String html = ThymeleafUtils.processString(template, context);

                            if(StringUtils.isBlank(html)){
                                tds.add("<td class='errorplugincode ezadmin-td ezadmin-td-'" + itemName + ">" + dataInDb + "</td>");
                            }else{
                                tds.add(html);
                            }
                    } catch (Exception e) {
                        Utils.addLog("获取列表数据异常:"+JSONUtils.toJSONString(th), e);
                    }
                }

                //处理按钮列
                try {
                    if (Utils.isNotEmpty(rowbtnList)) {

                        List<Map<String, Object>> tempRowItem = new ArrayList<>();
                        for (int i1 = 0; i1 < rowbtnList.size(); i1++) {
                            Map<String, Object> item = rowbtnList.get(i1);
                            Map<String, Object> m = new HashMap<String, Object>();

                            m.putAll(item);
                            m.put(JsoupUtil.URL, MapParser.parseDefaultEmpty(Utils.getStringByObject(item, JsoupUtil.URL), dataRow).getResult());
                            m.put(JsoupUtil.LABEL, MapParser.parseDefaultEmpty(Utils.getStringByObject(item, JsoupUtil.LABEL), dataRow).getResult());
                            m.put(JsoupUtil.WINDOW_NAME, MapParser.parseDefaultEmpty(Utils.getStringByObject(item, JsoupUtil.WINDOW_NAME), dataRow).getResult());
                            m.put(JsoupUtil.ITEM_ID, MapParser.parseDefaultEmpty(Utils.trimEmptyDefault(item.get(JsoupUtil.ITEM_ID), "0"), dataRow).getResult());
                            m.put("rowdataid", dataRow.get("ID"));
                            m.put("rowjson", dataRow.get("rowjson"));
                            String display = Utils.trimNull(item.get(JsoupUtil.DISPLAY));
                            if (StringUtils.isNotBlank(display)) {
                                String re = MapParser.parseDefaultEmpty(display, dataRow).getResult();
                                if (BooleanUtils.toBoolean(Utils.trimNull(re)) || "1".equals(Utils.trimNull(re))) {
                                    tempRowItem.add(m);
                                }
                            } else {
                                tempRowItem.add(m);
                            }
                        }
                        ;


                        Context context = new Context();
                        if (tempRowItem.size() > 0) {
                            if (tempRowItem.size() > 1) {
                                context.setVariable("itemsJson", JSONUtils.toJSONString(tempRowItem.subList(1, tempRowItem.size())));
                            } else {
                                context.setVariable("itemsJson", JSONUtils.toJSONString(tempRowItem));
                            }

                            Map<String, String> buttonPlugin =
                                    loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list", Utils.getStringByObject(tempRowItem.get(0), JsoupUtil.TYPE));
                            context.setVariable("rowButton0", tempRowItem.get(0));
                            context.setVariable("rowButtonItemList", tempRowItem);
                            context.setVariable("rowButtons", tempRowItem);
                            context.setVariable("rowbtnclass",coreMap.get("rowbtnclass"));
                            String template = Utils.trimNull(buttonPlugin.get("PLUGIN_BODY"));
                            String html = ThymeleafUtils.processString(template, context);
                            if (StringUtils.isNotBlank(html)) {
                                coreMap.put("rowbtnhtml", html);
                                tds.add(html);
                            } else {
                                tds.add("");
                            }
                        } else {
                            tds.add("");
                        }
                    }
                } catch (Exception e) {
                    LOG.error("EZADMIN LIST={}  初始化第{}行按钮错误{} ", i, dataRow, e);
                }
                rowList.add(dataRow);
                coreMap.put("dataList", rowList);
            }
        }catch (Exception e){
            Utils.addLog("获取列表数据异常:", e);
        }
    }
    @Override
    public void exportListById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception {
        try {
            Page pagination = loadingPage(list, requestParamMap);
            Map<String, Object> coreMap = (Map<String, Object>) list.get("core");
            List<Map<String, Object>> searchList = (List<Map<String, Object>>) list.get("search");

            List<Map<String, Object>> colList = (List<Map<String, Object>>) list.get("col");
            List<Map<String, Object>> rowList = new ArrayList<>();


            String globalEmptyShow = getString(coreMap, JsoupUtil.EMPTY_SHOW);
            String datasourceCore = getString(coreMap, JsoupUtil.DATASOURCE);
            DataSource dataSourceVO = EzClientBootstrap.instance().getDataSourceByKey(datasourceCore);
            //搜索项
            fillsearch(requestParamMap, sessionParamMap, coreMap, searchList, datasourceCore);
            //表头
            fillcol(coreMap,colList,requestParamMap);
            page(pagination, list, requestParamMap);
            //无需加载数据，比如tree,
            if (StringUtils.equals("0", Utils.trimNull(requestParamMap.get("loadDataFlag")))) {
                return;
            }
            //填充数据
            List<Map<String, Object>> dataList = getDataListByListId(dataSourceVO, list, requestParamMap, sessionParamMap, pagination);
            if (Utils.isEmpty(dataList)) {
                return;
            }
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> dataRow = dataList.get(i);
                List<String> tds = new ArrayList<>();
                dataRow.put("tds", tds);
                dataRow.put("rowjson",JSONUtils.toJSONString(dataRow));

                if (Utils.isEmpty(colList)) {
                    rowList.add(dataRow);
                    continue;
                }
                for (int j = 0; j < colList.size(); j++) {
                    Map<String, Object> th = colList.get(j);
                    String itemName = Utils.getStringByObject(th, JsoupUtil.ITEM_NAME);
                    String bodyPlugin = Utils.getStringByObject(th, JsoupUtil.BODY_PLUGIN_CODE);

                    String jdbcType = Utils.getStringByObject(th, JsoupUtil.JDBCTYPE);
                    String dataInDb = ObjectUtils.toString(dataRow.get(itemName));
                    String columnEmptyShow = Utils.getStringByObject(th, JsoupUtil.EMPTY_SHOW);

                    dataInDb = calulateData(dataInDb, globalEmptyShow, columnEmptyShow, jdbcType);
                    Map<String, String> plugin =  loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list",getString(th, JsoupUtil.BODY_PLUGIN_CODE));
                    if(StringUtils.isBlank(bodyPlugin)||StringUtils.isBlank("td-text")||StringUtils.isBlank(getString(th, JsoupUtil.DATA))){
                        tds.add(dataInDb);
                        continue;
                    }
                    try {
                        String columnDs = getString(th, JsoupUtil.DATASOURCE);
                        if (StringUtils.isBlank(columnDs)) {
                            columnDs = datasourceCore;
                        }
                        DataSource temp = EzClientBootstrap.instance().getDataSourceByKey(columnDs);
                        Map nm = new HashMap();
                        nm.putAll(requestParamMap);
                        nm.putAll(sessionParamMap);
                        ItemInitData items = getSelectItems(temp, getString(th, JsoupUtil.DATA), getString(th, JsoupUtil.DATATYPE),  nm);
                        boolean has=false;
                        if(Utils.isNotEmpty(items.getItems())){
                            for (int k = 0; k < items.getItems().size(); k++) {
                                  if(StringUtils.equals(dataInDb,Utils.trimNull(items.getItems().get(k).get("K")))){
                                      tds.add(Utils.trimNull(items.getItems().get(k).get("V")));
                                      has=true;
                                  }
                            }
                        }
                        if(!has){
                            tds.add(dataInDb);
                        }

                    } catch (Exception e) {
                        LOG.error("EZADMIN LIST={}  列数据异常{} ", JSONUtils.toJSONString(th), e);
                    }
                }
                rowList.add(dataRow);
                coreMap.put("dataList", rowList);
            }
        }catch (Exception e){
            Utils.addLog("获取列表数据异常:", e);
        }
    }
    private void fillcol(Map<String, Object> coreMap,List<Map<String, Object>> colList,Map<String, Object> requestParamMap) throws Exception {
        if(Utils.isNotEmpty(colList)){
            for (int i = 0; i < colList.size(); i++) {
                Map<String,Object> th= colList.get(i);
                Map<String, String> plugin =    loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list",getString(th, JsoupUtil.HEAD_PLUGIN_CODE));

                Context context = new Context();


                //自定义label
                String label=getString(th,JsoupUtil.LABEL);
                if(StringUtils.contains(label,"$")){
                    String newLable=MapParser.parseDefaultEmpty(label, requestParamMap).getResult();
                    th.put(JsoupUtil.LABEL,newLable);
                    Map<String,Object> layData=JSONUtils.parseObjectMap(getString(th,JsoupUtil.LAYDATA));
                    layData.put("title",newLable);
                    th.put(JsoupUtil.LAYDATA,JSONUtils.toJSONString(layData));
                }
                context.setVariables(th);
                String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));

                String html = ThymeleafUtils.processString(template, context);
                th.put(ParamNameEnum.itemParamValue.getName(),Utils.getStringByObject(th,JsoupUtil.ITEM_NAME));
                th.put(ParamNameEnum.itemParamValueStart.getName(),Utils.getStringByObject(th,JsoupUtil.ITEM_NAME+ "_START"));
                th.put(ParamNameEnum.itemParamValueEnd.getName(),Utils.getStringByObject(th,JsoupUtil.ITEM_NAME+ "_END"));
                th.put(ParamNameEnum.itemParamOrderValue.getName(),Utils.getStringByObject(th,JsoupUtil.ITEM_NAME+ "_ORDER"));
                th.put("html",html);
            }
        }
    }

    private void filltablebtn(Map<String, Object> coreMap,Map<String, Object> requestParamMap, List<Map<String, Object>> tablebtnList) throws Exception {
        if(Utils.isNotEmpty(tablebtnList)){
            for (int i = 0; i < tablebtnList.size(); i++) {
                Map<String,Object> table= tablebtnList.get(i);
                table.put(JsoupUtil.URL,MapParser.parseDefaultEmpty(getString(table,JsoupUtil.URL), requestParamMap).getResult());
                table.put(JsoupUtil.WINDOW_NAME,MapParser.parseDefaultEmpty(getString(table,JsoupUtil.WINDOW_NAME), requestParamMap).getResult());
                table.put(JsoupUtil.EZ_CALLBACK,Utils.trimNull(requestParamMap.get(JsoupUtil.EZ_CALLBACK)));

                table.put("importservice",MapParser.parseDefaultEmpty(getString(table,"importservice"), requestParamMap).getResult());
                table.put("importtips",MapParser.parseDefaultEmpty(getString(table,"importtips"), requestParamMap).getResult());
                table.put("importname",MapParser.parseDefaultEmpty(getString(table,"importname"), requestParamMap).getResult());

                Map<String, String> plugin =  loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list",getString(table, JsoupUtil.TYPE));

                Context context = new Context();
                context.setVariables(table);
                String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
                String html = ThymeleafUtils.processString(template, context);
                table.put("html",html);
            }
        }
    }

    private void fillsearch(Map<String, Object> requestParamMap, Map<String, String> sessionParamMap, Map<String, Object> coreMap, List<Map<String, Object>> searchList, String datasourceCore) {
        if(Utils.isEmpty(searchList)){
            return;
        }
            Map<String,Map<String,Object>> validRuleMap=new HashMap<>();
            Map<String,Map<String,Object>> validMsgMap=new HashMap<>();

            fillinitRequestValue(searchList,requestParamMap);

            for (int i = 0; i < searchList.size(); i++) {
                Map<String,Object> search= searchList.get(i);
                String item_name=getString(search,JsoupUtil.ITEM_NAME);
                String pluginCode=getString(search, JsoupUtil.TYPE);
                updateValidate(search, validRuleMap, validMsgMap);
                try {
                    Map<String, String> plugin = loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list",getString(search, JsoupUtil.TYPE));

                    String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
                    Context context = new Context();
                    context.setVariable("model", search);
                    String data = getString(search, JsoupUtil.DATA);
                    String datatype = getString(search, JsoupUtil.DATATYPE);
                    String datasource = getString(search, JsoupUtil.DATASOURCE);
                    //如果是联合搜索，需要传入下拉搜索项
                    //用于渲染页面
                    context.setVariable("itemNameList", search.get("children"));
                    if (StringUtils.isNotBlank(data)) {
                        try {
                            if(StringUtils.isBlank(datasource)){
                                datasource= datasourceCore;
                            }
                            DataSource temp= EzClientBootstrap.instance().getDataSourceByKey(datasource);
                            Map nm=new HashMap();
                            nm.putAll(requestParamMap);
                            nm.putAll(sessionParamMap);
                            ItemInitData items= getSelectItems(temp, data, datatype, nm);
                            context.setVariable("items", items.getItems());
                            String currentValue=getString(search,ParamNameEnum.itemParamValue.getName());
                          //  String currentValue=getString(requestParamMap,getString(search,item_name));
                            String array[]=StringUtils.split(currentValue,",");
                            context.setVariable("jsonArrayValue",JSONUtils.toJSONString(array) );
                            context.setVariable("itemsJson", JSONUtils.toJSONString(items.getItems()));
                        } catch (Exception e) {
                            if(Utils.getLog()!=null) {
                                Utils.addLog("search error:"+coreMap.get("listcode"), e);
                            }
                        }
                    }
                    //初始化值
                    if(  pluginCode.equalsIgnoreCase("input-text")
                    ){
                        Map<String,String> attrMap= (Map<String,String>)search.get("attrMap");
                        attrMap.put("value",StringEscapeUtils.escapeHtml(search.get(ParamNameEnum.itemParamValue.getName())+""));
                        attrMap.put("name",search.get(JsoupUtil.ITEM_NAME)+"");
                        attrMap.putIfAbsent("id","itemId-"+search.get(JsoupUtil.ITEM_NAME)+"");
                        attrMap.putIfAbsent("lay-affix","clear" );
                        attrMap.put("class","layui-input "+attrMap.get("class") );
                        StringBuilder sb=new StringBuilder("<input ");
                        attrMap.forEach((k,v)->{
                            if(StringUtils.equals(k,"data")||StringUtils.isBlank(v)){
                                ;return;
                            }
                            sb.append(k);
                            sb.append("='");
                            sb.append(v);
                            sb.append("'  ");
                        });
                        sb.append(">");
                        context.setVariable("serverDom",sb.toString());
                    }
                    if(  pluginCode.contains("xmselect")
                    ){
                        Map<String,String> attrMap= (Map<String,String>)search.get("attrMap");
                        attrMap.put("value",StringEscapeUtils.escapeHtml(search.get(ParamNameEnum.itemParamValue.getName())+""));
                        attrMap.put("name",search.get(JsoupUtil.ITEM_NAME)+"");
                        attrMap.putIfAbsent("id","itemId-"+search.get(JsoupUtil.ITEM_NAME)+"");
//                        attrMap.putIfAbsent("lay-affix","clear" );
                        attrMap.put("class","  "+Utils.trimNull(attrMap.get("class")) );
                        StringBuilder sb=new StringBuilder("<div ");
                        attrMap.forEach((k,v)->{
                            if(StringUtils.equals(k,"data")||StringUtils.isBlank(v)){
                                ;return;
                            }
                            sb.append(k);
                            sb.append("='");
                            sb.append(v);
                            sb.append("'  ");
                        });
                        sb.append("></div>");
                        context.setVariable("serverDom",sb.toString());
                    }


                    String html = ThymeleafUtils.processString(template, context);
                    search.put("html",html);
                } catch (Exception e) {
                    if(Utils.getLog()!=null) {
                            Utils.addLog("search error:"+coreMap.get("listcode"), e);
                    }
                }

            }
            if(Utils.isNotEmpty(validRuleMap)){
                coreMap.put("validateRules",JSONUtils.toJSONString(validRuleMap).replaceAll("\"","'"));
            }
            if(Utils.isNotEmpty(validMsgMap)){
                coreMap.put("validateMessages",JSONUtils.toJSONString(validMsgMap).replaceAll("\"","'"));
            }


    }

    private void fillinitRequestValue(List<Map<String, Object>> searchList, Map<String, Object> requestParamMap) {
        if(Utils.isEmpty(searchList)){return;}
        for (int i = 0; i < searchList.size(); i++) {
            Map<String,Object> search= searchList.get(i);
            String currentItemname=Utils.trimNull(search.get(JsoupUtil.ITEM_NAME));
            String type=Utils.trimNull(search.get(JsoupUtil.TYPE));
            String orgValue=Utils.trimNull(requestParamMap.get(currentItemname));
            search.put(ParamNameEnum.itemParamValue.getName(),orgValue);
            search.put(ParamNameEnum.itemParamValueStart.getName(),Utils.trimNull(requestParamMap.get(currentItemname+ "_START")));
            search.put(ParamNameEnum.itemParamValueEnd.getName(),Utils.trimNull(requestParamMap.get(currentItemname+ "_END")));
            //联动日期区间  -
            if(type.contains("daterange")&&StringUtils.isNotBlank(orgValue)){
                orgValue=DefaultParamEnum.getValue(orgValue);
                String[] valueSplit=orgValue.split(" - ");
                search.put(currentItemname,orgValue);
                search.put(ParamNameEnum.itemParamValue.getName(),orgValue);
                search.put(ParamNameEnum.itemParamValueStart.getName(),valueSplit[0]);
                search.put(ParamNameEnum.itemParamValueEnd.getName(),valueSplit[1]);
            }

            search.put(ParamNameEnum.itemParamOrderValue.getName(),Utils.trimNull(requestParamMap.get(currentItemname+ "_ORDER")));

            search.put(ParamNameEnum.itemSearchKey.getName(), Utils.getStringByObject(requestParamMap,"itemSearchKey"));
            search.put(ParamNameEnum.itemSearchValue.getName(), Utils.getStringByObject(requestParamMap,"itemSearchValue"));
            search.put(ParamNameEnum.itemSearchConcatValue.getName(), Utils.getStringByObject(requestParamMap,"itemSearchConcatValue"));
            search.put(ParamNameEnum.itemSearchDateKey.getName(), Utils.getStringByObject(requestParamMap,"itemSearchDateKey"));


            String start= DefaultParamEnum.getValue(Utils.trimNull(Utils.getStringByObject(requestParamMap, "itemSearchDateValueStart")));
            String end=DefaultParamEnum.getValue(Utils.trimNull(Utils.getStringByObject(requestParamMap, "itemSearchDateValueEnd")));
            search.put(ParamNameEnum.itemSearchDateValueStart.getName(), start);
            search.put(ParamNameEnum.itemSearchDateValueEnd.getName(), end);

            //联动日期区间  -
            String itemSearchDateValue=Utils.trimNull(requestParamMap.get("itemSearchDateValue"));
            if(StringUtils.isNotBlank(itemSearchDateValue)){
                itemSearchDateValue=DefaultParamEnum.getValue(itemSearchDateValue);
                search.put("itemSearchDateValue",itemSearchDateValue);
                String[] valueSplit=itemSearchDateValue.split(" - ");
                search.put(currentItemname,itemSearchDateValue);
                search.put(ParamNameEnum.itemParamValue.getName(),orgValue);
                search.put(ParamNameEnum.itemSearchDateValueStart.getName(),valueSplit[0]);
                search.put(ParamNameEnum.itemSearchDateValueEnd.getName(),valueSplit[1]);
            }
        }

        for (int i = 0; i < searchList.size(); i++) {
            Map<String, Object> search = searchList.get(i);
            String currentItemname = Utils.trimNull(search.get(JsoupUtil.ITEM_NAME));
            List<Map<String, Object>> childsearchList = chilrenByName(currentItemname, searchList);
            //用于生成sql
            search.put("children", childsearchList);
        }
    }

    private List<Map<String, Object>> chilrenByName(String item_name,   List<Map<String, Object>> searchList) {
        if (!StringUtils.contains(item_name, ","))//多字段逗号分隔{
        {
            return  new ArrayList<Map<String, Object>>();
        }
        List<Map<String, Object>> itemNameList = new ArrayList<Map<String, Object>>();
        for (int j = 0; j < searchList.size(); j++) {
            String itemName = Utils.trimNull(searchList.get(j).get(JsoupUtil.ITEM_NAME));
            if(StringUtils.contains(itemName, ",")){
                //不可以嵌套联合
                continue;
            }
            if (  StringUtils.contains(item_name  ,itemName)) {
                itemNameList.add(searchList.get(j));
            }
        }
       // context.setVariable("itemNameList", itemNameList);
        return itemNameList;
    }

    private void filltab(Map<String, Object> requestParamMap, List<Map<String, Object>> tabList ) {
        if(Utils.isEmpty(tabList)){
            return;
        }
       // boolean select=false;
        for (int i = 0; i < tabList.size(); i++) {
            Map<String,Object> item=new HashMap<>();
            String itemUrl=getString(item,JsoupUtil.URL);
            String url=MapParser.parseDefaultEmpty(itemUrl, requestParamMap).getResult();
            item.put(JsoupUtil.URL,url);
            item.put(JsoupUtil.SELECT,BooleanUtils.toBoolean(Utils.trimNull(item.get(JsoupUtil.SELECT))));

        }
    }

    @Override
    public void     fillTreeById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception {
        Page pagination= loadingPage(list,requestParamMap);
        Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
        List<Map<String,Object>> searchList=(List<Map<String,Object>>)list.get("search");
        List<Map<String,Object>> tabList=new ArrayList<>();
        List<Map<String,Object>> tablebtnList=new ArrayList<>();
        List<Map<String,Object>> colList=(List<Map<String,Object>>)list.get("col");
        List<Map<String,Object>> rowList=new ArrayList<>();
        List<Map<String,Object>> rowbtnList=new ArrayList<>();

        String globalEmptyShow=getString(coreMap,JsoupUtil.EMPTY_SHOW);
        String listcode=getString(coreMap,"listcode");
        String datasourceCore=getString(coreMap,JsoupUtil.DATASOURCE);
        String firstcol=getString(coreMap,"firstcol");
        DataSource dataSourceVO= EzClientBootstrap.instance().getDataSourceByKey(datasourceCore);
        //头部Nav
        if(Utils.isNotEmpty(tabList)){
            boolean select=false;
            for (int i = 0; i < tabList.size(); i++) {
                Map<String,Object> item=new HashMap<>();
                String itemUrl=getString(item,JsoupUtil.URL);
                String url=MapParser.parseDefaultEmpty(itemUrl, requestParamMap).getResult();
                item.put(JsoupUtil.URL,url);
                item.put(JsoupUtil.SELECT,StringUtils.contains(url,listcode)+"");
                if(StringUtils.contains(url,listcode)){
                    select=true;
                }
            }
            if(!select){
                tabList.get(0).put(JsoupUtil.SELECT,"true");
            }
        }
        //搜索项
        fillsearch(requestParamMap, sessionParamMap, coreMap, searchList, datasourceCore);
        //表按钮
        filltablebtn(coreMap,requestParamMap, tablebtnList);
        //表头
        if(Utils.isNotEmpty(colList)){
            for (int i = 0; i < colList.size(); i++) {
                Map<String,Object> th=colList.get(i);
                Map<String, String> plugin =
                        loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list",getString(th,JsoupUtil.HEAD_PLUGIN_CODE));

                Context context = new Context();
                context.setVariables(th);
                String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
                String html = ThymeleafUtils.processString(template, context);
                th.put("html",html);
            }
        }
        //填充数据
        List<Map<String, Object>> dataList=getDataListByListId(dataSourceVO, list, requestParamMap,sessionParamMap, pagination);
        if(Utils.isNotEmpty(dataList)){
            for (int i = 0; i <dataList.size() ; i++) {
                Map<String, Object> dataRow=dataList.get(i);
                List<String> tds=new ArrayList<>();
                dataRow.put("tds",tds);
                if(Utils.isNotEmpty(colList)){
                    rowList.add(dataRow);
                    continue;
                }
                for (int j = 0; j < colList.size(); j++) {
                        Map<String,Object> th=colList.get(j);
                        String itemName=Utils.getStringByObject(th,JsoupUtil.ITEM_NAME);
                        String bodyPlugin=Utils.getStringByObject(th,JsoupUtil.BODY_PLUGIN_CODE);
                        String jdbcType=Utils.getStringByObject(th,JsoupUtil.JDBCTYPE);
                        String url=Utils.getStringByObject(th,JsoupUtil.URL);
                        String windowname=Utils.getStringByObject(th,JsoupUtil.WINDOW_NAME);
                        String dataInDb = ObjectUtils.toString(dataRow.get(itemName));

                        String columnEmptyShow= Utils.getStringByObject(th,JsoupUtil.EMPTY_SHOW);

                        dataInDb=calulateData(dataInDb,globalEmptyShow,columnEmptyShow,jdbcType);

                        Map<String, String> plugin =
                                loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list",getString(th,JsoupUtil.BODY_PLUGIN_CODE));

                        try {
                            //处理第一列
                            if(ColTypeEnum.isFirst(Utils.getStringByObject(th,JsoupUtil.HEAD_PLUGIN_CODE))){
                                Context context = new Context();
                                context.setVariable("firstCol", firstcol);
                                context.setVariable("count", pagination.getStartRecord() + i + 1);
                                context.setVariable("_CHECK_ID_VALUE", dataRow.get("ID"));
                                context.setVariable("dataRow", dataRow.entrySet());
                                String template =  Utils.trimNull(loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list",getString(th,JsoupUtil.BODY_PLUGIN_CODE))
                                        .get("PLUGIN_BODY"));
                                String html = ThymeleafUtils.processString(template, context);
                                if (StringUtils.isBlank(html)) {
                                    tds.add("");
                                } else {
                                    tds.add(html);
                                }
                                continue;
                            }
                            //处理数据列
                            else{
                                if (StringUtils.isNotBlank(bodyPlugin)  ) {
                                    Context context = new Context();
                                    context.setVariables(th);
                                    context.setVariable(JsoupUtil.URL,MapParser.parseDefaultEmpty(url, dataRow).getResult());
                                    context.setVariable(JsoupUtil.WINDOW_NAME,MapParser.parseDefaultEmpty(windowname, dataRow).getResult());


                                    context.setVariables( dataRow);
                                    context.setVariable("dataInDb",dataInDb);
                                    context.setVariable("uploadUrl",requestParamMap.get("ContextPath")+ EzClientBootstrap.instance().getUploadUrl());

                                    if(StringUtils.startsWith(EzClientBootstrap.instance().getDownloadUrl(),"http")){
                                        context.setVariable("downloadUrl", EzClientBootstrap.instance().getDownloadUrl());

                                    }else{
                                        context.setVariable("downloadUrl",requestParamMap.get("ContextPath")+ EzClientBootstrap.instance().getDownloadUrl());
                                    }



                                    if (StringUtils.isNotBlank(getString(th,JsoupUtil.DATA))) {

                                        String columnDs=getString(th,JsoupUtil.DATASOURCE);
                                        if(StringUtils.isBlank(columnDs)){
                                            columnDs=datasourceCore;
                                        }
                                        DataSource temp= EzClientBootstrap.instance().getDataSourceByKey(columnDs);

                                        if(ItemDataSourceType.isEzList(getString(th,JsoupUtil.DATATYPE))){
                                            //获取
//                                            EzList listTemp = new DefaultEzList( getString(th,JsoupUtil.DATA), temp, requestParamMap,sessionParamMap);
//
//                                            list.renderHtml();
                                            //context.setVariable("data", listTemp.getEzListDto());
                                        }else {
                                            try {
                                                Map nm=new HashMap();
                                                nm.putAll(requestParamMap);
                                                nm.putAll(sessionParamMap);
                                                ItemInitData items =  getSelectItems(temp, getString(th,JsoupUtil.DATA),getString(th,JsoupUtil.DATATYPE),
                                                        nm );
                                                context.setVariable("items", items.getItems());
                                                context.setVariable("itemsJson", JSONUtils.toJSONString(items.getItems()));
                                            } catch (Exception e) {
                                                LOG.error("EZADMIN LIST={}  列数据异常{} ",JSONUtils.toJSONString(th),e);
                                            }
                                        }
                                    }

                                    String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
                                    String html = ThymeleafUtils.processString(template, context);
                                    tds.add(html);

                                } else {
                                    tds.add("<td class='  ezadmin-td ezadmin-td-'"+itemName+">" + dataInDb + "</td>");
                                }
                            }

                            //处理按钮列
                            try {


                                if(Utils.isNotEmpty(rowbtnList)) {

                                    List<Map<String,Object>> tempRowItem=new ArrayList<>();
                                    for (int i1 = 0; i1 < rowbtnList.size(); i1++) {
                                        Map<String,Object> item=rowbtnList.get(i1);
                                        Map<String,Object> m=new HashMap<String,Object>();

                                        m.putAll(item);
                                        m.put(JsoupUtil.URL,MapParser.parseDefaultEmpty(Utils.getStringByObject(item,JsoupUtil.URL),  dataRow ).getResult());
                                        m.put(JsoupUtil.LABEL,MapParser.parseDefaultEmpty(Utils.getStringByObject(item,JsoupUtil.LABEL),  dataRow ).getResult());
                                        m.put(JsoupUtil.WINDOW_NAME,MapParser.parseDefaultEmpty(Utils.getStringByObject(item,JsoupUtil.WINDOW_NAME),  dataRow ).getResult());
                                        m.put(JsoupUtil.ITEM_ID,MapParser.parseDefaultEmpty(Utils.trimEmptyDefault(item.get(JsoupUtil.ITEM_ID),"0"),  dataRow ).getResult());
                                        m.put(JsoupUtil.DISPLAY,MapParser.parseDefaultEmpty(Utils.trimEmptyDefault(item.get(JsoupUtil.DISPLAY),"true"),  dataRow ).getResult());
                                        if(BooleanUtils.toBoolean(Utils.getStringByObject(m,JsoupUtil.DISPLAY))){
                                            tempRowItem.add(m);
                                        }
                                    };



                                    Context context = new Context();
                                    if(tempRowItem.size()>0) {
                                        if (tempRowItem.size() > 1) {
                                            context.setVariable("itemsJson", JSONUtils.toJSONString(tempRowItem.subList(1, tempRowItem.size())));
                                        } else {
                                            context.setVariable("itemsJson", JSONUtils.toJSONString(tempRowItem));
                                        }
                                        Map<String, String> buttonPlugin =
                                                loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list", Utils.getStringByObject(tempRowItem.get(0), JsoupUtil.TYPE));

                                        context.setVariable("rowButton0", tempRowItem.get(0));
                                        context.setVariable("rowButtonItemList", tempRowItem);
                                        context.setVariable("rowButtons", tempRowItem);
                                        String template = Utils.trimNull(buttonPlugin.get("PLUGIN_BODY"));
                                        String html = ThymeleafUtils.processString(template, context);
                                        if (StringUtils.isNotBlank(html)) {
                                            tds.add(html);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                LOG.error("EZADMIN LIST={}  初始化第{}行按钮错误{} ",  i,dataRow,e);
                            }


                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                rowList.add(dataRow);
            }
            coreMap.put("dataList",rowList);
        }

        page(pagination,list,requestParamMap);
    }



    @Override
    public void fillCountById(Map<String, Object> list, Map<String, Object> requestParamMap, Map<String, String> sessionParamMap) throws Exception {
        Page pagination= loadingPage(list,requestParamMap);
        Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
        List<Map<String,Object>> searchList=(List<Map<String,Object>>)list.get("search");
        String datasourceCore=getString(coreMap,"datasource");
        String firstcol=getString(coreMap,"firstcol");
        DataSource dataSourceVO= EzClientBootstrap.instance().getDataSourceByKey(datasourceCore);
        fillsearch(requestParamMap, sessionParamMap, coreMap, searchList, datasourceCore);


        String globalEmptyShow=getString(coreMap,"empty_show");
        String listcode=getString(coreMap,"listcode");


        //填充数据
        long count =getDataCountByListId(dataSourceVO, list, requestParamMap,sessionParamMap );
        pagination.setTotalRecord(count);

        page(pagination,list,requestParamMap);

    }


    void page(Page pagination,Map<String, Object> list,Map<String, Object> requestParamMap) throws Exception {
        Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
        String listcode=getString(coreMap,"listcode");
        //分页
        Context context = new Context();
        Map<String, Page> map = new HashMap<>();
        map.put("page", pagination);
        context.setVariable("data", map);
        context.setVariable("encodeListId",listcode);
        String template = Utils.trimNull(
                loadPlugin(Utils.trimNullDefault(coreMap.get(JsoupUtil.ADMINSTYLE),"layui"),"list","page")
                .get("PLUGIN_BODY"));
        String html = ThymeleafUtils.processString(template, context);
        Map<String, Object> pagemap = new HashMap<>();
         pagemap.put("currentPage",pagination.getCurrentPage() );
        pagemap.put("perPageInt",pagination.getPerPageInt() );
        pagemap.put("totalPage",pagination.getTotalPage() );
        pagemap.put("totalRecord",pagination.getTotalRecord() );
        pagemap.put("html",html);
        list.put("page",pagemap);

    }
    public Page loadingPage(Map<String, Object> list,Map<String, Object> requestParamMap) {
        Page  pagination=new Page(requestParamMap);
        List<Map<String,Object>> colList=(List<Map<String,Object>>)list.get("col");
         Map<String,Object>  coreMap= (Map<String,Object> )list.get("core");
        if(Utils.isEmpty(colList)){
            return pagination;
        }
        for (int i = 0; i < colList.size(); i++) {
            Map<String,Object> item = colList.get(i);
            if (!StringUtils.equals(Utils.getStringByObject(item,JsoupUtil.ORDER), "1")) {
                continue;
            }
            String orderValue=Utils.trimNull( Utils.getStringByObject(requestParamMap,item.get(JsoupUtil.ITEM_NAME)+"_ORDER"));
            if (StringUtils.isNotBlank(orderValue)) {
                list.put("ezOrderName",item.get(JsoupUtil.ITEM_NAME)+"_ORDER");
                list.put("ezOrderValue",orderValue);
                pagination.setOrderByClause(item.get(JsoupUtil.ITEM_NAME) + " " + OrderEnum.getSort(orderValue));
                break;
            }

        }
        if(StringUtils.isBlank(pagination.getOrderByClause())){
            pagination.setOrderByClause(Utils.trimNull(coreMap.get("orderby_express")));
        }else{
            pagination.setOrderByClause(" order by "+pagination.getOrderByClause());
        }

        return pagination;
    }
    String getString(Map<String,Object> map,String key){
        return Utils.trimNullDefault(map.get(key));
    }
    private void updateValidate(Map<String,Object> item, Map<String,Map<String,Object>> validRuleMap,
                                Map<String,Map<String,Object>> validMsgMap) {
        String itemName=getString(item,JsoupUtil.ITEM_NAME);
        String rule=getString(item,JsoupUtil.VALIDATERULES);
        String message=getString(item,JsoupUtil.VALIDATEMESSAGES);
        try {
            if (StringUtils.isNotBlank(rule)) {
                Map<String,Object> cof=JSONUtils.parseObjectMap(rule);
//                if(StringUtils.toBoolean(Utils.trimNull(cof.get("required")))){
//                    // item.setLayVerify(Utils.trimNull(item.getLayVerify())+"|required");
//                }
                validRuleMap.put(itemName,cof) ;
                if (StringUtils.isNotBlank(message)) {
                    validMsgMap.put(itemName,JSONUtils.parseObjectMap(message)) ;
                }
            }
        }catch (Exception e){
            LOG.error("EZADMIN 解析校验json失败 itemName={} rule={} message={}", itemName,rule,message,e);
        }
    }
    private String calulateData(String dataInDb, String globalEmptyShow, String columnEmptyShow,String jdbcType) {
        if(StringUtils.isBlank(dataInDb)){
            return Utils.trimEmptyDefault(dataInDb,columnEmptyShow,globalEmptyShow);
        }
        switch (JdbcTypeEnum.get(jdbcType)){
            case NUMBER:
            case NUMBER2:
                BigDecimal result=   new BigDecimal(dataInDb).setScale(2,BigDecimal.ROUND_HALF_UP);
                return result.toString();
            case NUMBER1:
                return  new BigDecimal(dataInDb).setScale(1,BigDecimal.ROUND_HALF_UP).toString();
            case NUMBER3:
                return  new BigDecimal(dataInDb).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
            case NUMBER4:
                return  new BigDecimal(dataInDb).setScale(4,BigDecimal.ROUND_HALF_UP).toString();
            case DATE:
                return  EzDateUtils.toDateFormat(dataInDb);
            case DATETIME:
                return  EzDateUtils.toDateTimeFormat(dataInDb);
            default:
                return dataInDb;
        }
    }
}
