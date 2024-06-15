package top.ezadmin.ip;

import lombok.Data;

import java.util.Map;


@Data
public class IpActionDto {
    //app
    private String  a;
    //cookie
    private Map<String,String> c;
    //ip
    private String ip;
    //referer
    private String re;
    //url
    private String uri;
    //agent
    private String ag;
    //param
    private String p;
    //time
    private Long t;
}