package top.ezadmin.dao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.utils.JsoupUtil;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.web.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormDao extends JsoupUtil {

    public static final Logger log = LoggerFactory.getLogger(FormDao.class);

    private static FormDao dao = new FormDao();
    private static Map<String, Config> formConfigMap=new HashMap();
    private static   String [] names=new String[]{ JsoupUtil.VALIDATERULES,JsoupUtil.VALIDATEMESSAGES,JsoupUtil.DATATYPE,
            JsoupUtil.OPENTYPE,JsoupUtil.WINDOW_NAME,JsoupUtil.URL,
            JsoupUtil.DATA,JsoupUtil.JDBCTYPE,JsoupUtil.PLACEHOLDER,JsoupUtil.LAYVERIFY
            , JsoupUtil.NAME,
            JsoupUtil.ITEM_NAME, JsoupUtil.ITEM_ID,
            JsoupUtil.OPER
            ,JsoupUtil.STYLE
            ,JsoupUtil.MULTI,JsoupUtil.COLLAPSETAGS,JsoupUtil.SHOWALLLEVELS
            ,JsoupUtil.TOP_DESC,JsoupUtil.ITEM_DESC,JsoupUtil.RIGHT_DESC
            ,JsoupUtil.ALIAS,JsoupUtil.ALIGN,JsoupUtil.HELP,JsoupUtil.TYPE,JsoupUtil.COL
            ,JsoupUtil.CLASS,"readonly","disabled",JsoupUtil.MAXLENGTH
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
            for (int i = 0; i < EzClientBootstrap.instance().getFormConfigResources().size(); i++) {
                Config  item= EzClientBootstrap.instance().getFormConfigResources().get(i);
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
    public String eleToString(Element e,String tag){
        Map<String,String> m=JsoupUtil.attr2Map(e);
        // m.putIfAbsent("class","layui-bg-gray");
        m.putIfAbsent(JsoupUtil.EZCONFIG,JsoupUtil.attr2Json(e));
        StringBuilder sb=new StringBuilder();
        m.forEach((k,v)->{
            sb.append(k);
            sb.append("='");
            sb.append(v);
            sb.append("'  ");
        });
        return "<"+tag+"  "+sb.toString()+">" ;
    }
    public Map<String, Object> selectAllFormByHtml(String ezConfig ) {
        Map<String, Object> result=new HashMap<>();
        Document doc=Jsoup.parse(ezConfig);
        Element body = doc.body();
        Map<String, String> formMap = JsoupUtil.loadAttrNoChild(body);
        formMap.put("formcode", body.attr("id"));
        formMap.put("datasource", strip(body.attr("datasource")));
        formMap.put(JsoupUtil.SUCCESS_URL, strip(body.attr(JsoupUtil.SUCCESS_URL)));

        formMap.put("bodyTag",eleToString(doc.body(),"body"));
        formMap.put(JsoupUtil.EZCONFIG,JsoupUtil.attr2Json(doc.body()));


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

        //adminstyle

        Utils.putIfAbsent(formMap,JsoupUtil.ADMINSTYLE,EzClientBootstrap.instance().getAdminStyle());

        formMap.put("formSubmitUrl",body.attr("formSubmitUrl"));


        result.put("core",formMap);
        List<Map<String,Object>> cardsList=new ArrayList<>();
        result.put("cards",cardsList);


        Element inputForm=body.getElementById("inputForm");

        Elements cardColList= inputForm.select(".layui-card");
        if(cardColList!=null&&cardColList.size()>0){
            for (int i = 0; i < cardColList.size(); i++) {

                Map<String,Object> cardEl=new HashMap<>();
                cardEl.put("col",Utils.trimEmptyDefault(cardColList.get(i).attr("col"),"12"));
                cardEl.put(JsoupUtil.ITEM_NAME,Utils.trimNull(cardColList.get(i).attr(JsoupUtil.ITEM_NAME)));
                if(StringUtils.isNotBlank(Utils.trimNull(cardColList.get(i).attr(JsoupUtil.FRAMEURL)))){
                    cardEl.put(JsoupUtil.FRAMEURL,Utils.trimNull(cardColList.get(i).attr(JsoupUtil.FRAMEURL)));
                    cardEl.put(JsoupUtil.FRAMEHEIGHT,Utils.trimEmptyDefault(cardColList.get(i).attr(JsoupUtil.FRAMEHEIGHT),"527"));
                }

                if(StringUtils.isNotBlank(Utils.trimNull(cardColList.get(i).attr(JsoupUtil.FORM_CARD_BTN_NAME)))){
                    cardEl.put(JsoupUtil.FORM_CARD_BTN_NAME,Utils.trimNull(cardColList.get(i).attr(JsoupUtil.FORM_CARD_BTN_NAME)));
                    cardEl.put(JsoupUtil.FORM_CARD_BTN_URL,Utils.trimNull(cardColList.get(i).attr(JsoupUtil.FORM_CARD_BTN_URL)));
                    cardEl.put("btnopentype",Utils.trimNull(cardColList.get(i).attr("btnopentype")));
                }
                Element element=cardColList.get(i).selectFirst(".layui-card-button");
                if(element!=null){
                    cardEl.put("btntemplate",element.html());
                }
                try {
                    cardEl.put("cardname", cardColList.get(i).selectFirst(".layui-card-header").html());
                }catch(Exception e){}

                List<Map<String,Object>> formitemList=new ArrayList<>();

                Elements formitems=cardColList.get(i).select(".layui-form-item");
                for (int j = 0; j < formitems.size(); j++) {
                    Element label=formitems.get(j).selectFirst("label");
                    Element plugin=formitems.get(j).selectFirst("[item_name]");
                    if(plugin==null){
                        continue;
                    }
                     Map<String, Object> attrMap =new HashMap<>();
                    //JsoupUtil.loadAttrNoChild(plugin);
                    if(label!=null&& StringUtils.isNotBlank(label.text())&&!StringUtils.equalsIgnoreCase(EZ_DEFAULT_GROUP,label.text())){
                        attrMap.put(JsoupUtil.LABEL,label.text());
                    }
                    attrMap.put(JsoupUtil.COL,Utils.trimEmptyDefault(plugin.attr("col"),formitems.get(j).parent().attr("col"),"12"));
                    attrMap.put(JsoupUtil.TYPE,JsoupUtil.getTypeByElement(plugin));
                    attrMap.put(JsoupUtil.ITEM_ID,plugin.attr(JsoupUtil.ITEM_NAME).replaceAll(",","_"));
//                    if(StringUtils.isBlank(attrMap.get(JsoupUtil.URL))){
//                        attrMap.put(JsoupUtil.URL,plugin.attr("item_url"));
//                    }
                    Map<String,String>  formAttrMap=JsoupUtil.attr2Map(plugin);
                    attrMap.put("attrMap",formAttrMap);
                    formAttrMap.forEach((k,v)->{
                        attrMap.putIfAbsent(k,v);
                    });
                    attrMap.put(JsoupUtil.EZCONFIG,JsoupUtil.attr2Json(plugin));
                    formitemList.add(attrMap);
                }
              //  if(Utils.isNotEmpty(formitemList)){
                    cardEl.put("items",formitemList);
                    cardsList.add(cardEl);
              //  }
            }
        }else{//兼容没有card的情况
            Map<String,Object> cardEl=new HashMap<>();
            cardEl.put("col","12");
            List<Map<String,Object>> formitemList=new ArrayList<>();
            Elements formitems=inputForm.select(".layui-form-item");
            for (int j = 0; j < formitems.size(); j++) {
                Element label=formitems.get(j).selectFirst(".layui-form-label");

                Element plugin=formitems.get(j) .selectFirst("[item_name]");
                if(plugin==null){
                    log.error("没有找到插件，节点：{}",formitems.get(j));
                    continue;
                }
                Map<String, Object> attrMap =new HashMap<>();
                // JsoupUtil.loadAttrNoChild(plugin);
                if(StringUtils.isNotBlank(label.text())&&!StringUtils.equalsIgnoreCase(EZ_DEFAULT_GROUP,label.text())){
                    attrMap.put(JsoupUtil.LABEL,label.text());
                }
                attrMap.put(JsoupUtil.COL,Utils.trimEmptyDefault(plugin.attr("col"),formitems.get(j).parent().attr("col"),"12"));
                attrMap.put(JsoupUtil.TYPE,JsoupUtil.getTypeByElement(plugin));

                Map<String,String>  formAttrMap=JsoupUtil.attr2Map(plugin);
                formAttrMap.forEach((k,v)->{
                    attrMap.putIfAbsent(k,v);
                });
                attrMap.put("attrMap",formAttrMap);
                attrMap.put(JsoupUtil.EZCONFIG,JsoupUtil.attr2Json(plugin));
                formitemList.add(attrMap);

            }
            if(Utils.isNotEmpty(formitemList)){
                cardEl.put("items",formitemList);
                cardsList.add(cardEl);
            }
        }

        return result;
    }
    public   Map<String, Object> selectAllFormById(String formCode) {

        Config config=formConfigMap.get(formCode.toLowerCase());
        if(config==null){
            return null;
        }
        Document doc = config.getDoc();
        return selectAllFormByHtml(doc.html());
    }

    public String updateForm(Map<String, Object> form) throws IOException {
        Map<String,Object> coreMap=(Map<String,Object>)form.get("core");

        String formcode= Utils.trimNull( coreMap.get("formcode"));
        String formname=  Utils.trimNull( coreMap.get(JsoupUtil.FORM_NAME.toLowerCase()));
        String successurl=Utils.trimNull( coreMap.get(JsoupUtil.SUCCESS_URL));
        String datasource=Utils.trimNull( coreMap.get("datasource"));
        String initcode=  Utils.trimNull( coreMap.get(JsoupUtil.INIT_EXPRESS));
        String subcode=   Utils.trimNull( coreMap.get(JsoupUtil.SUBMIT_EXPRESS));
        String delcode=   Utils.trimNull( coreMap.get(JsoupUtil.DELETE_EXPRESS));
        String statuscode=   Utils.trimNull( coreMap.get(JsoupUtil.STATUS_EXPRESS));
        String appendhead=Utils.trimNull( coreMap.get(JsoupUtil.APPEND_HEAD));
        String appendfoot=Utils.trimNull( coreMap.get(JsoupUtil.APPEND_FOOT));
        String formSubmitUrl=Utils.trimNull( coreMap.get("formsubmiturl"));

        if(initcode.indexOf("<![CDATA[")==-1){
            initcode="\n<![CDATA[ \n"+ initcode+"\n]]>\n";
        }
        if(subcode.indexOf("<![CDATA[")==-1){
            subcode="\n<![CDATA[ \n"+ subcode+"\n]]>\n";
        }
        if(delcode.indexOf("<![CDATA[")==-1){
            delcode="\n<![CDATA[ \n"+ delcode+"\n]]>\n";
        }
        if(statuscode.indexOf("<![CDATA[")==-1){
            statuscode="\n<![CDATA[ \n"+ statuscode+"\n]]>\n";
        }



        Document doc =  JsoupUtil.newform();
        Element body = doc.body();


        String ezconfig=Utils.trimNull(coreMap.get(JsoupUtil.EZCONFIG));
        //把json赋值给body,前端需要组装json
        JsoupUtil.json2Attr(body,ezconfig);

        body.attr("id",StringUtils.lowerCase(formcode) );
        //处理主体
        body.attr(JsoupUtil.DATASOURCE,datasource);
        body.attr(JsoupUtil.SUCCESS_URL, successurl);
        body.attr("formSubmitUrl", formSubmitUrl);
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
                Element newcardElement=newCard(card);
                List<Map<String,Object>> formItemList=(List<Map<String,Object>>)card.get("items");
                for (int j = 0; j < formItemList.size(); j++) {
                    Map<String,Object> formItem=formItemList.get(j);
                    String formitemcol=Utils.trimEmptyDefault(formItem.get("col"),"12");
                    Element formItemHtmlElement=newFormItem(Utils.trimNull(formItem.get(JsoupUtil.LABEL)),formitemcol);

                    Map<String,String>  formAttrMap=(Map<String,String>)formItem.get("attrMap");
                    if(formAttrMap!=null){
                        formAttrMap.put("id","itemId-"+formAttrMap.get(JsoupUtil.ITEM_NAME));
                        formAttrMap.forEach((k,v)->{
                            if(StringUtils.equals(k,JsoupUtil.LABEL)
                            ||StringUtils.equals(k,"lay-affix")
                            ||StringUtils.equals(k,JsoupUtil.NAME)
                            ){
                                return;
                            }
                          //  String formItemAttrValue=Utils.trimNull(formItem.get(names[k]));
                            if(StringUtils.isNotBlank(v)){
                                formItemHtmlElement.getElementsByTag("object").attr(k,v);
                            }
                            if(k.equalsIgnoreCase("readonly")||k.equalsIgnoreCase("disabled")){
                                formItemHtmlElement.getElementsByTag("object").attr(k,v);
                            }
                        });
                    }else{
                        formItem.forEach((k,v)->{
                            if(StringUtils.isNotBlank(Utils.trimNull(v))
                            &&!StringUtils.equals(k,JsoupUtil.LABEL)
                            &&!StringUtils.equals(k,JsoupUtil.COL)
                            ){
                                formItemHtmlElement.getElementsByTag("object").attr(k,Utils.trimNull(v));
                            }
                        });
                    }

                    formItemHtmlElement.getElementsByTag("object").attr(JsoupUtil.ITEM_NAME,Utils.trimNull(formItem.get(JsoupUtil.ITEM_NAME)));




                     newcardElement.selectFirst(".layui-card-body")
                            .append("\n"+formItemHtmlElement.outerHtml()+"\n");
                }
                inputForm.appendChild(newcardElement);
            }
        }
        doc.outputSettings().prettyPrint(true).outline(true).escapeMode();
        String html=doc.html();
        EzClientBootstrap.instance().getEzCache().clear();
       return html;
    }


    Element newCard( Map<String,Object> card){
        //String label,String col,String url,String frameheight
        String cardName=Utils.trimNull(card.get("cardname"));
        String frameurl=Utils.trimNull(card.get("frameurl"));
        String frameheight=Utils.trimNull(card.get("frameheight"));
        String btnname=Utils.trimNull(card.get("btnname"));
        String item_name=Utils.trimNull(card.get("item_name"));
        String btnurl=Utils.trimNull(card.get("btnurl"));
        String btnopentype=Utils.trimNull(card.get("btnopentype"));
        String btntemplate=Utils.trimNull(card.get("btntemplate"));
        String col=Utils.trimNull(card.get("col"));
        StringBuilder sb=new StringBuilder();


        if(col.equals("12")||StringUtils.isBlank(col)){
            sb.append("\n\t<div   class='layui-card'  cardname='"+cardName+"' item_name='"+item_name+"'");
        }else{
            sb.append("\n\t<div   class='layui-card'  col='"+col+"' cardname='"+cardName+"' item_name='"+item_name+"'");
        }
        if(StringUtils.isNotBlank(frameurl)){
            sb.append(" frameurl='"+frameurl+"' frameheight='"+frameheight+"'" );
        }
        if(StringUtils.isNotBlank(btnname)){
            sb.append(" btnname='"+btnname+"' btnurl='"+btnurl+"' btnopentype='" +btnopentype+"'" );
        }
//        if(StringUtils.isNotBlank(btntemplate)){
//            sb.append(" btntemplate='"+btntemplate+"'  " );
//        }
        sb.append("        > \n");
        if(StringUtils.isNotBlank(cardName)&&!StringUtils.equalsIgnoreCase(EZ_DEFAULT_GROUP,cardName)){
            sb.append(  "\t\t<div class=\"layui-card-header\">\n" +cardName +"  </div>\n" );
        }
        sb.append(  " \t\t<div class=\"layui-card-body\"> </div>\n" );
        sb.append(  " \t\t<div class=\"layui-card-button\">"+btntemplate+" </div>\n" );
        sb.append("\t</div>\n\n");
        return Jsoup.parse(sb.toString()).body().child(0);
    }
    Element newFormItem(String label,String col){
        StringBuilder sb=new StringBuilder( );
        if(col.equals("12")||StringUtils.isNotBlank(col)){
            sb.append("\n\t<div  class='layui-form-item'  ");
        }else{
            sb.append("\n\t<div  class='layui-form-item'  col='"+col+"'");
        }
        sb.append(   "                        <label >"+label+"</label>\n" +
                "                        <div  >\n" +
                "                            <object   >\n" +
                "                            </object>\n" +
                "                        </div>\n" +

                "                </div>\n");
        return Jsoup.parse(sb.toString()).body().child(0);
    }


}
