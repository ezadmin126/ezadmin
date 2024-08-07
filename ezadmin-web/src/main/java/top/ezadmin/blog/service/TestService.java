package top.ezadmin.blog.service;


import org.springframework.stereotype.Service;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.plugins.imports.EzImport;
import top.ezadmin.web.EzResult;

import java.util.Map;

@Service("testService")
public class TestService implements EzImport {


    public EzResult test(Map<String, Object> request) {
        System.out.println(JSONUtils.toJSONString(request));
        EzResult result = EzResult.instance();
        result.dataMap("V1", EzResult.instance().fail().message("不存在"));
        result.dataMap("V2", EzResult.instance());
        return result;
    }

    @Override
    public Object doImport(Map<String, Object> request) {
        EzResult result = EzResult.instance();
        result.dataMap("V1", EzResult.instance().fail().message("不存在"));
        result.dataMap("V2", EzResult.instance());
        return result;
    }
}
