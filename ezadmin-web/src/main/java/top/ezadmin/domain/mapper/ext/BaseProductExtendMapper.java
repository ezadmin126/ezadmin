package top.ezadmin.domain.mapper.ext;

import org.apache.ibatis.annotations.Param;
import top.ezadmin.domain.model.ext.BaseProductExtend;

import java.util.List;

public interface BaseProductExtendMapper {

    List<BaseProductExtend> selectBaseProduct(@Param("companyId")  Long companyId, @Param("searchWord") String  searchWord
            );

}
