package com.ezadmin.plugins.express.str;

import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.plugins.express.AbstractOperator;
import com.ezadmin.plugins.express.OperatorParam;

public class BlankRequestParamOperator extends AbstractOperator {

    //string   session  request(默认)


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String key=objects[0].toString();
        OperatorParam operatorParam=(OperatorParam)Utils.getParam();
        String k2=objects.length>1?objects[1].toString():"";
        // session
        if(StringUtils.equalsIgnoreCase("session",k2)){
            return !StringUtils.isNotBlank(Utils.trimNull(operatorParam.getSessionParams().get(key)));
        }
        // string
        if(StringUtils.equalsIgnoreCase("string",k2)){
            return !StringUtils.isNotBlank(Utils.trimNull(key));
        }
        //默认request
        Object value=operatorParam.getRequestParams().get(key);
        return !StringUtils.isNotBlank(Utils.trimNull(value));
    }
}
