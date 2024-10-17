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
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.model.User;
import top.ezadmin.common.utils.DESUtils;
import top.ezadmin.common.utils.EzDateUtils;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.controller.CustomBaseController;
import top.ezadmin.domain.model.SysUser;
import top.ezadmin.service.UserService;
import top.ezadmin.web.EzResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/mycamunda/check/")
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
    @Autowired
    private UserService userService;


    /**
     * 判断当前流程是否已经启动
     * @param id
     * @param definitionKey
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/exist/{definitionKey}")
    @ResponseBody
    @Nologin
    public EzResult exist(String id, @PathVariable("definitionKey") String definitionKey, HttpServletRequest request, HttpServletResponse response){
        String bizKey=definitionKey+"_"+id;
        Map<String,Object> variables = Variables.createVariables();
        variables.put("id",id);
        variables.put("definitionKey",definitionKey);
        ProcessInstanceQuery query =runtimeService.createProcessInstanceQuery().processDefinitionKey(definitionKey)
                .processInstanceBusinessKey(bizKey);
        ProcessInstance processInstance = query.singleResult();
        if(processInstance!=null){
           return EzResult.instance();
        }
        return EzResult.instance().fail("已经启动");
    }

    /**
     * 获取 当前流程是否是轮到自己审核了
     * @param id
     * @param definitionKey
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/checkflag/{definitionKey}")
    @ResponseBody
    @Nologin
    public EzResult checkflag(String id,@PathVariable("definitionKey") String definitionKey,HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            Task  task  =taskService.createTaskQuery().processInstanceId(c.getProcessInstanceId())
                    .active().singleResult();
            List<String> roles=userService.getUserRoles(user.getUserId());
            if(!roles.contains(task.getName())&&!task.getAssignee().equals(DESUtils.encryptDES(user.getUserId()))){
                return EzResult.instance().fail("没有权限");
            }
        }
        return EzResult.instance() ;
    }

    /**
     * 审核某个流程
     * @return
     */
    @RequestMapping("/start/{definitionKey}")
    @ResponseBody
    public EzResult start(String id,@PathVariable("definitionKey") String definitionKey,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user=getSessionUser();
        String bizKey=definitionKey+"_"+id;
        Map<String,Object> variables = Variables.createVariables();
        variables.put("id",id);
        variables.put("definitionKey",definitionKey);

        variables.put("zhiguan",userService.getFirstUserByRoleName("质管",user.getCompanyId()));

        SysUser parentUser= userService.getParentUser(user.getUserId());
        variables.put("parentUser",DESUtils.encrypt(parentUser.getUserId()+""));
      //  variables.put("parentUser",DESUtils.encrypt(parentUser.getUserId()+""));

        ProcessInstanceQuery query =runtimeService.createProcessInstanceQuery().processDefinitionKey(definitionKey)
                .processInstanceBusinessKey(bizKey);
        ProcessInstance c=null;
        ProcessInstance processInstance = query.singleResult();
        if(processInstance!=null){
            return EzResult.instance().fail("已经发起审核，请勿重新发起审核。");
        }else{
            variables.put("starter",DESUtils.encrypt(user.getUserId()+""));
            c= runtimeService.startProcessInstanceByKey(definitionKey, bizKey, variables);
            Task  task  =taskService.createTaskQuery().processInstanceId(c.getProcessInstanceId())
                    .active().singleResult();
            taskService.createComment(task.getId(), c.getId(), "申请审核" );
            taskService.complete(task.getId(),variables);
        }
        return EzResult.instance().data("processInstanceId",c.getId());
    }
    /**
     * 开始某个流程
     * @return
     */
    @RequestMapping("/complete/{definitionKey}")
    @ResponseBody
    public EzResult complete(String id,@PathVariable("definitionKey") String definitionKey,
                             boolean pass,String comment,HttpServletRequest request, HttpServletResponse response) throws Exception {
        String bizKey=definitionKey+"_"+id;
        Map<String,Object> variables = Variables.createVariables();
        variables.put("id",id);
        variables.put("definitionKey",definitionKey);
        User user=getSessionUser();
        ProcessInstanceQuery query =runtimeService.createProcessInstanceQuery().processDefinitionKey(definitionKey)
                .processInstanceBusinessKey(bizKey);

        ProcessInstance processInstance = query.singleResult();
        if(processInstance==null){
            return EzResult.instance().fail("未启动");
        }else{
            Task  task  =taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                    .active().singleResult();
            List<String> roles=userService.getUserRoles(user.getUserId());
            if(roles.contains(task.getName())||task.getAssignee().equals(DESUtils.encryptDES(user.getUserId()))){
                task.setAssignee(DESUtils.encryptDES(user.getUserId()));
                if(pass){
                    taskService.createComment(task.getId(), processInstance.getId(), "审核通过" );
                    variables.put("pass",true);
                 }else{
                    taskService.createComment(task.getId(), processInstance.getId(), "审核不通过，原因："+comment );
                    variables.put("pass",false);
                 }
                taskService.complete(task.getId(),variables);
            }
        }
        return EzResult.instance() ;
    }




    @RequestMapping("/start2")
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

    @RequestMapping("/history/{definitionKey}")
    @Nologin
    public String history(Model model, String id,@PathVariable("definitionKey")  String definitionKey, HttpServletRequest request, HttpServletResponse response){
        String bizKey=definitionKey+"_"+id;
        Map<String,Object> variables = Variables.createVariables();
        variables.put("id",id);
        variables.put("definitionKey",definitionKey);
        User user=getSessionUser();
        List<Map<String,String>> resultList=new ArrayList<>();


        List<HistoricProcessInstance> list=  historyService.createHistoricProcessInstanceQuery()
               // .finished()
                .processDefinitionKey(definitionKey)
                .processInstanceBusinessKey(bizKey)
                .orderByProcessInstanceStartTime().asc()
                .listPage(0,10);
            list.forEach(item->{
//                sb.append("<br>操作人："+item.getStartUserId()+"\t ");
//                sb.append("<br>操作时间："+EzDateUtils.toDateTimeFormat(item.getStartTime())+" - "+EzDateUtils.toDateTimeFormat(item.getEndTime())+"\t");
                HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(item.getId());
                List<HistoricTaskInstance> historicTaskInstances = taskInstanceQuery
                        .orderByHistoricActivityInstanceStartTime().asc().listPage(0,10);
                for (int i =0; i <historicTaskInstances.size() ; i++) {
                    Map<String,String> historyItem=new HashMap<>();
                    HistoricTaskInstance task=historicTaskInstances.get(i);
                    try {
                        String currentId=DESUtils.decryptDES(task.getAssignee());


                        historyItem.put("assignee",userService.getUserById(currentId).getUserName());
                       //
                        StringBuilder sb=new StringBuilder();
                        List<HistoricIdentityLinkLog> list1= historyService.createHistoricIdentityLinkLogQuery().taskId(task.getId())
                                .type("candidate").list();
                        if(Utils.isNotEmpty(list1)){
                            for (int j = list1.size()-1; j >=0 ; j--) {
                                sb.append(""+list1.get(j).getUserId()+"\t"+list1.get(j).getType()+"\t" );//assignee candidate
                            }
                        }
                        historyItem.put("candidate",sb.toString());
                        List<Comment> comments = processEngine.getTaskService().getTaskComments(task.getId());
                        historyItem.put("comments", (Utils.isNotEmpty(comments)?comments.get(0).getFullMessage():""));
                        historyItem.put("startTime", EzDateUtils.toDateTimeFormat(task.getStartTime()));
                        historyItem.put("endTime", EzDateUtils.toDateTimeFormat(task.getEndTime()));
                        resultList.add(historyItem);
                     } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        model.addAttribute("data",resultList);
        model.addAttribute("id",id);
        model.addAttribute("definitionKey",definitionKey);
        return "jxc/historylist";
    }



}
