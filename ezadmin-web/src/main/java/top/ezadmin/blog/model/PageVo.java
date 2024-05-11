package top.ezadmin.blog.model;

import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.List;


public class PageVo {

    private String url;

    public String getAppendClass() {
        return appendClass;
    }

    public void setAppendClass(String appendClass) {
        this.appendClass = appendClass;
    }

    private String appendClass = "";
    private String label;
    private Integer pageNum;

    public PageInfo getPageHelper() {
        return pageHelper;
    }

    public void setPageHelper(PageInfo pageHelper) {
        this.pageHelper = pageHelper;
    }

    private PageInfo pageHelper;

    private String firstUrl;
    private String lastUrl;
    private String prePageUrl;
    private String nextPageUrl;

    private List<PageVo> navList=new ArrayList<>();




    public boolean isIsFirstPage() {
        return pageHelper.isIsFirstPage();
    }


    public boolean isIsLastPage() {
        return pageHelper.isIsLastPage();
    }


    public boolean isHasPreviousPage() {
        return pageHelper.isHasPreviousPage();
    }


    public boolean isHasNextPage() {
        return pageHelper.isHasNextPage();
    }

    public int getNavigateFirstPage() {
        return pageHelper.getNavigateFirstPage();
    }

    public int getNavigateLastPage() {
        return pageHelper.getNavigateLastPage();
    }
    public int getPages() {
        return pageHelper.getPages();
    }

    public String getFirstUrl() {
        return firstUrl;
    }

    public void setFirstUrl(String firstUrl) {
        this.firstUrl = firstUrl;
    }

    public String getLastUrl() {
        return lastUrl;
    }

    public void setLastUrl(String lastUrl) {
        this.lastUrl = lastUrl;
    }

    public List<PageVo> getNavList() {
        return navList;
    }

    public void setNavList(List<PageVo> navList) {
        this.navList = navList;
    }



    public static PageVo create(String url, Integer page, PageInfo pageHelper) {
        PageVo vo = new PageVo();
        vo.setPageHelper(pageHelper);
        vo.setUrl(url);
        vo.setPageNum(page);
        vo.setFirstUrl(url.replace("${page}",   "1"));
        vo.setLastUrl(url.replace("${page}",   pageHelper.getPages()+""));

        vo.setNextPageUrl(url.replace("${page}",   pageHelper.getNextPage()+""));
        vo.setPrePageUrl(url.replace("${page}",   pageHelper.getPrePage()+""));

        for (int i = 0; i < pageHelper.getNavigatepageNums().length; i++) {
            int item = pageHelper.getNavigatepageNums()[i];
            PageVo c = new PageVo();
            c.setUrl(url.replace("${page}",   item+""));
            c.setLabel(item+"");
            c.setPageNum(item);
            vo.getNavList().add(c);
        }
        return vo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getPrePageUrl() {
        return prePageUrl;
    }

    public void setPrePageUrl(String prePageUrl) {
        this.prePageUrl = prePageUrl;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }
}
