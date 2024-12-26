package top.ezadmin.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.ezadmin.common.utils.ArrayUtils;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.domain.mapper.*;
import top.ezadmin.domain.model.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.utils.NoDirect;

@Service("orderService")
@Slf4j
public class EzSaleorderService {


    @Resource
    private JxcSaleorderMapper saleorderMapper;
    @Resource
    private JxcSaleorderGoodsMapper saleorderGoodsMapper;
    @Resource
    BaseTraderMapper  baseTraderMapper;
    @Resource
    BaseTraderContactMapper baseTraderContactMapper;
    @Resource
    BaseTraderAddressMapper baseTraderAddressMapper;
    @Resource
    SysUserMapper sysUserMapper;
    @Resource
    BaseProductMapper baseProductMapper;
    @Resource
    BaseBrandMapper baseBrandMapper;
    @Resource
    BaseUnitMapper baseUnitMapper;

    public Object exe(String express){
            return express;
    }

    @Transactional(rollbackFor = Exception.class)
    public void doUpdate(Map<String, Object> request){
       // log.info("add order:{}",JSONUtils.toJSONString(request));

        JxcSaleorder saleorder = new JxcSaleorder();
        saleorder.setTraderId(Utils.toLong(request.get("TRADER_ID")));
        saleorder.setTraderAddressId(Utils.toLong(request.get("TRADER_ADDRESS_ID")));
        saleorder.setTraderContactId(Utils.toLong(request.get("TRADER_CONTACT_ID")));

        saleorder.setTakeTraderId(Utils.toLong(request.get("TAKE_TRADER_ID")));
        saleorder.setTakeTraderAddressId(Utils.toLong(request.get("TAKE_TRADER_ADDRESS_ID")));
        saleorder.setTakeTraderContactId(Utils.toLong(request.get("TAKE_TRADER_CONTACT_ID")));

        saleorder.setInvoiceTraderId(Utils.toLong(request.get("INVOICE_TRADER_ID")));
        saleorder.setInvoiceTraderAddressId(Utils.toLong(request.get("INVOICE_TRADER_ADDRESS_ID")));
        saleorder.setInvoiceTraderContactId(Utils.toLong(request.get("INVOICE_TRADER_CONTACT_ID")));
        saleorder.setInvoiceType(Utils.toInt(request.get("INVOICE_TYPE")));
        saleorder.setInvoiceEmail(Utils.trimNull(request.get("INVOICE_EMAIL")));

        saleorder.setLogisticsComments(Utils.trimNull(request.get("LOGISTICS_COMMENTS")));

        saleorder.setUserId(Utils.toLong(request.get("USER_ID")));
        saleorder.setValidUserId(Utils.toLong(request.get("VALID_USER_ID")));
        saleorder.setTraderComments(Utils.trimNull(request.get("TRADER_COMMENTS")));
        saleorder.setAdditionalClause(Utils.trimNull(request.get("ADDITIONAL_CLAUSE")));
        saleorder.setComments(Utils.trimNull(request.get("COMMENTS")));

        saleorder.setUpdateTime(new Date());


        BaseTrader trader= baseTraderMapper.selectByPrimaryKey(saleorder.getTraderId());
        saleorder.setTraderName(trader.getTraderName());

        BaseTraderContact traderContact= baseTraderContactMapper.selectByPrimaryKey(saleorder.getTraderContactId());
        saleorder.setTraderContactName(traderContact.getContactName());
        saleorder.setTraderContactMobile(traderContact.getContactMobile());

        BaseTraderAddress traderAddress= baseTraderAddressMapper.selectByPrimaryKey(saleorder.getTraderAddressId());
        saleorder.setTraderAddress(traderAddress.getTraderAddress());
        saleorder.setTraderRegion(traderAddress.getRegionId());




        SysUser user= sysUserMapper.selectByPrimaryKey(Utils.toLong(request.get("EZ_SESSION_USER_ID_KEY")));

        saleorder.setUpdateId(user.getUserId());
        saleorder.setCompanyId(user.getCompanyId());
       Long ID= Utils.toLong(request.get("ID"));
        if(ID>0){
            saleorder.setSaleorderId(ID);
            saleorderMapper.updateByPrimaryKeySelective(saleorder);
        }
        else{
//            saleorder.setAddId(user.getUserId());
//            saleorder.setAddTime(new Date());
//            saleorder.setSaleorderNo(NoDirect.saleorderNo());
//            saleorderMapper.insertSelective(saleorder);
        }
        BigDecimal totalPrice=BigDecimal.ZERO;
        JxcSaleorderGoodsExample example=new JxcSaleorderGoodsExample();
        example.createCriteria().andDeleteFlagEqualTo(0).andSaleorderIdEqualTo(saleorder.getSaleorderId());
        List<JxcSaleorderGoods> goodsList= saleorderGoodsMapper.selectByExample(example);
        if(Utils.isNotEmpty(goodsList)){
            for (int i = 0; i < goodsList.size(); i++) {
                totalPrice=totalPrice.add(goodsList.get(i).getBasePrice().multiply(new BigDecimal(goodsList.get(i).getProdNum())));
                BaseProduct baseProduct= baseProductMapper.selectByPrimaryKey(goodsList.get(i).getProdId());
                BaseBrand brand= baseBrandMapper.selectByPrimaryKey(baseProduct.getBrandId());
                BaseUnit unit= baseUnitMapper.selectByPrimaryKey(baseProduct.getBaseUnitId());
                goodsList.get(i).setUnitName(unit.getUnitName());
                goodsList.get(i).setBrandName(brand.getBrandName());
                saleorderGoodsMapper.updateByPrimaryKeySelective(goodsList.get(i));
            }
        }
        JxcSaleorder saleorderUpdate = new JxcSaleorder();
        saleorderUpdate.setSaleorderId(saleorder.getSaleorderId());
        saleorderUpdate.setTotalAmount(totalPrice);
        saleorderMapper.updateByPrimaryKeySelective(saleorderUpdate);
    }
}
