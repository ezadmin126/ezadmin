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
import java.util.Map;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.utils.NoDirect;

@Service("orderService")
@Slf4j
public class EzSaleorderService {


    @Resource
    private SaleorderMapper saleorderMapper;
    @Resource
    private SaleorderGoodsMapper saleorderGoodsMapper;
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

    @Transactional(rollbackFor = Exception.class)
    public void doUpdate(Map<String, Object> request){
        log.info("add order:{}",JSONUtils.toJSONString(request));

        Saleorder saleorder = new Saleorder();
        saleorder.setTraderId(Utils.toLong(request.get("TRADER_ID")));
        saleorder.setTraderAddressId(Utils.toLong(request.get("TRADER_ADDRESS_ID")));
        saleorder.setTraderContactId(Utils.toLong(request.get("TRADER_CONTACT_ID")));
        saleorder.setUserId(Utils.toLong(request.get("USER_ID")));
        saleorder.setTraderComments(Utils.trimNull(request.get("TRADER_COMMENTS")));
        saleorder.setAdditionalClause(Utils.trimNull(request.get("ADDITIONAL_CLAUSE")));
        saleorder.setComments(Utils.trimNull(request.get("COMMENTS")));
        saleorder.setAddTime(new Date());
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
        saleorder.setAddId(user.getUserId());
        saleorder.setUpdateId(user.getUserId());
        saleorder.setCompanyId(user.getCompanyId());
        saleorder.setSaleorderNo(NoDirect.saleorderNo());
        saleorderMapper.insert(saleorder);



        //EZ_SESSION_USER_NAME_KEY EZ_SESSION_USER_ID_KEY
        String[] basePriceArrays=(String[])  request.get("BASE_PRICE_ARRAY");
        String[] prodIdArrays=(String[])  request.get("CHILD_PROD_ID_ARRAY");
        String[] childNumArrays=(String[])  request.get("CHILD_NUM_ARRAY");
        BigDecimal totalPrice=BigDecimal.ZERO;
        if(!ArrayUtils.isEmpty(prodIdArrays)){
            for (int i = 0; i < prodIdArrays.length; i++) {
                SaleorderGoods saleorderGoods = new SaleorderGoods();
                saleorderGoods.setSaleorderId(saleorder.getSaleorderId());
                saleorderGoods.setAddTime(new Date());
                saleorderGoods.setUpdateTime(new Date());
                saleorderGoods.setUpdateId(user.getUserId());

                saleorderGoods.setProdId(Utils.toLong(prodIdArrays[i]));
                saleorderGoods.setBasePrice(new BigDecimal(basePriceArrays[i]));
                saleorderGoods.setProdNum(Utils.toInt(childNumArrays[i]));
                BaseProduct baseProduct= baseProductMapper.selectByPrimaryKey(saleorderGoods.getProdId());
                saleorderGoods.setProdCode(baseProduct.getProdCode());
                saleorderGoods.setProdModelSpec(baseProduct.getProdModelSpec());
                saleorderGoods.setProdName(baseProduct.getProdName());

                BaseBrand brand= baseBrandMapper.selectByPrimaryKey(baseProduct.getBrandId());
                BaseUnit unit= baseUnitMapper.selectByPrimaryKey(baseProduct.getBaseUnitId());
                saleorderGoods.setUnitName(unit.getUnitName());
                saleorderGoods.setBrandName(brand.getBrandName());
                totalPrice= totalPrice.add(saleorderGoods.getBasePrice().multiply(new BigDecimal(saleorderGoods.getProdNum()+"")));
                saleorderGoodsMapper.insert(saleorderGoods);
            }
            Saleorder saleorderUpdate = new Saleorder();
            saleorderUpdate.setSaleorderId(saleorder.getSaleorderId());
            saleorderUpdate.setTotalAmount(totalPrice);
            saleorderMapper.updateByPrimaryKeySelective(saleorderUpdate);
        }
    }
}
