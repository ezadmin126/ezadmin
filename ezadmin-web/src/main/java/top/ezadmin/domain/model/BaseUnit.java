package top.ezadmin.domain.model;

import java.util.Date;

public class BaseUnit {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.UNIT_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Long unitId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.UNIT_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private String unitName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.COMPANY_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Long companyId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.ADD_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Date addTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.ADD_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Long addId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.ADD_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private String addName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.UPDATE_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.UPDATE_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Long updateId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.UPDATE_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private String updateName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_BASE_UNIT.DELETE_FLAG
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Byte deleteFlag;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.UNIT_ID
     *
     * @return the value of T_BASE_UNIT.UNIT_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Long getUnitId() {
        return unitId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.UNIT_ID
     *
     * @param unitId the value for T_BASE_UNIT.UNIT_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.UNIT_NAME
     *
     * @return the value of T_BASE_UNIT.UNIT_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.UNIT_NAME
     *
     * @param unitName the value for T_BASE_UNIT.UNIT_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName == null ? null : unitName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.COMPANY_ID
     *
     * @return the value of T_BASE_UNIT.COMPANY_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Long getCompanyId() {
        return companyId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.COMPANY_ID
     *
     * @param companyId the value for T_BASE_UNIT.COMPANY_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.ADD_TIME
     *
     * @return the value of T_BASE_UNIT.ADD_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Date getAddTime() {
        return addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.ADD_TIME
     *
     * @param addTime the value for T_BASE_UNIT.ADD_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.ADD_ID
     *
     * @return the value of T_BASE_UNIT.ADD_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Long getAddId() {
        return addId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.ADD_ID
     *
     * @param addId the value for T_BASE_UNIT.ADD_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setAddId(Long addId) {
        this.addId = addId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.ADD_NAME
     *
     * @return the value of T_BASE_UNIT.ADD_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public String getAddName() {
        return addName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.ADD_NAME
     *
     * @param addName the value for T_BASE_UNIT.ADD_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setAddName(String addName) {
        this.addName = addName == null ? null : addName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.UPDATE_TIME
     *
     * @return the value of T_BASE_UNIT.UPDATE_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.UPDATE_TIME
     *
     * @param updateTime the value for T_BASE_UNIT.UPDATE_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.UPDATE_ID
     *
     * @return the value of T_BASE_UNIT.UPDATE_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Long getUpdateId() {
        return updateId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.UPDATE_ID
     *
     * @param updateId the value for T_BASE_UNIT.UPDATE_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.UPDATE_NAME
     *
     * @return the value of T_BASE_UNIT.UPDATE_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public String getUpdateName() {
        return updateName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.UPDATE_NAME
     *
     * @param updateName the value for T_BASE_UNIT.UPDATE_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setUpdateName(String updateName) {
        this.updateName = updateName == null ? null : updateName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_BASE_UNIT.DELETE_FLAG
     *
     * @return the value of T_BASE_UNIT.DELETE_FLAG
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Byte getDeleteFlag() {
        return deleteFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_BASE_UNIT.DELETE_FLAG
     *
     * @param deleteFlag the value for T_BASE_UNIT.DELETE_FLAG
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setDeleteFlag(Byte deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}