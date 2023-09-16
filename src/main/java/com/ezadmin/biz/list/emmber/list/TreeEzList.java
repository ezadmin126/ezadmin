//package com.ezadmin.biz.list.emmber.list;
//
//import com.ezadmin.EzBootstrap;
//import com.ezadmin.biz.emmber.ListRow;
//import com.ezadmin.biz.emmber.list.EzListDTO;
//import com.ezadmin.biz.list.service.ListService;
//import com.ezadmin.biz.model.EzSearchModel;
//import com.ezadmin.biz.model.ItemInitData;
//import com.ezadmin.common.enums.ItemDataSourceType;
//import com.ezadmin.common.enums.JdbcTypeEnum;
//import com.ezadmin.common.enums.ParamNameEnum;
//import com.ezadmin.common.enums.template.TemplateEnum;
//import com.ezadmin.common.utils.*;
//import com.ezadmin.plugins.cache.Callback;
//import com.ezadmin.plugins.parser.MapParser;
//import org.apache.commons.lang.BooleanUtils;
//import org.apache.commons.lang.ObjectUtils;
//import org.thymeleaf.context.Context;
//
//import javax.sql.DataSource;
//import java.lang.reflect.InvocationTargetException;
//import java.math.BigDecimal;
//import java.util.*;
//
//
///**
// *
// **/
//public class TreeEzList extends AbstractEzList {
//
//
//    private String encodeListId;
//
//     private ListService listService = EzProxy.singleInstance(ListService.class);
//
//
//
//    public TreeEzList(DataSource dataSource, Map<String, Object>  requestParamMap, Map<String, String>  sessionParamMap) {
//        super(dataSource,requestParamMap,sessionParamMap);
//    }
//    public TreeEzList(String encodeListId, DataSource dataSource, Map   requestParamMap, Map<String, String>  sessionParamMap) {
//        super(dataSource,requestParamMap,sessionParamMap);
//
//        this.encodeListId=encodeListId;
//        loading();
//    }
//
//
//    @Override
//    public EzListDTO loadingBase() {
//        EzListDTO list= null;
//        try {
//            Map<String, String> listDb = listService.selectListById(""  ,encodeListId);
//
//            if(Utils.getLog()!=null){
//                Utils.getLog().add( listDb.toString());
//            }
//            list=EzListDTO.mapTo(listDb);
//            list.setOriginMap(listDb);
//
//            //自动替换数据库
//
//            dataSource= EzBootstrap.instance().getDataSourceByKey(StringUtils.lowerCase(listDb.get(JsoupUtil.DATASOURCE)) );
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={} loadingBase",encodeListId,e);
//            list=new EzListDTO();
//        }
//        return list;
//    }
//
//    @Override
//    public List<Map<String,String>> loadingNavBar() {
//        try {
//            List<Map<String, String>>  listDb = listService.selectNavByListId( encodeListId);
//            return listDb;
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={}  ",encodeListId,e);
//        }
//        return Collections.emptyList();
//    }
//
//    @Override
//    public List<EzSearchModel> loadingSearch() {
//        try {
//            List<EzSearchModel> modelList=new ArrayList<>();
//            List<Map<String, String>>  listDb = listService.selectSearchByListId( encodeListId);
//            for (int i = 0; i < listDb.size(); i++) {
//                EzSearchModel m=new EzSearchModel();
//                //配置复制
//                m.config().putAll(listDb.get(i));
//                //request引用
//                m.setpParam(requestParamMap);
//                m.setSession(sessionParamMap);
//                modelList.add(m);
//            }
//            return modelList;
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={}  ",encodeListId,e);
//        }
//        return Collections.emptyList();
//    }
//
//    /**
//     * 初始化验证规则
//
//     */
//    private void updateValidate(EzSearchModel item, Map<String,Map<String,Object>> validRuleMap,
//                                Map<String,Map<String,Object>> validMsgMap) {
//        String itemName=item.getConfig(JsoupUtil.ITEM_NAME);
//        String rule=item.getConfig(JsoupUtil.VALIDATERULES);
//        String message=item.getConfig(JsoupUtil.VALIDATEMESSAGES);
//        try {
//            if (StringUtils.isNotBlank(rule)) {
//                Map<String,Object> cof=JSONUtils.parseObjectMap(rule);
//                if(StringUtils.toBoolean(Utils.trimNull(cof.get("required")))){
//                   // item.setLayVerify(Utils.trimNull(item.getLayVerify())+"|required");
//                }
//                validRuleMap.put(itemName,cof) ;
//                if (StringUtils.isNotBlank(message)) {
//                    validMsgMap.put(itemName,JSONUtils.parseObjectMap(message)) ;
//                }
//            }
//        }catch (Exception e){
//            LOG.error("EZADMIN LIST={} 解析校验json失败 itemName={} rule={} message={}",encodeListId,itemName,rule,message,e);
//        }
//    }
//
//    @Override
//    public List<Map<String, String>>  loadingTableButton() {
//        try {
//            List<Map<String, String>>  list=new ArrayList<>();
//            List<Map<String, String>>  listDb = listService.selectTableButtonByListId( "" ,encodeListId);
//            for (int i = 0; i < listDb.size(); i++) {
//                Map<String, String> item=listDb.get(i);
//                Map<String,String> map=new HashMap<String,String>();
//                map.putAll(item);
//                map.put(JsoupUtil.URL,MapParser.parseDefaultEmpty(item.get(JsoupUtil.URL),requestParamMap).getResult());
//                map.put(JsoupUtil.WINDOW_NAME,MapParser.parseDefaultEmpty(item.get(JsoupUtil.WINDOW_NAME),requestParamMap).getResult());
//                map.put(JsoupUtil.EZ_CALLBACK,Utils.trimNull(requestParamMap.get(JsoupUtil.EZ_CALLBACK)));
//                 list.add(map);
//            };
//            return list;
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={}  ",encodeListId,e);
//        }
//        return Collections.emptyList();
//    }
//
//    @Override
//    public List<Map<String, String>>  loadingColumn() {
//        try {
//            List<Map<String, String>>  listDb = listService.selectColumnByListId(""  ,encodeListId);
//
//            for (int i = 0; i < listDb.size(); i++) {
//                Map<String, String> item=listDb.get(i);
//                 item.put(ParamNameEnum.itemParamValue.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME))));
//                 item.put(ParamNameEnum.itemParamValueStart.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME+ "_START"))));
//                 item.put(ParamNameEnum.itemParamValueEnd.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME+ "_END"))));
//                 item.put(ParamNameEnum.itemParamOrderValue.getName(),Utils.trimNull(requestParamMap.get(item.get(JsoupUtil.ITEM_NAME+ "_ORDER"))));
//            } ;
//            return listDb;
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={}  ",encodeListId,e);
//        }
//        return Collections.emptyList();
//    }
//
//    @Override
//    public List<Map<String,String>> loadingRowButton() {
//        try {
//            List<Map<String, String>>  list=new ArrayList<>();
//            List<Map<String, String>>  listDb = listService.selectRowButtonByListId(""  ,encodeListId);
//            return listDb;
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={}  ",encodeListId,e);
//        }
//        return Collections.emptyList();
//    }
//
//
//    @Override
//    public List<Map<String, String>> loadingData()   {
//        List<Map<String, String>> dataList = new ArrayList<>();
//
//        return dataList;
//    }
//    void renderNavbar(){
//        if(Utils.isNotEmpty(ezListDTO.getNavBarsList())){
//            boolean select=false;
//            List<Map<String,String>> list=ezListDTO.copyNavItemList();
//            for (int i = 0; i <  list.size(); i++) {
//                Map<String,String> item=list.get(i);
//                try {
//                    String itemUrl=item.get(JsoupUtil.URL);
//                    String url=MapParser.parseDefaultEmpty(itemUrl, requestParamMap).getResult();
//                    item.put(JsoupUtil.URL,url);
//                    item.put(JsoupUtil.SELECT,StringUtils.contains(url,encodeListId)+"");
//                    if(StringUtils.contains(url,encodeListId)){
//                        select=true;
//                    }
//                }catch (Exception e){
//                    LOG.error("EZADMIN LIST={}  渲染导航错误",encodeListId,e);
//                }
//            }
//            ezListDTO.setNavBarsList(list);
//            //默认第一个选中
//            if(!select){
//               ezListDTO.getNavBarsList().get(0).put(JsoupUtil.SELECT,"true");
//            }
//        }
//
//    }    @Override
//    public void renderHtml() {
//
//        String globalEmptyShow= ezListDTO.getEmptyShow();
//        //头部Nav
//        renderNavbar();
//        //
//        Map<String,EzSearchModel> searchVOMap= renderSearch();
//        //表按钮
//        renderTableButton();
//        //表头
//        renderThead(searchVOMap);
//        List<ListRow> rows = new ArrayList<>();
//        if (Utils.isNotEmpty(ezListDTO.getDataList())){
//
//            for (int i = 0; i < ezListDTO.getDataList().size(); i++) {
//                //不指定类型，减少内存copy
//                Map dataRow=ezListDTO.getDataList().get(i);
//
//                if(LOG.isDebugEnabled()){
//                    LOG.debug("EZADMIN LIST={}  渲染行按钮开始 第 {} 行  开始 {}",encodeListId ,i ,dataRow);
//                }
//                ListRow resultRow = new ListRow();//TR
//                if(dataRow.containsKey("PARENT_ID")){
//                    resultRow.setId(NumberUtils.toInt(""+dataRow.get("ID")));
//                    resultRow.setPid(NumberUtils.toInt(""+dataRow.get("PARENT_ID")));
//                }
//                //补充首列
//                try {
//                    if(StringUtils.isNotBlank(ezListDTO.getFirstCol())){
//                        if(LOG.isDebugEnabled()){
//                            LOG.debug("EZADMIN LIST={}  渲染行按钮开始 第 {} 行  首列{}",encodeListId ,i ,ezListDTO.getFirstCol());
//                        }
//                        Context context = new Context();
//                        context.setVariable("firstCol", ezListDTO.getFirstCol());
//                        context.setVariable("count", pagination.getStartRecord()+i+1);
//                        context.setVariable("_CHECK_ID_VALUE", dataRow.get("ID"));
//                        context.setVariable("dataRow", dataRow);
//                        String template = Utils.trimNull(listService.getDbTemplateByCode(ezListDTO.getFirstCol(),ezListDTO.getTemplateId(),"list")
//                                .get("PLUGIN_BODY"));
//                        String html = ThymeleafUtils.processString(template, context);
//                        if(StringUtils.isBlank(html)){
//                            resultRow.addTd("");
//                        }else{
//                            resultRow.addTd(html);
//                        }
//                    }
//
//                } catch (Exception e) {
//                    LOG.error("EZADMIN LIST={}  补充首列 第 {} 行 异常 {}",encodeListId ,i ,ezListDTO.getFirstCol());
//                }
//
//                //补充数据列
//                for (int j = 0; j < ezListDTO.getColumnItemList().size(); j++) {
//                    Map<String,String> column=ezListDTO.getColumnItemList().get(j);
//                    String itemName=column.get(JsoupUtil.ITEM_NAME);
//                    if(LOG.isDebugEnabled()){
//                        LOG.debug("EZADMIN LIST={}  渲染列开始 第 {} 行 第 {} 列  {}",encodeListId ,i,j ,JSONUtils.toJSONString(column));
//                    }
//                    Map<String, String> plugin = new HashMap<>();
//                    if (StringUtils.isNotBlank(column.get(JsoupUtil.BODY_PLUGIN_CODE))  ) {
//                        try {
//                            plugin = listService.getDbTemplateByCode(column.get(JsoupUtil.BODY_PLUGIN_CODE),ezListDTO.getTemplateId(),"list");
//                        } catch (Exception e) {
//                            if(Utils.getLog()!=null){
//                                Utils.addLog("获取插件错误"+column.get(JsoupUtil.BODY_PLUGIN_CODE),e);
//                            }
//                        }
//                    }
//                    Map<String, String> pluginEdit = new HashMap<>();
//                    if ("1".equals(column.get(JsoupUtil.EDIT_FLAG))&&StringUtils.isNotBlank(column.get(JsoupUtil.EDIT_PLUGIN))  ) {
//                        try {
//                            pluginEdit = listService.getDbTemplateByCode(column.get(JsoupUtil.EDIT_PLUGIN),ezListDTO.getTemplateId(),"form");
//                        } catch (Exception e) {
//                            LOG.error("获取插件错误"+column.get(JsoupUtil.EDIT_PLUGIN),e);
//                            if(Utils.getLog()!=null){
//                                Utils.addLog("获取插件错误"+column.get(JsoupUtil.EDIT_PLUGIN),e);
//                            }
//                        }
//                    }
//                    String dataInDb = ObjectUtils.toString(dataRow.get(column.get(JsoupUtil.ITEM_NAME)));
//
//                    String columnEmptyShow= column.get(JsoupUtil.EMPTY_SHOW);
//
//                    dataInDb=calulateData(dataInDb,globalEmptyShow,columnEmptyShow,column.get(JsoupUtil.JDBCTYPE));
//
//                    if (StringUtils.isNotBlank(column.get(JsoupUtil.BODY_PLUGIN_CODE))  ) {
//                        Context context = new Context();
//                        for(Map.Entry<String,String> entry:column.entrySet()){
//                            context.setVariable(entry.getKey(),entry.getValue());
//                        }
//                        context.setVariable(JsoupUtil.URL,MapParser.parseDefaultEmpty(column.get(JsoupUtil.URL), dataRow).getResult());
//                        context.setVariable(JsoupUtil.WINDOW_NAME,MapParser.parseDefaultEmpty(column.get(JsoupUtil.WINDOW_NAME), dataRow).getResult());
//
//
//                        context.setVariables( dataRow);
//                        context.setVariable("dataInDb",dataInDb);
//                        context.setVariable("uploadUrl",requestParamMap.get("ContextPath")+EzBootstrap.instance().getUploadUrl());
//
//                        if(StringUtils.startsWith(EzBootstrap.instance().getDownloadUrl(),"http")){
//                            context.setVariable("downloadUrl", EzBootstrap.instance().getDownloadUrl());
//
//                        }else{
//                            context.setVariable("downloadUrl",requestParamMap.get("ContextPath")+EzBootstrap.instance().getDownloadUrl());
//                        }
//
//
//                        if(ItemDataSourceType.isHTTPCACHE(column.get(JsoupUtil.DATATYPE))){
//                            try {
//                              final  String finalItemName=itemName;
//                              final  Map<String,String> finalColumn=column;
//                                String value=(String) EzBootstrap.instance().getCache().get60("HTTP_SQL_CACHE_"+column.get(JsoupUtil.ITEM_NAME)+"_", dataInDb
//                                        , new Callback() {
//                                            @Override
//                                            public Object call(String key) {
//                                                try {
//                                                    if(LOG.isDebugEnabled()){
//                                                        LOG.debug("EZADMIN LIST={}  渲染列HTTP_SQL_CACHE_列{}  {}",encodeListId ,finalItemName ,key);
//                                                    }
//                                                    return Utils.loadHttp(key, finalColumn.get(JsoupUtil.DATA));
//                                                }catch(Exception e){
//                                                    LOG.error("EZADMIN LIST={}  http列{}异常 {}",encodeListId ,finalItemName, key);
//                                                    return "";
//                                                }
//                                            }
//
//                                        });
//
//                                if(LOG.isDebugEnabled()){
//                                    LOG.debug("EZADMIN LIST={}  渲染列HTTP_SQL_CACHE_列{}  {}",encodeListId ,itemName ,value);
//                                }
//                                resultRow.addTd("<td class='  ezadmin-td ezadmin-td-'"+itemName+">"+value+"</td>");
//                            }catch(Exception e){
//                                 LOG.error("EZADMIN LIST={}  http列{}异常  ",encodeListId ,itemName, e);
//                                resultRow.addTd("<td></td>");
//                            }
//                        } else{
//                            if (StringUtils.isNotBlank(column.get(JsoupUtil.DATA))) {
//                                DataSource temp=dataSource;
//                                if(StringUtils.isNotBlank(column.get(JsoupUtil.DATASOURCE))&&EzBootstrap.instance().getDataSourceByKey(column.get(JsoupUtil.DATASOURCE))!=null){
//                                    temp=EzBootstrap.instance().getDataSourceByKey(column.get(JsoupUtil.DATASOURCE));
//                                }
//
//                                if(ItemDataSourceType.isEzList(column.get(JsoupUtil.DATATYPE) )){
//                                    //获取
//                                    EzList list = new TreeEzList( column.get(JsoupUtil.DATA), temp, requestParamMap,sessionParamMap);
//
//                                    list.renderHtml();
//                                    context.setVariable("data", list.getEzListDto());
//                                }else {
//                                    try {
//                                        Map nm=new HashMap();
//                                        nm.putAll(requestParamMap);
//                                        nm.putAll(sessionParamMap);
//                                        ItemInitData items = listService.getSelectItems(temp, column.get(JsoupUtil.DATA), column.get(JsoupUtil.DATATYPE),
//                                                nm );
//                                        context.setVariable("items", items.getItems());
//                                        context.setVariable("itemsJson", JSONUtils.toJSONString(items.getItems()));
//                                    } catch (Exception e) {
//                                        LOG.error("EZADMIN LIST={}  列数据异常{} ",encodeListId,JSONUtils.toJSONString(column),e);
//                                    }
//                                }
//                            }
//                            if("1".equals(column.get(JsoupUtil.EDIT_FLAG))){
//                                String templateEdit = Utils.trimNull(pluginEdit.get("PLUGIN_BODY"));
//                                context.setVariable(ParamNameEnum.itemParamValue.getName(),dataInDb);
//                                String htmlEdit = ThymeleafUtils.processString(templateEdit, context);
//                                context.setVariable("editHtml", htmlEdit);
//                            }
//                            String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
//                            String html = ThymeleafUtils.processString(template, context);
//                            resultRow.addTd(html);
//                        }
//                    } else {
//                        resultRow.addTd("<td class='  ezadmin-td ezadmin-td-'"+column.get(JsoupUtil.ITEM_NAME)+">" + dataInDb + "</td>");
//                    }
//                }
//
//                rows.add(resultRow);
//                if(LOG.isDebugEnabled()){
//                    LOG.debug("EZADMIN LIST={}  渲染行按钮开始 第 {} 行  结束",encodeListId ,i  );
//                }
//            }
//
//        }
//        ezListDTO.setDataRow(rows);
//
//        renderPage();
//    }
//
//    private void renderThead(Map<String, EzSearchModel> searchVOMap) {
//        if (Utils.isNotEmpty(ezListDTO.getColumnItemList())) {
//            for (int i = 0; i < ezListDTO.getColumnItemList().size(); i++) {
//                Map<String,String> item=ezListDTO.getColumnItemList().get(i);
//                if(LOG.isDebugEnabled()){
//                    LOG.debug("EZADMIN LIST={}  渲染表头开始 第 {} 列  {}",encodeListId ,i ,JSONUtils.toJSONString(item));
//                }
//                try {
//                    Map<String, String> plugin = listService.getDbTemplateByCode(item.get(JsoupUtil.HEAD_PLUGIN_CODE),ezListDTO.getTemplateId(),"list");
//                    Context context = new Context();
//                    EzSearchModel searchVO= searchVOMap.get(item.get(JsoupUtil.ITEM_NAME));
//                    //页头搜索
//                    if(searchVO!=null&&JsoupConfigHolder.existHtmlList(encodeListId)){
//                        item.put("searchHtml",searchVO.getTableHtml());
//                        if("hidden".equals(searchVO.getConfig(JsoupUtil.PLUGIN))){
//                            item.put("searchHtml","");
//                        }
//                        String value=searchVO.getParam(searchVO.getConfig(JsoupUtil.ITEM_NAME));
//                        String valueS=searchVO.getParam(searchVO.getConfig(JsoupUtil.ITEM_NAME)+"_START");
//                        String valueE=searchVO.getParam(searchVO.getConfig(JsoupUtil.ITEM_NAME)+"_END");
//                        if(StringUtils.isNotBlank(value)||
//                                StringUtils.isNotBlank(valueS)||
//                                StringUtils.isNotBlank(valueE)
//                        ){
//                            //用于表头颜色控制
//                            item.put(ParamNameEnum.itemParamValue.getName(),"1");
//                        }
//                    }
//
//                    //debug覆盖
//                    if(StringUtils.equals(Utils.getStringByObject(requestParamMap,  "IS_DEBUG"),"1")){
//                        Map<String,String> layDataMap= JSONUtils.parseMap(item.get(JsoupUtil.LAYDATA));
//                        layDataMap.put("minWidth","175");
//                        context.setVariable(JsoupUtil.LAYDATA,JSONUtils.toJSONString(layDataMap));
//                    }
//                    //
//                    for(Map.Entry<String,String> entry:item.entrySet()) {
//                        context.setVariable(entry.getKey(),entry.getValue());
//                    } ;
//
//                    String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
//                    String html = ThymeleafUtils.processString(template, context);
//                    item.put("html",html);
//                    //页脚统计
//                } catch (Exception e) {
//                    LOG.error("EZADMIN LIST={}  渲染表头错误{} ",encodeListId,JSONUtils.toJSONString(item),e);
//                }
//
//            }
//        }
//    }
//
//
//
//    private Map<String,EzSearchModel>  renderSearch(   ) {
//        Map<String,EzSearchModel>searchVOMap =new HashMap<>();
//        if (Utils.isNotEmpty(ezListDTO.getSearchItemList())) {
//            if(LOG.isDebugEnabled()){
//                LOG.debug("EZADMIN LIST={}  渲染搜索开始 {} ",encodeListId ,JSONUtils.toJSONString(searchVOMap));
//            }
//            Map<String,Map<String,Object>> validRuleMap=new HashMap<>();
//            Map<String,Map<String,Object>> validMsgMap=new HashMap<>();
//             for (int i = 0; i < ezListDTO.getSearchItemList().size(); i++) {
//                EzSearchModel item=ezListDTO.getSearchItemList().get(i);
//                if(LOG.isDebugEnabled()){
//                    LOG.debug("EZADMIN LIST={}  渲染搜索开始 第 {} 项 {} ",encodeListId ,i,JSONUtils.toJSONString(item));
//                }
//                searchVOMap.put(item.getConfig(JsoupUtil.ITEM_NAME),item);
//                updateValidate(item, validRuleMap, validMsgMap);
//                 try {
//                    Map<String, String> plugin = listService.getDbTemplateByCode(item.getConfig(JsoupUtil.PLUGIN),ezListDTO.getTemplateId(),"list");
//                    String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
//                    //如果处于编辑状态
//                    if(StringUtils.equals(Utils.getStringByObject(requestParamMap,  "IS_DEBUG"),"1")&&StringUtils.isBlank(template)){
//                        plugin = listService.getDbTemplateByCode(TemplateEnum.TEXTSEARCH.getCode(), ezListDTO.getTemplateId(),"list");
//                        template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
//                    }
//
//                    Context context = new Context();
//                    context.setVariable("model",item);
//                    String data=item.getConfig(JsoupUtil.DATA);
//                    String datatype=item.getConfig(JsoupUtil.DATATYPE);
//                    String datasource=item.getConfig(JsoupUtil.DATASOURCE);
//
//
//                    if (StringUtils.isNotBlank(data)) {
//                        //支持session "EZ_SESSION_MY_USER_MAP_KEY"
//                        if(StringUtils.startWithTrimAndLower(data,"${session.")){
//                            String sk=sessionParamMap.get(StringUtils.repaceAll(data,"${session.","")
//                                    .replace("}",""));
//                            List<Map<String, Object>> items =  JSONUtils.parseListMapString(sk);
//                            if(Utils.isEmpty(items)){
//                                items=new ArrayList<>();
//                            }
//                            context.setVariable("items", items);
//                            context.setVariable("itemsJson", JSONUtils.toJSONString(items));
//
//                            if(LOG.isDebugEnabled()){
//                                LOG.debug("EZADMIN LIST={}  渲染搜索开始 第 {} 项 {} 包含Session参数{} ",encodeListId ,i,JSONUtils.toJSONString(item),sk);
//                            }
//                        }else {
//                            try {
//                                DataSource temp=dataSource;
//                                if(StringUtils.isNotBlank(datasource)&&EzBootstrap.instance().getDataSourceByKey(datasource)!=null){
//                                    temp=EzBootstrap.instance().getDataSourceByKey(datasource);
//                                }
//                                Map nm=new HashMap();
//                                nm.putAll(requestParamMap);
//                                nm.putAll(sessionParamMap);
//                                ItemInitData items=listService.getSelectItems(temp, data, datatype,
//                                        nm);
//                                context.setVariable("items", items.getItems());
//                                String array[]=StringUtils.split(item.getParam(item.getConfig(JsoupUtil.ITEM_NAME)),",");
//                                context.setVariable("jsonArrayValue",JSONUtils.toJSONString(array) );
//                                context.setVariable("itemsJson", JSONUtils.toJSONString(items.getItems()));
//                            } catch (Exception e) {
//                                LOG.error("EZADMIN LIST={}  搜索项初始化数据失败 {}",encodeListId,JSONUtils.toJSONString(item),e);
//                            }
//                        }
//                    }
//                     List<EzSearchModel> list=new ArrayList<EzSearchModel>();
//                     for (int j = 0; j < ezListDTO.getSearchItemList().size(); j++) {
//                         if((!StringUtils.equalsIgnoreCase(item.getConfig(JsoupUtil.ITEM_NAME), ezListDTO.getSearchItemList().get(j).getConfig(JsoupUtil.ITEM_NAME))
//                                 &&StringUtils.contains(item.getConfig(JsoupUtil.ITEM_NAME)
//                                 ,ezListDTO.getSearchItemList().get(j).getConfig(JsoupUtil.ITEM_NAME)))){
//                             list.add(ezListDTO.getSearchItemList().get(j));
//                         }
//                     }
//                    context.setVariable("itemNameList",list);
////
//                    context.setVariable("searchFlag",true);
//                    context.setVariable("tableSearchFlag",false);
//                    String html = ThymeleafUtils.processString(template, context);
//                    if(StringUtils.equalsIgnoreCase("N/A",html)){
//                        LOG.error("插件渲染错误"+item.getConfig(JsoupUtil.PLUGIN));
//                    }
//                    item.setHtml(html);
//
//                    context.setVariable("searchFlag",false);
//                    context.setVariable("tableSearchFlag",true);
//                    String searchHtml = ThymeleafUtils.processString(template, context);
//                    item.setTableHtml(searchHtml);
//                } catch (Exception e) {
//                    LOG.error("EZADMIN LIST={}  渲染搜索项错误",encodeListId,e);
//                }
//
//            }
//
//            if(Utils.isNotEmpty(validRuleMap)){
//                ezListDTO.setValidateRules(JSONUtils.toJSONString(validRuleMap));
//            }
//            if(Utils.isNotEmpty(validMsgMap)){
//                ezListDTO.setValidateMessages(JSONUtils.toJSONString(validMsgMap));
//            }
//        }
//        return searchVOMap;
//    }
//
//    private void renderPage() {
//        try {
//
//            Context context = new Context();
//            Map<String, Page> map = new HashMap<>();
//            map.put("page", pagination);
//            context.setVariable("data", map);
//            context.setVariable("encodeListId",encodeListId);
//            String template = Utils.trimNull(listService.getDbTemplateByCode(TemplateEnum.PAGE.getCode(), ezListDTO.getTemplateId(),"list")
//                    .get("PLUGIN_BODY"));
//            String html = ThymeleafUtils.processString(template, context);
//
//            ezListDTO.setPageHtml(html);
//        } catch (Exception e) {
//            LOG.error("EZADMIN LIST={}  页脚异常 ",encodeListId,e);
//        }
//    }
//
//    private void renderTableButton() {
//        if (Utils.isNotEmpty(ezListDTO.getTableButtonItemList())) {
//            for (int i = 0; i < ezListDTO.getTableButtonItemList().size(); i++) {
//                Map<String, String> item=ezListDTO.getTableButtonItemList().get(i);
//                try {
//                    if(LOG.isDebugEnabled()){
//                        LOG.debug("EZADMIN LIST={}  渲染表头按钮开始 {}",encodeListId , JSONUtils.toJSONString(item));
//                    }
//                    Map<String, String> plugin = listService.getDbTemplateByCode(item.get(JsoupUtil.PLUGIN),ezListDTO.getTemplateId(),"list");
//                    Context context = new Context();
//                    for (Map.Entry<String, String> entry:item.entrySet()) {
//                        context.setVariable(entry.getKey(),entry.getValue());
//                    }
//                    String template = Utils.trimNull(plugin.get("PLUGIN_BODY"));
//                    String html = ThymeleafUtils.processString(template, context);
//                    item.put("html",html);
//                } catch (Exception e) {
//                        LOG.error("EZADMIN LIST={}  渲染表头按钮开始 {}",encodeListId , JSONUtils.toJSONString(item));
//                }
//            } ;
//        }
//    }
//
//    /**
//     * 默认值
//
//     * @return
//     */
//
//    private String calulateData(String dataInDb, String globalEmptyShow, String columnEmptyShow,String jdbcType) {
//        if(StringUtils.isBlank(dataInDb)){
//            return Utils.trimNullDefault(dataInDb,columnEmptyShow,globalEmptyShow);
//        }
//        switch (JdbcTypeEnum.get(jdbcType)){
//            case NUMBER:
//            case NUMBER2:
//                BigDecimal result=   new BigDecimal(dataInDb).setScale(2,BigDecimal.ROUND_HALF_UP);
//                return result.toString();
//            case NUMBER1:
//                return  new BigDecimal(dataInDb).setScale(1,BigDecimal.ROUND_HALF_UP).toString();
//            case NUMBER3:
//                return  new BigDecimal(dataInDb).setScale(3,BigDecimal.ROUND_HALF_UP).toString();
//            case NUMBER4:
//                return  new BigDecimal(dataInDb).setScale(4,BigDecimal.ROUND_HALF_UP).toString();
//            case DATE:
//                return  EzDateUtils.toDateFormat(dataInDb);
//            case DATETIME:
//                return  EzDateUtils.toDateTimeFormat(dataInDb);
//            default:
//                return dataInDb;
//        }
//    }
//
//    @Override
//    public List<List<Object>> renderExcel() {
//        return Collections.emptyList();
//    }
//
//
//}
