package com.ezadmin.biz.emmber;

import com.ezadmin.common.utils.SqlUtils;
import com.ezadmin.common.utils.Utils;

import java.util.Map;

/**
 *
 **/
public class EzForm {

    private static void fillItemValue(Map<String, String> request, Map<String, String> item) {
        String key = Utils.trimNull(item.get("ITEM_NAME"));
        item.put(SqlUtils._ITEM_VALUE_KEY, request.get(key));
        item.put(SqlUtils._ITEM_VALUE_START_KEY, request.get(key + "_START"));
        item.put(SqlUtils._ITEM_VALUE_END_KEY, request.get(key + "_END"));
        item.put(SqlUtils._ITEM_VALUE_ORDER_KEY, Utils.trimNull(request.get(key + "_ORDER")));
    }
}
