package top.ezadmin.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.model.User;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.Dao;
import top.ezadmin.domain.mapper.ext.BaseProductExtendMapper;
import top.ezadmin.domain.model.BaseProduct;
import top.ezadmin.domain.model.ext.BaseProductExtend;
import top.ezadmin.domain.mapper.BaseProductMapper;
import top.ezadmin.domain.model.BaseProductExample;
import top.ezadmin.web.EzResult;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/jxc")
@Nologin
public class JxcController extends CustomBaseController {
    @Autowired
    BaseProductExtendMapper baseProductExtendMapper;
    @Autowired
    DataSource dataSource;
    @RequestMapping("/product/search.html")
    @Nologin
    @ResponseBody
    public EzResult search(HttpServletRequest request,String id, String keyword, String page) {
        User user = getSessionUser();
        List<Map<String, String>> result = new ArrayList<>();
        Page page2 = PageHelper.startPage(NumberUtils.toInt(page + "", 1), 6);
        if (StringUtils.isNotBlank(keyword)) {
            List<BaseProductExtend> list = baseProductExtendMapper.selectBaseProduct(user.getCompanyId(), keyword);
            list.forEach(item -> {
                Map<String, String> m = new HashMap<>();
                m.put("K", item.getProdId() + "");
                m.put("V", item.getProdCode() + "-" + item.getProdName());
                result.add(m);
            });
        }
        PageHelper.clearPage();
        if (StringUtils.isNotBlank(id)) {
            BaseProduct baseProduct=baseProductExtendMapper.selectByPrimaryKey(Long.parseLong(id));
            Map<String, String> m = new HashMap<>();
            m.put("K", baseProduct.getProdId() + "");
            m.put("V", baseProduct.getProdCode() + "-" + baseProduct.getProdName());
            result.add(m);
        }
        EzPage p = new EzPage(NumberUtils.toInt(page + "", 1), 6, "");
        p.setTotalRecord(page2.getTotal());

        return EzResult.instance().count(p.getTotalPage()).data(result);
    }


    @RequestMapping(value = { "/trader/addressAndContact.html"})
    @ResponseBody
    @Nologin
    public EzResult se(HttpServletRequest request, Long  TRADER_ID) throws Exception {
        User user = getSessionUser();
        List<Map<String,Object>> kv= Dao.getInstance().executeQuery(dataSource,"select  A.TRADER_ADDRESS_ID K, CONCAT(coalesce(C.REGION_FULL_NAME),\n" +
                "    coalesce(B.REGION_FULL_NAME),coalesce(TBR.REGION_FULL_NAME),'  ' \n" +
                "        ,coalesce(A.TRADER_ADDRESS)) V,A.TRADER_ADDRESS,A.REGION_ID,A.CITY_ID,A.PROVICE_ID\n" +
                "from T_BASE_TRADER_ADDRESS A\n" +
                "    LEFT JOIN T_BASE_REGION TBR on A.REGION_ID = TBR.REGION_FULL_ID\n" +
                "    LEFT JOIN T_BASE_REGION B ON TBR.PARENT_ID=B.REGION_FULL_ID\n" +
                "    LEFT JOIN T_BASE_REGION C ON B.PARENT_ID=C.REGION_FULL_ID\n" +
                "WHERE  A.TRADER_ID=? AND A.COMPANY_ID=? AND A.DELETE_FLAG=0",new Object[]{TRADER_ID,user.getCompanyId()});

        List<Map<String,Object>> kv2= Dao.getInstance().executeQuery(dataSource,"select A.TRADER_CONTACT_ID K,CONCAT(A.CONTACT_NAME,'/',A.CONTACT_MOBILE)  V " +
                "from T_BASE_TRADER_CONTACT A WHERE A.TRADER_ID=? AND A.COMPANY_ID=? " +
                "AND A.DELETE_FLAG=0",new Object[]{TRADER_ID,user.getCompanyId()});

        //  BaseTrader trader= baseTraderMapper.selectByPrimaryKey(TRADER_ID);

        return EzResult.instance().data("address",JSONUtils.toJSONString(kv) )
                .data("contact", JSONUtils.toJSONString(kv2))
                ; // .data("traderTmp",Utils.trimNull(trader.getTraderTmp()));
    }

}
