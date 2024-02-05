package com.ezcloud.plugins.express;

import com.ezcloud.common.utils.DESUtils;

public class EncodeOperator extends AbstractOperator {



    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return DESUtils.encryptDES(objects[0]);
    }
}
