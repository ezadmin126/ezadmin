package top.ezadmin.plugins;


import com.ql.util.express.Operator;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.spring.SpringContextHolder;

public class SpringBeanOperator extends Operator {
    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return SpringContextHolder.getBean(Utils.trimNull(objects[0]));
    }
}
