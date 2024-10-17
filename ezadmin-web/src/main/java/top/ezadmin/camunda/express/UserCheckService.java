package top.ezadmin.camunda.express;

import org.springframework.stereotype.Service;
import top.ezadmin.service.UserService;

import javax.annotation.Resource;
import java.util.List;

@Service("userCheck")
public class UserCheckService {
    @Resource
    UserService userService;
    public List<String> getRoleUsers(String roleName){
       // userService.getFirstUserByRoleName()
        return null;
    }
}
