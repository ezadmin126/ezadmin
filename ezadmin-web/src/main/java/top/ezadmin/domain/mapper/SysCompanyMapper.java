package top.ezadmin.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import top.ezadmin.domain.model.SysCompany;
import top.ezadmin.domain.model.SysCompanyExample;

public interface SysCompanyMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    long countByExample(SysCompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int deleteByExample(SysCompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int deleteByPrimaryKey(Long companyId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int insert(SysCompany record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int insertSelective(SysCompany record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    List<SysCompany> selectByExample(SysCompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    SysCompany selectByPrimaryKey(Long companyId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int updateByExampleSelective(@Param("record") SysCompany record, @Param("example") SysCompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int updateByExample(@Param("record") SysCompany record, @Param("example") SysCompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int updateByPrimaryKeySelective(SysCompany record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_COMPANY
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int updateByPrimaryKey(SysCompany record);
}