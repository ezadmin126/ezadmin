package top.ezadmin.blog.utils;

/**
 * @title: UrlTool
 * @Author Hank.he
 * @Date: 2022/3/12 19:10
 */
public class UrlTool {
    public static String imageShowUrl(String id){
        return "/core/downloadDesc.html?fileId="+id;
    }
    public static String blogDetail(Integer id){
        return "/blog/detail-"+id+"-1.html";
    }
    public static String blogDetail(Integer blogId,Integer commentPage){
        return "/blog/detail-"+blogId+"-"+commentPage+".html";
    }
    public static String blogSearch( Integer categoryId, Integer currentPage) {
        categoryId=categoryId==null?0:categoryId;
        currentPage=currentPage==null?0:currentPage;
        return String.format("/blog/search-%s-%s.html", categoryId, currentPage);
    }

}
