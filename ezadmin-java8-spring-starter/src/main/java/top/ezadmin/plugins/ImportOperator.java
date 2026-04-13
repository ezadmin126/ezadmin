package top.ezadmin.plugins;


import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.AbstractOperator;
import top.ezadmin.plugins.express.OperatorParam;
import top.ezadmin.plugins.imports.EzImport;
import top.ezadmin.spring.SpringContextHolder;
import top.ezadmin.web.EzResult;

public class ImportOperator extends AbstractOperator {
    public ImportOperator() {

    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam = (OperatorParam) Utils.getParam();
        Object cl = SpringContextHolder.getBean(operatorParam.getRequestParams().get("importservice") + "");
        if (cl instanceof EzImport) {
            return ((EzImport) cl).doImport(operatorParam.getRequestParams());
        }
        return EzResult.instance().fail("找不到导入实例，请给按钮添加importservice属性");
    }

}