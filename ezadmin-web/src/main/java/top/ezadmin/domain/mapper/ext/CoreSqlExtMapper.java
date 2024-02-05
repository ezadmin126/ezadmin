package top.ezadmin.domain.mapper.ext;

import top.ezadmin.domain.model.CoreSql;
import top.ezadmin.domain.mapper.CoreSqlMapper;
import org.apache.ibatis.annotations.Param;

public interface CoreSqlExtMapper extends CoreSqlMapper {
        CoreSql selectOneSql(@Param("encode") String encode, @Param("appName") String appName);
}
