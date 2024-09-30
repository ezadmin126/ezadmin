package top.ezadmin.camunda;


import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricIdentityLinkLog;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.model.User;
import top.ezadmin.common.utils.DESUtils;
import top.ezadmin.common.utils.EzDateUtils;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.controller.CustomBaseController;
import top.ezadmin.web.EzResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/camunda/check/")
@Nologin
public class CamundaController extends CustomBaseController {
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

    @RequestMapping("/exist")
    @ResponseBody
    public EzResult exist(String id,String definitionKey,HttpServletRequest request, HttpServletResponse response){
        String bizKey=definitionKey+"_"+id;
        Map<String,Object> variables = Variables.createVariables();
        variables.put("id",id);
        variables.put("definitionKey",definitionKey);
        User user=getSessionUser();
        ProcessInstanceQuery query =runtimeService.createProcessInstanceQuery().processDefinitionKey(definitionKey)
                .processInstanceBusinessKey(bizKey);
        ProcessInstance c=null;
        ProcessInstance processInstance = query.singleResult();
        if(processInstance!=null){
           return EzResult.instance();
        }
        return EzResult.instance().fail("已经启动");
    }
    @RequestMapping("/checkflag")
    @ResponseBody
    public EzResult checkflag(String id,String definitionKey,HttpServletRequest request, HttpServletResponse response) throws Exception {
        String bizKey=definitionKey+"_"+id;
        Map<String,Object> variables = Variables.createVariables();
        variables.put("id",id);
        variables.put("definitionKey",definitionKey);
        User user=getSessionUser();
        ProcessInstanceQuery query =runtimeService.createProcessInstanceQuery().processDefinitionKey(definitionKey)
                .processInstanceBusinessKey(bizKey);
        ProcessInstance c=null;
        ProcessInstance processInstance = query.singleResult();
        if(processInstance==null){
            return EzResult.instance().fail("未启动");
        }else{
            List<Task> taskList =taskService.createTaskQuery().processInstanceId(c.getProcessInstanceId())
                    .taskAssignee(DESUtils.encryptDES(user.getUserId())).active().list();

        }
        return EzResult.instance() ;
    }

    @RequestMapping("/start")
    @ResponseBody
    public String start(){
        String bizKey="2";
        Map<String,Object> variables = Variables.createVariables();
        variables.put("orderNo","Order1234567");
        variables.put("self","self1");
        variables.put("zhuguan","zhuguan2");
        variables.put("jingli","jingli3");
        variables.put("jingli3","jingli3,zizhi5");
        variables.put("zhiguan","zhiguan4");
        variables.put("zizhi","zizhi5");
        variables.put("pass","true");
        //设置当前操作人是self1
        identityService.setAuthenticatedUserId("jingli3");
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
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskList.get(0).getId());
            if(Utils.isNotEmpty(identityLinks)){
                for (IdentityLink identityLink : identityLinks) {
                    System.out.println("<br>==========候选人："+identityLink.getUserId()+"\t" );
                }
            }
        }

        variables.put("total",100000);
        taskService.createComment(taskList.get(0).getId(), c.getId(), "已提交"+System.currentTimeMillis());
        taskService.complete(taskList.get(0).getId(),variables);
        return "OK" ;
    }

    @RequestMapping("/history")
    @ResponseBody
    public String history(){
        String bizKey="2";
        Map<String,Object> variables = Variables.createVariables();
        variables.put("self","self1");
        variables.put("zhuguan","zhuguan2");
        variables.put("jingli","jingli3");
        variables.put("jingli3","jingli3,zizhi5");
        variables.put("zhiguan","zhiguan4");
        variables.put("zizhi","zizhi5");
         //设置当前操作人是1_self
        //identityService.setAuthenticatedUserId("1_self");
        StringBuilder sb=new StringBuilder();
        List<HistoricProcessInstance> list=  historyService.createHistoricProcessInstanceQuery()
               // .finished()
                .processDefinitionKey("Process_order")
                .processInstanceBusinessKey(bizKey)
                .orderByProcessInstanceStartTime().desc()
                .list();
            list.forEach(item->{
                sb.append("<br>操作人："+item.getStartUserId()+"\t ");
                sb.append("<br>操作时间："+EzDateUtils.toDateTimeFormat(item.getStartTime())+" - "+EzDateUtils.toDateTimeFormat(item.getEndTime())+"\t");
                HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(item.getId());
                List<HistoricTaskInstance> historicTaskInstances = taskInstanceQuery
                        .orderByHistoricActivityInstanceStartTime().asc().list();
                historicTaskInstances.forEach(task->{



                    sb.append("<br>==========执行人："+task.getAssignee()+"\t" );
                   List<HistoricIdentityLinkLog> list1= historyService.createHistoricIdentityLinkLogQuery().taskId(task.getId())
                           .type("candidate").list();
                    if(Utils.isNotEmpty(list1)){
                        for (HistoricIdentityLinkLog identityLink : list1) {
                            sb.append("<br>==========候选人："+identityLink.getUserId()+"\t"+identityLink.getType()+"\t" );//assignee candidate
                        }
                    }
                    sb.append("<br>==========执行时间："+EzDateUtils.toDateTimeFormat(task.getStartTime())+" - "+EzDateUtils.toDateTimeFormat(task.getEndTime())+"\t");
                    List<Comment> comments = processEngine.getTaskService().getTaskComments(task.getId());
                    sb.append("<br>==========执行备注："+ (Utils.isNotEmpty(comments)?comments.get(0).getFullMessage():"null"));
                    sb.append("<br>" +
                            "===============================================================================<br>");
                });
                sb.append("<br>节点状态："+item.getState()+"\t<br>" +
                        "===============================================================================<br>");
            });

        return "OK"+sb;
    }



}
