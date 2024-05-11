package top.ezadmin.blog.constants;

import org.apache.commons.lang.StringUtils;

public enum SuffixType {
    png("png", FileType.img,"image/png"),
    jpg("jpg", FileType.img,"image/jpeg"),
    jpeg("jpeg", FileType.img,"image/jpeg"),
    gif("gif", FileType.img,"image/gif"),
    svg("svg", FileType.img,"image/svg+xml"),
    bmp("bmp", FileType.img,"image/jpeg"),
    pdf("pdf", FileType.pdf,"application/pdf"),
    doc("doc", FileType.word,"application/msword"),
    xls("xls", FileType.excel,"application/vnd.ms-excel"),
    docx("docx", FileType.word, "application/msword"),
    xlsx("xlsx", FileType.excel,"application/vnd.ms-excel"),
    mp4("mp4", FileType.video,"video/mp4"),
    binary("", FileType.binary,"application/octet-stream")
    ;

    private String suffix;

    private FileType fileType;

    private String mime;

    public static SuffixType getContentType(String suffix) {
        if (StringUtils.isEmpty(suffix)) {
            return binary;
        }
        for(SuffixType t:values()){
            if(suffix.toLowerCase().endsWith(t.getSuffix() ) ){
                    return t ;
            }
        }
        return binary;
    }

    SuffixType(String suffix, FileType fileType, String mime) {
        this.suffix = suffix;
        this.fileType = fileType;
        this.mime = mime;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}

