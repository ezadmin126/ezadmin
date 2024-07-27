import com.alibaba.fastjson.JSON;
import org.junit.Assert;
import org.junit.Test;
import top.ezadmin.common.utils.SqlUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.InsertSimpleOperator;
import top.ezadmin.plugins.express.OperatorParam;
import top.ezadmin.plugins.express.jdbc.InsertParam;

public class CustomSearchTest {


    @Test
    public   void test1() {
        String json="{\"g\":[{\"c\":{\"g\":[],\"s\":[{\"t\":\"and\",\"f\":\"BRAND_ID\",\"o\":\"eq\",\"v\":\"1\"},{\"t\":\"or\",\"f\":\"BRAND_ID\",\"o\":\"eq\",\"v\":\"2\"}]},\"t\":\"and\"}],\"s\":[{\"t\":\"and\",\"f\":\"PROD_NAME\",\"o\":\"like\",\"v\":\"1\"},{\"t\":\"and\",\"f\":\"PROD_KEYWORDS\",\"o\":\"\",\"v\":\"2\"}],\"o\":[{\"f\":\"PROD_CODE\",\"o\":\" desc \"},{\"f\":\"PROD_NAME\",\"o\":\" asc \"},{\"f\":\"\",\"o\":\"\"}]}";
        System.out.println(SqlUtils.customSearchJsonToSql(json,null));
    }

    public static void main(String[] args) {
        new CustomSearchTest().test1();
    }
}
