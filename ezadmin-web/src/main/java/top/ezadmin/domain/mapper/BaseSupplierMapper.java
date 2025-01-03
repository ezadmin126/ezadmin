package top.ezadmin.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import top.ezadmin.domain.model.BaseSupplier;
import top.ezadmin.domain.model.BaseSupplierExample;

public interface BaseSupplierMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    long countByExample(BaseSupplierExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int deleteByExample(BaseSupplierExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int deleteByPrimaryKey(Long supplierId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int insert(BaseSupplier record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int insertSelective(BaseSupplier record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    List<BaseSupplier> selectByExample(BaseSupplierExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    BaseSupplier selectByPrimaryKey(Long supplierId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByExampleSelective(@Param("record") BaseSupplier record, @Param("example") BaseSupplierExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByExample(@Param("record") BaseSupplier record, @Param("example") BaseSupplierExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByPrimaryKeySelective(BaseSupplier record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_BASE_SUPPLIER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByPrimaryKey(BaseSupplier record);
}