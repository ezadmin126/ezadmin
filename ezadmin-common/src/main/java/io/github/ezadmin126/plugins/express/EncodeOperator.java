package io.github.ezadmin126.plugins.express;

import io.github.ezadmin126.common.utils.DESUtils;

public class EncodeOperator extends AbstractOperator {


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return DESUtils.encryptDES(objects[0]);
    }
}
