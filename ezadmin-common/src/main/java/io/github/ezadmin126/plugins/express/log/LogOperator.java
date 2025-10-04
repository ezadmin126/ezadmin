package io.github.ezadmin126.plugins.express.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ezadmin126.common.utils.JSONUtils;
import io.github.ezadmin126.common.utils.Utils;
import io.github.ezadmin126.plugins.express.AbstractOperator;

public class LogOperator extends AbstractOperator {

    Logger logger = LoggerFactory.getLogger(LogOperator.class);


    @Override
    public Object executeInner(Object[] objects) throws Exception {

        if (Utils.getLog() != null) {
            Utils.addLog(JSONUtils.toJSONString(objects));
        }
        logger.info(objects[0] + "\t" + objects[0].getClass() + "\t" + JSONUtils.toJSONString(objects));
        return null;
    }
}
