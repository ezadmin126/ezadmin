package top.ezadmin.blog.vo;



import top.ezadmin.common.utils.JSONUtils;

import java.util.Calendar;
import java.util.Map;

public class BlogConfigurations {

    private String websiteName="EZ 纯Servlet嵌入式列表表单工具";
    private String websiteDescription="EZ 纯Servlet嵌入式列表表单工具";
    private String websiteIcon="EZ 纯Servlet嵌入式列表表单工具";
    private String sysAuthorImg="/static/info-img.png?1";

    private String sysAuthor="EZ";



    private String sysCopyRight;


private static Map<String,String> obj= JSONUtils.parseMap(JSONUtils.toJSONString(new BlogConfigurations()));


public static Map<String,String> config(){
   return obj;
}
    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteDescription() {
        return websiteDescription;
    }

    public void setWebsiteDescription(String websiteDescription) {
        this.websiteDescription = websiteDescription;
    }

    public String getWebsiteIcon() {
        return websiteIcon;
    }

    public void setWebsiteIcon(String websiteIcon) {
        this.websiteIcon = websiteIcon;
    }

    public String getSysAuthorImg() {
        return sysAuthorImg;
    }

    public void setSysAuthorImg(String sysAuthorImg) {
        this.sysAuthorImg = sysAuthorImg;
    }

    public String getSysAuthor() {
        return sysAuthor;
    }

    public void setSysAuthor(String sysAuthor) {
        this.sysAuthor = sysAuthor;
    }

    public String getSysCopyRight() {
        return Calendar.getInstance().get(Calendar.YEAR)+" "+sysAuthor;
    }

    public void setSysCopyRight(String sysCopyRight) {
        this.sysCopyRight = sysCopyRight;
    }
}
