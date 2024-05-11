package top.ezadmin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.ezadmin.common.utils.DESUtils;

@Component
@Order(1)
class SaasListener implements CommandLineRunner {

    @Value("${topezadmin.password:123123}")
    private String ezadminPassword;
//    @Value("${tencent.smsid}")
//    private String smsid;
//    @Value("${tencent.smskey}")
//    private String smskey;
//    @Value("${tencent.appid}")
//    private String appid;

    @Override
    public void run(String... args) throws Exception {
        DESUtils.init(ezadminPassword);
//        SendSms.init(smsid,smskey,appid);

    }
}
