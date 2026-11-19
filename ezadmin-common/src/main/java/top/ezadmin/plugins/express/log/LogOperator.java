package top.ezadmin.plugins.express.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.AbstractOperator;

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
