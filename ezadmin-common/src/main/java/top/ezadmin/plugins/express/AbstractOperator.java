package top.ezadmin.plugins.express;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOperator implements CustomFunction {
    public Logger logger = LoggerFactory.getLogger(AbstractOperator.class);

    public AbstractOperator() {
    }

    @Override
    public Object call(QContext qContext, Parameters parameters) throws Throwable {
        Object[] objects = new Object[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            objects[i] = parameters.getValue(i);
        }
        return executeInner(objects);
    }

    public abstract Object executeInner(Object[] objects) throws Exception;
}
