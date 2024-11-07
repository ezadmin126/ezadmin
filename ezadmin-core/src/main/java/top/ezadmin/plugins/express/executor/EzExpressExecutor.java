package top.ezadmin.plugins.express.executor;

import top.ezadmin.plugins.express.EnvOperator;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import top.ezadmin.common.utils.Page;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.*;
import top.ezadmin.plugins.express.log.LogOperator;
import top.ezadmin.plugins.express.str.*;
import top.ezadmin.plugins.imports.ImportOperator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


public   class EzExpressExecutor {
    /**
     * 表达式
     */
    protected  String express;
    /**
     * 返回值
     */
    protected Object result=null;
    /**
     * 传进来的参数
     * ITEM_SEARCH_OPER操作符
     * ITEM_NAME字段名称
     * ITEM_JDBC_TYPE字段类型
     *
     * ${ITEM_NAME字段名称}  对应的值
     * ${ITEM_NAME字段名称}+"_START"  区间开始（如果有）
     * ${ITEM_NAME字段名称}+"_END"  区间结束（如果有）
     *
     */
    private OperatorParam operatorParam=new OperatorParam();
    //全局配置参数
    private static Map<String,Object> envParams=new HashMap<>();

    public OperatorParam getOperatorParam(){
        return operatorParam;
    }

    public EzExpressExecutor datasource(DataSource dd){
        operatorParam.setDs(dd);
        return this;
    }
    public EzExpressExecutor page(Page dd){
        operatorParam.setPage(dd);
        return this;
    }
    public EzExpressExecutor express(String express){
        this.express=express;
        return this;
    }
    public EzExpressExecutor addParam(Map params){
        if(operatorParam.getParams()==null){
            operatorParam.setParams(new HashMap<String, Object>());
        }
        operatorParam.getParams().putAll(params);
        return this;
    }
    public EzExpressExecutor addSessionParam(Map params){
        if(operatorParam.getSessionParams()==null){
            operatorParam.setSessionParams(new HashMap<String, Object>());
        }
        if(params!=null&&params.size()>0){
            operatorParam.getSessionParams().putAll(params);
        }
        return this;
    }
    public EzExpressExecutor addParam(String key, String value){
        if(operatorParam.getParams()==null){
            operatorParam.setParams(new HashMap<String, Object>());
        }
        operatorParam.getParams().put(key,value);
        return this;
    }
    private static ExpressRunner runner = new ExpressRunner();


    public static void initSpringFunction(Operator operator){
        //if(runner.getFunction("spring")==null){
        try {
            runner.addFunction("spring", operator);
        }catch (Exception e){}
       // }
    }
    public static void initEnv(String k,Object v){
        envParams.put(k,v);
    }

    static{
        runner.addFunction("select",new SelectOperator());
        runner.addFunction("selectOne",new SelectOneOperator());
        runner.addFunction("search",new SearchOperator( ));
        runner.addFunction("treeSearch",new TreeSearchOperator( ));
        runner.addFunction("count",new CountOperator());
        runner.addFunction("join",new JoinOperator( ));
        runner.addFunction("recursive",new RecursiveSelectOperator( ));
        runner.addFunction("$",new RequestParamOperator());
        runner.addFunction("$$",new SessionParamOperator());
        runner.addFunction("isNotBlank",new NotBlankRequestParamOperator( ));
        runner.addFunction("isBlank",new BlankRequestParamOperator( ));
         runner.addFunction("encode",new EncodeOperator());
        runner.addFunction("logger",new LogOperator());
        runner.addFunction("update",new UpdateOperator( ));
        runner.addFunction("insert",new InsertOperator( ));
        //自动增减字段
        runner.addFunction("insertSimple",new InsertSimpleOperator( ));
        runner.addFunction("updateSimple",new UpdateSimpleOperator( ));
        runner.addFunction("prepareUpdate",new PrepareUpdateOperator( ));
        runner.addFunction("unionall",new UnionOperator( ));
        runner.addFunction("split",new SplitOperator( ));
        runner.addFunction("env",new EnvOperator( ));
        runner.addFunction("imports",new ImportOperator( ));
    }
    public Object run( String express) throws Exception {
        if(StringUtils.isBlank(express) ){
            return null;
        }

        operatorParam.setEnv(envParams);
        Utils.addParam(operatorParam);
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        if(operatorParam.getParams()!=null){
           for(Map.Entry<String, Object>  e: operatorParam.getParams().entrySet()){
               context.put(e.getKey(),e.getValue());
           }
        }

        Object r = runner.execute(express, context, null, true, false);
        result = r;
        return r;

    }

    public Map<String,Object> executeAndReturnMap() throws Exception {
        run( express );
        if(result==null)return new HashMap<>();
        return (Map<String,Object>)result;
    }
    public Object execute() throws Exception {
        run( express );
        return result;
    }


    public EzExpressExecutor addRequestParam(Map  request){
        if(operatorParam.getRequestParams()==null){
            operatorParam.setRequestParams(new HashMap<String, Object>());
            operatorParam.getRequestParams().putAll(operatorParam.getParams());
            operatorParam.getRequestParams().putAll(operatorParam.getSessionParams());
        }
        if(request!=null&&request.size()>0){
            operatorParam.getRequestParams().putAll(request);
        }
        return this;
    }

}
