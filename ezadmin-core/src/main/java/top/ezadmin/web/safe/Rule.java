package top.ezadmin.web.safe;


public interface Rule {
    boolean onMessage(IpActionDto dto);

    void clear(String ip);
    String print();
}
