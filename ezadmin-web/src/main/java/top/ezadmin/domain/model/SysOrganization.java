package top.ezadmin.domain.model;

public class SysOrganization {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.ORG_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Integer orgId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.COMPANY_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Integer companyId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.PARENT_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Integer parentId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.ORG_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private String orgName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.LEVEL
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Byte level;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.TYPE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Integer type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.ADD_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Long addTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.CREATOR
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Integer creator;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.MOD_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Long modTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column T_SYS_ORGANIZATION.UPDATER
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    private Integer updater;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.ORG_ID
     *
     * @return the value of T_SYS_ORGANIZATION.ORG_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.ORG_ID
     *
     * @param orgId the value for T_SYS_ORGANIZATION.ORG_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.COMPANY_ID
     *
     * @return the value of T_SYS_ORGANIZATION.COMPANY_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Integer getCompanyId() {
        return companyId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.COMPANY_ID
     *
     * @param companyId the value for T_SYS_ORGANIZATION.COMPANY_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.PARENT_ID
     *
     * @return the value of T_SYS_ORGANIZATION.PARENT_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.PARENT_ID
     *
     * @param parentId the value for T_SYS_ORGANIZATION.PARENT_ID
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.ORG_NAME
     *
     * @return the value of T_SYS_ORGANIZATION.ORG_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.ORG_NAME
     *
     * @param orgName the value for T_SYS_ORGANIZATION.ORG_NAME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.LEVEL
     *
     * @return the value of T_SYS_ORGANIZATION.LEVEL
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Byte getLevel() {
        return level;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.LEVEL
     *
     * @param level the value for T_SYS_ORGANIZATION.LEVEL
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setLevel(Byte level) {
        this.level = level;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.TYPE
     *
     * @return the value of T_SYS_ORGANIZATION.TYPE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.TYPE
     *
     * @param type the value for T_SYS_ORGANIZATION.TYPE
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.ADD_TIME
     *
     * @return the value of T_SYS_ORGANIZATION.ADD_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Long getAddTime() {
        return addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.ADD_TIME
     *
     * @param addTime the value for T_SYS_ORGANIZATION.ADD_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.CREATOR
     *
     * @return the value of T_SYS_ORGANIZATION.CREATOR
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Integer getCreator() {
        return creator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.CREATOR
     *
     * @param creator the value for T_SYS_ORGANIZATION.CREATOR
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.MOD_TIME
     *
     * @return the value of T_SYS_ORGANIZATION.MOD_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Long getModTime() {
        return modTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.MOD_TIME
     *
     * @param modTime the value for T_SYS_ORGANIZATION.MOD_TIME
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setModTime(Long modTime) {
        this.modTime = modTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column T_SYS_ORGANIZATION.UPDATER
     *
     * @return the value of T_SYS_ORGANIZATION.UPDATER
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public Integer getUpdater() {
        return updater;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column T_SYS_ORGANIZATION.UPDATER
     *
     * @param updater the value for T_SYS_ORGANIZATION.UPDATER
     *
     * @mbg.generated Mon Sep 16 14:44:45 CST 2024
     */
    public void setUpdater(Integer updater) {
        this.updater = updater;
    }
}