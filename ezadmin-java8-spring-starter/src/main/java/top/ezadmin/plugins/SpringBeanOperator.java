package top.ezadmin.plugins;

import top.ezadmin.plugins.express.AbstractOperator;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.spring.SpringContextHolder;

public class SpringBeanOperator   extends AbstractOperator {
    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return SpringContextHolder.getBean(Utils.trimNull(objects[0]));
    }
}
