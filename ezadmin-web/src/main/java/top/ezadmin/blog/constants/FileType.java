package top.ezadmin.blog.constants;


import org.apache.commons.lang.StringUtils;

public enum FileType {
    //img/audio/video/word/excel/pdf/

    img("img", "img"),
    audio("audio", "audio"),
    video("video", "video"),
    word("word", "word"),
    excel("excel", "excel"),
    pdf("pdf", "pdf"),
    binary("binary", "binary")
    ;

    private String type;

    // 描述
    private String desc;

    public static FileType getFileType(String fileType) {
        if (StringUtils.isEmpty(fileType)) {
            return binary;
        }

        for (FileType type : FileType.values()) {
            if (type.getType().equalsIgnoreCase(fileType)) {
                return type;
            }
        }

        return binary;
    }

    private FileType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

