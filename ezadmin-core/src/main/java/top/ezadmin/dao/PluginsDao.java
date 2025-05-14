package top.ezadmin.dao;

import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.web.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class PluginsDao {
    public static final Logger log = LoggerFactory.getLogger(PluginsDao.class);

    private static PluginsDao dao = new PluginsDao();

    private static Map<String, Config> pluginsAllConfigMap=new HashMap();


    private PluginsDao() {

    }

    public static PluginsDao getInstance() {
        return dao;
    }

    public   void init(){
        loadPlugins();
    }


    private static Map<String, String> docToPluginMap(Document doc){
        Map<String, String> p = new HashMap<>();
        String id = doc.body().id().toLowerCase();
        String type = doc.body().attr("type");
        if (doc.getElementById("content") != null) {
            p.put("PLUGIN_BODY", doc.getElementById("content").html());
        } else {
            p.put("PLUGIN_BODY", doc.body().html());
        }
        if(doc.getElementById("template")!=null){
            p.put("TEMPLATE",doc.getElementById("template").html());
        }else{
            p.put("TEMPLATE","");

        }
        p.put("PLUGIN_NAME", doc.title());
        p.put("V", doc.title());
        p.put("ICON", Utils.trimEmptyDefault(doc.body().attr("icon"),"layui-icon-app") );
        p.put("PLUGIN_TYPE", type);
        p.put("PLUGIN_CODE", id);
        p.put("K", id);
        return p;
    }

    public   List<Map<String, Object>> listPlugin(String pre) {
        List<Map<String, Object>> l = new ArrayList<>();

        return l;
    }

    public   List<Map<String, Object>> allFormPlugin() {
        String key="layui_form";
        List<Config>  configList = pluginsAllConfigMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(key))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        List<Map<String, Object>> l = new ArrayList<>();

        for (int i = 0; i < configList.size(); i++) {
            Map  v=docToPluginMap(configList.get(i).getDoc());
            l.add(v);
        }
        return l;
    }
    List<Map<String, Object>> LIST_PLUGINS=new ArrayList<>();
    public   List<Map<String, Object>> allListPlugin(String type) {
        String key="layui_list_";
        List<Config>  configList = pluginsAllConfigMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(key))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        List<Map<String, Object>> l = new ArrayList<>();
        for (int i = 0; i < configList.size(); i++) {
            Map  v=docToPluginMap(configList.get(i).getDoc());
            if(StringUtils.equals(v.get("PLUGIN_TYPE")+"",type)){
                l.add(v);
            }
        }
        return l;
    }



    private   void loadPlugins() {
        for (int i = 0; i < EzClientBootstrap.instance().getPluginsFormConfigResources().size(); i++) {
            explainConfig(EzClientBootstrap.instance().getPluginsFormConfigResources().get(i),pluginsAllConfigMap);
        }
        for (int i = 0; i < EzClientBootstrap.instance().getPluginsListConfigResources().size(); i++) {
            explainConfig(EzClientBootstrap.instance().getPluginsListConfigResources().get(i),pluginsAllConfigMap);
        }
        for (int i = 0; i < EzClientBootstrap.instance().getPluginsDetailConfigResources().size(); i++) {
            explainConfig(EzClientBootstrap.instance().getPluginsDetailConfigResources().get(i),pluginsAllConfigMap);
        }
    }

    private  void explainConfig(Config item, Map<String, Config> configMap  ){
        try {
            InputStream stream = null;
            if(item.isJar()){
                stream=item.getIn();
                Document doc = Jsoup.parse(stream, "UTF-8", "");
                item.setDoc(doc);
                configMap.put(doc.body().id(),item);
                configMap.put(doc.body().attr("alias"),item);
                configMap.put(doc.body().attr("alias2"),item);
                String[] path=item.getPath().substring(item.getPath().indexOf("topezadmin/config")+"topezadmin/config".length()).split("/");
                configMap.put(path[1]+"_"+path[3]+"_"+doc.body().id(),item);
                configMap.put(path[1]+"_"+path[3]+"_"+doc.body().attr("alias"),item);
                configMap.put(path[1]+"_"+path[3]+"_"+doc.body().attr("alias2"),item);
                //jar包中的流确保只用一次，初始化之后就关闭流
                stream.close();
            }else{
                stream=new FileInputStream(item.getFile());
                Document doc = Jsoup.parse(stream, "UTF-8", "");
                item.setDoc(doc);
                configMap.put(doc.body().id(),item);
                configMap.put(doc.body().attr("alias"),item);
                configMap.put(doc.body().attr("alias2"),item);
                String[] path=item.getPath().substring(item.getPath().indexOf("topezadmin/config")+"topezadmin/config".length()).split("/");
                configMap.put(path[1]+"_"+path[3]+"_"+doc.body().id(),item);
                configMap.put(path[1]+"_"+path[3]+"_"+doc.body().attr("alias"),item);
                configMap.put(path[1]+"_"+path[3]+"_"+doc.body().attr("alias2"),item);

                stream.close();
            }
        }catch (Exception e){
            //jar   包中的 无需重新加载
            throw new RuntimeException(e);
        }
    }


    public Map<String, String> getPlugin(String adminstyle, String fold, String pluginCode) {

           String key = adminstyle + "_" + fold + "_" + pluginCode;
           Config config = pluginsAllConfigMap.get(key);
           if(config==null){
               log.info("null plugin"+key);
               return new HashMap<>();
           }
           Document doc = config.getDoc();
           return docToPluginMap(doc);

    }

    public void loadPluginFile(File file)  throws  Exception{
        Config item=new Config();
        Document doc = Jsoup.parse(file, "UTF-8", "");
        item.setUrl(file.toURI().toURL());
        item.setPath(file.toURI().toURL().getPath());
        item.setProtocol("file");
        item.setDoc(doc);
        item.setFile(file);

        String key = "layui_list_" + doc.body().id().toLowerCase();
        if(pluginsAllConfigMap.containsKey(key)){
            pluginsAllConfigMap.put(key,item);
        }
        key = "layui_form_" + doc.body().id().toLowerCase();
        if(pluginsAllConfigMap.containsKey(key)){
            pluginsAllConfigMap.put(key,item);
        }
    }
}
