package top.ezadmin.domain.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import top.ezadmin.domain.model.JxcBuyorder;
import top.ezadmin.domain.model.JxcBuyorderExample;

public interface JxcBuyorderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    long countByExample(JxcBuyorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int deleteByExample(JxcBuyorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int deleteByPrimaryKey(Long buyorderId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int insert(JxcBuyorder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int insertSelective(JxcBuyorder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    List<JxcBuyorder> selectByExample(JxcBuyorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    JxcBuyorder selectByPrimaryKey(Long buyorderId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByExampleSelective(@Param("record") JxcBuyorder record, @Param("example") JxcBuyorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByExample(@Param("record") JxcBuyorder record, @Param("example") JxcBuyorderExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByPrimaryKeySelective(JxcBuyorder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table T_JXC_BUYORDER
     *
     * @mbg.generated Fri Dec 27 21:36:51 CST 2024
     */
    int updateByPrimaryKey(JxcBuyorder record);
}