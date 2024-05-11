package top.ezadmin.blog.model;

import lombok.Data;

/**
 *
 **/
@Data
public class UploadDTO {
    private String fileUrl;
    private String fileId;
    private String fileName;

    private String width;
    private String height;

}
