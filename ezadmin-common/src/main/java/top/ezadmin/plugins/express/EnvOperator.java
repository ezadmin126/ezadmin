package top.ezadmin.plugins.express;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvOperator extends AbstractOperator {

    Logger logger = LoggerFactory.getLogger(EnvOperator.class);


    @Override
    public Object executeInner(Object[] objects) throws Exception {
//        String k = objects[0].toString();
//        return Utils.trimNull(EzBootstrap.getInstance().getConfig().get(k));
        return null;
    }
}
