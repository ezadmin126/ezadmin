package top.ezadmin.web;
//


import org.jsoup.nodes.Document;

import java.io.File;
import java.io.InputStream;
import java.net.URL;


//
//

public class Config {

    private String path;

    private File file;
    private URL url;
    private String protocol;
    private InputStream in;

    private Document doc;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public boolean isJar(){
        return "jar".equals(protocol);
    }

}
