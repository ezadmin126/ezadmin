package com.ezadmin.biz.dao;

import com.ezadmin.EzBootstrap;
import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.web.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class PluginsDao {
    public static final Logger log = LoggerFactory.getLogger(PluginsDao.class);

    private static PluginsDao dao = new PluginsDao();

    /**
     * key:plugincode
     * value:config
     */
    private static Map<String, Config> pluginsFormConfigMap=new HashMap();
    private static Map<String, Config> pluginsListConfigMap=new HashMap();
    private static Map<String, Config> pluginsDetailConfigMap=new HashMap();

    /**
     * key:plugintype
     * value:List-config
     */
    private static Map<String, List<Config>> pluginsFormTypeConfigMap=new HashMap();
    private static Map<String, List<Config>> pluginsDetailTypeConfigMap=new HashMap();
    private static Map<String, List<Config>> pluginsListTypeConfigMap=new HashMap();

    private PluginsDao() {

    }

    public static PluginsDao getInstance() {
        return dao;
    }

    public   void init(){
        loadPlugins();
    }
    public   Map<String, String> getDbTemplateByCode(String code, String fold) {
        Config config=null;
        if (StringUtils.equalsIgnoreCase(fold, "list")) {
              config= pluginsListConfigMap.get(code);
        }else if (StringUtils.equalsIgnoreCase(fold, "form")) {
                config= pluginsFormConfigMap.get(code);
        }else{
            config= pluginsDetailConfigMap.get(code);
        }
        if(config==null){
            log.error("plugin is null : {} {}",code,fold);
            return Collections.emptyMap();
        }
        Document doc =config.getDoc()  ;

        return docToPluginMap(doc);
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
        for (Map.Entry<String, List<Config>> entry:pluginsListTypeConfigMap.entrySet()){
            List<Config> configList=entry.getValue();
            if(StringUtils.isBlank(pre)){
                for (int i = 0; i < configList.size(); i++) {
                    Map  v=docToPluginMap(configList.get(i).getDoc());
                    Map<String, Object> m = new HashMap<>();
                    m.put("K", v.get("PLUGIN_CODE"));
                    m.put("V", v.get("PLUGIN_NAME"));
                    l.add(m);
                }
            } else{
                if(StringUtils.equalsIgnoreCase(entry.getKey(),pre)){
                    for (int i = 0; i < configList.size(); i++) {
                        Map  v=docToPluginMap(configList.get(i).getDoc());
                        Map<String, Object> m = new HashMap<>();
                        m.put("K", v.get("PLUGIN_CODE"));
                        m.put("V", v.get("PLUGIN_NAME"));
                        l.add(m);
                    }
                }
            }
        }

        return l;
    }

    public   List<Map<String, Object>> allFormPlugin() {
        List<Config> configList=pluginsFormTypeConfigMap.get("form");
        List<Map<String, Object>> l = new ArrayList<>();

        for (int i = 0; i < configList.size(); i++) {
            Map  v=docToPluginMap(configList.get(i).getDoc());
            l.add(v);
        }
        return l;
    }
    public   List<Map<String, Object>> allListPlugin(String type) {
        List<Config> configList=pluginsListTypeConfigMap.get(type);
        List<Map<String, Object>> l = new ArrayList<>();
        for (int i = 0; i < configList.size(); i++) {
            Map  v=docToPluginMap(configList.get(i).getDoc());
            l.add(v);
        }
        return l;
    }
    public   List<Map<String, Object>> formPlugin(String pre) {
        List<Map<String, Object>> l = new ArrayList<>();
        for (Map.Entry<String, List<Config>> entry:pluginsFormTypeConfigMap.entrySet()){
            List<Config> configList=entry.getValue();
            if(StringUtils.isBlank(pre)){
                for (int i = 0; i < configList.size(); i++) {
                    Map  v=docToPluginMap(configList.get(i).getDoc());
                    Map<String, Object> m = new HashMap<>();
                    m.put("K", v.get("PLUGIN_CODE"));
                    m.put("V", v.get("PLUGIN_NAME"));
                    l.add(m);
                }
            } else{
                if(StringUtils.equalsIgnoreCase(entry.getKey(),pre)){
                    for (int i = 0; i < configList.size(); i++) {
                        Map  v=docToPluginMap(configList.get(i).getDoc());
                        Map<String, Object> m = new HashMap<>();
                        m.put("K", v.get("PLUGIN_CODE"));
                        m.put("V", v.get("PLUGIN_NAME"));
                        l.add(m);
                    }
                }
            }
        }
        return l;
    }

    private   void loadPlugins() {
        for (int i = 0; i < EzBootstrap.instance().getPluginsFormConfigResources().size(); i++) {
            itemToMap(EzBootstrap.instance().getPluginsFormConfigResources().get(i),pluginsFormConfigMap,pluginsFormTypeConfigMap);
        }
        for (int i = 0; i < EzBootstrap.instance().getPluginsListConfigResources().size(); i++) {
            itemToMap(EzBootstrap.instance().getPluginsListConfigResources().get(i),pluginsListConfigMap,pluginsListTypeConfigMap);
        }
        for (int i = 0; i < EzBootstrap.instance().getPluginsDetailConfigResources().size(); i++) {
            itemToMap(EzBootstrap.instance().getPluginsDetailConfigResources().get(i),pluginsDetailConfigMap,pluginsDetailTypeConfigMap);
        }
    }
    private   void itemToMap(Config item, Map<String, Config> configMap,Map<String, List<Config>> pluginsTypeConfigMap){
        try {
            InputStream stream = null;
            if(item.isJar()){
                stream=item.getIn();
                Document doc = Jsoup.parse(stream, "UTF-8", "");
                item.setDoc(doc);
                configMap.put(doc.body().id(),item);
                configMap.put(doc.body().attr("alias"),item);
                //jar包中的流确保只用一次，初始化之后就关闭流
                stream.close();
                addPluginType(doc.body().attr("type"),item,pluginsTypeConfigMap);
            }else{
                stream=new FileInputStream(item.getFile());
                Document doc = Jsoup.parse(stream, "UTF-8", "");
                item.setDoc(doc);
                configMap.put(doc.body().id(),item);
                configMap.put(doc.body().attr("alias"),item);
                stream.close();
                addPluginType(doc.body().attr("type"),item,pluginsTypeConfigMap);
            }
        }catch (Exception e){
            //jar   包中的 无需重新加载
            throw new RuntimeException(e);
        }
    }
    private   void addPluginType(String type,Config config,Map<String, List<Config>> pluginsTypeConfigMap){
        type=type.toLowerCase();
        if(!pluginsTypeConfigMap.containsKey(type )){
            pluginsTypeConfigMap.put(type,new ArrayList<Config>());
        }
        pluginsTypeConfigMap.get(type).add(config);
    }

}
