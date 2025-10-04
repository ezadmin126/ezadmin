package io.github.ezadmin126.plugins.express;

public class HttpOperator extends AbstractOperator {


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        System.out.println("sss");
        return objects[0];
    }
}
