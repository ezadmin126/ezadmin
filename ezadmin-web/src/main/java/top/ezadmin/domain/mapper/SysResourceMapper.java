package top.ezadmin.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import top.ezadmin.domain.model.SysResource;
import top.ezadmin.domain.model.SysResourceExample;

public interface SysResourceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    long countByExample(SysResourceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int deleteByExample(SysResourceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int deleteByPrimaryKey(Long resourceId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int insert(SysResource record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int insertSelective(SysResource record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    List<SysResource> selectByExample(SysResourceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    SysResource selectByPrimaryKey(Long resourceId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int updateByExampleSelective(@Param("record") SysResource record, @Param("example") SysResourceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int updateByExample(@Param("record") SysResource record, @Param("example") SysResourceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int updateByPrimaryKeySelective(SysResource record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_SYS_RESOURCE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    int updateByPrimaryKey(SysResource record);
}