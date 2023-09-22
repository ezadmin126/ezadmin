package com.ezadmin.biz.dao;

import com.ezadmin.EzBootstrap;
import com.ezadmin.common.enums.ColTypeEnum;
import com.ezadmin.common.enums.JdbcTypeEnum;
import com.ezadmin.common.enums.OperatorEnum;
import com.ezadmin.common.utils.*;
import com.ezadmin.web.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ListDao extends  JsoupUtil{
    public static final Logger log = LoggerFactory.getLogger(ListDao.class);

    private static ListDao dao = new ListDao();
    private static Map<String, Config> listConfigMap=new HashMap();
    private static String DEFAULT_TD = "td-text";
    private static String DEFAULT_TH = "th";
    private static String DEFAULT_SEARCH = "input-text";
    private static  String [] colNames=new String[]{ JsoupUtil.ITEM_NAME,
            JsoupUtil.URL, JsoupUtil.HEAD_PLUGIN_CODE,JsoupUtil.LABEL,
            JsoupUtil.BODY_PLUGIN_CODE, JsoupUtil.ORDER,
            JsoupUtil.DATATYPE,JsoupUtil.DATA
            ,JsoupUtil.AREA, JsoupUtil.OPENTYPE,JsoupUtil.STYLE,
            JsoupUtil.WINDOW_NAME
            ,JsoupUtil.JDBCTYPE,JsoupUtil.EMPTY_SHOW,
            JsoupUtil.DATASOURCE,JsoupUtil.EDIT_FLAG,
            JsoupUtil.EDIT_EXPRESS,JsoupUtil.EDIT_PLUGIN,
            JsoupUtil.WIDTH,JsoupUtil.MIN_WIDTH,MIN_HEIGHT
    };
    private static   String [] names=new String[]{ JsoupUtil.VALIDATERULES,JsoupUtil.VALIDATEMESSAGES,JsoupUtil.DATATYPE,
            JsoupUtil.OPENTYPE,JsoupUtil.WINDOW_NAME,JsoupUtil.URL,
            JsoupUtil.DATA,JsoupUtil.JDBCTYPE,JsoupUtil.PLACEHOLDER,JsoupUtil.LAYVERIFY
            , JsoupUtil.NAME,
            JsoupUtil.ITEM_NAME,
            JsoupUtil.ITEM_ID,
            JsoupUtil.OPER
            ,JsoupUtil.STYLE
            ,JsoupUtil.MULTI,JsoupUtil.COLLAPSETAGS,JsoupUtil.SHOWALLLEVELS
            ,JsoupUtil.TOP_DESC,JsoupUtil.ITEM_DESC,JsoupUtil.RIGHT_DESC
            ,JsoupUtil.ALIAS,JsoupUtil.ALIGN,JsoupUtil.HELP,JsoupUtil.TYPE,JsoupUtil.COL,JsoupUtil.PLUGIN
    };
    private ListDao() {

    }

    public static ListDao getInstance() {
        return dao;
    }

    public void init()   {
        loadAllLists( );
    }


    public   Map<String, String> selectListById(String encodeListId) {

        if (StringUtils.isBlank(encodeListId)||!listConfigMap.containsKey(encodeListId.toLowerCase())) {

            log.warn("找不到列表id={}",encodeListId);
            return Collections.emptyMap();
        }
        Config config=listConfigMap.get(encodeListId.toLowerCase());

        Document doc = config.getDoc();
        Map<String, String> listData = new HashMap<>();
        listData.put("LIST_NAME", JsoupUtil.strip(doc.title()));
        listData.put("ENCRYPT_LIST_ID", JsoupUtil.strip(doc.body().attr("id")));
        listData.put(JsoupUtil.APPEND_HEAD, doc.getElementById(JsoupUtil.APPEND_HEAD) == null ? "" : doc.getElementById(JsoupUtil.APPEND_HEAD).html());
        listData.put(JsoupUtil.APPEND_FOOT, doc.getElementById(JsoupUtil.APPEND_FOOT) == null ? "" : doc.getElementById(JsoupUtil.APPEND_FOOT).html());

        listData.putAll(JsoupUtil.loadAttrNoChild(doc.body() ));

        listData.put("FIX_NUMBER", JsoupUtil.strip(doc.body().attr("fixedNumber")));
        listData.put("FIX_NUMBER_RIGHT", JsoupUtil.strip(doc.body().attr("fixedNumberRight")));
        listData.put("TABLE_SEARCH_FLAG", JsoupUtil.strip(doc.body().attr("tableSearchFlag")));
        listData.put("TREEGRID", Utils.trimEmptyDefault(JsoupUtil.strip(doc.body().attr("treegrid")),"0"));
        listData.put(JsoupUtil.TABLESTYLE, Utils.trimEmptyDefault(JsoupUtil.strip(doc.body().attr(JsoupUtil.TABLESTYLE)),"layui"));


        listData.put("FIRST_COL", JsoupUtil.strip("firstcol-" + doc.body().attr("firstCol")));

        Element express = doc.getElementById("express");

        if (express != null) {
            listData.put("SELECT_EXPRESS", express.text());
            listData.putAll(JsoupUtil.loadAttrNoChild(express ));
        }

        Element count = doc.getElementById("count");

        if (count != null) {
            listData.put("COUNT_EXPRESS", count.text());
        }
        //列表数字排序加的
        Elements expressExt = doc.select(".expressExt");

        if (expressExt != null&&expressExt.size()>0) {
            for (int i = 0; i < expressExt.size(); i++) {
                Element c=expressExt.get(i);
                listData.put(c.attr(JsoupUtil.ITEM_NAME), c.text());
            }
        }
        //计算是否包含图片
        Element column = doc.getElementById("column");
        boolean haspic=false;
        if (column != null) {
            Elements thList=column.getElementsByTag("th");
            for (int x = 0; x < thList.size(); x++) {
                Element th=thList.get(x);
                if (th.id().equalsIgnoreCase("rowbutton")) {
                    continue;
                }
               // Map<String, String> thMap = JsoupUtil.loadplugin(th );
                if(isImageTd(th)){
                    haspic=true;break;
                }
            }
        }





        Element rowbutton = doc.getElementById("rowbutton");
        if(rowbutton!=null) {
            String Json = Utils.trimEmptyDefault(rowbutton.attr(JsoupUtil.LAYDATA), "{\"field\":\"oper\", \"minWidth\":\"230\", \"fixed\":\"right\"}");
            listData.put(JsoupUtil.LAYDATA, Json);
            //由于layui没有提供height操作，需要对height做一层处理
            try {
                String userStyle= rowbutton.attr("style");
                Map<String, String> json = JSONUtils.parseMap(Json);
                if(haspic){
                    if(StringUtils.isBlank(userStyle) ){
                        userStyle="height: 110px;white-space: normal";
                    }else{
                        userStyle+=";height: 110px;white-space: normal";
                    }
                }else{

                    userStyle+=";white-space: normal";
                }
                json.put("style",userStyle);
                listData.put(JsoupUtil.LAYDATA, JSONUtils.toJSONString(json));
            } catch (Exception e) {
                log.error("", e);
            }
        }
        //首列宽度


        return listData;

        //外层已经有了缓存，此处可以 强制刷新
        //  return LIST.get(encodeListId);
    }


    public   List<Map<String, String>> selectSearchByListId(String encodeListId) {

        if (StringUtils.isBlank(encodeListId)||!listConfigMap.containsKey(encodeListId.toLowerCase())) {
            return Collections.emptyList();
        }
        Config config=listConfigMap.get(encodeListId.toLowerCase());

        Document doc = config.getDoc();
        Element search = doc.getElementById("search");
        if (search == null || search.children().size() == 0) {
            return Collections.emptyList();
        }
        List<Map<String, String>> list = new ArrayList<>();
        List<Map<String, String>> listHidden = new ArrayList<>();
        List<Element> searchList=search.getElementsByClass("list-search-item");
        if(Utils.isNotEmpty(searchList)){
            for (int x = 0; x < searchList.size(); x++) {
                Element item=searchList.get(x);
                Map<String, String> listitem = JsoupUtil.loadplugin(item);
                String [] names=new String[]{ JsoupUtil.NAME, JsoupUtil.ALIAS,JsoupUtil.ITEM_NAME,
                        JsoupUtil.OPER,JsoupUtil.JDBCTYPE,JsoupUtil.DATA,JsoupUtil.DATATYPE,
                        JsoupUtil.VALIDATERULES,JsoupUtil.VALIDATEMESSAGES,JsoupUtil.STYLE,JsoupUtil.PLACEHOLDER
                        ,JsoupUtil.MULTI,JsoupUtil.COLLAPSETAGS,JsoupUtil.SHOWALLLEVELS
                };
                for (int i = 0; i < names.length; i++) {
                    Utils.putIfAbsent(listitem,names[i], strip(item.attr(names[i])));
                }

                listitem.putAll(loadDataAttrNoChild(item));
                //special
                Utils.putIfAbsent(listitem,JsoupUtil.LABEL, strip(item.parent().parent().child(0).text()));
                Utils.putIfAbsent(listitem,"item_id", item.attr(JsoupUtil.NAME).replaceAll(",", "-"));
                listitem.put(JsoupUtil.ITEM_NAME, Utils.trimEmptyDefault(item.attr(JsoupUtil.NAME),item.attr(JsoupUtil.ITEM_NAME)));
                //如果是xmselect  默认为in
                if(listitem.get(JsoupUtil.PLUGIN).equalsIgnoreCase("xmselect")){
                    if(StringUtils.isBlank(listitem.get(JsoupUtil.OPER))){
                        listitem.put(JsoupUtil.OPER, OperatorEnum.IN.getOperC());
                    }
                }
                //如果区间，默认为between
                if(
                        listitem.get(JsoupUtil.PLUGIN).equalsIgnoreCase("daterange")||
                                listitem.get(JsoupUtil.PLUGIN).equalsIgnoreCase("datetimerange")||
                                listitem.get(JsoupUtil.PLUGIN).equalsIgnoreCase("numberrange")
                ){
                    if(StringUtils.isBlank(listitem.get(JsoupUtil.OPER))){
                        listitem.put(JsoupUtil.OPER, OperatorEnum.BETWEEN.getOperC());
                    }
                }
                if (StringUtils.equals(item.attr("type"), "hidden")) {
                    listHidden.add(listitem);
                } else {
                    list.add(listitem);
                }
            }
        }
        list.addAll(listHidden);
        return list;
    }
    public   List<Map<String, String>> selectNavByListId(String encodeListId) {

        if (StringUtils.isBlank(encodeListId)||!listConfigMap.containsKey(encodeListId.toLowerCase())) {
            return Collections.emptyList();
        }
        Config config=listConfigMap.get(encodeListId.toLowerCase());

        Document doc = config.getDoc();
        Element tab = doc.getElementById("tab");
        List<Map<String, String>> list = new ArrayList<>();
        if (tab == null || tab.children().size() == 0) {
            return list;
        }
        List<Element> alist=tab.getElementsByTag("a");
        for (int i = 0; i < alist.size(); i++) {
            Element item=alist.get(i);
            Map<String, String> listitem = JsoupUtil.loadAttrNoChild(item);
            Utils.putIfAbsent(listitem,JsoupUtil.LABEL, item.html());
            Utils.putIfAbsent(listitem,JsoupUtil.URL, item.attr(JsoupUtil.HREF));
            if (StringUtils.isBlank(item.attr(JsoupUtil.NAME))) {
                Utils.putIfAbsent(listitem,JsoupUtil.ITEM_NAME, item.text());
            }
            list.add(listitem);
        }
        return list;
    }
    public   List<Map<String, String>> selectRowButtonByListId(String encodeListId) {

        if (StringUtils.isBlank(encodeListId)||!listConfigMap.containsKey(encodeListId.toLowerCase())) {
            return Collections.emptyList();
        }
        Config config=listConfigMap.get(encodeListId.toLowerCase());

        Document doc = config.getDoc();
        Element column = doc.getElementById("column");

        List<Map<String, String>> rowbtnList = new ArrayList<>();
        if (column != null) {

            List<Element> thList=column.getElementsByTag("th");
            for (int i = 0; i < thList.size(); i++) {
                Element th=thList.get(i);
                if (th.id().equalsIgnoreCase("rowbutton")) {
                    Elements buttons=th.getElementsByTag("button");
                    if(buttons!=null){
                        for (int i1 = 0; i1 < buttons.size(); i1++) {
                            Element btn=buttons.get(i1);
                            Map<String, String> btnMap = JsoupUtil.loadplugin(btn );
                            initButtonMap(btnMap, btn);
                            rowbtnList.add(btnMap);
                        }
                    }
                }
            }
        }
        return rowbtnList;
    }
    public   List<Map<String, String>> selectTableButtonByListId(String encodeListId) {

        if (StringUtils.isBlank(encodeListId)||!listConfigMap.containsKey(encodeListId.toLowerCase())) {
            return Collections.emptyList();
        }
        Config config=listConfigMap.get(encodeListId.toLowerCase());
        Document doc = config.getDoc();
        List<Map<String, String>> tbbtnList = new ArrayList<>();
        Element tableButton=doc.getElementById("tableButton");
        if(tableButton!=null){
            Elements buttons=tableButton.getElementsByTag("button");
            if(buttons!=null){
                for (int i1 = 0; i1 < buttons.size(); i1++) {
                    Element btn=buttons.get(i1);
                    Map<String, String> btnMap = JsoupUtil.loadplugin(btn );
                    initButtonMap(btnMap, btn);
                    tbbtnList.add(btnMap);
                }
            }
        }
        return tbbtnList;
    }
    public   List<Map<String, String>> selectColumnByListId(String encodeListId) {

        if (StringUtils.isBlank(encodeListId)||!listConfigMap.containsKey(encodeListId.toLowerCase())) {
            return Collections.emptyList();
        }
        Config config=listConfigMap.get(encodeListId.toLowerCase());

        Document doc = config.getDoc();
        Element column = doc.getElementById("column");
        List<Map<String, String>> columnList = new ArrayList<>();
        if (column != null) {
            insertFirstCol(doc,columnList);
            Elements thList=column.getElementsByTag("th");
            for (int x = 0; x < thList.size(); x++) {
                Element th=thList.get(x);
                if (th.id().equalsIgnoreCase("rowbutton")) {
                   continue;
                }
                Map<String, String> thMap = JsoupUtil.loadplugin(th );

                for (int i = 0; i < colNames.length; i++) {
                    Utils.putIfAbsent(thMap,colNames[i], strip(th.attr(colNames[i])));
                }

                Utils.putIfAbsent(thMap,JsoupUtil.HEAD_PLUGIN_CODE, DEFAULT_TH);
                Utils.putIfAbsent(thMap,JsoupUtil.BODY_PLUGIN_CODE, DEFAULT_TD);

                thMap.put(JsoupUtil.LABEL, th.html());


                //laydata属性
                String laydata=th.attr(JsoupUtil.LAYDATA);
                Map<String,Object> laydataMap=JSONUtils.parseObjectMap(laydata);
                if(laydataMap==null){
                    laydataMap=new HashMap<>();
                }

                //最容易理解的是style，因此，用style来生成laydata
                styleTomap(laydataMap,th);

                defaultMap(laydataMap,th);
                thMap.put(JsoupUtil.LAYDATA,JSONUtils.toJSONString(laydataMap));
                columnList.add(thMap);
            }
        }
        return columnList;
    }

    private void insertFirstColObj(Document doc, List<Map<String, Object>> columnList) {
        Map<String,Object> laydataMap=new HashMap<>();
        String firstOld=doc.body().attr("firstCol");
        if(StringUtils.isNotJsBlank(firstOld)){
            firstOld=firstOld.replace("th-","");
        }else{
            return;
        }
        Map<String, Object> thMap = new HashMap<>();
        thMap.put(JsoupUtil.ITEM_NAME,"firstCol");
        if(ColTypeEnum.isCheckbox(firstOld)
        ){
            laydataMap.put("title","<input type=\"checkbox\" class=\"list-head-checkbox\" lay-filter=\"list-head-checkbox\" >");

            laydataMap.put("field","firstcol");
            thMap.put(JsoupUtil.BODY_PLUGIN_CODE,  ColTypeEnum.checkbox.bodycode());
            thMap.put(JsoupUtil.HEAD_PLUGIN_CODE,ColTypeEnum.checkbox.code());
        }
        if(ColTypeEnum.isRadio(firstOld) ){
            laydataMap.put("title","序号");
            laydataMap.put("field","firstcol");

            thMap.put(JsoupUtil.BODY_PLUGIN_CODE,  ColTypeEnum.radio.bodycode());
            thMap.put(JsoupUtil.HEAD_PLUGIN_CODE,ColTypeEnum.radio.code());

        }
        if(ColTypeEnum.isNumbers(firstOld)){
            laydataMap.put("title","序号");
            laydataMap.put("field","firstcol");

            thMap.put(JsoupUtil.BODY_PLUGIN_CODE,  ColTypeEnum.numbers.bodycode());
            thMap.put(JsoupUtil.HEAD_PLUGIN_CODE,ColTypeEnum.numbers.code());
        }
        //计算是否包含图片
        Element column = doc.getElementById("column");
        boolean haspic=false;
        if (column != null) {
            Elements thList=column.getElementsByTag("th");
            for (int x = 0; x < thList.size(); x++) {
                Element th=thList.get(x);
                if (th.id().equalsIgnoreCase("rowbutton")) {
                    continue;
                }
                // Map<String, String> thMap = JsoupUtil.loadplugin(th );
                if(isImageTd(th)){
                    haspic=true;break;
                }
            }
        }
        if(ColTypeEnum.isFirst(firstOld)){
//            laydataMap.put("fixed","left");
//            if(haspic){
//                Utils.putIfAbsent(laydataMap,"height","110");
//            }
            thMap.put(JsoupUtil.LAYDATA,JSONUtils.toJSONString(laydataMap));
            columnList.add(thMap);
        }

    }
    private void insertFirstCol(Document doc, List<Map<String, String>> columnList) {
        Map<String,Object> laydataMap=new HashMap<>();
        String firstOld=doc.body().attr("firstCol");
        if(StringUtils.isNotJsBlank(firstOld)){
            firstOld=firstOld.replace("th-","");
        }else{
            return;
        }
        Map<String, String> thMap = new HashMap<>();
        thMap.put(JsoupUtil.ITEM_NAME,"firstCol");
        if(ColTypeEnum.isCheckbox(firstOld)
        ){
            laydataMap.put("title","<input type=\"checkbox\" class=\"list-head-checkbox\" lay-filter=\"list-head-checkbox\" >");

            laydataMap.put("field","firstcol");
            thMap.put(JsoupUtil.BODY_PLUGIN_CODE,  ColTypeEnum.checkbox.bodycode());
            thMap.put(JsoupUtil.HEAD_PLUGIN_CODE,ColTypeEnum.checkbox.code());
        }
        if(ColTypeEnum.isRadio(firstOld) ){
            laydataMap.put("title","序号");
            laydataMap.put("field","firstcol");

            thMap.put(JsoupUtil.BODY_PLUGIN_CODE,  ColTypeEnum.radio.bodycode());
            thMap.put(JsoupUtil.HEAD_PLUGIN_CODE,ColTypeEnum.radio.code());

        }
        if(ColTypeEnum.isNumbers(firstOld)){
            laydataMap.put("title","序号");
            laydataMap.put("field","firstcol");

            thMap.put(JsoupUtil.BODY_PLUGIN_CODE,  ColTypeEnum.numbers.bodycode());
            thMap.put(JsoupUtil.HEAD_PLUGIN_CODE,ColTypeEnum.numbers.code());
        }
        if(ColTypeEnum.isFirst(firstOld)){
            thMap.put(JsoupUtil.LAYDATA,JSONUtils.toJSONString(laydataMap));
            columnList.add(thMap);
        }

    }

    private boolean isImageTd(Element th){
        String plugin=Utils.trimNull(th.attr(JsoupUtil.BODY_PLUGIN_CODE));
        return  "td-pic".equals(plugin)||
                "td-image".equals(plugin);
    }

    /**
     * 靠左靠右 排序 宽度
     * @param laydataMap
     * @param th
     */
    private void defaultMap(Map<String, Object> laydataMap, Element th) {
        if(isImageTd(th)){
            Utils.putIfAbsent(laydataMap,"minHeight","110");
            Utils.putIfAbsent(laydataMap,"minWidth","200");
            //兼容高度
            if(!StringUtils.contains(Utils.trimNull(laydataMap.get(JsoupUtil.STYLE)),"min-height")){
                laydataMap.put(JsoupUtil.STYLE,Utils.trimNull(laydataMap.get(JsoupUtil.STYLE))+"min-height:110px");
            }
        }
        //如果是数字默认靠右
        if(JdbcTypeEnum.isNumberType(th.attr(JsoupUtil.JDBCTYPE))){
            Utils.putIfAbsent(laydataMap,"align","right");
        }

        if(StringUtils.equals(th.attr("order"),"1")){
            Utils.putIfAbsent(laydataMap,"sort",true);
        }


        //默认宽度设置为110
         //   Utils.putIfAbsent(laydataMap,"width","110");
    }

    private void styleTomap(Map<String, Object> layDataMap,Element th) {
        try {
            Utils.putIfAbsent(layDataMap,"field", th.attr(JsoupUtil.ITEM_NAME));
            Utils.putIfAbsent(layDataMap,"title", Utils.trimNull(th.html()));
            layDataMap.put("escape", false);
            //fixed
            Utils.putIfAbsent(layDataMap,"fixed", Utils.trimNull(th.attr("fixed")));
            //属性的权重比style高
            Utils.putIfAbsent(layDataMap,"minWidth", Utils.trimNull(th.attr("minwidth")));
            Utils.putIfAbsent(layDataMap,"minHeight", Utils.trimNull(th.attr("minheight")));
            Utils.putIfAbsent(layDataMap,"width", Utils.trimNull(th.attr("width")));
            Utils.putIfAbsent(layDataMap,"align", Utils.trimNull(th.attr("align")));

            String style = th.attr(JsoupUtil.STYLE);
            //style会补充到 laydata里面
            if (StringUtils.isNotBlank(style)) {
                Utils.putIfAbsent(layDataMap,JsoupUtil.STYLE, style);
                String kvs[] = StringUtils.split(style, ";");
                if(kvs!=null){
                    for (int i = 0; i < kvs.length; i++) {
                        String kv[]=kvs[i].split(":");
                        if(kv!=null&&kv.length==2){
                            for (int j = 0; j <kv.length; j++) {
                                    String k=StringUtils.lowerCase(Utils.trimNull(kv[0]));
                                    String v=StringUtils.lowerCase(Utils.trimNull(kv[1]));
                                    switch (k){
                                        case "width":
                                            Utils.putIfAbsent(layDataMap,"width",v.replace("px",""));
                                            break;
                                        case "min-width":
                                            Utils.putIfAbsent(layDataMap,"minWidth",v.replace("px",""));
                                            break;
                                        case "max-width":
                                            Utils.putIfAbsent(layDataMap,"maxWidth",
                                                    Utils.trimNull(v.replace("px","").replace("!important","")));
                                            break;
                                        case "text-align":
                                            Utils.putIfAbsent(layDataMap,"align",v);
                                            break;
                                        case "colspan":
                                            Utils.putIfAbsent(layDataMap,"colspan",v);
                                            break;
                                        case "rowspan":
                                            Utils.putIfAbsent(layDataMap,"rowspan",v);
                                            break;
                                        default:;
                                    }
                            }
                        }
                    }
                }
            }

        }catch (Exception e){
            log.error("",e);
        }
    }


    public   boolean existHtmlList(String encodeListId) {
        if (StringUtils.isBlank(encodeListId) || encodeListId.equals("T04NiWt5IAI")) {
            return true;
        }
        return listConfigMap.containsKey(encodeListId.toLowerCase());
    }

    public   List<Map<String, String>> selectListByHtmlConfig(String page, String name, String url) {
        List<Map<String, String>> list = new ArrayList<>();
        for (Map.Entry<String, Config> entry:listConfigMap.entrySet()){
            Map<String,String> item=selectListById(entry.getKey());
            item.put("E_LIST_ID",item.get("ENCRYPT_LIST_ID"));
            if (StringUtils.isNotBlank(name) && StringUtils.isBlank(url)) {
                if (item.get("LIST_NAME").toLowerCase().indexOf(name.toLowerCase()) >= 0) {
                    list.add(item);
                }
            } else if (StringUtils.isBlank(name) && StringUtils.isNotBlank(url)) {
                if (item.get("ENCRYPT_LIST_ID").toLowerCase().indexOf(url.toLowerCase()) >= 0) {
                    list.add(item);
                }
            } else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(url)) {
                if (item.get("LIST_NAME").toLowerCase().indexOf(name.toLowerCase()) >= 0 && item.get("ENCRYPT_LIST_ID").toLowerCase().indexOf(url.toLowerCase()) >= 0
                ) {
                    list.add(item);
                }
            } else {
                list.add(item);
            }
        }
        Page page1 = new Page();
        page1.setCurrentPage(NumberUtils.toInt(page));
        return list.subList(page1.getStartRecord(), Math.min(list.size(), page1.getEndRecord()));
    }
    public   int listSize(String page, String name, String url) {
        final AtomicInteger i = new AtomicInteger(0);
        for (Map.Entry<String, Config> entry:listConfigMap.entrySet()){
            String k=entry.getKey();
            Map<String,String> item=selectListById(k);
            if (StringUtils.isNotBlank(name) && StringUtils.isBlank(url)) {
                if (item.get("LIST_NAME").toLowerCase().indexOf(name.toLowerCase()) >= 0) {
                    i.getAndIncrement();
                }
            } else if (StringUtils.isBlank(name) && StringUtils.isNotBlank(url)) {
                if (item.get("ENCRYPT_LIST_ID").toLowerCase().indexOf(url.toLowerCase()) >= 0) {
                    i.getAndIncrement();
                }
            } else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(url)) {
                if (item.get("LIST_NAME").toLowerCase().indexOf(name.toLowerCase()) >= 0 && item.get("ENCRYPT_LIST_ID").toLowerCase().indexOf(url.toLowerCase()) >= 0
                ) {
                    i.getAndIncrement();
                }
            } else {
                i.getAndIncrement();
            }
        } ;
        return i.get();
    }


    //
    public   Integer updateSearchByListId(String encodeListId, String name, Map<String, String> request) throws IOException {

        return 1;
    }
    private static void dealSearchElementByForm(Element element, Map<String, String> request) {
        String [] names=new String[]{JsoupUtil.NAME,JsoupUtil.ITEM_NAME,  JsoupUtil.ALIAS,
                JsoupUtil.OPER,JsoupUtil.JDBCTYPE,JsoupUtil.DATA,JsoupUtil.DATATYPE,
                JsoupUtil.VALIDATERULES,JsoupUtil.VALIDATEMESSAGES,JsoupUtil.STYLE,JsoupUtil.PLACEHOLDER
        };
        for (int i = 0; i < names.length; i++) {
            attr(element, names[i], Utils.trimNull(request.get(names[i])));
        }
    }



    public   Integer deleteSearchByListId(String encodeListId, String name) throws IOException {

        return 1;
    }

    public   void updateListSearchSort(String encodeListId, String addSearch) {

    }
    public   void updateListColumnSort(String encodeListId, String addSearch) {

    }
    public Integer updateColumnByListId(String encodeListId, String name, Map<String, String> request) throws IOException {

        return 1;
    }

    public   Integer deleteColumnByListId(String encodeListId, String name) throws IOException {



        return 1;
    }


    public   Integer updateCoreByListId(String encodeListId, String name, Map<String, String> request) throws IOException {

        return 1;
    }


    public   Integer updateNavByListId(String encodeListId, String name, Map<String, String> request) throws IOException {

        return 1;
    }

    public   void updateListNavSort(String encodeListId, String addSearch) {


    }



    public   Integer deleteNavByListId(String encodeListId, String name) throws IOException {


        Config config = listConfigMap.get(encodeListId.toLowerCase());
        Document doc=config.getDoc();

        Element nav = doc.body().getElementById("tab");


        Element element = StringUtils.isBlank(name) ? null : nav.selectFirst("["+JsoupUtil.ITEM_NAME+"='" + name + "']");

        if (element != null) {
            element.remove();
           updateConfig(config);
        }
        return 1;
    }


    public   void updateListTableButtonSort(String encodeListId, String addSearch) {
        // TODO Auto-generated method stub
        try {
//            List<Map<String, String>> list = LIST_SEARCH.get(encodeListId);
            if (StringUtils.isBlank(addSearch)) {
                return;
            }
            Config config = listConfigMap.get(encodeListId.toLowerCase());
            Document doc=config.getDoc();

            Elements items = doc.body().getElementById("tableButton").children();

            List<Element> searchList = new ArrayList<>();
            String[] names = addSearch.split(",");

            for (String name : names) {
                for (int i = 0; i < items.size(); i++) {
                    if (name.equalsIgnoreCase(items.get(i).attr(JsoupUtil.ITEM_NAME)) || name.equalsIgnoreCase(items.get(i).text())) {
                        searchList.add(items.get(i));
                    }
                }
            }
            //doc.body().getElementById("tableButton").html("");
            doc.body().getElementById("tableButton").children().remove();
            for (int i = 0; i < searchList.size(); i++) {
                doc.body().getElementById("tableButton").append(searchList.get(i).outerHtml());
            }
            updateConfig(config);
        } catch (Exception e) {
            Utils.addLog("", e);
        }
    }



    //通过name查找
    public   Integer updateTableButtonByListId(String encodeListId, String name, Map<String, String> request) throws IOException {
        String pre=request.get("pre");
        Config config = listConfigMap.get(encodeListId.toLowerCase());
        Document doc=config.getDoc();

        Element body = doc.body();

        Element express = body.getElementById("tableButton");
        if(express==null){
            return 0;
        }
        List<String> itemSortList = new ArrayList<>();
        boolean has = false;
        for (int i = 0; i < express.children().size(); i++) {
            Element item = express.children().get(i);
            String itemname = item.attr(JsoupUtil.ITEM_NAME);
            if (StringUtils.isBlank(itemname)) {
                continue;
            }
            if (StringUtils.equalsIgnoreCase(itemname, name)) {
                initButtonByMap(request,item);
                has = true;
            }
            itemSortList.add(itemname);
            if (StringUtils.equalsIgnoreCase(itemname, pre)) {
                itemSortList.add(request.get(JsoupUtil.ITEM_NAME));
            }
        }
        if (!has) {
            Element item = doc.createElement("button");
            initButtonByMap(request,item);
            express.append(item.outerHtml());
        }
        updateConfig(config);
        return 1;
    }

    //通过name查找
    public   Integer deleteTableButtonByListId(String encodeListId, String name) throws IOException {

        Config config = listConfigMap.get(encodeListId.toLowerCase());
        Document doc=config.getDoc();
        Element nav = doc.body().getElementById("tableButton");


        Element element = StringUtils.isBlank(name) ? null : nav.selectFirst("["+JsoupUtil.ITEM_NAME+"=" + name + "]");

        if (element != null) {
            element.remove();

         updateConfig(config);
        }


        return 1;
    }


    public static void updateListRowButtonSort(String encodeListId, String rowButtonNames) {
        try {
            if (StringUtils.isBlank(rowButtonNames)) {
                return;
            }
            Config config = listConfigMap.get(encodeListId.toLowerCase());
            Document doc=config.getDoc();
            Element rowbutton = doc.body().getElementById("rowbutton");
            Elements items = rowbutton.children();

            List<Element> searchList = new ArrayList<>();
            String[] names = rowButtonNames.split(",");
            for (String name : names) {
                for (int i = 0; i < items.size(); i++) {
                    if (name.equalsIgnoreCase(items.get(i).attr(JsoupUtil.ITEM_NAME))) {
                        searchList.add(items.get(i));
                    }
                }
            }
            rowbutton.html("");
            for (int i = 0; i < searchList.size(); i++) {
                rowbutton.append(searchList.get(i).outerHtml());
            }
           updateConfig(config);
        } catch (Exception e) {
            Utils.addLog("", e);
        }
    }



    //通过name查找
    public   Integer updateRowButtonByListId(String encodeListId, String name, Map<String, String> request) throws IOException {
        String pre = request.get("pre");
        Config config = listConfigMap.get(encodeListId.toLowerCase());
        Document doc=config.getDoc();
        Element body = doc.body();
        Element express = body.getElementById("rowbutton");
        if(express==null){
            return 0;
        }
        List<String> itemSortList = new ArrayList<>();
        boolean has = false;
        for (int i = 0; i < express.children().size(); i++) {
            Element item = express.children().get(i);
            String itemname = item.attr(JsoupUtil.ITEM_NAME);
            if (StringUtils.isBlank(itemname)) {
                continue;
            }
            if (StringUtils.equalsIgnoreCase(itemname, name)) {
                initButtonByMap(request,item);
                has = true;
            }
            itemSortList.add(itemname);
            if (StringUtils.equalsIgnoreCase(itemname, pre)) {
                itemSortList.add(request.get(JsoupUtil.ITEM_NAME));
            }
        }
        if (!has) {
            Element item = doc.createElement("button");
            initButtonByMap(request,item);
            express.append("\n"+item.outerHtml());
        }
        updateConfig(config);
        return 1;
    }
    public   Integer deleteRowButtonByListId(String encodeListId, String name) throws IOException {

        Config config = listConfigMap.get(encodeListId.toLowerCase());
        Document doc=config.getDoc();

        Element nav = doc.body().getElementById("rowbutton");


        Element element = StringUtils.isBlank(name) ? null : nav.selectFirst("["+JsoupUtil.ITEM_NAME+"='" + name + "']");

        if (element != null) {
            element.remove();
           updateConfig(config);
        }
        return 1;
    }
   public  void loadListFile(File file) throws  Exception {
        Config item=new Config();
        Document doc = Jsoup.parse(file, "UTF-8", "");
        item.setUrl(file.toURI().toURL());
        item.setPath(file.toURI().toURL().getPath());
        item.setProtocol("file");
        item.setDoc(doc);
        item.setFile(file);
        listConfigMap.put(doc.body().id().toLowerCase(),item);
     }
    private   void loadAllLists()   {
        log.info("开始加载所有list size={}",EzBootstrap.instance().getListConfigResources().size());
        for (int i = 0; i < EzBootstrap.instance().getListConfigResources().size(); i++) {

            Config item=EzBootstrap.instance().getListConfigResources().get(i);

            if(item==null ){
                log.error("文件异常{}",item);
                continue;
            }
            log.info("loadAllList:{}",item.getFile());
            try {
                InputStream stream = null;
                if(item.isJar() ){
                    stream=item.getIn();
                    Document doc = Jsoup.parse(stream, "UTF-8", "");
                    item.setDoc(doc);
                    listConfigMap.put(doc.body().id().toLowerCase(),item);
                    //jar包中的流确保只用一次，初始化之后就关闭流
                    stream.close();
                }else{
                    loadListFile(item.getFile());
                }
            }catch (Exception e){
                log.error("loadAllList:{}",item.getFile(),e);
                //jar   包中的 无需重新加载
                throw new RuntimeException(e);
            }
        }
    }


    private static void initButtonMap(Map<String, String> btnMap, Element btn) {
        String label = Utils.trimEmptyDefault(JsoupUtil.strip(btn.text()),btn.attr(JsoupUtil.ITEM_NAME));
        Utils.putIfAbsent(btnMap,JsoupUtil.LABEL, label);
        String [] names=new String[]{ JsoupUtil.NAME, JsoupUtil.WINDOW_NAME,JsoupUtil.ITEM_NAME
                ,JsoupUtil.AREA,JsoupUtil.URL,JsoupUtil.OPENTYPE,JsoupUtil.CLASS,JsoupUtil.STYLE,
                JsoupUtil.EZ_CALLBACK
        };
        for (int i = 0; i < names.length; i++) {
            Utils.putIfAbsent(btnMap,names[i], strip(btn.attr(names[i])));
        }
    }
    private static void initButtonMapObj(Map<String, Object> btnMap, Element btn) {
        String label = Utils.trimEmptyDefault(JsoupUtil.strip(btn.text()),btn.attr(JsoupUtil.ITEM_NAME));
        Utils.putIfAbsent(btnMap,JsoupUtil.LABEL, label);
        String [] names=new String[]{ JsoupUtil.NAME, JsoupUtil.WINDOW_NAME,JsoupUtil.ITEM_NAME
                ,JsoupUtil.AREA,JsoupUtil.URL,JsoupUtil.OPENTYPE,JsoupUtil.CLASS,JsoupUtil.STYLE,
                JsoupUtil.EZ_CALLBACK,JsoupUtil.ITEM_ID
        };
        for (int i = 0; i < names.length; i++) {
            Utils.putIfAbsent(btnMap,names[i], strip(btn.attr(names[i])));
        }
        Utils.putIfAbsent(btnMap,JsoupUtil.LABEL, btn.html());
    }
    private static void initButtonByMap(Map<String, String> request, Element item) {
        item.html(StringUtils.isNotBlank(request.get(JsoupUtil.LABEL))?request.get(JsoupUtil.LABEL):request.get(JsoupUtil.ITEM_NAME));
        String [] names=new String[]{ JsoupUtil.NAME, JsoupUtil.WINDOW_NAME,JsoupUtil.ITEM_NAME
                ,JsoupUtil.AREA,JsoupUtil.URL,JsoupUtil.OPENTYPE,
                JsoupUtil.EZ_CALLBACK,JsoupUtil.STYLE,JsoupUtil.CLASS};
        for (int j = 0; j < names.length; j++) {
            item.attr(names[j], request.get(names[j]));
        }
        dealElementByPluginCode(request.get(JsoupUtil.PLUGIN), item);
    }
    public void clear() {
    }

    public void updateOrder(String code,List<String> search, List<String> col, String fixNumber, String fixNumberRight) throws IOException {
        if(StringUtils.isBlank(code)){
            return;
        }
        Config config=listConfigMap.get(code.toLowerCase());

        Document doc =config.getDoc();
        Element body = doc.body();
        attr(body,"fixedNumber", fixNumber);
        attr(body,"fixedNumberRight", fixNumberRight);


        Elements colItems = doc.body().getElementById("column").children();
        List<Element> collist = new ArrayList<>();

        for (String name : col) {
            for (int i = 0; i < colItems.size(); i++) {
                if (name.equalsIgnoreCase(colItems.get(i).attr(JsoupUtil.ITEM_NAME))) {
                    collist.add(colItems.get(i));
                }
            }
        }
        String buttons = "";
        try {
            buttons = doc.body().getElementById("rowbutton").outerHtml();
        } catch (Exception e) {
        }

        doc.body().getElementById("column").children().remove();
        for (int i = 0; i < collist.size(); i++) {
            doc.body().getElementById("column").append(collist.get(i).outerHtml());
        }
        //button
        doc.body().getElementById("column").append(buttons);

        //

        Elements searchItems = doc.body().getElementsByClass("list-search-item");

        List<Element> searchList = new ArrayList<>();

        for (String name : search) {
            for (int i = 0; i < searchItems.size(); i++) {
                if (name.equalsIgnoreCase(searchItems.get(i).attr(JsoupUtil.ITEM_NAME))) {
                    searchList.add(searchItems.get(i).parent().parent());
                }
            }
        }
        doc.body().getElementById("search").html("");
        for (int i = 0; i < searchList.size(); i++) {
            doc.body().getElementById("search").append(searchList.get(i).outerHtml());
        }
        JsoupUtil.updateConfig(config);
    }
    void fillcore(Map<String,Object> coreMap,Config config){
        Document doc=config.getDoc();
        coreMap.put("listname", JsoupUtil.strip(doc.title()));
        coreMap.put("ENCRYPT_LIST_ID", JsoupUtil.strip(doc.body().attr("id")));
        coreMap.put("listcode", JsoupUtil.strip(doc.body().attr("id")));
        coreMap.put(JsoupUtil.APPEND_HEAD, doc.getElementById(JsoupUtil.APPEND_HEAD) == null ? "" : doc.getElementById(JsoupUtil.APPEND_HEAD).html());
        coreMap.put(JsoupUtil.APPEND_FOOT, doc.getElementById(JsoupUtil.APPEND_FOOT) == null ? "" : doc.getElementById(JsoupUtil.APPEND_FOOT).html());

        coreMap.putAll(JsoupUtil.loadAttrNoChild(doc.body() ));

        Element express = doc.getElementById("express");

        if (express != null) {
            coreMap.put("select_express", express.text());
            coreMap.put("orderby_express", express.attr("orderby"));
            coreMap.put("groupby_express", express.attr("groupby"));
        }
        Element count = doc.getElementById("count");
        if (count != null) {
            coreMap.put("count_express", count.text());
        }
    }
    public   Map<String, Object> selectAllListById(String listcode) {
        Map<String, Object> result=new HashMap<>();
        Config config=listConfigMap.get(listcode.toLowerCase());
        Map<String,Object> coreMap=new HashMap<>();
        fillcore(coreMap,config);
        fillbuttoncol(coreMap,config);


        result.put("core",coreMap);
        List<Map<String,Object>> searchList=new ArrayList<>();
        fillsearch(searchList,config);
        List<Map<String,Object>> tabList=new ArrayList<>();
        filltab(tabList,config);
        List<Map<String,Object>> tablebtnList=new ArrayList<>();
        filltablebtn(tablebtnList,config);
        List<Map<String,Object>> colList=new ArrayList<>();
        fillcol(colList,config);
        List<Map<String,Object>> rowbtnList=new ArrayList<>();
        fillrowbtn(rowbtnList,config);



        result.put("search",searchList);
        result.put("tab",tabList);
        result.put("tablebtn",tablebtnList);
        result.put("col",colList);
        result.put("rowbtn",rowbtnList);


        return result;
    }

    private void fillbuttoncol(Map<String, Object> coreMap, Config config) {
        Element column = config.getDoc().getElementById("column");
        boolean haspic=false;
        if (column != null) {
            Elements thList=column.getElementsByTag("th");
            for (int x = 0; x < thList.size(); x++) {
                Element th=thList.get(x);
                if (th.id().equalsIgnoreCase("rowbutton")) {
                    continue;
                }
                // Map<String, String> thMap = JsoupUtil.loadplugin(th );
                if(isImageTd(th)){
                    haspic=true;break;
                }
            }
        }





        Element rowbutton = config.getDoc().getElementById("rowbutton");
        if(rowbutton!=null) {
            String Json = Utils.trimEmptyDefault(rowbutton.attr(JsoupUtil.LAYDATA), "{\"field\":\"oper\", \"minWidth\":\"230\", \"fixed\":\"right\"}");
            coreMap.put(JsoupUtil.LAYDATA, Json);
            //由于layui没有提供height操作，需要对height做一层处理
            try {
                String userStyle= rowbutton.attr("style");
                Map<String, String> json = JSONUtils.parseMap(Json);
                if(haspic){
                    if(StringUtils.isBlank(userStyle) ){
                        userStyle="height: 110px;white-space: normal";
                    }else{
                        userStyle+=";height: 110px;white-space: normal";
                    }
                }else{

                    userStyle+=";white-space: normal";
                }
                json.put("style",userStyle);
                coreMap.put(JsoupUtil.LAYDATA, JSONUtils.toJSONString(json));
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    void fillcol(List<Map<String, Object>> searchConfigList, Config config){
        Document doc = config.getDoc();
        Element column = doc.getElementById("column");
         if (column != null) {
             insertFirstColObj(doc,searchConfigList);
            Elements thList=column.getElementsByTag("th");
            for (int x = 0; x < thList.size(); x++) {
                Element th=thList.get(x);
                if (th.id().equalsIgnoreCase("rowbutton")) {
                    continue;
                }
                Map<String, Object> thMap =new HashMap<>();
                thMap.put(JsoupUtil.PLUGIN,JsoupUtil.getTypeByElement(th));

                for (int i = 0; i < colNames.length; i++) {
                    Utils.putIfAbsent(thMap,colNames[i], strip(th.attr(colNames[i])));
                }

                Utils.putIfAbsent(thMap,JsoupUtil.HEAD_PLUGIN_CODE, DEFAULT_TH);
                Utils.putIfAbsent(thMap,JsoupUtil.BODY_PLUGIN_CODE, DEFAULT_TD);


                thMap.put(JsoupUtil.LABEL, Utils.trimEmptyDefault(thMap.get(JsoupUtil.LABEL),th.html()) );
                thMap.put(JsoupUtil.ITEM_NAME, StringUtils.upperCase(th.attr(JsoupUtil.ITEM_NAME)));


                //laydata属性
                String laydata=th.attr(JsoupUtil.LAYDATA);
                Map<String,Object> laydataMap=JSONUtils.parseObjectMap(laydata);
                if(laydataMap==null){
                    laydataMap=new HashMap<>();
                }

                //最容易理解的是style，因此，用style来生成laydata
                styleTomap(laydataMap,th);

                defaultMap(laydataMap,th);

                String fixed=Utils.trimNull(laydataMap.get("fixed"));
                if(StringUtils.isNotBlank(fixed)){
                    thMap.put("fixed",fixed);
                }
                thMap.put(JsoupUtil.LAYDATA,JSONUtils.toJSONString(laydataMap));
                searchConfigList.add(thMap);
            }
        }
    }
    void fillrowbtn(List<Map<String, Object>> searchConfigList, Config config){
        Document doc = config.getDoc();
         Element column = doc.getElementById("rowbutton");

         if (column != null) {
             Elements buttons=column.getElementsByTag("button");

             for (int i1 = 0; i1 < buttons.size(); i1++) {
                Element btn=buttons.get(i1);
                Map<String, Object> btnMap = new HashMap<>();
                btnMap.put(JsoupUtil.PLUGIN,JsoupUtil.getTypeByElement(btn));
                //JsoupUtil.loadplugin(btn );
                initButtonMapObj(btnMap, btn);
                searchConfigList.add(btnMap);
            }
        }
    }
    void filltablebtn(List<Map<String, Object>> searchConfigList, Config config){
        Document doc = config.getDoc();
        Element tableButton=doc.getElementById("tableButton");
        if(tableButton!=null){
            Elements buttons=tableButton.getElementsByTag("button");
            if(buttons!=null){
                for (int i1 = 0; i1 < buttons.size(); i1++) {
                    Element btn=buttons.get(i1);
                    Map<String, Object> btnMap = new HashMap<>();

                    btnMap.put(JsoupUtil.PLUGIN,JsoupUtil.getTypeByElement(btn));

                    initButtonMapObj(btnMap, btn);
                    btnMap.putIfAbsent(JsoupUtil.TYPE,"button-table");
                    searchConfigList.add(btnMap);
                }
            }
        }
    }
    void filltab(List<Map<String, Object>> searchConfigList, Config config){
        Document doc = config.getDoc();
        Element tab = doc.getElementById("tab");
         if (tab == null || tab.children().size() == 0) {
            return  ;
        }
        List<Element> alist=tab.getElementsByTag("a");
        for (int i = 0; i < alist.size(); i++) {
            Element item=alist.get(i);
            Map<String, Object> listitem = new HashMap<>();
            Utils.putIfAbsent(listitem,JsoupUtil.LABEL, item.html());
            Utils.putIfAbsent(listitem,JsoupUtil.URL, Utils.trimNullDefault(item.attr(JsoupUtil.HREF),item.attr(JsoupUtil.URL)));
            Utils.putIfAbsent(listitem,JsoupUtil.ITEM_NAME, item.attr(JsoupUtil.ITEM_NAME));
            Utils.putIfAbsent(listitem,JsoupUtil.SELECT, item.attr(JsoupUtil.SELECT));
            searchConfigList.add(listitem);
        }
    }
    String [] search_attrs=new String[]{ JsoupUtil.NAME, JsoupUtil.ALIAS,JsoupUtil.ITEM_NAME,
            JsoupUtil.OPER,JsoupUtil.JDBCTYPE,JsoupUtil.DATA,JsoupUtil.DATATYPE,
            JsoupUtil.VALIDATERULES,JsoupUtil.VALIDATEMESSAGES,JsoupUtil.STYLE,JsoupUtil.PLACEHOLDER
            ,JsoupUtil.MULTI,JsoupUtil.COLLAPSETAGS,JsoupUtil.SHOWALLLEVELS,JsoupUtil.COL,JsoupUtil.LAYVERIFY
            ,JsoupUtil.TYPE,JsoupUtil.URL
    };
    private void fillsearch(List<Map<String, Object>> searchConfigList, Config config) {
        Document doc=config.getDoc();
        List<Element> searchList=doc.getElementsByClass("list-search-item");
        if(Utils.isNotEmpty(searchList)){
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> listHidden = new ArrayList<>();
            for (int x = 0; x < searchList.size(); x++) {
                Element item=searchList.get(x);
                 Map<String, Object> listitem = new HashMap<>();//JsoupUtil.loadplugin(item);

                for (int i = 0; i < search_attrs.length; i++) {
                    Utils.putIfAbsent(listitem,search_attrs[i], strip(item.attr(search_attrs[i])));
                }
                listitem.put(JsoupUtil.PLUGIN,JsoupUtil.getTypeByElement(item));

                listitem.putAll(loadDataAttrNoChild(item));
                //special
                Utils.putIfAbsent(listitem,JsoupUtil.LABEL, strip(item.parent().parent().child(0).text()));
                Utils.putIfAbsent(listitem,"item_id", item.attr(JsoupUtil.NAME).replaceAll(",", "-"));
                listitem.put(JsoupUtil.ITEM_NAME, StringUtils.upperCase(Utils.trimEmptyDefault(item.attr(JsoupUtil.ITEM_NAME),item.attr(JsoupUtil.NAME))));
                //如果是xmselect  默认为in
                if(listitem.get(JsoupUtil.PLUGIN).toString() .equalsIgnoreCase("xmselect")){
                    if(StringUtils.isBlank(Utils.trimNull(listitem.get(JsoupUtil.OPER)))){
                        listitem.put(JsoupUtil.OPER, OperatorEnum.IN.getOperC());
                    }
                }
                //如果区间，默认为between
                if(
                        listitem.get(JsoupUtil.PLUGIN).toString().equalsIgnoreCase("daterange")||
                                listitem.get(JsoupUtil.PLUGIN).toString().equalsIgnoreCase("datetimerange")||
                                listitem.get(JsoupUtil.PLUGIN).toString().equalsIgnoreCase("numberrange")
                ){
                    if(StringUtils.isBlank(Utils.trimNull(listitem.get(JsoupUtil.OPER)))){
                        listitem.put(JsoupUtil.OPER, OperatorEnum.BETWEEN.getOperC());
                    }
                }
                if (StringUtils.equals(item.attr("type"), "hidden")) {
                    listHidden.add(listitem);
                } else {
                    list.add(listitem);
                }
            }
            searchConfigList.addAll(list);
            searchConfigList.addAll(listHidden);
            //list.addAll(listHidden);
        }
    }

    static String[] attr=new String[]{"datasource",
    "fixnumber","fixnumberright",
    "empty_show","tablestyle",
    "firstcol"
    };
    public void updateList(Map<String, Object> list) throws  Exception {

        Map<String,Object> coreMap=(Map<String,Object>)list.get("core");
        List<Map<String,Object>> searchList=(List<Map<String,Object>>)list.get("search");
        List<Map<String,Object>> tabList=(List<Map<String,Object>>)list.get("tab");
        List<Map<String,Object>> tablebtnList=(List<Map<String,Object>>)list.get("tablebtn");
        List<Map<String,Object>> colList=(List<Map<String,Object>>)list.get("col");
        List<Map<String,Object>> rowbtnList=(List<Map<String,Object>>)list.get("rowbtn");

        String listcode= Utils.trimNull( coreMap.get("listcode"));
        String listname=  Utils.trimNull( coreMap.get("listname"));
        String select_express=  Utils.trimNull( coreMap.get("select_express"));
        String count_express=  Utils.trimNull( coreMap.get("count_express"));
        String orderby_express=  Utils.trimNull( coreMap.get("orderby_express"));
        String groupby_express=  Utils.trimNull( coreMap.get("groupby_express"));
        String APPEND_HEAD=  Utils.trimNull( coreMap.get(JsoupUtil.APPEND_HEAD));
        String APPEND_FOOT=  Utils.trimNull( coreMap.get(JsoupUtil.APPEND_FOOT));
        Config config=null;
        if (config==null) {
            Config tempConfig=listConfigMap.get("listtemplate");
            // path = "ezadmin/config/list/template.html";
            Document doc =tempConfig.getDoc().clone();
            String editPath=EzBootstrap.instance().getEditLocation()+File.separator+"list";
            editPath=editPath+(File.separator+listcode.toLowerCase()+".html");
            //创建新文件
            Config c=new Config();
            c.setFile(new File(editPath));
            c.setUrl(new File(editPath).toURI().toURL());
            c.setPath(new File(editPath).toURI().toURL().getPath());
            c.setProtocol("file");
            if(!new File(editPath).exists()){
                Files.createFile(Paths.get(editPath));
            }

            doc.body().attr("id",listcode.toLowerCase());
            c.setDoc(doc);
            listConfigMap.put(listcode.toLowerCase(),c);
            config=c;
        }
        Document doc=listConfigMap.get(listcode.toLowerCase()).getDoc();
        Element body = doc.body();
        //处理主体
        doc.title(listname);
        body.attr("id", listcode);
        body.getElementById("express").html(select_express);
        body.getElementById("express").attr("orderby",orderby_express);
        body.getElementById("express").attr("groupby",groupby_express);
        body.getElementById("count").html(count_express);
        body.getElementById(JsoupUtil.APPEND_HEAD).html(APPEND_HEAD);
        body.getElementById(JsoupUtil.APPEND_FOOT).html(APPEND_FOOT);

        for (int i = 0; i < attr.length; i++) {
            String value=   Utils.trimNull( coreMap.get(attr[i]));
            body.attr(attr[i],value);
        }
        //处理tab
        tab(tabList, body);
        //处理search
        search(searchList, body);
        //处理tablebutton
        tablebtn(tablebtnList, body); 
        //处理rowbutton
        rowbtn(rowbtnList, body);
        //处理列
        col(colList, body);

        JsoupUtil.updateConfig(config);
    }

    private void col(List<Map<String, Object>> colList, Element body) {
        if(Utils.isEmpty(colList)){
            return;
        }
        for (int i = 0; i < colList.size(); i++) {
            Map<String,Object> tab= colList.get(i);
            Element tabHtml=newCol();
            for (int k = 0; k < colNames.length; k++) {
                String formItemAttrValue=Utils.trimNull(tab.get(colNames[k]));
                if(StringUtils.isNotBlank(formItemAttrValue)){
                    tabHtml.getElementsByTag("th").attr(colNames[k],formItemAttrValue);
                }
            }
            tabHtml.html(Utils.trimNullDefault(tab.get(JsoupUtil.LABEL),"文案"));
            body.getElementById("column").appendChild(tabHtml);
        }
    }

    private void rowbtn(List<Map<String, Object>> rowbtnList, Element body) {
        if(Utils.isEmpty(rowbtnList)){
            return;
        }
        for (int i = 0; i < rowbtnList.size(); i++) {
            Map<String,Object> tab= rowbtnList.get(i);
            Element tabHtml=newButton();
            for (int k = 0; k < names.length; k++) {
                String formItemAttrValue=Utils.trimNull(tab.get(names[k]));
                if(StringUtils.isNotBlank(formItemAttrValue)){
                    tabHtml.attr(names[k],formItemAttrValue);
                }
            }
            tabHtml.html(Utils.trimNullDefault(tab.get(JsoupUtil.LABEL),"文案"));
            body.getElementById("rowbutton").appendChild(tabHtml);
        }
    }

    private void tablebtn(List<Map<String, Object>> tablebtnList, Element body) {
        if(Utils.isEmpty(tablebtnList)){
            return;
        }
        for (int i = 0; i < tablebtnList.size(); i++) {
            Map<String,Object> tab= tablebtnList.get(i);
            Element tabHtml=newButton();
            for (int k = 0; k < names.length; k++) {
                String formItemAttrValue=Utils.trimNull(tab.get(names[k]));
                if(StringUtils.isNotBlank(formItemAttrValue)){
                    tabHtml.attr(names[k],formItemAttrValue);
                }
            }
            tabHtml.html(Utils.trimNullDefault(tab.get(JsoupUtil.LABEL),"文案"));
            body.getElementById("tableButton").appendChild(tabHtml);
        }
    }

    private void search(List<Map<String, Object>> searchList, Element body) {
        if(Utils.isEmpty(searchList)){
            return;
        }
        for (int i = 0; i < searchList.size(); i++) {
            Map<String,Object> tab= searchList.get(i);
            Element tabHtml=newSearch(tab.get(JsoupUtil.ITEM_NAME),tab.get(JsoupUtil.LABEL) );
            for (int k = 0; k < names.length; k++) {
                String formItemAttrValue=Utils.trimNull(tab.get(names[k]));
                if(StringUtils.isNotBlank(formItemAttrValue)){
                    tabHtml.getElementsByTag("object").attr(names[k],formItemAttrValue);
                }
            }
            body.getElementById("search").appendChild(tabHtml);
        }
    }

    private void tab(List<Map<String, Object>> tabList, Element body) {
        if(Utils.isEmpty(tabList)){
            return;
        }
        for (int i = 0; i < tabList.size(); i++) {
            Map<String,Object> tab= tabList.get(i);
            Element tabHtml=newTab( );
            tabHtml.getElementsByTag("a").attr(JsoupUtil.SELECT,Utils.trimNull(tab.get(JsoupUtil.SELECT)));
            tabHtml.getElementsByTag("a").attr(JsoupUtil.ITEM_NAME,Utils.trimNull(tab.get(JsoupUtil.ITEM_NAME)));
            tabHtml.getElementsByTag("a").attr(JsoupUtil.URL,Utils.trimNull(tab.get(JsoupUtil.URL)));
            tabHtml.getElementsByTag("a").html(Utils.trimNull(tab.get(JsoupUtil.LABEL)));
            body.getElementById("tab").appendChild(tabHtml);
        }
    }

    private Element newCol() {
        return new Element("th" ) ;
    }

    public Element newTab( ){
        return Jsoup.parse("<li  >\n" +
                "   <a     \"class=\"tablink\"  >  </a>\n" +
                "</li>").body().child(0);
    }
    public Element newButton(  ){
        return Jsoup.parse("<button\n" +
                "        class=\" layui-btn   layui-btn-sm layui-btn-normal  \">\n" +
                "</button> ").body().child(0);
    }
    public Element newSearch(Object name,Object label ){
        return Jsoup.parse(" <div class=\"selector layui-col-md3 list-item \" type=\"input-text\"  >\n" +
                "    <div  class=\"layui-form-item\" >\n" +
                "        <label class=\"layui-form-label\"  >"+label+"</label>\n" +
                "        <div class=\"layui-input-block\">\n" +
                "                <object class=\"list-search-item\"  item_name=\""+name+"\"></object>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>").body().child(0);
    }

    public static void main(String[] args) {
        System.out.println(ListDao.getInstance().newButton( ));
    }

}


