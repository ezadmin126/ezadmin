package top.ezadmin.plugins.express;

public class HttpOperator extends AbstractOperator {



    @Override
    public Object executeInner(Object[] objects) throws Exception {
        System.out.println("sss");
        return objects[0];
    }
}
