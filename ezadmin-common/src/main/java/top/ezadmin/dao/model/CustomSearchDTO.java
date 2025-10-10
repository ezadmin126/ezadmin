package top.ezadmin.dao.model;

import java.util.List;

public class CustomSearchDTO {
    private List<CustomSearchGroup> g;
    private List<CustomSearchSingle> s;
    private List<CustomSearchOrder> o;

    public List<CustomSearchGroup> getG() {
        return g;
    }

    public void setG(List<CustomSearchGroup> g) {
        this.g = g;
    }

    public List<CustomSearchSingle> getS() {
        return s;
    }

    public void setS(List<CustomSearchSingle> s) {
        this.s = s;
    }

    public List<CustomSearchOrder> getO() {
        return o;
    }

    public void setO(List<CustomSearchOrder> o) {
        this.o = o;
    }
}

