package top.ezadmin.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import top.ezadmin.domain.model.BaseSupplierAddress;
import top.ezadmin.domain.model.BaseSupplierAddressExample;

public interface BaseSupplierAddressMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    long countByExample(BaseSupplierAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int deleteByExample(BaseSupplierAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int deleteByPrimaryKey(Long supplierAddressId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int insert(BaseSupplierAddress record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int insertSelective(BaseSupplierAddress record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    List<BaseSupplierAddress> selectByExample(BaseSupplierAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    BaseSupplierAddress selectByPrimaryKey(Long supplierAddressId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByExampleSelective(@Param("record") BaseSupplierAddress record, @Param("example") BaseSupplierAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByExample(@Param("record") BaseSupplierAddress record, @Param("example") BaseSupplierAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByPrimaryKeySelective(BaseSupplierAddress record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER_ADDRESS
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByPrimaryKey(BaseSupplierAddress record);
}