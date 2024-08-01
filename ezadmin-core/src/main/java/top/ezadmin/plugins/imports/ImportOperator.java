package top.ezadmin.plugins.imports;

import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.AbstractOperator;
import top.ezadmin.plugins.express.OperatorParam;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.SpringContextHolder;

public class ImportOperator extends AbstractOperator {
    public ImportOperator( ) {

    }
    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
           Object cl= SpringContextHolder.getBean(operatorParam.getRequestParams().get("importservice")+"");
           if(cl instanceof  EzImport){
              return ((EzImport)cl).doImport(operatorParam.getRequestParams());
           }
return EzResult.instance().fail("找不到导入实例，请给按钮添加importservice属性");
    }

}