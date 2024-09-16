package top.ezadmin.domain.model.ext;

import lombok.Data;
import top.ezadmin.domain.model.BaseProduct;

@Data
public class BaseProductExtend extends BaseProduct {

    private Integer prodNum;
    private Long tmFileId;
    private String spaceName;
    private Long parentId;
    private Long grandParentId;
    private Long parentOrigin;
    private Long grandParentOrigin;
}