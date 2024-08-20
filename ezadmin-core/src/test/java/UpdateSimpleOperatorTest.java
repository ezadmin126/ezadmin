 import org.junit.Assert;
import org.junit.Test;
 import top.ezadmin.common.utils.JSONUtils;
 import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.OperatorParam;
import top.ezadmin.plugins.express.UpdateSimpleOperator;
import top.ezadmin.plugins.express.jdbc.UpdateParam;
import top.ezadmin.plugins.parser.parse.ResultModel;

public class UpdateSimpleOperatorTest {

    private static String TABLE="table";

    @Test
    public void test1() {
        UpdateSimpleOperator operator = new UpdateSimpleOperator();
        UpdateParam param = new UpdateParam();
        param.table(TABLE);
        param.add("#{userId}");
        param.add("#{userName}");
        param.where(" where 1=1");
        OperatorParam param1 = new OperatorParam();
        param1.getParams().put("a", "b");
        Utils.addParam(param1);
        Assert.assertEquals("没有传参，且没有默认值", "update  "+TABLE+" set userId=?,userName=?  where 1=1", operator.generateSql(param).getResult());
    }
    @Test
    public void test2() {
        UpdateSimpleOperator operator = new UpdateSimpleOperator();
        UpdateParam param = new UpdateParam();
        param.table(TABLE);
        param.add("#{userId,value=null}");
        param.add("#{userName,value=abc}");
        param.add("#{userSex,jdbcType=NUMBER}");
        param.add("#{userSex2,jdbcType=NUMBER,value=0}");
        param.where(" where 1=1");
        OperatorParam param1 = new OperatorParam();
        param1.getParams().put("a", "b");
        Utils.addParam(param1);
        ResultModel model=operator.generateSql(param);
        Assert.assertEquals("没有传参，但有默认值", "update  "+TABLE+" set userId=?,userName=?,userSex=?,userSex2=?  where 1=1",
                model.getResult());
        Assert.assertEquals("没有传参，但有默认值  剥离参数", "[null,\"abc\",null,\"0\"]",
                JSONUtils.toJSONString(model.getParamsStatic()));
    }
    @Test
    public void test3() {
        UpdateSimpleOperator operator = new UpdateSimpleOperator();
        UpdateParam param = new UpdateParam();
        param.table(TABLE);
        param.add("#{userId,value=null}");
        param.add("#{userName,value=abc}");
        param.add("#{userSex,jdbcType=NUMBER}");
        param.add("#{userSex2,jdbcType=NUMBER,value=0}");
        param.where(" where 1=1");
        OperatorParam param1 = new OperatorParam();
        param1.getParams().put("userId", "userId");
        param1.getParams().put("userName", "userName");
        param1.getParams().put("userSex", "3");
        param1.getParams().put("userSex2", "2");
        Utils.addParam(param1);
        ResultModel model=operator.generateSql(param);
        Assert.assertEquals("有传参，有默认值", "update  "+TABLE+" set userId=?,userName=?,userSex=?,userSex2=?  where 1=1",
                model.getResult());
        Assert.assertEquals("有传参，有默认值  剥离参数", "[\"userId\",\"userName\",3,2]",
                JSONUtils.toJSONString(model.getParamsStatic()));
    }
    @Test
    public void test4() {
        UpdateSimpleOperator operator = new UpdateSimpleOperator();
        UpdateParam param = new UpdateParam();
        param.table(TABLE);
        param.add("#{userId}");
        param.add("#{userName}");
        param.add("#{userName2}");
        param.add("#{userName3}");
        param.add("#{userSex,jdbcType=NUMBER}");
        param.add("#{userSex2,jdbcType=NUMBER}");
        param.where(" where 1=1");
        OperatorParam param1 = new OperatorParam();
        param1.getParams().put("userId", "userId");
        param1.getParams().put("userName", "null");
        param1.getParams().put("userName2",null);
        param1.getParams().put("userName3"," ");
        param1.getParams().put("userSex", "3");
        param1.getParams().put("userSex2", "2");
        Utils.addParam(param1);
        ResultModel model=operator.generateSql(param);
        Assert.assertEquals("有传参，没有默认值", "update  "+TABLE+" set userId=?,userName=?,userName2=?,userName3=?,userSex=?,userSex2=?  where 1=1",
                model.getResult());
        Assert.assertEquals("有传参，没有默认值  剥离参数", "[\"userId\",null,null,null,3,2]",
                JSONUtils.toJSONString(model.getParamsStatic()));
    }
    @Test
    public void test5() {
        UpdateSimpleOperator operator = new UpdateSimpleOperator();
        UpdateParam param = new UpdateParam();
        param.table(TABLE);
        param.add("#{userId}");
        param.add("#{userName,value='abc'}");
        param.add("#{userSex,value='ab'c'}");
        param.add("#{userSex2,value=ab'c}");
        param.add("#{userSex3,value='ab=c'}");
        param.add("#{userSex4,value='NOW()'}");
        param.add("#{userSex5,value=NOW()}");
        param.where(" where 1=1");
        OperatorParam param1 = new OperatorParam();
        Utils.addParam(param1);
        ResultModel model=operator.generateSql(param);
        Assert.assertEquals("老版本默认值兼容", "update  table set userId=?,userName=?,userSex=?,userSex2=?,userSex3=?,userSex4=NOW(),userSex5=NOW()  where 1=1",
                model.getResult());
        Assert.assertEquals("老版本默认值兼容", "[null,\"abc\",\"ab'c\",\"ab'c\",\"ab=c\"]",
                JSONUtils.toJSONString(model.getParamsStatic()));
    }

}
