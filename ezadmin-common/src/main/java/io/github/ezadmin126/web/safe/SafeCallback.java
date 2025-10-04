package io.github.ezadmin126.web.safe;

public interface SafeCallback {
    void doCallback(String ip, long count, long period, long max);
}
