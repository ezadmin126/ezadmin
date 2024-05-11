package top.ezadmin.blog.vo;


import top.ezadmin.domain.model.BlogCategory;

public class BlogCategoryVO extends BlogCategory {
    private String categoryUrl;

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }
}
