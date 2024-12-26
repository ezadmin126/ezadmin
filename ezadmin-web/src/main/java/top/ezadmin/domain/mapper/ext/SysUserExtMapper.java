package top.ezadmin.domain.mapper.ext;

import org.apache.ibatis.annotations.Param;
import top.ezadmin.domain.mapper.SysUserMapper;
import top.ezadmin.domain.model.SysResource;
import top.ezadmin.domain.model.SysUser;

import java.util.List;

public interface SysUserExtMapper extends SysUserMapper {
    List<String> selectUserRoles(Long userId);

    List<String> selectUserResources(Long userId);

     SysUser selectFirstUserByRoleName(@Param("roleName")String roleName, @Param("companyId")Long companyId);

    List<SysResource> selectUserResourcesByParentId(@Param("userId")Long userId, @Param("parentId") Long parentId);
}
