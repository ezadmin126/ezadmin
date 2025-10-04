package io.github.ezadmin126.plugins;

import com.ql.util.express.Operator;
import io.github.ezadmin126.spring.SpringContextHolder;
import io.github.ezadmin126.common.utils.Utils;

public class SpringBeanOperator extends Operator {
    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return SpringContextHolder.getBean(Utils.trimNull(objects[0]));
    }
}
