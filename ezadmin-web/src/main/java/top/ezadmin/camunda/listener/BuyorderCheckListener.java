package top.ezadmin.camunda.listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import top.ezadmin.camunda.constants.CheckStatus;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.domain.mapper.BaseManufacturerMapper;
import top.ezadmin.domain.model.BaseManufacturer;

import javax.annotation.Resource;
import java.util.Date;

@Service("buyorderCheck")
public class BuyorderCheckListener {
    //0待提交审核 1审核中 2 驳回 3 审核通过
    @Resource
    BaseManufacturerMapper baseManufacturerMapper;
    public void complete(DelegateExecution execution){
        boolean pass= Boolean.valueOf(Utils.trimNull(execution.getVariable("pass")));
        Long id= Utils.toLong(execution.getVariable("id"));
        BaseManufacturer product=new BaseManufacturer();
        product.setManufacturerId(id);

        product.setUpdateTime(new Date());
        if(pass){
            product.setCheckStatus(CheckStatus.PASS.getValue());
        }else{
            product.setCheckStatus(CheckStatus.REJECT.getValue());
        }
        baseManufacturerMapper.updateByPrimaryKeySelective(product);
    }
    public void start(DelegateExecution execution){
        Long id= Utils.toLong(execution.getVariable("id"));
        BaseManufacturer product=new BaseManufacturer();
        product.setManufacturerId(id);
        product.setCheckStatus(CheckStatus.PREPARE.getValue());
        product.setUpdateTime(new Date());
        baseManufacturerMapper.updateByPrimaryKeySelective(product);
    }
}
