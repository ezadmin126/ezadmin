package top.ezadmin.ip;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class IpCountDto {
    private AtomicLong time;
    private AtomicInteger count;
}
