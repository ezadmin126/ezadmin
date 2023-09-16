package com.ezadmin.plugins.express.executor;

import com.ezadmin.biz.emmber.list.EzListDTO;
import com.ezadmin.common.utils.Utils;

import java.util.Map;

/**
 *
 */
public class ListExpressExecutor extends EzExpressExecutor {


    public static ListExpressExecutor createInstance(){
        return new ListExpressExecutor();
    }


    public ListExpressExecutor listDTOAndSearchParam(Map<String, Object> dto){
        getOperatorParam().setListDto(dto);
        return this;
    }



    public Object executeCount() throws Exception {
        getOperatorParam().setCount(true);
        run(express );
        return result;
    }

}
