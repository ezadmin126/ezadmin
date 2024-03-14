package com.ezcloud.plugins.express;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.AbstractOperator;
import top.ezadmin.plugins.express.OperatorParam;

public class EnvOperator extends AbstractOperator {

    Logger logger =LoggerFactory.getLogger(EnvOperator.class);


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        String k=objects[0].toString();
        return operatorParam.getEnv(k);
    }
}
