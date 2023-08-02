package com.ezadmin.plugins.express.log;

import com.ezadmin.common.utils.JSONUtils;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.plugins.express.AbstractOperator;
import com.ezadmin.plugins.express.OperatorParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogOperator extends AbstractOperator {

    Logger logger =LoggerFactory.getLogger(LogOperator.class);


    @Override
    public Object executeInner(Object[] objects) throws Exception {

        if(Utils.getLog()!=null){
            Utils.addLog(JSONUtils.toJSONString(objects));
        }
        logger.info(JSONUtils.toJSONString(objects));
       return null;
    }
}
