package top.ezadmin.plugins.express;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.AbstractOperator;
import top.ezadmin.plugins.express.OperatorParam;

public class EnvOperator extends AbstractOperator {

    Logger logger =LoggerFactory.getLogger(EnvOperator.class);


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String k=objects[0].toString();
        return Utils.trimNull(EzClientBootstrap.instance().getConfig().get(k));
    }
}
