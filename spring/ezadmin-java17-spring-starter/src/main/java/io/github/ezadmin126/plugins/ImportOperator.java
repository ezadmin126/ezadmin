package io.github.ezadmin126.plugins;

import io.github.ezadmin126.spring.SpringContextHolder;
import io.github.ezadmin126.common.utils.Utils;
import io.github.ezadmin126.plugins.express.AbstractOperator;
import io.github.ezadmin126.plugins.express.OperatorParam;
import io.github.ezadmin126.plugins.imports.EzImport;
import io.github.ezadmin126.web.EzResult;

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