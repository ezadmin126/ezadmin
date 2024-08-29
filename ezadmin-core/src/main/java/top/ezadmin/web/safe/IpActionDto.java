package top.ezadmin.web.safe;

import java.util.Map;



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
    //type  1.正常请求  2.攻击请求 3.白名单
    private String ty;
    //time
    private Long t;

    public boolean isWhite(){
        return "3".equals(ty);
    }
    public boolean isAttach(){
        return "2".equals(ty);
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public Map<String, String> getC() {
        return c;
    }

    public void setC(Map<String, String> c) {
        this.c = c;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRe() {
        return re;
    }

    public void setRe(String re) {
        this.re = re;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getAg() {
        return ag;
    }

    public void setAg(String ag) {
        this.ag = ag;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getTy() {
        return ty;
    }

    public void setTy(String ty) {
        this.ty = ty;
    }

    public Long getT() {
        return t;
    }

    public void setT(Long t) {
        this.t = t;
    }
}