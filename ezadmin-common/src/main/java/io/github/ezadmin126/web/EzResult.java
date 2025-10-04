package io.github.ezadmin126.web;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EzResult {
    private String code = "0";
    private boolean success = true;
    private String message;
    private long count;
    private Object data;
 

    private EzResult() {
    }

    public static EzResult instance() {
        return new EzResult();
    }

    public EzResult data(Object data) {
        this.data = data;
        return this;
    }

    public EzResult data(String k, String v) {
        if (data == null) {
            data = new HashMap<String, String>();
        }
        ((HashMap<String, String>) data).put(k, v);
        return this;
    }

    public EzResult dataMap(String k, Object v) {
        if (data == null) {
            data = new HashMap<String, Object>();
        }
        ((HashMap<String, Object>) data).put(k, v);
        return this;
    }

    public EzResult dataKVList(String k, Object v) {
        if (data == null) {
            data = new ArrayList<HashMap<String, Object>>();
        }
        Map<String, Object> kvmap = new HashMap<String, Object>();
        kvmap.put("K", k);
        kvmap.put("V", v);
        ((ArrayList) data).add(kvmap);
        return this;
    }

    public EzResult fail() {
        this.code = "500";
        success = false;
        return this;
    }

    public EzResult fail(String message) {
        this.code = "500";
        this.message = message;
        success = false;
        return this;
    }

    public EzResult success() {
        this.success = true;
        return this;
    }

    public EzResult count(long count) {
        this.count = count;
        return this;
    }

    public EzResult msg(String code, String msg) {
        this.message = msg;
        this.code = code;
        return this;
    }

    public EzResult code(String code) {
        this.code = code;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public EzResult setMessage(String msg) {
        this.message = msg;
        return this;
    }

    public EzResult message(String msg) {
        this.message = msg;
        return this;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public EzResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public EzResult success(boolean success) {
        this.success = success;
        return this;
    }

}
