package top.ezadmin.dao.model;

public class TreeConfig {

    private String treeId = "VALUE";
    private String treePid = "PARENT_ID";
    private String treeLabel = "LABEL";
    private String treeChildren = "CHILDREN";
    private String treeIsParent = "IS_PARENT";
    private String rootPid = "0";

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public String getTreePid() {
        return treePid;
    }

    public void setTreePid(String treePid) {
        this.treePid = treePid;
    }

    public String getTreeLabel() {
        return treeLabel;
    }

    public void setTreeLabel(String treeLabel) {
        this.treeLabel = treeLabel;
    }

    public String getTreeChildren() {
        return treeChildren;
    }

    public void setTreeChildren(String treeChildren) {
        this.treeChildren = treeChildren;
    }

    public String getTreeIsParent() {
        return treeIsParent;
    }

    public void setTreeIsParent(String treeIsParent) {
        this.treeIsParent = treeIsParent;
    }

    public String getRootPid() {
        return rootPid;
    }

    public void setRootPid(String rootPid) {
        this.rootPid = rootPid;
    }
}
