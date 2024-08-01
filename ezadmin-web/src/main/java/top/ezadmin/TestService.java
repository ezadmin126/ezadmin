package top.ezadmin;

import org.springframework.stereotype.Service;
import top.ezadmin.web.EzResult;

import java.util.Map;

@Service("testService")
public class TestService {
    public EzResult test(Map<String, Object> request) {
        EzResult result = EzResult.instance();
        result.dataMap("V1", EzResult.instance().fail().message("不存在"));
        result.dataMap("V2", EzResult.instance());
        return result;
    }
}