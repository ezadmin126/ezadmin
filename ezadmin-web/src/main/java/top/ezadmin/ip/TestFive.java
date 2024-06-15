package top.ezadmin.ip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class TestFive {
    public static void main(String[] args) {
        // 创建一个调度线程池
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

        // 创建一个用于执行任务的线程池
        ExecutorService executor = Executors.newFixedThreadPool(50);

        // 定义任务
        Runnable task = () -> {
            System.out.println("开始执行==============");
            List<CompletableFuture> futures = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                final int j=i;
                futures.add(  CompletableFuture.supplyAsync(() -> {
                    // 这里放置你的任务逻辑
                    IpActionDto dto=new IpActionDto();
                    dto.setIp("1.1.1."+j);
                    dto.setT(System.currentTimeMillis());
                    dto.setUri("/w/1.html");
                    if(j%2==0){
                        dto.setC(new HashMap<>());
                        dto.getC().put("track_cookieId","1");
                    }

                    return "Task Completed ::"+j+"\t" +IpWafService.ipSafe(dto);
                }, executor).thenAccept(result -> {
                    // 处理异步任务的结果
                     System.out.println(result);
                })
                );
            }

            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                // 所有任务完成后执行的逻辑
                IpWafService.print();
            });

        };
        // 每60秒执行一次任务
        scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);
      //  scheduler.scheduleAtFixedRate(task2, 0, 61, TimeUnit.SECONDS);

        // 为了示例需要，这里让程序运行一段时间后关闭
        try {
            Thread.sleep(3010000); // 运行10秒后停止
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 关闭线程池
        scheduler.shutdown();
        executor.shutdown();
    }
}
