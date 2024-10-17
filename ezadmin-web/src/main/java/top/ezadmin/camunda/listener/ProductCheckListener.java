package top.ezadmin.camunda.listener;

import org.apache.commons.lang.BooleanUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
  import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.domain.mapper.ext.BaseProductExtendMapper;
import top.ezadmin.domain.model.BaseProduct;

import javax.annotation.Resource;
import java.util.Date;

@Service("prodCheck")
public class ProductCheckListener {
    //0待提交审核 1审核中 2 驳回 3 审核通过
    @Resource
    BaseProductExtendMapper productMapper;
    public void complete(DelegateExecution execution){
        boolean pass= Boolean.valueOf(Utils.trimNull(execution.getVariable("pass")));
        Long id= Utils.toLong(execution.getVariable("id"));
        BaseProduct product=new BaseProduct();
        product.setProdId(id);

        product.setUpdateTime(new Date());
        if(pass){
            product.setCheckStatus(3);
        }else{
            product.setCheckStatus(2);
        }
        productMapper.updateByPrimaryKeySelective(product);
    }
    public void start(DelegateExecution execution){
        Long id= Utils.toLong(execution.getVariable("id"));
        BaseProduct product=new BaseProduct();
        product.setProdId(id);
        product.setCheckStatus(1);
        product.setUpdateTime(new Date());
        productMapper.updateByPrimaryKeySelective(product);
    }
}
