package top.ezadmin.web.safe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RuleHolder {
    private static final Logger logger = LoggerFactory.getLogger(RuleHolder.class);

    private  static UrlLimitRule urlLimitRule =null;
    private  static IpPoolRule ipPoolRule =null;
    private  static IpPeriodRule ipPeriodRule =null;

    private static RuleHolder holder=new RuleHolder();

    private RuleHolder() {}

    public static RuleHolder getInstance() {
        return holder;
    }

    public  void init(SafeCallback urlLimitCallback, SafeCallback ipPoolCallback
            , SafeCallback nightLimitCallback, SafeCallback ipPeriodCallback){
        if(urlLimitRule!=null){
            return;
        }
        urlLimitRule = new UrlLimitRule();
        urlLimitRule.init(urlLimitCallback);

        ipPoolRule=new IpPoolRule();
        ipPoolRule.init(ipPoolCallback);

        ipPeriodRule=new IpPeriodRule();
        ipPeriodRule.init(ipPeriodCallback);
    }

    public boolean onMessage(IpActionDto dto){
       // logger.info("收到消息{}", JSONUtils.toJSONString(dto));
        urlLimitRule.onMessage(dto);
        ipPoolRule.onMessage(dto);
        ipPeriodRule.onMessage(dto);
        return false;
    }



    public void clear(String ip) {
        urlLimitRule.clear(ip);
        ipPoolRule.clear(ip);
        ipPeriodRule.clear(ip);
    }

    public String print() {
      return "url:" + urlLimitRule.print()+"<br>pool:"+
        ipPoolRule.print()+"<br>night:"+
        "<br>Period:<br>"+
              ipPeriodRule.print()+"<br> " ;
    }
}
