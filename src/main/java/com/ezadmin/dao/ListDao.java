package com.ezadmin.dao;

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

    static String[] BODY_ATTRS=new String[]{"datasource",
            "fixnumber","fixnumberright","success_url",
            "empty_show","tablestyle","adminstyle","pagesync",
            "firstcol","export","cellminwidth","linestyle"
    };
    private static  String [] colNames=new String[]{ JsoupUtil.ITEM_NAME,
            JsoupUtil.URL, JsoupUtil.HEAD_PLUGIN_CODE,
            JsoupUtil.BODY_PLUGIN_CODE, JsoupUtil.ORDER,
            JsoupUtil.DATATYPE,JsoupUtil.DATA
            ,JsoupUtil.AREA, JsoupUtil.OPENTYPE,JsoupUtil.STYLE,
            JsoupUtil.WINDOW_NAME
            ,JsoupUtil.JDBCTYPE,JsoupUtil.EMPTY_SHOW,
            JsoupUtil.DATASOURCE,JsoupUtil.EDIT_FLAG,
            JsoupUtil.EDIT_EXPRESS,JsoupUtil.EDIT_PLUGIN,
            JsoupUtil.WIDTH,JsoupUtil.MIN_WIDTH,JsoupUtil.MIN_HEIGHT,JsoupUtil.FROZEN,JsoupUtil.CLASS
    };
    private static   String [] names=new String[]{ JsoupUtil.VALIDATERULES,JsoupUtil.VALIDATEMESSAGES,JsoupUtil.DATATYPE,
            JsoupUtil.OPENTYPE,JsoupUtil.WINDOW_NAME,JsoupUtil.URL,
            JsoupUtil.DATA,JsoupUtil.JDBCTYPE,JsoupUtil.PLACEHOLDER,JsoupUtil.LAYVERIFY,
            JsoupUtil.ITEM_NAME,
            JsoupUtil.OPER
            ,JsoupUtil.STYLE
            ,JsoupUtil.MULTI,JsoupUtil.COLLAPSETAGS,JsoupUtil.SHOWALLLEVELS
            ,JsoupUtil.TOP_DESC,JsoupUtil.ITEM_DESC,JsoupUtil.RIGHT_DESC
            ,JsoupUtil.ALIAS,JsoupUtil.ALIGN,JsoupUtil.HELP,JsoupUtil.TYPE,JsoupUtil.COL,JsoupUtil.PLUGIN
            ,JsoupUtil.DISPLAY ,JsoupUtil.CLASS
    };
    private ListDao() {

    }

    public static ListDao getInstance() {
        return dao;
    }

    public void init()   {
        loadAllLists( );
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
            Utils.putIfAbsent(laydataMap,"minHeight",110);
            Utils.putIfAbsent(laydataMap,"minWidth",200);
            //兼容高度
            if(!StringUtils.contains(Utils.trimNull(laydataMap.get(JsoupUtil.STYLE)),"min-height")){
                laydataMap.put(JsoupUtil.STYLE,Utils.trimNull(laydataMap.get(JsoupUtil.STYLE))+";min-height:110px");
            }
        }
        //如果是数字默认靠右
        if(JdbcTypeEnum.isNumberType(th.attr(JsoupUtil.JDBCTYPE))){
            Utils.putIfAbsent(laydataMap,"align","right");
        }

        if(StringUtils.equals(th.attr("order"),"1")){
            Utils.putIfAbsent(laydataMap,"sort",true);
        }

    }

    private void attrToLayData(Map<String, Object> layDataMap,Element th) {
        try {
            Utils.putIfAbsent(layDataMap,"field", th.attr(JsoupUtil.ITEM_NAME));
            Utils.putIfAbsent(layDataMap,"title", Utils.trimNull(th.html()));
            layDataMap.put("escape", false);
            //fixed
             Utils.putIfAbsent(layDataMap,"fixed", Utils.trimNull(th.attr("fixed")));
            Utils.putIfAbsent(layDataMap, "align", Utils.trimNullDefault(th.attr("align"),"left"));
            if(StringUtils.isNotBlank(th.attr("colspan"))){
                layDataMap.put("colspan",th.attr("colspan"));
             }
            if(StringUtils.isNotBlank(th.attr("rowspan"))){
                layDataMap.put("rowspan",th.attr("rowspan"));
             }

            //属性的权重比style高,
             int minwidth=NumberUtils.toInt(Utils.trimNull(th.attr("minwidth")));
             int maxwidth=NumberUtils.toInt(Utils.trimNull(th.attr("maxwidth")));
             int minheight=NumberUtils.toInt(Utils.trimNull(th.attr("minheight")));
             int width=NumberUtils.toInt(Utils.trimNull(th.attr("width")));
             Utils.putIfAbsentExclude0(layDataMap,"minWidth",minwidth );
             if(maxwidth>0) {
                 //maxwidth 仅限有滚动条的情况
                 Utils.putIfAbsentExclude0(layDataMap, "maxWidth",maxwidth);
             }
             Utils.putIfAbsentExclude0(layDataMap,"minHeight",minheight);
             Utils.putIfAbsentExclude0(layDataMap,"width",width);




            String style = th.attr(JsoupUtil.STYLE);
            //style会补充到 laydata里面
            if (StringUtils.isNotBlank(style)) {
                Utils.putIfAbsent(layDataMap,JsoupUtil.STYLE, style);
            }

        }catch (Exception e){
            log.error("",e);
        }
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
           // log.info("loadAllList:{}",item.getFile());
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


    private static void initButtonMapObj(Map<String, Object> btnMap, Element btn) {
        String label = Utils.trimEmptyDefault(JsoupUtil.strip(btn.text()),btn.attr(JsoupUtil.ITEM_NAME));
        Utils.putIfAbsent(btnMap,JsoupUtil.LABEL, label);
        String [] names=new String[]{   JsoupUtil.WINDOW_NAME,JsoupUtil.ITEM_NAME
                ,JsoupUtil.AREA,JsoupUtil.URL,JsoupUtil.OPENTYPE,JsoupUtil.CLASS,JsoupUtil.STYLE,
                JsoupUtil.EZ_CALLBACK,JsoupUtil.ITEM_ID,JsoupUtil.DISPLAY,JsoupUtil.TYPE
        };
        for (int i = 0; i < names.length; i++) {
            Utils.putIfAbsent(btnMap,names[i], strip(btn.attr(names[i])));
        }
        Utils.putIfAbsent(btnMap,JsoupUtil.LABEL, btn.html());
    }

    public void clear() {
    }
 
    void fillcore(Map<String,Object> coreMap,Config config){
        Document doc=config.getDoc();
        coreMap.put("listname", JsoupUtil.strip(doc.title()));
        coreMap.put("ENCRYPT_LIST_ID", JsoupUtil.strip(doc.body().attr("id")));
        coreMap.put("listcode", JsoupUtil.strip(doc.body().attr("id")));
        coreMap.put(JsoupUtil.APPEND_HEAD, doc.getElementById(JsoupUtil.APPEND_HEAD) == null ? "" : doc.getElementById(JsoupUtil.APPEND_HEAD).html());
        coreMap.put(JsoupUtil.APPEND_FOOT, doc.getElementById(JsoupUtil.APPEND_FOOT) == null ? "" : doc.getElementById(JsoupUtil.APPEND_FOOT).html());

        coreMap.putAll(JsoupUtil.loadAttrNoChild(doc.body() ));


        for (int i = 0; i < BODY_ATTRS.length; i++) {
            String attrValue=doc.body().attr(BODY_ATTRS[i]);
            if(StringUtils.isNotBlank(attrValue)){
                coreMap.put(BODY_ATTRS[i],attrValue);
            }
        }

        Element express = doc.getElementById("express");

        if (express != null) {
            coreMap.put(JsoupUtil.SELECT_EXPRESS, express.text());
            coreMap.put(JsoupUtil.ORDERBY_EXPRESS, express.attr("orderby"));
            coreMap.put(JsoupUtil.GROUPBY_EXPRESS, express.attr("groupby"));
        }
        Element count = doc.getElementById("count");
        if (count != null) {
            coreMap.put(JsoupUtil.COUNT_EXPRESS, count.text());
        }
        //displayorder
        Element display = doc.getElementById(JsoupUtil.DISPLAYORDER_EXPRESS);
        if (display != null) {
            coreMap.put(JsoupUtil.DISPLAYORDER_EXPRESS, display.text());
        }
        //adminstyle
        String[] path=config.getPath().substring(config.getPath().indexOf("ezadmin/config")+"ezadmin/config".length()).split("/");
        coreMap.put(JsoupUtil.ADMINSTYLE,path[1]);
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
                if(isImageTd(th)){
                    haspic=true;break;
                }
            }
        }
        Element rowbutton = config.getDoc().getElementById("rowbutton");
        if(rowbutton!=null) {
            Map<String, Object> json=new HashMap<>();
            json.put("field",Utils.trimEmptyDefault(rowbutton.attr(JsoupUtil.ITEM_NAME),"oper"));
            attrToLayData(json,rowbutton);
            //默认两个按钮的宽度，每个按钮两个字
            json.putIfAbsent("width",150);

            //由于layui没有提供height操作，需要对height做一层处理
            try {
                String userStyle= rowbutton.attr("style");
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
                json.put("title","操作");
                coreMap.put(JsoupUtil.LAYDATA, JSONUtils.toJSONString(json).replaceAll("\"","'"));
                coreMap.put("rowbtnwidth",Utils.trimEmptyDefault(rowbutton.attr("width"),"175"));
                coreMap.put("rowbtnclass",Utils.trimNull(rowbutton.attr("class") ));
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    void fillcol(List<Map<String, Object>> searchConfigList, Config config){
        Document doc = config.getDoc();
        Element column = doc.getElementById("column");
         if (column != null) {
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
                // laydata属性
                attrToLayData(laydataMap,th);
                defaultMap(laydataMap,th);

//                //默认宽度设置为110
//                if(laydataMap.get("width")==null
//                        &&StringUtils.isBlank(th.attr("width"))
//                &&!StringUtils.equals(doc.body().attr("autowidth"),"1")
//                ){
//                    Utils.putIfAbsent(laydataMap,"width","110");
//                }

                String fixed=Utils.trimNull(laydataMap.get("fixed"));
                if(StringUtils.isNotBlank(fixed)){
                    thMap.put("fixed",fixed);
                }
                thMap.put(JsoupUtil.LAYDATA,JSONUtils.toJSONString(laydataMap).replaceAll("\"","'"));
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
                 btnMap.putIfAbsent(JsoupUtil.TYPE,"button-single");

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
            Utils.putIfAbsent(listitem,JsoupUtil.URL, Utils.trimEmptyDefault(item.attr(JsoupUtil.HREF),item.attr(JsoupUtil.URL)));
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
        Element search=doc.getElementById("search");

        List<Element> searchList=search.select("[item_name]");
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

        if(select_express.indexOf("<![CDATA[")==-1){
            select_express="\n<![CDATA[ \n"+ select_express+"\n]]>\n";
        }

        Config config=null;
        if (config==null) {
            Document doc =JsoupUtil.newlist();
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
        body.getElementById("express").html("\n"+select_express+"\n");
        body.getElementById("express").attr("orderby",orderby_express);
        body.getElementById("express").attr("groupby",groupby_express);
        body.getElementById("count").html("\n"+count_express+"\n");
        body.getElementById(JsoupUtil.APPEND_HEAD).html(APPEND_HEAD);
        body.getElementById(JsoupUtil.APPEND_FOOT).html(APPEND_FOOT);

        for (int i = 0; i < BODY_ATTRS.length; i++) {
            String value=   Utils.trimNull( coreMap.get(BODY_ATTRS[i]));
            if(StringUtils.isNotBlank(value)){
                body.attr(BODY_ATTRS[i],value);
            }
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
                    if(colNames[k].equals("body")&&formItemAttrValue.equals("td-text")){
                        tabHtml.getElementsByTag("th").removeAttr(colNames[k]);
                        continue;
                    }
                    if(colNames[k].equals("head")&&formItemAttrValue.equals("th")){
                        tabHtml.getElementsByTag("th").removeAttr(colNames[k]);
                        continue;
                    }
                    tabHtml.getElementsByTag("th").attr(colNames[k],formItemAttrValue);
                }
            }
            tabHtml.html(Utils.trimNullDefault(tab.get(JsoupUtil.LABEL),"文案"));
            body.getElementById("column").append("\n\t\t\t"+tabHtml.outerHtml() );
        }
    }

    private void rowbtn(List<Map<String, Object>> rowbtnList, Element body) {
        if(Utils.isEmpty(rowbtnList)){
            return;
        }
        for (int i = 0; i < rowbtnList.size(); i++) {
            Map<String,Object> tab= rowbtnList.get(i);
            Element rowbtn=newButton();
            for (int k = 0; k < names.length; k++) {
                String formItemAttrValue=Utils.trimNull(tab.get(names[k]));
                if(StringUtils.isNotBlank(formItemAttrValue)){
                    rowbtn.attr(names[k],formItemAttrValue);
                }
            }
            rowbtn.html(Utils.trimNullDefault(tab.get(JsoupUtil.LABEL),"文案"));
            body.getElementById("rowbutton").append("\n\t\t\t"+rowbtn.outerHtml());
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
            body.getElementById("tableButton").append("\n"+tabHtml.outerHtml()+"\n");
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
            body.getElementById("search").append("\n"+tabHtml.outerHtml()+"\n");
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
            body.getElementById("tab").append("\n"+tabHtml.outerHtml()+"\n");
        }
    }

    private Element newCol() {
        return new Element("th" ) ;
    }

    public Element newTab( ){
        return Jsoup.parse("<li  >\n" +
                "   <a     class=\"tablink\"  >  </a>\n" +
                "</li>\n").body().child(0);
    }
    public Element newButton(  ){
        return Jsoup.parse("\n<button\n" + ">\n" +
                "</button>\n " ).body().child(0) ;
    }
    public Element newSearch(Object name,Object label ){
        return Jsoup.parse(
"\t\t\t<div>\n" +
"\t\t\t\t<label   >"+label+"</label>\n" +
"\t\t\t\t\t<div  >\n" +
"\t\t\t\t\t\t<object  item_name=\""+name+"\"></object>\n" +
"\t\t\t\t\t</div>\n" +
"\t\t\t\t</div>\n"
                 ).body().child(0);
    }

}


