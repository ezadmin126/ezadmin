package top.ezadmin.domain.mapper.ext;

import java.util.List;

public interface SysUserExtMapper {
    List<String> selectUserRoles(Long userId);
}
