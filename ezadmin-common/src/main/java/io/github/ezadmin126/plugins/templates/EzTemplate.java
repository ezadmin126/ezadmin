package io.github.ezadmin126.plugins.templates;

import io.github.ezadmin126.EzBootstrapConfig;

import java.util.Map;

public interface EzTemplate {
    void init(EzBootstrapConfig config);
    String renderString(String content, Map<String,Object> data);
    String renderFile(String path, Map<String,Object> data);
    boolean clearCache();
}
