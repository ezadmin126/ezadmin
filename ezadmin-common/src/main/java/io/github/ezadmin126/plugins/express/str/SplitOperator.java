package io.github.ezadmin126.plugins.express.str;

import io.github.ezadmin126.common.utils.StringUtils;
import io.github.ezadmin126.plugins.express.AbstractOperator;

public class SplitOperator extends AbstractOperator {

    //string   session  request(默认)


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String key = objects[0].toString();
        String k2 = objects.length > 1 ? objects[1].toString() : "";

        return StringUtils.split(key, k2);
    }
}
