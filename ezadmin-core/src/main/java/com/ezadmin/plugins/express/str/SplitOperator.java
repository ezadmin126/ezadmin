package com.ezadmin.plugins.express.str;

import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.plugins.express.AbstractOperator;
import com.ezadmin.plugins.express.OperatorParam;

public class SplitOperator extends AbstractOperator {

    //string   session  request(默认)


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String key=objects[0].toString();
        String k2=objects.length>1?objects[1].toString():"";

        return StringUtils.split(key,k2);
    }
}
