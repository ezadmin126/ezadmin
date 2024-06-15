package top.ezadmin.ip;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IpFilter {
    //ip  {count,lasttime}
    private Map<String,IpCountDto> COUNT_HOLDER=new ConcurrentHashMap<>();
    //ip  {count,lasttime}
    private Map<String,IpCountDto> BLACK_HOLDER=new ConcurrentHashMap<>();

    private long period;
    private long maxcount;

    public String toString(){
        return "IpFilter：：：period："+ StringUtils.leftPad(period+"",8," ")+" maxcount："+
                StringUtils.leftPad(maxcount+"",8," ")+" COUNT_HOLDER："+COUNT_HOLDER+
                "\tBLACK_HOLDER："+BLACK_HOLDER;
    }

    public IpFilter(long period,int maxcount  ){
        this.period=period;
        this.maxcount=maxcount;

    }
   final Object lock=new Object();
    /**
     *
     * @param ip
     * @param visitTime
     * @return 0 已经是黑名单 1 正常加入  2 当前出发黑名单
     */
    public int doFilter(String ip,long visitTime){
        if(BLACK_HOLDER.containsKey(ip)){
            //在黑名单里面还继续访问，始终延长制定的时间
            if(visitTime-BLACK_HOLDER.get(ip).getTime().get()<period){
                BLACK_HOLDER.get(ip).getTime().set(visitTime);
                BLACK_HOLDER.get(ip).getCount().incrementAndGet();
                COUNT_HOLDER.get(ip).getCount().getAndIncrement();
                return 0;
            }else{
                //超过指定时间，则解除控制
                BLACK_HOLDER.remove(ip);
            }
        }else{
            //  System.out.println("黑名单不包含"+ip);
        }

        //IP第一次进来，设置第一次进来的时间
        COUNT_HOLDER.computeIfAbsent(ip,k->{
           // System.out.println("COUNT_HOLDER computeIfAbsent");
            IpCountDto c= new IpCountDto();
            c.setTime(new AtomicLong(visitTime));
            c.setCount(new AtomicInteger(0));
            return c;
        });
       IpCountDto countDto=COUNT_HOLDER.get(ip);



        //多线程多个请求时间 ，任意一个是expireTime秒之前请求，代表1分钟已经过期， 重置
        if(visitTime-countDto.getTime().get()>period){
            synchronized (lock){
                try {
                    if (visitTime - countDto.getTime().get() > period) {
                        System.out.println("重置 "+maxcount);
                        countDto.getCount().set(1);
                        countDto.getTime().set(visitTime);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        //1分钟内请求的次数大于maxcount次，则加入黑名单
        int count= COUNT_HOLDER.get(ip).getCount().getAndIncrement();

        if(count>maxcount){
          //  System.out.println("超过"+period+"内最大线程数："+maxcount+"，纳入黑名单");
            BLACK_HOLDER.computeIfAbsent(ip,k-> {
                IpCountDto c= new IpCountDto();
                c.setTime(new AtomicLong(visitTime));
                c.setCount(new AtomicInteger(1));
                return c;
            });
            BLACK_HOLDER.get(ip).getTime().set(visitTime);
            BLACK_HOLDER.get(ip).getCount().incrementAndGet();
            return 0;
        }

      //  System.out.println(ip+" "+period+"内最大线程数："+count+" ");
        return 1;
    }


}
