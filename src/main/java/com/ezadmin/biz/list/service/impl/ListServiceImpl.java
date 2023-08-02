package com.ezadmin.biz.list.service.impl;

import com.ezadmin.biz.emmber.list.EzListDTO;
import com.ezadmin.biz.list.service.ListService;
import com.ezadmin.biz.model.ItemInitData;
import com.ezadmin.common.annotation.EzCacheAnnotation;
import com.ezadmin.common.constants.SelectKVContants;
import com.ezadmin.common.enums.*;
import com.ezadmin.common.utils.*;
import com.ezadmin.plugins.cache.Callback;
 import com.ezadmin.plugins.parser.CommentsSqlParser;
import com.ezadmin.plugins.parser.MapParser;
import com.ezadmin.plugins.parser.parse.ResultModel;
import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.list.emmber.list.DefaultEzList;
import com.ezadmin.biz.list.emmber.list.EzList;

import com.ezadmin.plugins.express.executor.DefaultExpressExecutor;
import com.ezadmin.plugins.express.executor.ListExpressExecutor;
import com.ezadmin.biz.dao.Dao;
import com.ezadmin.biz.base.service.CoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;

public class ListServiceImpl implements ListService {

    Logger LOG = LoggerFactory.getLogger(ListServiceImpl.class);
    Dao dao=Dao.getInstance();
    EzBootstrap bootstrap=EzBootstrap.instance();
    CoreService coreService =  EzProxy.singleInstance(CoreService.class);

    @Override
    @EzCacheAnnotation
    public Map<String, String> selectListById(String listId,String encodeListId)   {

            Map<String, String> map= JsoupConfigHolder.selectListById(encodeListId);

            return map;

    }
    @Override
    @EzCacheAnnotation
    public Map<String, String> selectTemplateById(String tempId) throws Exception {
        String sql="select a.*  from  T_CORE_TEMPLATE a " +
                "where TEMPLATE_ID=?";
        return coreService.selectListOne(sql,new Object[]{tempId});
    }


    @Override
    @EzCacheAnnotation
    public List<Map<String, String>> selectSearchByListId(String encodeListId) throws Exception {
        return JsoupConfigHolder.selectSearchByListId(encodeListId);

    }

    @Override
    @EzCacheAnnotation
    public List<Map<String, String>> selectNavByListId(  String encodeListId) throws Exception {
        return JsoupConfigHolder.selectNavByListId(encodeListId);
    }

    @Override
    @EzCacheAnnotation
    public List<Map<String, String>> selectRowButtonByListId( String listId,String encodeListId) throws Exception {

        return JsoupConfigHolder.selectRowButtonByListId(encodeListId);

    }

    @Override
    @EzCacheAnnotation
    public List<Map<String, String>> selectTableButtonByListId(String listId, String encodeListId) throws Exception {

            return JsoupConfigHolder.selectTableButtonByListId(encodeListId);

    }

    @Override
    @EzCacheAnnotation
    public List<Map<String, String>> selectColumnByListId( String listId,String encodeListId) throws Exception {
            return JsoupConfigHolder.selectColumnByListId(encodeListId);
    }




    @Override
    public List<Map<String, String>> getDataListByListId(DataSource dataSource, EzListDTO list, Map<String, Object> request  , Map<String, String> session, Page page) throws Exception {
        //
        ListExpressExecutor listExpressExecutor = ListExpressExecutor.createInstance();
        listExpressExecutor.datasource(dataSource)
                .express(Utils.trimNull(transSqlToQl(list.getSelectExpress())))
                .page(page);
        //额外设置listDTO
        listExpressExecutor.listDTOAndSearchParam(list);
        //计算group by
        String group=excuteGroup(list,request,session);

        listExpressExecutor.addParam("_CHECKD_IDS",Utils.getStringByObject(request,"_CHECKD_IDS"));
        listExpressExecutor.addParam("EZ_SUM_FLAG",Utils.getStringByObject(request,"EZ_SUM_FLAG"));
        listExpressExecutor.addParam("GROUP_BY",group);
        listExpressExecutor.addSessionParam(session);
        listExpressExecutor.addRequestParam( request);
        List<Map<String, String>> dataList  = (List<Map<String, String>>)listExpressExecutor.execute();
        return dataList;
    }




