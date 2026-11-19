package top.ezadmin.plugins.templates;

import top.ezadmin.EzBootstrapConfig;

import java.util.Map;

public interface EzTemplate {
    void init(EzBootstrapConfig config);

    String renderString(String content, Map<String, Object> data);

    String renderFile(String path, Map<String, Object> data);

    boolean clearCache();
}
