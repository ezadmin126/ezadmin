package top.ezadmin.dao.model;

import java.util.List;

public class CustomSearchGroup{
    private String t;
    private List<CustomSearchDTO> c;

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public List<CustomSearchDTO> getC() {
        return c;
    }

    public void setC(List<CustomSearchDTO> c) {
        this.c = c;
    }
}