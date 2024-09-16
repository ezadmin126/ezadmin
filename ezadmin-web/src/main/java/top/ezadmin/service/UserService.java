package top.ezadmin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.ezadmin.blog.model.User;
import top.ezadmin.common.utils.DESUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.dao.model.Info;
import top.ezadmin.domain.mapper.*;
import top.ezadmin.domain.mapper.ext.SysUserExtMapper;
import top.ezadmin.domain.model.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.*;

@Service
public class UserService {
    @Resource
    SysUserMapper userMapper;
    @Resource
    SysUserExtMapper sysUserExtMapper;
    @Resource
    SysCompanyMapper sysCompanyMapper;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    SysRoleMapper sysRoleMapper;
    @Resource
    SysRoleResourceMapper sysRoleResourceMapper;
    @Resource
    SysResourceMapper sysResourceMapper;
    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;

    @Transactional
    public User doReg(String username, String password) throws Exception {
        //初始化
        SysUser sysUser=new SysUser();
        sysUser.setUserName(username);
        sysUser.setPassword(DESUtils.encrypt(password));
        SysCompany company=new SysCompany();
        company.setCompanyName(username);
        company.setAddTime(new Date());
        sysCompanyMapper.insert(company);
        sysUser.setCompanyId(company.getCompanyId());
        sysUser.setDeleteFlag(0);
        sysUser.setStatus(1);
        userMapper.insert(sysUser);

        SysRole role=new SysRole();
        role.setCompanyId(sysUser.getCompanyId());
        role.setAddTime(new Date());
        role.setRoleName("注册");
        role.setParentRoleId(0L);
        role.setRoleWeight(1);
        role.setDeleteFlag(0);
        role.setRoleDesc("初始化");

        sysRoleMapper.insert(role);
        SysUserRole sysUserRole=new SysUserRole();
        sysUserRole.setUserId(sysUser.getUserId());
        sysUserRole.setRoleId(role.getRoleId());
        sysUserRole.setAddTime(new Date());
        sysUserRole.setDeleteFlag(0);
        sysUserRoleMapper.insert(sysUserRole);

        List<SysResource> resources=sysUserExtMapper.selectUserResourcesByParentId(2l,0L);
        doResource(resources,sysUser.getCompanyId(),role.getRoleId());
        User user = new User();
        user.setUserId(sysUser.getUserId());
        user.setUserName(sysUser.getUserName());
        user.setCompanyName(sysUser.getCompanyId()+"");
        user.setCompanyId( sysUser.getCompanyId());
        user.setParentId(sysUser.getParentId());
        user.setRoleNames(sysUserExtMapper.selectUserRoles(sysUser.getUserId()));
        user.setResourceIds(sysUserExtMapper.selectUserResources(sysUser.getUserId()));
        return user;
    }

    void doResource(List<SysResource> resources,Long companyId,Long roleId){
        if(Utils.isEmpty(resources)){
            return;
        }
        resources.forEach(item->{
            Long resourceId=item.getResourceId();
            item.setResourceId(null);
            item.setCompanyId(companyId);
            item.setAddTime(new Date());
            item.setDeleteFlag(0);
            sysResourceMapper.insert(item);
            SysRoleResource sysRoleResource=new SysRoleResource();
            sysRoleResource.setRoleId(roleId);
            sysRoleResource.setResourceId(item.getResourceId());
            sysRoleResource.setAddTime(new Date());
            sysRoleResource.setDeleteFlag(0);
            sysRoleResourceMapper.insert(sysRoleResource);
            List<SysResource> resources2=sysUserExtMapper.selectUserResourcesByParentId(2l,resourceId);
            if(!Utils.isEmpty(resources2)){
                resources2.forEach(cc->{
                    cc.setParentResourceId(item.getResourceId());
                });
                doResource(resources2,companyId,roleId);
            }
        });
    }
}
