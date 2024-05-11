package top.ezadmin.blog.model;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * : User
 * @author Hank.he
 * @since: 2022/2/12 20:08
 */

public class User {
    private Long userId;
    private Long parentId;
    private Long companyId;
    private String companyName;
    private String userName;
    private String userKey;
    private List<String> roleNames=new ArrayList<>();
    private List<String> resourceIds;

    private List<Long> parentUserIdList;
    private List<Long> childUserIdList;
    private List<String> parentUserNameList;

    private Long orgId;


    public boolean hasRole(String role){
        if(StringUtils.isEmpty(role)){
            return false;
        }
        String ro[]=role.split(",");
        for (int i = 0; i < ro.length; i++) {
            if(roleNames.contains(ro[i])){
                return true;
            }
        }
            return false;
    }
    public  boolean hasChild(Long cuserId){
        if(cuserId==null){
            return false;
        }
        if(userId.longValue()==cuserId.longValue()){
            return true;
        }
        if(CollectionUtils.isEmpty(childUserIdList)){
            return false;
        }
        return childUserIdList.contains(cuserId);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    public List<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<String> resourceIds) {
        this.resourceIds = resourceIds;
    }


//    public List<Long> getParentUserIdList() {
//        return parentUserIdList;
//    }
//
//    public void setParentUserIdList(List<Long> parentUserIdList) {
//        this.parentUserIdList = parentUserIdList;
//    }
//
//    public List<String> getParentUserNameList() {
//        return parentUserNameList;
//    }
//
//    public void setParentUserNameList(List<String> parentUserNameList) {
//        this.parentUserNameList = parentUserNameList;
//    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    private String orgName;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Deprecated
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<Long> getChildUserIdList() {
        return childUserIdList;
    }

    public void setChildUserIdList(List<Long> childUserIdList) {
        this.childUserIdList = childUserIdList;
    }
}
