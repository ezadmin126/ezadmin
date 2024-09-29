package top.ezadmin.camunda.subscription;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HandlerConfiguration {

  @Bean
  @ExternalTaskSubscription("autoCheckCert")
  public ExternalTaskHandler creditScoreCheckerHandler() {
    return (externalTask, externalTaskService) -> {
      Map<String, Object> variables = externalTask.getAllVariables();
     // Map<String,Object> variables = Variables.createVariables();
      variables.put("pass",false );
      externalTaskService.complete(externalTask, variables);
    };
  }


}