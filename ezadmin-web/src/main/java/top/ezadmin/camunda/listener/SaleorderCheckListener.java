package top.ezadmin.camunda.listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import top.ezadmin.camunda.constants.CheckStatus;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.domain.mapper.BaseManufacturerMapper;
import top.ezadmin.domain.mapper.JxcSaleorderMapper;
import top.ezadmin.domain.model.BaseManufacturer;
import top.ezadmin.domain.model.JxcSaleorder;

import javax.annotation.Resource;
import java.util.Date;

@Service("saleorderCheck")
public class SaleorderCheckListener {
    //0待提交审核 1审核中 2 驳回 3 审核通过
    @Resource
    JxcSaleorderMapper jxcSaleorderMapper;
    public void complete(DelegateExecution execution){
        boolean pass= Boolean.valueOf(Utils.trimNull(execution.getVariable("pass")));
        Long id= Utils.toLong(execution.getVariable("id"));
        JxcSaleorder product=new JxcSaleorder();
        product.setSaleorderId(id);

        product.setUpdateTime(new Date());
        if(pass){
            product.setCheckStatus(CheckStatus.PASS.getValue());
        }else{
            product.setCheckStatus(CheckStatus.REJECT.getValue());
        }
        jxcSaleorderMapper.updateByPrimaryKeySelective(product);
    }
    public void start(DelegateExecution execution){
        Long id= Utils.toLong(execution.getVariable("id"));
        JxcSaleorder product=new JxcSaleorder();
        product.setSaleorderId(id);
        product.setCheckStatus(CheckStatus.PREPARE.getValue());
        product.setUpdateTime(new Date());
        JxcSaleorder order=jxcSaleorderMapper.selectByPrimaryKey(id);
        execution.setVariable("total",order.getTotalAmount());

        jxcSaleorderMapper.updateByPrimaryKeySelective(product);
    }
}
