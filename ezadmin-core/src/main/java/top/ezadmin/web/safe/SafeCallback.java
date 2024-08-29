package top.ezadmin.web.safe;

public interface SafeCallback {
    void doCallback(String ip,long count,long period,long max);
}
