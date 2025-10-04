package io.github.ezadmin126.plugins.express.str;

import io.github.ezadmin126.common.utils.StringUtils;
import io.github.ezadmin126.common.utils.Utils;
import io.github.ezadmin126.plugins.express.AbstractOperator;
import io.github.ezadmin126.plugins.express.OperatorParam;

public class RequestParamOperator extends AbstractOperator {


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String key = objects[0].toString();
        OperatorParam operatorParam = (OperatorParam) Utils.getParam();

        if (StringUtils.equalsIgnoreCase("request", key)) {
            return operatorParam.getRequestParams();
        }
        try {
            Object value = operatorParam.getRequestParams().get(key);
            if (objects.length > 1 && objects[1] != null && Boolean.parseBoolean(objects[1].toString())) {
                //系统做了一层转换
                String[] result = (String[]) operatorParam.getRequestParams().get(key + "_ARRAY");
                if (result == null) {
                    result = new String[0];
                }
                return result;
            }
            return StringUtils.safeDb(Utils.trimNull(value));
        } catch (Exception e) {
            Utils.addLog("请求参数没有找到：" + key);
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println((char) (byte) 0x3B);
    }


}
