package com.ezadmin.plugins.express.executor;

import com.ezadmin.biz.emmber.list.EzListDTO;
import com.ezadmin.common.utils.Utils;

/**
 *
 */
public class ListExpressExecutor extends EzExpressExecutor {


    public static ListExpressExecutor createInstance(){
        return new ListExpressExecutor();
    }


    public ListExpressExecutor listDTOAndSearchParam(EzListDTO dto){
        getOperatorParam().setListDTO(dto);
//        dto.getSearchItemList().forEach(item->{
//            if(item.getPluginCode().equalsIgnoreCase("hidden-nowhere")){
//                //不拼接where
//                return;
//            }
//            addParam(item.getItemName(),item.getItemParamValue());
//            addParam(item.getItemNameStart(),item.getItemParamValueStart());
//            addParam(item.getItemNameEnd(),item.getItemParamValueEnd());
//            addParam(item.getItemNameOrder(),item.getItemParamOrderValue());
//            addParam("itemSearchKey",item.getItemSearchKey());
//            addParam("itemSearchValue", Utils.trimNull(item.getItemSearchValue()));
//            addParam("itemSearchConcatValue",Utils.trimNull(item.getItemSearchConcatValue()));
//            addParam("itemSearchDateValueStart",Utils.trimNull(item.getItemSearchDateValueStart()));
//            addParam("itemSearchDateValueEnd",Utils.trimNull(item.getItemSearchDateValueEnd()));
//            addParam("itemSearchDateKey",Utils.trimNull(item.getItemSearchDateKey()));
//        });
        return this;
    }



    public Object executeCount() throws Exception {
        getOperatorParam().setCount(true);
        run(express );
        return result;
    }

}
