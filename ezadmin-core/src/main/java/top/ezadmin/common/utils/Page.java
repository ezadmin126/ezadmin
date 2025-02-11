package top.ezadmin.common.utils;

import java.util.Map;

public class Page {
    private String dialect = "mysql";
    private String groupBy="";

    private int currentPage = 1;
    private int perPageInt = 10;
    private int startRecord = 0;
    private int endRecord = 10;
    /**
     * 列表总记录数
     */
    private long totalRecord = 0;

//    public int getStartForPage() {
//        return startForPage;
//    }
//
//    public void setStartForPage(int startForPage) {
//        this.startForPage = startForPage;
//    }
//
//    public int getEndForPage() {
//        return endForPage;
//    }
//
//    public void setEndForPage(int endForPage) {
//        this.endForPage = endForPage;
//    }

    private int startForPage = 1, endForPage = 1;


    private long totalPage;






    public int getCurrentPage() {
        return currentPage < 0 ? 0 : currentPage;
    }

    public Page setCurrentPage(int currentPage) {
        this.currentPage = currentPage <= 0 ? 1 : currentPage;
        calcSplitPage();
        return this;
    }

    public int getPerPageInt() {
        return perPageInt <= 0 ? 10 : perPageInt;
    }

    public Page setPerPageInt(int perPageInt) {
        this.perPageInt = perPageInt <= 0 ?10 : perPageInt;
        calcSplitPage();
        return this;
    }

    public static void main(String[] args) {
        Page page=new Page();
        page.setPerPageInt(50000);
        page.setCurrentPage(1);
        System.out.println(page.getEndRecord());
    }

    /**
     * 计算分页信息。记录从0开始
     */
    private void calcSplitPage() {
        startRecord = ((currentPage - 1) < 0 ? 0 : (currentPage - 1)) * perPageInt;
        endRecord = currentPage * perPageInt > 1000 ? 1000 : currentPage * perPageInt;

//        endForPage = Math.min(Integer.valueOf(getTotalPage() + ""), currentPage + 5);
//        startForPage = Math.min(endForPage, Math.max(1, currentPage - 5));
    }

    public int getStartRecord() {
        return startRecord;
    }

    public int getEndRecord() {
        return endRecord;
    }

    public Page setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
        calcSplitPage();
        return this;
    }

    public long getTotalRecord() {
        return totalRecord;
    }

    public long getTotalPage() {

        totalPage = (totalRecord / getPerPageInt() == 0 ? 1
                : (totalRecord % getPerPageInt() > 0 ? (totalRecord / getPerPageInt() + 1)
                : totalRecord / getPerPageInt()));
        //totalPage = currentPage > totalPage ? 0 : totalPage;
       // totalRecord = currentPage > totalPage ? 0 : totalRecord;
        //  currentPage=Math.min(Integer.valueOf(totalPage+""),currentPage);
        return totalPage;
    }

    private String orderByClause;


    public Page() {
    }

    public Page(int i, int j, String orderByClause) {
        setCurrentPage(i);
        setPerPageInt(j);
        this.orderByClause = orderByClause;
    }

    public Page(Map<String,Object> request) {
        setCurrentPage(NumberUtils.toInt(request.get("currentPage")+"", 1));
        setPerPageInt(NumberUtils.toInt(request.get("perPageInt")+"", 10));
        //orderByClause = request.getParameter("orderByClause");
    }

    public void setOrderByClause(String orderByClause) {
          this.orderByClause=orderByClause;
    }
    public String getOrderByClause() {
        return orderByClause;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
}
