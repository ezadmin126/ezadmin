package top.ezadmin.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import top.ezadmin.domain.model.Saleorder;
import top.ezadmin.domain.model.SaleorderExample;

public interface SaleorderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    long countByExample(SaleorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    int deleteByExample(SaleorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    int deleteByPrimaryKey(Long saleorderId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    int insert(Saleorder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    int insertSelective(Saleorder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    List<Saleorder> selectByExample(SaleorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    Saleorder selectByPrimaryKey(Long saleorderId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    int updateByExampleSelective(@Param("record") Saleorder record, @Param("example") SaleorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    int updateByExample(@Param("record") Saleorder record, @Param("example") SaleorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    int updateByPrimaryKeySelective(Saleorder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SALEORDER
     *
     * @mbg.generated Mon Oct 07 20:25:57 CST 2024
     */
    int updateByPrimaryKey(Saleorder record);
}