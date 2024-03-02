package top.ezadmin.domain.mapper.ext;

import top.ezadmin.blog.vo.BlogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogExtendMapper {
    List<BlogVO> search(@Param("keyword") String keyword,
                        @Param("categoryId") Integer categoryId);

    List<BlogVO> hottest();

    List<BlogVO> newtest();
}