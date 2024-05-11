package top.ezadmin.plugins.express.str;

import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.AbstractOperator;
import top.ezadmin.plugins.express.OperatorParam;

public class SessionParamOperator extends AbstractOperator {



    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String key = objects[0].toString();
        try {        OperatorParam operatorParam=(OperatorParam)Utils.getParam();


            String value = Utils.trimNull(operatorParam.getSessionParams().get(key));
//        if(objects.length>1&&objects[1]!=null&&Boolean.parseBoolean(objects[1].toString())){
//            String[] result= StringUtils.split (value.toString(),",");
//            if(result==null){
//                result=new String[0];
//            }
//            return result;
//        }
            return value;
        }catch(Exception e){
            Utils.addLog("没有找到："+key);
            return "";
        }
    }


}
