package top.ezadmin.camunda.subscription;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HandlerConfiguration {

  @Autowired
  private TaskService taskService;

  @Bean
  @ExternalTaskSubscription("autoCheckCert")
  public ExternalTaskHandler creditScoreCheckerHandler() {
    return (externalTask, externalTaskService) -> {
      Map<String, Object> variables = externalTask.getAllVariables();
     // Map<String,Object> variables = Variables.createVariables();
      variables.put("pass",false );
      taskService.createComment(externalTask.getId(), externalTask.getProcessInstanceId(),
              "自动审核不通过"+System.currentTimeMillis());
      externalTaskService.complete(externalTask, variables);
    };
  }


}