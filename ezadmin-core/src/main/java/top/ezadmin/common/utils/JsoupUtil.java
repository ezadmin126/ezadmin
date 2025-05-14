package top.ezadmin.common.utils;

import top.ezadmin.web.Config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsoupUtil {
    //自定义的模版
    public static final String TEMPLATE = "template";
    public static final String FRAMEURL = "frameurl";
    public static final String FORM_CARD_BTN_NAME = "btnname";
    public static final String FORM_CARD_BTN_URL = "btnurl";
    public static final String FRAMEHEIGHT = "frameheight";
    protected static final String HREF ="href" ;
    protected static final String COL = "col";
    public static final String TYPE ="type" ;
    public static final String ADMINSTYLE = "adminstyle";
    protected static final String MAXLENGTH = "maxlength";
    protected static final String FIXED = "fixed";
    public static final String ORGSRC = "orgsrc";
    public static String TABLESTYLE="tablestyle";
    protected static String FROZEN="frozen";
    private static  Logger logger= LoggerFactory.getLogger(JsoupUtil.class);

    public static final String DATASOURCE = "datasource";
    public static final String DATATYPE ="datatype" ;
    public static final String JDBCTYPE ="jdbctype" ;
    public static final String ALIAS ="alias" ;
    public static final String ORDER ="order" ;

    public static final String LABEL ="label" ;
    public static final String OPER ="oper" ;
    public static final String CLASS ="class" ;
    public static final String PLACEHOLDER ="placeholder" ;
    public static final String MULTI = "multi";
    public static final String COLLAPSETAGS = "collapsetags";
    public static final String SHOWALLLEVELS = "showalllevels";
    public static final String STYLE = "style";
    public static final String LAYDATA = "laydata";
    public static final String NAME ="name" ;
    public static final String WINDOW_NAME = "windowname";
    public static final String AREA = "area";
    //
    public static final String URL = "url";
    public static final String OPENTYPE = "opentype";
    public static final String EZ_CALLBACK ="ez_callback" ;
    public static final String SELECT = "select";
    public static final String HEAD_PLUGIN_CODE = "head";
    public static final String BODY_PLUGIN_CODE = "body";
    public static final String EMPTY_SHOW = "emptyshow";
    public static final String EDIT_FLAG = "editflag";
    public static final String FORM_EDIT_FLAG = "FORM_EDIT_FLAG";
    public static final String EDIT_EXPRESS ="editexpress" ;
    public static final String EDIT_PLUGIN = "editplugin";
    public static final String WIDTH = "width";
    public static final String MIN_WIDTH = "minWidth";
    public static final String MIN_HEIGHT = "minHeight";

    public static final String ALIGN = "align";
    public static final String TOP_DESC ="top_desc" ;
    public static final String ITEM_DESC ="item_desc" ;
    public static final String RIGHT_DESC = "right_desc";
    public static final String HELP ="help" ;
    public static final String SUBMIT_EXPRESS = "submit_express";
    public static final String INIT_EXPRESS ="init_express" ;
    public static final String DELETE_EXPRESS = "delete_express";
    public static final String STATUS_EXPRESS = "status_express";


    public static final String SELECT_EXPRESS = "select_express";
    public static final String COUNT_EXPRESS = "count_express";
    public static final String ORDERBY_EXPRESS = "orderby_express";
    public static final String GROUPBY_EXPRESS = "groupby_express";
    public static final String DISPLAYORDER_EXPRESS = "displayorder_express";

    public static final String GROUP_DATA = "group_data";
    public static final String APPEND_HEAD ="append_head" ;
    public static final String APPEND_FOOT ="append_foot" ;
    public static final String FORM_NAME ="FORM_NAME" ;
    public static final String EZCONFIG ="ezconfig" ;
    public static final String SUCCESS_URL = "success_url";
    public static final String GROUP_NAME ="group_name" ;
    public static final String ITEM_ID ="item_id";
    public static final String DISPLAY = "display";
    //
    public static String EZ_DEFAULT_GROUP="EZ_DEFAULT_GROUP";
     public static String PLUGIN="plugin_code";
    public static String PLUGIN_FOLD="pluginFolder";
    //
    public static String ITEM_NAME="item_name";
    public static String ORDER_TYPE="ordertype";
    public static String DATA="data";
    public static String VALIDATERULES="valid_rule";
    public static String VALIDATEMESSAGES="valid_msg";
    //
    public static String LAYVERIFY="lay_verify";
    public static String SPECIALCOL="specialcol";

    public static void attr(Element item, String key, String values) {

        if (item!=null) {
            if(StringUtils.isBlank(values)){//如果属性为空，就不展示属性了
                item.removeAttr(key);
            }else{
                item.attr(key, Utils.trimNull(values));
            }
        }
    }
    public static void attrAll(Element item, Map<String, String> request) {
        if(request!=null){
            for(Map.Entry<String, String> e:request.entrySet()) {
                attr(item,e.getKey()  ,e.getValue());
            };
        }
    }
    public static String attr2Json(Element item) {
        return JSONUtils.toJSONString(attr2Map(item));
    }
    public static Map<String,String> attr2Map(Element item) {
        List<Attribute> list=item.attributes().asList();
        Map<String,String> at=new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Attribute attribute=list.get(i);
            at.put(attribute.getKey(),strip(Utils.trimNull(attribute.getValue())));
        }
        return at;
    }
    public static void json2Attr(Element item,String json) {
        if(item==null||StringUtils.isBlank(json)){
            return;
        }
        try {
            Map<String, String> at = JSONUtils.parseMap(json);
            attrAll(item, at);
        }catch (Exception e){
            logger.warn("自定义配置异常,不是json格式：{}",json);
        }
    }



    public static void text(Element item, String values) {
        if (item!=null) {
            item.text(Utils.trimNull(values));
        }
    }
    public static void html(Element item, String values) {
        if (item!=null) {
            item.text(Utils.trimNull(values));
        }
    }
    public static Document parse(String path) throws IOException {
        Document doc = null;
        File file = getFileByUrl(path);
        try {
            doc = Jsoup.parse(file,"UTF-8");
        } catch (FileNotFoundException e) {
            InputStream stream = getStreamByUrl(path);
            doc = Jsoup.parse(stream, "UTF-8", "");
            stream.close();
        }
        return doc;
    }
    public static File getFileByUrl(String path) throws IOException {
        try {
            if (Resources.getResourceURL(path) == null) {
                return null;
            }
        } catch (Exception e) {
        }
        try {
            String editPath = compilePath(path) ;
            File file = new File(editPath);
            if (file.exists()) {
                return file;
            }
            return Resources.getResourceAsFile(path);
        } catch (Exception e) {
            String editPath = editPath(path) ;
            File file = new File(editPath);
            if (file.exists()) {
                return file;
            }
            return Resources.getResourceAsFile(path);
        }
    }

    public static InputStream getStreamByUrl(String path) throws IOException {
        try {
            if (Resources.getResourceURL(path) == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        try {
            String editPath = compilePath(path)
                   ;
            File file = new File(editPath);
            if (file.exists()) {
                return new FileInputStream(file);
            }
            return Resources.getResourceAsStream(path);
        } catch (Exception e) {
            String editPath = editPath(path) ;
            File file = new File(editPath);
            if (file.exists()) {
                return new FileInputStream(file);
            }
            return Resources.getResourceAsStream(path);
        }
    }
    public  static String projectRootPath( ) throws IOException {
        String  path= Resources.getResourceURL("/").getPath();
        if(System.getProperty("os.name").toLowerCase().indexOf("windows")>=0){
            return path.substring(1);
        }else{
            return path;
        }
    }


    public   static String editPath(String pathx) throws IOException {
            String rootPath= projectRootPath();
      //  logger.info("rootpath::{}",rootPath);
        rootPath= rootPath.substring(0,rootPath.indexOf("target")) +"src"+
                    File.separator+"main"+File.separator+"resources"+File.separator+pathx;
        if(Files.notExists(Paths.get(rootPath))){
            Files.createDirectories(Paths.get(rootPath));
        }
        return rootPath;
    }
    public  static String compilePath(String pathx) throws IOException {
        return projectRootPath()+pathx;
    }

    private static Map<String, String> ATTR_KEY_MAPPING = new HashMap<>();

    static {
        ATTR_KEY_MAPPING.put("jdbctype", "ITEM_JDBC_TYPE");
        ATTR_KEY_MAPPING.put("href", "ITEM_URL");
        ATTR_KEY_MAPPING.put("url", "ITEM_URL");
        ATTR_KEY_MAPPING.put("name", "ITEM_NAME");
        ATTR_KEY_MAPPING.put("opentype", "OPEN_TYPE");
        ATTR_KEY_MAPPING.put("style", "ITEM_STYLE");
        ATTR_KEY_MAPPING.put("data", "INIT_DATA");
        ATTR_KEY_MAPPING.put("datatype", "INIT_DATA_TYPE");
        ATTR_KEY_MAPPING.put("head", "HEAD_PLUGIN_CODE");
        ATTR_KEY_MAPPING.put("body", "BODY_PLUGIN_CODE");
        ATTR_KEY_MAPPING.put("windowname", "WINDOW_NAME");
        ATTR_KEY_MAPPING.put("alias", "ITEM_ALIAS");
        ATTR_KEY_MAPPING.put("placeholder", "ITEM_PLACEHOLDER");
        ATTR_KEY_MAPPING.put("oper", "ITEM_SEARCH_OPERATOR");
        ATTR_KEY_MAPPING.put("orderby", "DEFAULT_ORDER");
        ATTR_KEY_MAPPING.put("order", "ITEM_ORDER");
        ATTR_KEY_MAPPING.put("groupby", "DEFAULT_GROUP");
        ATTR_KEY_MAPPING.put("label", "ITEM_LABEL");
        ATTR_KEY_MAPPING.put("upload", "ITEM_MAX_UPLOAD");
    }
    public static String strip(String a) {
        return StringUtils.strip(a);
    }

    public static Map<String, String> loadAttrNoChild(Element item ) {
        Map<String, String> ITEM_MAP = new HashMap<>();
        String type = strip(item.attr("type"));
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(type)) {
            sb.append(item.tagName());
        } else if (type.contains("-")) {
            sb.append(type);
        } else if (StringUtils.equalsIgnoreCase(item.tagName(), "object")) {
            sb.append(type);
        } else {
            sb.append(item.tagName() + "-" + type);
        }
      //  ITEM_MAP.put("PLUGIN_CODE", sb.toString().toLowerCase());
        List<Attribute> list=item.attributes().asList();
        for (int i = 0; i < list.size(); i++) {
            Attribute attribute=list.get(i);
           // ITEM_MAP.put(attribute.getKey().toUpperCase(), strip(attribute.getValue()));
            ITEM_MAP.put(attribute.getKey().toLowerCase(), strip(attribute.getValue()));
//             if (ATTR_KEY_MAPPING.containsKey(attribute.getKey().toLowerCase())) {
//                Utils.putIfAbsent(ITEM_MAP,ATTR_KEY_MAPPING.get(attribute.getKey().toLowerCase()), strip(attribute.getValue()));
//            }
             if(StringUtils.equalsIgnoreCase(attribute.getKey(),"readonly")){
                 ITEM_MAP.put(attribute.getKey().toLowerCase(), "readonly");
             }
             if(StringUtils.equalsIgnoreCase(attribute.getKey(),"disabled")){
                 ITEM_MAP.put(attribute.getKey().toLowerCase(), "disabled");
             }

        } ;
        return ITEM_MAP;
    }
    public static Map<String, String> loadDataAttrNoChild(Element item ) {
        Map<String, String> ITEM_MAP = new HashMap<>();
        List<Attribute> list=item.attributes().asList();
        for (int i = 0; i < list.size(); i++) {
            Attribute attribute=list.get(i);
            String key=attribute.getKey().toLowerCase();
            if(key.startsWith("data-")){
                ITEM_MAP.put(key, strip(attribute.getValue()));
            }
        };
        return ITEM_MAP;
    }

    public static String getTypeByElement(Element item){
        String type = strip(item.attr("type"));
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(type)) {
            sb.append(item.tagName());
        } else if (type.contains("-")) {
            sb.append(type);
        } else if (StringUtils.equalsIgnoreCase(item.tagName(), "object")) {
            sb.append(type);
        } else {
            sb.append(item.tagName() + "-" + type);
        }
        //默认 输入框
        if(StringUtils.isBlank(sb.toString())||StringUtils.equals(sb.toString(),"object")){
            return "input-text";
        }
        return sb.toString();
    }

    public static Map<String, String> loadplugin(Element item ) {
        Map<String, String> ITEM_MAP = new HashMap<>();
        String type = strip(item.attr("type"));
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(type)) {
            sb.append(item.tagName());
        } else if (type.contains("-")) {
            sb.append(type);
        } else if (StringUtils.equalsIgnoreCase(item.tagName(), "object")) {
            sb.append(type);
        } else {
            sb.append(item.tagName() + "-" + type);
        }


        ITEM_MAP.put(JsoupUtil.TYPE, sb.toString().toLowerCase());
        List<Attribute> list=item.attributes().asList();
        for (int i = 0; i < list.size(); i++) {
            Attribute attribute=list.get(i);
            //type为 plugin的关键字，不传递
            if(attribute.getKey().toLowerCase().equals("type")){
                continue;
            }
             ITEM_MAP.put(attribute.getKey().toLowerCase(), strip(attribute.getValue()));
        } ;
        return ITEM_MAP;
    }


    public static void loadConfigByName(Element configForm, Map<String, String> formMap,String key) {
        Element item=configForm.selectFirst("["+JsoupUtil.ITEM_NAME+"='"+key+"']");
        if(item==null){
            return;
        }
        formMap.put(key,  Utils.trimNull(item.text()));
    }

    public static String updateConfig(Config config) throws IOException {
        config.getDoc().outputSettings().prettyPrint(true).outline(true).escapeMode();
        String html=config.getDoc().html();
        try {

            Files.write(config.getFile().toPath(), html.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
            return html;
        }catch (Exception e){
            logger.error("",e);
        }
        return html;
    }

    public static    void dealElementByPluginCode(String plugin_code, Element element) {
        String plugin = plugin_code;
        if (StringUtils.isBlank(plugin)) {
            plugin = "input-text";
        }
        String[] pluginArray = StringUtils.split(plugin, "-");
        if (pluginArray.length == 1) {
            if(pluginArray[0].equalsIgnoreCase("pre")){
                element.tagName("pre");
                element.attr("type", "");
            }else if(pluginArray[0].equalsIgnoreCase("select")){
                element.tagName("select");
                element.attr("type", "");
            }else if(pluginArray[0].equalsIgnoreCase("textarea")){
                element.tagName("textarea");
                element.attr("type", "");
            }
            else{
                element.tagName("object");
                element.attr("type", pluginArray[0]);
            }
        }
        if (pluginArray.length == 2) {
            element.tagName(pluginArray[0]);
            element.attr("type", pluginArray[1]);
        }
    }
  static  String list=
"<html >\n" +
"   <head>\n" +
"     <title>模版</title>\n" +
"   </head>\n" +
"<body id=\"listtemplate\">\n" +
"   <div id=\"append_head\">\n" +
"   </div>\n" +
"   <ul id=\"tab\">\n" +
"   </ul>\n" +
"   <form id=\"search\">\n" +
"   </form>\n" +
"   <div id=\"tableButton\">\n" +
"   </div>\n" +
"   <table>\n" +
"   <tbody>\n" +
"       <tr id=\"column\">\n" +
"           <th id=\"rowbutton\"></th>\n" +
"       </tr>\n" +
"   </tbody>\n" +
"   </table>\n" +
"   <pre id=\"express\">     </pre>\n" +
"   <pre id=\"count\">    </pre>\n" +
        "   <pre    item_name=\"displayorder_express\" type=\"\">" +
        "" +
        "</pre>\n" +
"   <div id=\"append_foot\"></div>\n" +
"</body>\n" +
"</html>";
    public static String order="import java.lang.Math;\n" +
            "      oldOrder=Integer.parseInt($(\"oldOrder\"));\n" +
            "      newOrder=Integer.parseInt($(\"displayOrder\"));\n" +
            "      if(newOrder>oldOrder){\n" +
            "         update(\"update #table# set       DISPLAY_ORDER=DISPLAY_ORDER-1  where DISPLAY_ORDER>=\"+Math.min(newOrder,oldOrder)+\" and DISPLAY_ORDER <=\"+Math.max(newOrder,oldOrder));\n" +
            "      }else{\n" +
            "         update(\"update #table# set       DISPLAY_ORDER=DISPLAY_ORDER+1  where DISPLAY_ORDER>=\"+Math.min(newOrder,oldOrder)+\" and DISPLAY_ORDER <=\"+Math.max(newOrder,oldOrder));\n" +
            "      }\n" +
            "        cc=count(\"select count(1) from #table# where DELETE_FLAG=0 \");\n" +
            "        update(\"update #table# set DISPLAY_ORDER=\"+Math.min(newOrder,cc)+\"  where   ID=${orderId}\");\n" +
            "     ";

    public String newOrder(String table){
        return order.replaceAll("#table#",table);
    }
    public static Document newlist(){
         return Jsoup.parse(list);
    }
    public static String form=
            "<html >\n" +
            "    <title>template</title>\n" +
            "<body id=\"formtemplate\" datasource=\"datasource\" success_url=\"reload\">\n" +
            "<div id=\"append_head\"  ></div>\n" +
            "    <form id=\"inputForm\" >\n" +
            "    </form>\n" +
            "    <form id=\"configForm\" >\n" +
            "   <pre    item_name=\"init_express\" type=\"\"></pre>\n" +
            "   <pre    item_name=\"submit_express\" type=\"\"></pre>\n" +
            "   <pre    item_name=\"delete_express\" type=\"\"></pre>\n" +
            "   <pre    item_name=\"status_express\" type=\"\"></pre>\n" +
            "   <pre    item_name=\"displayorder_express\" type=\"\">" +
                    "" +
                    "</pre>\n" +
            "    </form>\n" +
            "<div id=\"append_foot\"  >\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    public static Document newform(){
        return Jsoup.parse(form);
    }

    public static void main(String[] args) {
//        System.out.println(Jsoup.parse("<div id=aa>123<data>ccc</data></div>").sel
//                .ownText());
    }

}

