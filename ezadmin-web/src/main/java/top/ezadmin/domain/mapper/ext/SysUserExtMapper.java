package top.ezadmin.domain.mapper.ext;

import org.apache.ibatis.annotations.Param;
import top.ezadmin.domain.model.SysResource;

import java.util.List;

public interface SysUserExtMapper {
    List<String> selectUserRoles(Long userId);

    List<String> selectUserResources(Long userId);

    List<SysResource> selectUserResourcesByParentId(@Param("userId")Long userId,@Param("parentId") Long parentId);
}
