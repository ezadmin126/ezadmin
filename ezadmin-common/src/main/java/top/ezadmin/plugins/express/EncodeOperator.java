package top.ezadmin.plugins.express;

import top.ezadmin.common.utils.DESUtils;

public class EncodeOperator extends AbstractOperator {


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return DESUtils.encodeSafeUrl(objects[0]);
    }
}
