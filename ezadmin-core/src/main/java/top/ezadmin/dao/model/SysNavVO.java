package top.ezadmin.dao.model;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统菜单VO
 */
public class SysNavVO {
    private Long id;
    private String text;
    private String iconCls;
    private String state;
    private List<SysNavVO> children ;
    private String navGroup;
    private String navUrl;
    public Long getId() {
        return id;
    }

    public SysNavVO addReturnCurrent(Long id,String text, String iconCls, String url){
        if(children==null){
            children=new ArrayList<>();
        }
        SysNavVO nav=new SysNavVO();
        nav.setText(text);
        nav.setId(id);
        nav.setIconCls(iconCls);
        nav.setNavUrl(url);
        children.add(nav);
        return this;
    }
    public SysNavVO addReturnChild(String text, String iconCls, String url){
        if(children==null){
            children=new ArrayList<>();
        }
        SysNavVO nav=new SysNavVO();
        nav.setText(text);
        nav.setIconCls(iconCls);
        nav.setNavUrl(url);
        children.add(nav);
        return nav;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIconCls() {
        return iconCls ;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<SysNavVO> getChildren() {
        return children;
    }

    public void setChildren(List<SysNavVO> children) {
        this.children = children;
    }

    public String getNavGroup() {
        return navGroup;
    }

    public void setNavGroup(String navGroup) {
        this.navGroup = navGroup;
    }

    public String getNavUrl() {
        return navUrl;
    }

    public void setNavUrl(String navUrl) {
        this.navUrl = navUrl;
    }
}
