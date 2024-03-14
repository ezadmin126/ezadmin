package top.ezadmin.common.utils;

import java.util.Map;

public class EzPage {
    private int currentPage = 1;
    private int perPageInt = 10;
    private int startRecord = 0;
    private int endRecord = 10;
    /**
     * 列表总记录数
     */
    private long totalRecord = 0;

    public int getStartForPage() {
        return startForPage;
    }

    public void setStartForPage(int startForPage) {
        this.startForPage = startForPage;
    }

    public int getEndForPage() {
        return endForPage;
    }

    public void setEndForPage(int endForPage) {
        this.endForPage = endForPage;
    }

    private int startForPage = 1, endForPage = 1;


    private long totalPage;






    public int getCurrentPage() {
        return currentPage < 0 ? 0 : currentPage;
    }

    public EzPage setCurrentPage(int currentPage) {
        this.currentPage = currentPage < 0 ? 0 : currentPage;
        calcSplitPage();
        return this;
    }

    public int getPerPageInt() {
        return perPageInt < 0 ? 0 : perPageInt>5000?5000:perPageInt;
    }

    public EzPage setPerPageInt(int perPageInt) {
        this.perPageInt = perPageInt < 0 ? 0 : perPageInt>5000?5000:perPageInt;
        calcSplitPage();
        return this;
    }

    /**
     * 计算分页信息。记录从0开始
     */
    private void calcSplitPage() {
        startRecord = ((currentPage - 1) < 0 ? 0 : (currentPage - 1)) * perPageInt;
        endRecord = currentPage * perPageInt > 1000 ? 1000 : currentPage * perPageInt;

        endForPage = Math.min(Integer.valueOf(getTotalPage() + ""), currentPage + 5);
        startForPage = Math.min(endForPage, Math.max(1, currentPage - 5));
    }

    public int getStartRecord() {
        return startRecord;
    }

    public int getEndRecord() {
        return endRecord;
    }

    public EzPage setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
        calcSplitPage();
        return this;
    }

    public long getTotalRecord() {
        return totalRecord;
    }

    public long getTotalPage() {
        if(getPerPageInt()==0){
            return 0;
        }
        totalPage = (totalRecord / getPerPageInt() == 0 ? 1
                : (totalRecord % getPerPageInt() > 0 ? (totalRecord / getPerPageInt() + 1)
                : totalRecord / getPerPageInt()));
        totalPage = currentPage > totalPage ? 0 : totalPage;
        totalRecord = currentPage > totalPage ? 0 : totalRecord;
        //  currentPage=Math.min(Integer.valueOf(totalPage+""),currentPage);
        return totalPage;
    }

    private String orderByClause;


    public EzPage() {
    }

    public EzPage(int i, int j, String orderByClause) {
        setCurrentPage(i);
        setPerPageInt(j);
        this.orderByClause = orderByClause;
    }

    public EzPage(Map<String,String> request) {
        setCurrentPage(NumberUtils.toInt(request.get("currentPage"), 1));
        setPerPageInt(NumberUtils.toInt(request.get("perPageInt"), 10));
    }

    public void setOrderByClause(String orderByClause) {
          this.orderByClause=orderByClause;
    }
    public String getOrderByClause() {
        return orderByClause;
    }
}
