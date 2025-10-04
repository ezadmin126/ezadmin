package io.github.ezadmin126.web.safe;


public interface Rule {
    boolean onMessage(IpActionDto dto);

    void clear(String ip);

    String print();
}
