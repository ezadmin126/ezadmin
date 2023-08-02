package com.ezadmin.plugins.express;

import com.ezadmin.biz.dao.Dao;
import com.ezadmin.common.utils.DESUtils;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.plugins.parser.CommentsSqlParser;
import com.ezadmin.plugins.parser.parse.ResultModel;

public class EncodeOperator extends AbstractOperator {



    @Override
    public Object executeInner(Object[] objects) throws Exception {
        return DESUtils.encryptDES(objects[0]);
    }
}
