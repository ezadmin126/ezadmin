package top.ezadmin.camunda;


import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.ezadmin.blog.constants.Nologin;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/camunda/check/")
@Nologin
public class TestController {

    @Autowired
    private RuntimeService runtimeService;
    @RequestMapping("/start")
    @ResponseBody
    public String start(){
        String bizKey="1";
        Map<String,Object> variables = Variables.createVariables();
        variables.put("bizKey1",bizKey);
        org. camunda. bpm. engine. runtime. ProcessInstance instance=
                runtimeService.startProcessInstanceByKey("Process_shop",bizKey,"1",variables)
                ;
        return "OK"+instance.getProcessInstanceId()+instance;
    }
}
