package com.ezadmin.dao;

import com.ezadmin.EzBootstrap;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FormDao extends JsoupUtil{

    public static final Logger log = LoggerFactory.getLogger(FormDao.class);

    private static FormDao dao = new FormDao();
    private static Map<String, Config> formConfigMap=new HashMap();
    private static   String [] names=new String[]{ JsoupUtil.VALIDATERULES,JsoupUtil.VALIDATEMESSAGES,JsoupUtil.DATATYPE,
            JsoupUtil.OPENTYPE,JsoupUtil.WINDOW_NAME,JsoupUtil.URL,
            JsoupUtil.DATA,JsoupUtil.JDBCTYPE,JsoupUtil.PLACEHOLDER,JsoupUtil.LAYVERIFY
            , JsoupUtil.NAME,
            JsoupUtil.ITEM_NAME,
            JsoupUtil.OPER
            ,JsoupUtil.STYLE
            ,JsoupUtil.MULTI,JsoupUtil.COLLAPSETAGS,JsoupUtil.SHOWALLLEVELS
            ,JsoupUtil.TOP_DESC,JsoupUtil.ITEM_DESC,JsoupUtil.RIGHT_DESC
            ,JsoupUtil.ALIAS,JsoupUtil.ALIGN,JsoupUtil.HELP,JsoupUtil.TYPE
    };
    private FormDao() {

    }

    public static FormDao getInstance() {
        return dao;
    }
    public  void loadFormFile(File file) throws  Exception {
        Config item=new Config();
        Document doc = Jsoup.parse(file, "UTF-8", "");
        item.setUrl(file.toURI().toURL());
        item.setPath(file.toURI().toURL().getPath());
        item.setProtocol("file");
        item.setDoc(doc);
        item.setFile(file);
        formConfigMap.put(doc.body().id().toLowerCase(),item);
    }
    public void init()   {
            for (int i = 0; i < EzBootstrap.instance().getFormConfigResources().size(); i++) {
                Config  item=EzBootstrap.instance().getFormConfigResources().get(i);
                try {
                    InputStream stream = null;
                    if(item.isJar()){
                        stream=item.getIn();
                        Document doc = Jsoup.parse(stream, "UTF-8", "");
                        item.setDoc(doc);
                        formConfigMap.put(doc.body().id().toLowerCase(),item);
                        //jar包中的流确保只用一次，初始化之后就关闭流
                        stream.close();
                    }else{
                        loadFormFile(item.getFile());
                    }
                }catch (Exception e){
                    log.error("",e);
                }
            }
    }


    public   Map<String, String> selectFormById(String formCode) {
        
        Config config=formConfigMap.get(formCode.toLowerCase());
        Document doc = config.getDoc();
        Element body = doc.body();
        Map<String, String> formMap = JsoupUtil.loadAttrNoChild(body);
        formMap.put("ENCRYPT_FORM_ID", formCode);
        formMap.put("datasource", strip(body.attr("datasource")));
        formMap.put("success_url", strip(body.attr("success_url")));

        Element configForm=body.getElementById("configForm");
        if(configForm!=null){
            JsoupUtil.loadConfigByName(configForm, formMap,JsoupUtil.SUBMIT_EXPRESS);
            JsoupUtil.loadConfigByName(configForm, formMap,JsoupUtil.INIT_EXPRESS);
            JsoupUtil.loadConfigByName(configForm, formMap,JsoupUtil.DELETE_EXPRESS);
            JsoupUtil.loadConfigByName(configForm, formMap,JsoupUtil.STATUS_EXPRESS);
            JsoupUtil.loadConfigByName(configForm, formMap,JsoupUtil.GROUP_DATA);
        }else{//不再兼容老的
        }
        formMap.put(JsoupUtil.APPEND_HEAD, doc.getElementById(JsoupUtil.APPEND_HEAD) == null ? "" : doc.getElementById(JsoupUtil.APPEND_HEAD).html());
        formMap.put(JsoupUtil.APPEND_FOOT, doc.getElementById(JsoupUtil.APPEND_FOOT) == null ? "" : doc.getElementById(JsoupUtil.APPEND_FOOT).html());
        formMap.put(JsoupUtil.FORM_NAME, doc.title());
        formMap.put(JsoupUtil.FORM_NAME.toLowerCase(), doc.title());


        return formMap;
    }

    public   boolean existHtmlForm(String encodeListId) {
        if (StringUtils.isBlank(encodeListId)) {
            return false;
        }
        return formConfigMap.containsKey(encodeListId.toLowerCase());
    }

    public   List<Map<String, String>> getItemListByFormId(String encodeId) {
        
        Config config=formConfigMap.get(encodeId.toLowerCase());

        Element form=config.getDoc().body().getElementById("inputForm");
        List<Map<String, String>> list = new ArrayList<>();
        if (form == null || form.children().size() == 0) {
            return list;
        }
        Element configForm=config.getDoc().body().getElementById("configForm");
        Map<String,String> formCore=selectFormById(encodeId.toLowerCase());
        Elements formitemList=form.getElementsByClass("form-item");
        for (int x = 0; x < formitemList.size(); x++) {
            Element item=formitemList.get(x);
            Map<String, String> itemMap = new HashMap<>();
            //前台渲染的属性
            try {
                Utils.putIfAbsent(itemMap,"label", item.parent().parent().child(0).text());
            }catch (Exception e){
                e.printStackTrace();
            }
            if(StringUtils.isNotBlank(strip(item.attr("readonly")))){
                Utils.putIfAbsent(itemMap,"readonly", strip(item.attr("readonly")));

            }
            Utils.putIfAbsent(itemMap,JsoupUtil.LAYVERIFY , strip(item.attr("lay-verify")));
            Utils.putIfAbsent(itemMap,JsoupUtil.PLACEHOLDER, strip(item.attr(JsoupUtil.PLACEHOLDER)));


            for (int i = 0; i < names.length; i++) {
                Utils.putIfAbsent(itemMap,names[i], strip(item.attr(names[i])));
            }

            Utils.putIfAbsent(itemMap,"id", strip(item.attr("id")));
            //兼容
            Utils.putIfAbsent(itemMap,"item_url",strip(item.attr("item_url")));
            Utils.putIfAbsent(itemMap,"group_data", strip(Utils.trimNull(item.attr("group_data"))));
            //


            Map<String, String> listitem = JsoupUtil.loadplugin(item);

            listitem.putAll(itemMap);

            Utils.putIfAbsent(listitem,"ITEM_LABEL", item.parent().parent().child(0).text());
            Utils.putIfAbsent(listitem,"ITEM_LAYOUT", StringUtils.join(item.parent().classNames(), " "));
            Utils.putIfAbsent(listitem,"layout", StringUtils.join(item.parent().classNames(), " "));


             Utils.putIfAbsent(listitem,"item_id", item.attr("item_name"));


            String groupData=Utils.trimNull(item.attr("group_data"));
             Utils.putIfAbsent(listitem,"group_data",groupData);
            //用于编辑
            listitem.put("group_data_init", formCore.get("group_data"));
            Utils.putIfAbsent(listitem,JsoupUtil.NAME, item.attr("item_name"));
            Utils.putIfAbsent(listitem,JsoupUtil.COL, Utils.trimEmptyDefault(item.attr("col"),"12"));
            list.add(listitem);
        }
        return list;
    }





    public   int formSize(String page, String name, String url) {
        final AtomicInteger i = new AtomicInteger(0);
        for (Map.Entry<String, Config> entry:formConfigMap.entrySet()){
            Map<String,String> item=selectFormById(entry.getKey());
            if (StringUtils.isNotBlank(name) && StringUtils.isBlank(url)) {
                if (item.get(JsoupUtil.FORM_NAME).toLowerCase().indexOf(name.toLowerCase()) >= 0) {
                    i.getAndIncrement();
                }
            } else if (StringUtils.isBlank(name) && StringUtils.isNotBlank(url)) {
                if (item.get("ENCRYPT_FORM_ID").toLowerCase().indexOf(url.toLowerCase()) >= 0) {
                    i.getAndIncrement();
                }
            } else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(url)) {
                if (item.get(JsoupUtil.FORM_NAME.toLowerCase()).indexOf(name.toLowerCase()) >= 0 && item.get("ENCRYPT_FORM_ID").toLowerCase().indexOf(url.toLowerCase()) >= 0
                ) {
                    i.getAndIncrement();
                }
            } else {
                i.getAndIncrement();
            }
        } ;
        return i.get();
    }



      Element formitemByNameValue(Document doc,Element form,Element cardBody, String  label,
                                       String pluginCode,String name,String value ){
        Element formItem=null;
        //找到对应元素
        Element item = cardBody.selectFirst("["+JsoupUtil.ITEM_NAME+"='" + name + "']");
        if(item==null){
            //有可能是从其他分组挪过来的
            item=form.selectFirst("["+JsoupUtil.ITEM_NAME+"='" + name + "']");
            if(item!=null){
                cardBody.appendChild(item.parent().parent());
            }
        }
        if(item==null){
            formItem= doc.createElement("div");
            formItem.addClass("layui-form-item ");
            Element labelE = doc.createElement("label");
            labelE.addClass("layui-form-label").text(Utils.trimNull(label));

            Element block = doc.createElement("div");
            block.addClass("layui-input-block form-group");

            Element input = doc.createElement("object");
            input.addClass("form-item");
            input.attr(JsoupUtil.NAME,name);
            input.attr(JsoupUtil.ITEM_NAME,name);
            block.appendChild(input);
            item=input;
            formItem.appendChild(labelE).appendChild(block);
            cardBody.appendChild(formItem);
        }else{
            formItem=item.parent().parent();
        }
        dealElementByPluginCode(Utils.trimNull(pluginCode), item);
        if(item.tagName().equalsIgnoreCase("pre")){
            item.text(Utils.trimNull(value));
            item.addClass("layui-code");
        }else {
            item.attr("value", Utils.trimNull(value));
        }
        return formItem;
    }

    private   Element foot(Document doc,String appendHead, String append_head) {
        Element el=doc.getElementById(appendHead);
        if(doc.getElementById(appendHead)==null){
            Element item = doc.createElement("div");
            item.attr("id", appendHead);
            item.html(Utils.trimNull(append_head));
            doc.body().appendChild(item);
            return item;
        }else{
            el.html(Utils.trimNull(append_head));
        }
        return el;
    }
    private   Element head(Document doc,String appendHead, String append_head) {
        Element el=doc.getElementById(appendHead);
        if(doc.getElementById(appendHead)==null){
            Element item = doc.createElement("div");
            item.attr("id", appendHead);
            item.html(Utils.trimNull(append_head));
            doc.body().prependChild(item);
            return item;
        }else{
            el.html(Utils.trimNull(append_head));
            return el;
        }
    }

    public void clear() {
    }
    public   Map<String, Object> selectAllFormById(String formCode) {
        Map<String, Object> result=new HashMap<>();

        Config config=formConfigMap.get(formCode.toLowerCase());
        Document doc = config.getDoc();
        Element body = doc.body();
        Map<String, String> formMap = JsoupUtil.loadAttrNoChild(body);
        formMap.put("formcode", formCode);
        formMap.put("datasource", strip(body.attr("datasource")));
        formMap.put("success_url", strip(body.attr("success_url")));

        Element configForm=body.getElementById("configForm");
        if(configForm!=null){
            JsoupUtil.loadConfigByName(configForm, formMap,JsoupUtil.SUBMIT_EXPRESS);
            JsoupUtil.loadConfigByName(configForm, formMap,JsoupUtil.INIT_EXPRESS);
            JsoupUtil.loadConfigByName(configForm, formMap,JsoupUtil.DELETE_EXPRESS);
            JsoupUtil.loadConfigByName(configForm, formMap,JsoupUtil.STATUS_EXPRESS);
         }
        formMap.put(JsoupUtil.APPEND_HEAD, doc.getElementById(JsoupUtil.APPEND_HEAD) == null ? "" : doc.getElementById(JsoupUtil.APPEND_HEAD).html());
        formMap.put(JsoupUtil.APPEND_FOOT, doc.getElementById(JsoupUtil.APPEND_FOOT) == null ? "" : doc.getElementById(JsoupUtil.APPEND_FOOT).html());
        formMap.put(JsoupUtil.FORM_NAME, doc.title());
         formMap.put(JsoupUtil.FORM_NAME.toLowerCase(), doc.title());

        result.put("core",formMap);
        List<Map<String,Object>> cardsList=new ArrayList<>();
        result.put("cards",cardsList);


        Element inputForm=body.getElementById("inputForm");

        Elements cardColList= inputForm.select(".layui-card");
        if(cardColList!=null&&cardColList.size()>0){
            for (int i = 0; i < cardColList.size(); i++) {

                Map<String,Object> cardEl=new HashMap<>();
                cardEl.put("col",Utils.trimEmptyDefault(cardColList.get(i).parent().attr("col"),"12"));
                try {
                    cardEl.put("cardname", cardColList.get(i).selectFirst(".layui-card-header").html());
                }catch(Exception e){}

                List<Map<String,String>> formitemList=new ArrayList<>();

                Elements formitems=cardColList.get(i).select(".layui-form-item");
                for (int j = 0; j < formitems.size(); j++) {
                    Element label=formitems.get(j).selectFirst(".layui-form-label");
                    Element plugin=formitems.get(j).selectFirst("[item_name]");
                    if(plugin==null){
                        continue;
                    }
                    Map<String, String> attrMap = JsoupUtil.loadAttrNoChild(plugin);
                    if(StringUtils.isNotBlank(label.text())&&!StringUtils.equalsIgnoreCase(EZ_DEFAULT_GROUP,label.text())){
                        attrMap.put(JsoupUtil.LABEL,label.text());
                    }
                    attrMap.put(JsoupUtil.COL,Utils.trimEmptyDefault(formitems.get(j).parent().attr("col"),"12"));
                    attrMap.put(JsoupUtil.TYPE,JsoupUtil.getTypeByElement(plugin));
                    if(StringUtils.isBlank(attrMap.get(JsoupUtil.URL))){
                        attrMap.put(JsoupUtil.URL,plugin.attr("item_url"));
                    }
                    formitemList.add(attrMap);
                }
                if(Utils.isNotEmpty(formitemList)){
                    cardEl.put("items",formitemList);
                    cardsList.add(cardEl);
                }
            }
        }else{//兼容没有card的情况
            Map<String,Object> cardEl=new HashMap<>();
            cardEl.put("col","12");
            List<Map<String,String>> formitemList=new ArrayList<>();
            Elements formitems=inputForm.select(".layui-form-item");
            for (int j = 0; j < formitems.size(); j++) {
                Element label=formitems.get(j).selectFirst(".layui-form-label");

                Element plugin=formitems.get(j) .selectFirst("[item_name]");
                   if(plugin==null){
                       log.error("没有找到插件，节点：{}",formitems.get(j));
                       continue;
                   }
                Map<String, String> attrMap = JsoupUtil.loadAttrNoChild(plugin);
                if(StringUtils.isNotBlank(label.text())&&!StringUtils.equalsIgnoreCase(EZ_DEFAULT_GROUP,label.text())){
                    attrMap.put(JsoupUtil.LABEL,label.text());
                }
                attrMap.put(JsoupUtil.COL,formitems.get(j).parent().attr("col"));
                attrMap.put(JsoupUtil.TYPE,JsoupUtil.getTypeByElement(plugin));
                if(StringUtils.isBlank(attrMap.get(JsoupUtil.URL))){
                    attrMap.put(JsoupUtil.URL,plugin.attr("item_url"));
                }
                formitemList.add(attrMap);

            }
            if(Utils.isNotEmpty(formitemList)){
                cardEl.put("items",formitemList);
                cardsList.add(cardEl);
            }
        }

         return result;
    }

    public void updateForm(Map<String, Object> form) throws IOException {
        Map<String,Object> coreMap=(Map<String,Object>)form.get("core");

        String formcode= Utils.trimNull( coreMap.get("formcode"));
        String formname=  Utils.trimNull( coreMap.get(JsoupUtil.FORM_NAME.toLowerCase()));
        String successurl=Utils.trimNull( coreMap.get("successurl"));
        String datasource=Utils.trimNull( coreMap.get("datasource"));
        String initcode=  Utils.trimNull( coreMap.get("initcode"));
        String subcode=   Utils.trimNull( coreMap.get("subcode"));
        String delcode=   Utils.trimNull( coreMap.get("delcode"));
        String statuscode=   Utils.trimNull( coreMap.get("statuscode"));
        String appendhead=Utils.trimNull( coreMap.get(JsoupUtil.APPEND_HEAD));
        String appendfoot=Utils.trimNull( coreMap.get(JsoupUtil.APPEND_FOOT));


        Config config=null;
        if (config==null) {


            Document doc =  JsoupUtil.newform();

            String editPath=EzBootstrap.instance().getEditLocation()+File.separator+"form";
            editPath=editPath+(File.separator+formcode+".html");



            //创建新文件
            Config c=new Config();
            c.setFile(new File(editPath));
            c.setUrl(new File(editPath).toURI().toURL());
            c.setPath(new File(editPath).toURI().toURL().getPath());
            c.setProtocol("file");
            if(!new File(editPath).exists()){
                Files.createFile(Paths.get(editPath));
            }
            doc.body().attr("id",formcode.toLowerCase());
            c.setDoc(doc);
            formConfigMap.put(formcode.toLowerCase(),c);
            // stream.close();
            config=c;
        }
        Document doc=config.getDoc();
        Element body = doc.body();
        //处理主体
        body.attr("id", formcode);
        body.attr(JsoupUtil.DATASOURCE,datasource);
        body.attr(JsoupUtil.SUCCESS_URL, successurl);
        Element configForm= doc.getElementById("configForm");
        configForm.selectFirst("[item_name=init_express]").html(initcode);
        configForm.selectFirst("[item_name=submit_express]").html(subcode);
        configForm.selectFirst("[item_name=delete_express]").html(delcode);
        configForm.selectFirst("[item_name=status_express]").html(statuscode);
        doc.title(formname);
        head(doc,JsoupUtil.APPEND_HEAD,appendhead);
        foot(doc,JsoupUtil.APPEND_FOOT,appendfoot);
        //处理元素
        Element inputForm=doc.getElementById("inputForm");

        List<Map<String,Object>> cardsList=(List<Map<String,Object>>)form.get("cards");
        if(Utils.isNotEmpty(cardsList)){
            for (int i = 0; i < cardsList.size(); i++) {
                Map<String,Object> card=cardsList.get(i);
                String cardName=Utils.trimNull(card.get("cardname"));
                String col=Utils.trimNull(card.get("col"));
                Element newcardElement=newCard(cardName,col);
                List<Map<String,Object>> formItemList=(List<Map<String,Object>>)card.get("items");
                for (int j = 0; j < formItemList.size(); j++) {
                    Map<String,Object> formItem=formItemList.get(j);
                    String formitemcol=Utils.trimEmptyDefault(formItem.get("col"),"12");
                    Element formItemHtmlElement=newFormItem(Utils.trimNull(formItem.get(JsoupUtil.LABEL)),formitemcol);
                    for (int k = 0; k < names.length; k++) {
                        String formItemAttrValue=Utils.trimNull(formItem.get(names[k]));
                        if(StringUtils.isNotBlank(formItemAttrValue)){
                            formItemHtmlElement.getElementsByTag("object").attr(names[k],formItemAttrValue);
                        }
                    }
                     newcardElement.selectFirst(".layui-card-body")
                            .appendChild(formItemHtmlElement);
                }
                inputForm.appendChild(newcardElement);
            }
        }
        updateConfig(config);
    }


    Element newCard(String label,String col){
        StringBuilder sb=new StringBuilder();
        sb.append(" <div class=\"cardcol layui-col-md"+col+"\" col='"+col+"'  ><div class=\"layui-card\"  >\n");
        if(StringUtils.isNotBlank(label)&&!StringUtils.equalsIgnoreCase(EZ_DEFAULT_GROUP,label)){
            sb.append(  " <div class=\"layui-card-header\">\n" +label +"  </div>\n" );
        }
        sb.append(  " <div class=\"layui-card-body\"> </div>\n" );
        sb.append("</div></div>");
        return Jsoup.parse(sb.toString()).body().child(0);
    }
    Element newFormItem(String label,String col){
        return Jsoup.parse("\n<div class=\"layui-col-space10  layui-col-md"+col+"\"  col='"+col+"'   >\n" +
                "                    <div class=\"layui-form-item \">\n" +
                "                        <label class=\"layui-form-label  \">"+label+"</label>\n" +
                "                        <div class=\"layui-input-block\">\n" +
                "                            <object   >\n" +
                "                            </object>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n").body().child(0);
    }

}
