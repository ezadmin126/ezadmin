package com.ezadmin.web;

import com.ezadmin.common.utils.Utils;
import com.ql.util.express.Operator;

public class SpringBeanOperator extends Operator {
    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return SpringContextHolder.getBean(Utils.trimNull(objects[0]));
    }
}
