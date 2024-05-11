package top.ezadmin.plugins.parser.parse;

import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.utils.ArrayUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;



public class ResultModel {
    private List<Params> params=new ArrayList<>();
    private List<Object> paramsStatic=new ArrayList<>();
    private String result;
    public final static ResultModel EMPTY=new ResultModel();
    public static ResultModel getInstance() {
        return EMPTY;
    }

    @Override
    public String toString() {
        return "ResultModel{" +
                "params=" + params +
                ", result='" + result + '\'' +
                '}';
    }

    public void addParam(Params p){
        if(p==null ){
            return;
        }
//        if(StringUtils.isBlank(Utils.trimNull(p.getParamValue()))){
//            if(JdbcTypeEnum.NUMBERNULL.getName().equals(p.getJdbcType())){
//                p.setParamValue(null);
//            }else
//            if(JdbcTypeEnum.NUMBER.getName().equals(p.getJdbcType())){
//                p.setParamValue(0);
//            }
//            else if (JdbcTypeEnum.DATE.getName().equalsIgnoreCase(p.getJdbcType())
//                    ||JdbcTypeEnum.DATETIME.getName().equalsIgnoreCase(p.getJdbcType())) {
//                 //不处理
//            }
//            else{
//                p.setParamValue("");
//            }
//        }

        params.add(p);
        paramsStatic.add(p.getParamValue());
    }

    public List<Params> getParams() {
        return params;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Object[] getParamsStatic() {
        return ArrayUtils.toArray(paramsStatic);
    }

    public List<Object> getParamStaticList(){
        return paramsStatic;
    }

}
