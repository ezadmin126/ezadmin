package top.ezadmin.camunda;


import cn.hutool.json.JSON;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.common.utils.JSONUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/camunda/check/")
@Nologin
public class TestController {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IdentityService identityService;

    @Resource
    private ProcessEngine processEngine;
    @RequestMapping("/start")
    @ResponseBody
    public String start(){
        String bizKey="1";
        Map<String,Object> variables = Variables.createVariables();
        variables.put("orderNo","Order123456");
        variables.put("self","1_self");
        variables.put("zhuguan","2_zhuguan");
        variables.put("jingli","3_jingli");
        variables.put("zhiguan","4_zhiguan");
//        ProcessInstanceDTO instanceDTO=new ProcessInstanceDTO();
//        instanceDTO.setVariables(variables);
//        instanceDTO.setBusinessKey(bizKey);
//        instanceDTO.setProcessDefinitionKey("Process_order");
//        RestfulResult<ProcessInstanceReturnDTO> result= camundaService.startProcessInstance(instanceDTO);
        //设置当前操作人是1_self
        identityService.setAuthenticatedUserId("1_self");
        ProcessInstanceQuery query =runtimeService.createProcessInstanceQuery().processDefinitionKey("Process_order")
                .processInstanceBusinessKey(bizKey);
        ProcessInstance c=null;
        ProcessInstance processInstance = query.singleResult();
        if(processInstance!=null){
            c=processInstance;
        }else{
            c= runtimeService.startProcessInstanceByKey("Process_order", bizKey, variables);
        }
        //查询当前所有task
        List<Task> taskList =taskService.createTaskQuery().processInstanceId(c.getProcessInstanceId()).active().list();
        if(taskList!=null&&taskList.size()>0){
            //打印当前任务的参数
            System.out.println(JSONUtils.toJSONString(taskService.getVariables(taskList.get(0).getId())));
            System.out.println(taskList.get(0).getAssignee());
        }


        variables.put("total",100000);
        taskService.createComment(taskList.get(0).getId(), c.getId(), "已提交");
        taskService.complete(taskList.get(0).getId(),variables);

        return "OK" ;
    }

    @RequestMapping("/history")
    @ResponseBody
    public String history(){
        String bizKey="1";
        Map<String,Object> variables = Variables.createVariables();
        variables.put("orderNo","Order123456");
        variables.put("self","1_self");
        variables.put("zhuguan","2_zhuguan");
        variables.put("jingli","3_jingli");
        variables.put("zhiguan","4_zhiguan");
         //设置当前操作人是1_self
        identityService.setAuthenticatedUserId("1_self");
        ProcessInstanceQuery query =runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("Process_order")
                .processInstanceBusinessKey(bizKey);

        StringBuilder sb=new StringBuilder();
        List<ProcessInstance> processInstance = query.list();
        processInstance.forEach(a->{
            List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(a.getProcessInstanceId())
                    .orderByHistoricActivityInstanceStartTime()
                    .asc()
                    .list();
            list.forEach(item->{
                sb.append(item.getId()+"\t");
                sb.append(item.getAssignee()+"\t");
                sb.append(item.getDurationInMillis()+"\t");
                sb.append(item.getStartTime()+"\t");
                sb.append(item.getEndTime()+"\t");
                sb.append(item.getActivityName()+"\t<br>\n");
            });
        });
        return "OK"+sb;
    }


}
