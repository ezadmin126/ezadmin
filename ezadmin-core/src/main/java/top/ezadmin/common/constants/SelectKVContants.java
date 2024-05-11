package top.ezadmin.common.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 **/
public class SelectKVContants {
    public static void main(String[] args) {
        System.out.println("asdf~!~safsd,asdf".split("~!~|,")[2]);
    }
    public final static String KEY="K";
    public final static String VALUE="V";
    public final static String SPLIT="~!~";
    private final static  List<Map<String, Object>> KV_STATUS = new ArrayList<>();
    private final static  List<Map<String, Object>> KV_DELETE_FLAG = new ArrayList<>();
    private final static  List<Map<String, Object>> KV_YESNO = new ArrayList<>();
    static{
        Map<String, Object> map = new HashMap<>();
                map.put(SelectKVContants.KEY , "0");
                map.put(SelectKVContants.VALUE, "禁用");

        Map<String, Object> map2 = new HashMap<>();
                map2.put(SelectKVContants.KEY, "1");
                map2.put(SelectKVContants.VALUE, "启用");

        KV_STATUS.add(map2);
        KV_STATUS.add(map);




        Map<String, Object> map3 = new HashMap<>();
        map3.put(SelectKVContants.KEY, "0");
        map3.put(SelectKVContants.VALUE, "否");

        Map<String, Object> mapx4 = new HashMap<>();
        mapx4.put(SelectKVContants.KEY, "1");
        mapx4.put(SelectKVContants.VALUE, "是");
        KV_YESNO.add(mapx4);
        KV_YESNO.add(map3);


        Map<String, Object> map6 = new HashMap<>();
        map6.put(SelectKVContants.KEY , "1");
        map6.put(SelectKVContants.VALUE, "禁用");

        Map<String, Object> map7 = new HashMap<>();
        map7.put(SelectKVContants.KEY, "0");
        map7.put(SelectKVContants.VALUE, "启用");
        KV_DELETE_FLAG.add(map6);
        KV_DELETE_FLAG.add(map7);
    }
    public static  List<Map<String, Object>> KV_STATUS(){
        return KV_STATUS;
    }
    public static  List<Map<String, Object>> KV_YESNO(){
        return KV_YESNO;
    }
    public static  List<Map<String, Object>> KV_DELETE_FLAG(){
        return KV_DELETE_FLAG;
    }




}
