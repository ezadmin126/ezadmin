package com.ezcloud.plugins.express;


import com.ezcloud.common.utils.Utils;
import com.ezcloud.plugins.express.AbstractOperator;
import com.ezcloud.plugins.express.OperatorParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvOperator extends AbstractOperator {

    Logger logger =LoggerFactory.getLogger(EnvOperator.class);


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        String k=objects[0].toString();
        return operatorParam.getEnv(k);
    }
}