    @Override
    public long getDataCountByListId(DataSource dataSource, EzListDTO list, Map<String, Object> request, Map<String, String> session  ) throws Exception {
        if(StringUtils.isNotBlank(list.getCountExpress())&&!"count(1)".equalsIgnoreCase(list.getCountExpress())){
            ListExpressExecutor listExpressExecutor = ListExpressExecutor.createInstance();
            listExpressExecutor.datasource(dataSource)
                    .express(transSqlCountToQl(Utils.trimNull(list.getCountExpress( ))));
            //
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
        }else{
            ListExpressExecutor listExpressExecutor = ListExpressExecutor.createInstance();
            listExpressExecutor.datasource(dataSource)
                    .express(transSqlCountToQl(Utils.trimNull(list.getSelectExpress( ))));
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
    }

    private String excuteGroup( EzListDTO list, Map<String, Object> request, Map<String, String> session  )   {
       try {
           //兼容老的 group by
           if(StringUtils.startWithTrimAndLower(list.getDefaultGroup(),"group ")){
                return list.getDefaultGroup();
           }
           ListExpressExecutor groupExe = ListExpressExecutor.createInstance();
           groupExe.listDTOAndSearchParam(list);
           groupExe.express(list.getDefaultGroup());
           groupExe.addSessionParam(session);
           groupExe.addRequestParam( request);
           Object obj = groupExe.executeCount();
           return Utils.trimNull(obj);
       }catch (Exception e){
           LOG.error("EZADMIN LIST={}  group by 配置错误 {} ,",list.getEncodeListId() ,list.getDefaultGroup(), e);
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



    @Override
    public Map<String, Object> selectLogById(String logId, String userID) throws Exception {
        String sql="select FILE.FILE_BLOB,FILE.FILE_NAME from T_CORE_LOG LOG LEFT JOIN T_CORE_FILE FILE " +
                "ON LOG.LOG_CONTENT = FILE.FILE_MD5 " +
                "WHERE LOG.LOG_ID=? and LOG.OPT_USER_ID=? ";
        return Dao.getInstance().executeQueryOne(bootstrap.getOriginDataSource(),sql, new Object[]{logId,userID});
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
            for (String item:EzBootstrap.instance().datasourceKeys()) {
                Map<String, Object> map2 = new HashMap<>();
                map2.put(SelectKVContants.KEY, item);
                map2.put(SelectKVContants.VALUE, item);
                keyvalues.add(map2);
            }
            return data.items(keyvalues);
        }
        if (ShortItemValueEnum.FirstColEnum.name().equalsIgnoreCase(sql)) {
            List<Map<String, Object>> keyvalues = new ArrayList();
            for (int i = 0; i < FirstColEnum.values().length; i++) {
                Map<String, Object> map2 = new HashMap<>();
                map2.put(SelectKVContants.KEY, FirstColEnum.values()[i].name());
                map2.put(SelectKVContants.VALUE, FirstColEnum.values()[i].name());
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
            return data.items(JsoupConfigHolder.listPlugin(""));
        }
        if ("plugins4td".equalsIgnoreCase(sql)) {
            return data.items(JsoupConfigHolder.listPlugin("td"));
        }
        if ("plugins4th".equalsIgnoreCase(sql)) {
            return data.items(JsoupConfigHolder.listPlugin("th"));
        }
        if ("plugins4search".equalsIgnoreCase(sql)) {
            return data.items(JsoupConfigHolder.listPlugin("search"));
        }
        if ("listbutton".equalsIgnoreCase(sql)) {
            return data.items(JsoupConfigHolder.listPlugin("listbutton"));
        }
        if ("plugins4form".equalsIgnoreCase(sql)) {
            return data.items(JsoupConfigHolder.formPlugin());
        }
        if("group_data_init".equalsIgnoreCase(sql)){
            Map<String, String>  form= JsoupConfigHolder.selectFormById(Utils.trimNull(request.get("ID")));
            try {
            return     data.items(JSONUtils.parseListMapString(form.get(JsoupUtil.GROUP_DATA)));
            }catch (Exception e){

            }
        }
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
                    List list= dao.executeQueryString(datasource, model.getResult(), null);
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
            return  data.items(( List<Map<String, Object>>) EzBootstrap.instance().getCache().get5("KV_SQL_CACHE_", key
                    , callback));
        }

        return  data.items(( List<Map<String, Object>>) EzBootstrap.instance().getCache().get60("KV_SQL_CACHE_", key
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
        for (Map.Entry<String,Object> entry:params.entrySet())
        {
            curMap.put(entry.getKey(),entry.getValue()+"");
        } ;
        String str=  MapParser.parse(initData,params).getResult();
        Map<String,String> configMap=JSONUtils.parseMap(str);
        curMap.putAll(configMap);
        EzList list = new DefaultEzList( initData, bootstrap.getOriginDataSource(), curMap ,curMap);
        list.renderHtml();
        data.setEzList(list.getEzListDto());
        return data;
    }
    public ItemInitData getSelectItems(DataSource datasource, String initData, String initType, Map<String, Object> params
    ) throws Exception {
        ItemInitData data=new ItemInitData();
        initType=StringUtils.upperCase(StringUtils.isBlank(initType)?ItemDataSourceType.TEXT.name():initType);

        switch (ItemDataSourceType.get(initType)){
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

    private ItemInitData kvTreeSqlCache(String initData, Map<String, Object> params, DataSource datasource, String s) {
       final ItemInitData data=kvSqlCache(initData,params,datasource,s);
        String key= StringUtils.filterLetterOrDigit(initData);
        List<Map<String,Object>> d=(List<Map<String,Object>>) EzBootstrap.instance().getCache().get("kvtreesqlcache", key, new Callback() {
            @Override
            public Object call(String key) {
                return Utils.flatTree(data.getItems());
            }
        });
        data.items(d);
        return data;
    }

    @EzCacheAnnotation
    public Map<String, String> getDbTemplateByCode(String code, int templateId,String fold) throws Exception {
        try {
            if (StringUtils.equalsIgnoreCase("firstcol-", code)) {
                return Collections.emptyMap();
            }
            if (StringUtils.equalsIgnoreCase("hidden-nowhere", code)) {
                return Collections.emptyMap();
            }
            Map<String, String> plugin = JsoupConfigHolder.getDbTemplateByCode(code, fold);
            if (Utils.isNotEmpty(plugin)) {
                return plugin;
            }
            if (StringUtils.equalsIgnoreCase("firstcol-", code)) {
                return plugin;
            }
        }catch (Exception e){
            LOG.warn("plugin error{}",code,e);
        }
        return Collections.emptyMap();
    }
}
