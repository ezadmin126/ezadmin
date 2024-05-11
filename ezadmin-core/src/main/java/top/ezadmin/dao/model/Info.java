package top.ezadmin.dao.model;

import java.util.ArrayList;
import java.util.List;

public   class Info{
        private String title;
        private String id;
        private String pid;
        private String href;
        private String image;
        private String target;
        private String icon;
        private List<Info> child=new ArrayList<>();
        public Info(){}
        public Info(String title, String href, String image, String target, String icon) {
            this.title = title;
            this.href = href;
            this.image = image;
            this.target = target;
            this.icon = icon;
        }
        public Info addChild(String title, String href, String image, String target, String icon) {
            Info i=new Info(  title,   href,   image,   target,   icon);
            child.add(i);
           return this;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public List<Info> getChild() {
            return child;
        }

        public void setChild(List<Info> child) {
            this.child = child;
        }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}