package com.ezcloud.plugins.express.log;

import com.ezcloud.common.utils.JSONUtils;
import com.ezcloud.common.utils.Utils;
import com.ezcloud.plugins.express.AbstractOperator;
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
