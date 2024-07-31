package top.ezadmin.blog.service;


import org.springframework.stereotype.Service;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.web.EzResult;

@Service("testService")
public class TestService {

    public EzResult test(Object request){
        System.out.println(JSONUtils.toJSONString(request));
        return EzResult.instance();
    }
}
