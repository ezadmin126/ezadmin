package io.github.ezadmin126;

import com.alibaba.fastjson.JSON;
import io.github.ezadmin126.spring.ConfigUtils;
import org.junit.Test;
import io.github.ezadmin126.web.EzResult;
import io.github.ezadmin126.web.RequestContext;

import java.io.IOException;

public class ListTest {

    @Test
    public void hello() throws IOException {
        EzBootstrapConfig ezBootstrapConfig = new EzBootstrapConfig();
        String listResourceLocation = "classpath*:/topezadmin/config/layui/list/**/*.html";
        String formResourceLocation = "classpath*:/topezadmin/config/layui/form/**/*.html";
        String pluginsFormResourceLocation = "classpath*:/topezadmin/config/layui/plugins/form/**/*.html";
        String pluginsListResourceLocation = "classpath*:/topezadmin/config/layui/plugins/list/**/*.html";
        ezBootstrapConfig.setListConfigResources(ConfigUtils.loadFiles(listResourceLocation));
        ezBootstrapConfig.setFormConfigResources(ConfigUtils.loadFiles(formResourceLocation));
        ezBootstrapConfig.setPluginsFormConfigResources(ConfigUtils.loadFiles(pluginsFormResourceLocation));
        ezBootstrapConfig.setPluginsListConfigResources(ConfigUtils.loadFiles(pluginsListResourceLocation));
        EzBootstrap.getInstance().init(ezBootstrapConfig);
        RequestContext requestContext = new RequestContext();
        requestContext.setRequestURI("/topezadmin/list/list-listall");
        EzResult result=EzBootstrap.getInstance().generate(requestContext);
        System.out.println(JSON.toJSONString(result));
    }
}
