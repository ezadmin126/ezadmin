package top.ezadmin.camunda.listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import top.ezadmin.camunda.constants.CheckStatus;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.domain.mapper.BaseRegistrationMapper;
import top.ezadmin.domain.model.BaseRegistration;

import javax.annotation.Resource;
import java.util.Date;

@Service("registrationCheck")
public class RegistrationCheckListener {
    //0待提交审核 1审核中 2 驳回 3 审核通过
    @Resource
    BaseRegistrationMapper baseRegistrationMapper;
    public void complete(DelegateExecution execution){
        boolean pass= Boolean.valueOf(Utils.trimNull(execution.getVariable("pass")));
        Long id= Utils.toLong(execution.getVariable("id"));
        BaseRegistration product=new BaseRegistration();
        product.setRegistrationId(id);

        product.setUpdateTime(new Date());
        if(pass){
            product.setCheckStatus(CheckStatus.PASS.getValue());
        }else{
            product.setCheckStatus(CheckStatus.REJECT.getValue());
        }
        baseRegistrationMapper.updateByPrimaryKeySelective(product);
    }
    public void start(DelegateExecution execution){
        Long id= Utils.toLong(execution.getVariable("id"));
        BaseRegistration product=new BaseRegistration();
        product.setRegistrationId(id);
        product.setCheckStatus(CheckStatus.PREPARE.getValue());
        product.setUpdateTime(new Date());
        baseRegistrationMapper.updateByPrimaryKeySelective(product);
    }
}
