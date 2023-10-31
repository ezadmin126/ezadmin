import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @title: TestSlowlog
 * @Author Hank.he
 * @Date: 2023/10/17 10:23
 */
public class TestSlowlog {
    public static void main(String[] args) throws IOException {
        Path p= Paths.get("d:/ruoyi/slowquery.txt");
        List<String> list= Files.readAllLines(p);
        List<String> sqls=new ArrayList<>();
        StringBuilder sqlLine=new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).indexOf("# Time:")>=0){
                sqls.add(sqlLine.toString());
                sqlLine=new StringBuilder();
            }
            sqlLine.append(list.get(i));
        }
        Map<String,Integer> count=new LinkedHashMap<>();

        Map<String,BigDecimal> query_time_map=new LinkedHashMap<>();
        Map<String,BigDecimal> lock_time_map=new LinkedHashMap<>();
        Map<String,BigDecimal> row_send_map=new LinkedHashMap<>();
        Map<String,BigDecimal> row_ex_map=new LinkedHashMap<>();
        Map<String,BigDecimal> row_aff_map=new LinkedHashMap<>();

        List<String> backup=new ArrayList<>();
        for (int i = 0; i < sqls.size(); i++) {
            String x=sqls.get(i);
            String[] items=x.split("#");
            if(items.length>5){
                String time=items[1];
                String user=items[2];
                if(user.indexOf("backup")>=0
                ){
                    backup.add(x);
                    continue;
                }
                String schema=items[3];
                String querytime=items[4];
                String sql=items[5].replaceAll("\n","").toLowerCase();
//                System.out.println("i>>"+i+"\t"+time+"\t"+user+"\t"+schema+"\t"+querytime+"\t"
//                        +sql+"\t");
              //  int start=Math.max(sql.indexOf(";update"),Math.max(sql.indexOf(";select"),sql.indexOf(";delete")));
                String sqlinner="";
                if(sql.indexOf(";update")>0){
                    try {
                        sqlinner = sql.substring(sql.indexOf(";update"), sql.indexOf("=",sql.indexOf(";update")));
                    }catch (Exception e){
                        e.printStackTrace();;
                    }
                }
                if(sql.indexOf(";select")>0){
                    if(sql.lastIndexOf("where")==-1){
                        sqlinner=sql;
                    }else{
                        sqlinner=sql.substring(sql.indexOf(";select"), sql.lastIndexOf("where"));
                    }
                }
                if(sql.indexOf(";delete")>0){
                    if(sql.lastIndexOf("where")==-1){
                        sqlinner=sql;
                    }else{
                        sqlinner=sql.substring(sql.indexOf(";delete"), sql.lastIndexOf("where"));
                    }
                }

                if(!count.containsKey(sqlinner)){
                    count.put(sqlinner,1);
                }else{
                    count.put(sqlinner,count.get(sqlinner)+1);
                }
                String [] qqtime=querytime.split(" ");
                if(qqtime.length==15){
                    System.out.println(qqtime[0]);
                    BigDecimal query_time=new BigDecimal(qqtime[2]);
                    BigDecimal lock_time=new BigDecimal(qqtime[5]);
                    BigDecimal row_send=new BigDecimal(qqtime[8]);
                    BigDecimal row_ex=new BigDecimal(qqtime[11]);
                    BigDecimal row_aff=new BigDecimal(qqtime[14]);

                    mapmax(query_time_map, sqlinner,query_time );

                    mapmax(lock_time_map, sqlinner,lock_time );
                    mapmax(row_send_map, sqlinner,row_send );
                    mapmax(row_ex_map, sqlinner,row_ex );
                    mapmax(row_aff_map, sqlinner,row_aff );
                }

            }
        }
        System.out.println("count================================");
        sortMap(count).forEach((k,v)->{
            System.out.println(v+"\t"+k);
        });
        System.out.println("backup================================");
        backup.forEach(item->{
            System.out.println(item);
        });

        System.out.println("query_time_map================================");
        sortMap2(query_time_map).forEach((k,v)->{
            System.out.println(v+"\t"+k);
        });
        System.out.println("lock_time_map================================");

        sortMap2(lock_time_map).forEach((k,v)->{
            System.out.println(v+"\t"+k);
        });
        System.out.println("row_send_map================================");

        sortMap2(row_send_map).forEach((k,v)->{
            System.out.println(v+"\t"+k);
        });
        System.out.println("row_aff_map================================");

        sortMap2(row_ex_map).forEach((k,v)->{
            System.out.println(v+"\t"+k);
        });
        sortMap2(row_aff_map).forEach((k,v)->{
            System.out.println(v+"\t"+k);
        });
    }

    public static void mapmax(Map<String,BigDecimal> m,String k,BigDecimal b){
        if(!m.containsKey(k)){
            m.put(k,new BigDecimal(0));
        }
        m.put(k,  b.compareTo(m.get(k))>0?b:m.get(k));
    }

    public static Map<String, Integer> sortMap(Map<String, Integer> map) {
        Map<String, Integer> collect = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(oldVal, newVal) -> newVal, LinkedHashMap::new));
        return collect;

    }
    public static Map<String, BigDecimal> sortMap2(Map<String, BigDecimal> map) {
        Map<String, BigDecimal> collect = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(oldVal, newVal) -> newVal, LinkedHashMap::new));
        return collect;

    }
}
