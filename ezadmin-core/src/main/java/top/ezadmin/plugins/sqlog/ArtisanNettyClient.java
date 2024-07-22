//package top.ezadmin.plugins.sqlog;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.handler.codec.string.StringEncoder;
//
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class ArtisanNettyClient {
//    private String host;
//    private int port;
//    private Bootstrap bootstrap;
//    private EventLoopGroup group;
//
//    private static  BlockingQueue<String> MESSAGE=new LinkedBlockingQueue<String>(10);
//    public String readMessage() throws InterruptedException {
//        return MESSAGE.take();
//    }
//    public static AtomicBoolean CHANNER_SUCCESS=new AtomicBoolean(false);
//    public   void sendMessage(String message){
//        if(CHANNER_SUCCESS.get()){
//           boolean add= MESSAGE.offer(message);
//          //  System.out.println("sendMessage: "+add +"\t"+message);
//        }else{
//           // System.out.println("通道关闭无法发送信息");
//        }
//    }
//
//    public ArtisanNettyClient(String host, int port) {
//        this.host = host;
//        this.port = port;
//        init();
//    }
//    private void init() {
//        // 客户端需要一个事件循环组
//        group = new NioEventLoopGroup();
//        // 创建客户端启动对象
//        // bootstrap 可重用， 只需在NettyClient实例化的时候初始化即可.
//        bootstrap = new Bootstrap();
//        bootstrap.group(group)
//                .channel(NioSocketChannel.class)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        // 加入处理器
//                        ch.pipeline().addLast(new StringDecoder());
//                        ch.pipeline().addLast(new StringEncoder());
//                        ch.pipeline().addLast(new ArtisanNettyClientHandler(ArtisanNettyClient.this));
//                    }
//                });
//    }
//    public void connect() throws Exception {
//      //  System.out.println("netty client start。。");
//        // 启动客户端去连接服务器端
//        ChannelFuture cf = bootstrap.connect(host, port);
//        cf.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if (!future.isSuccess()) {
//                    // 重连交给后端线程执行
//                    CHANNER_SUCCESS.getAndSet(false);
//                    future.channel().eventLoop().schedule(() -> {
//                        System.err.println("重连服务端...");
//                        try {
//                            connect();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }, 3000, TimeUnit.MILLISECONDS);
//                } else {
//                    System.out.println("服务端连接成功...");
//                    CHANNER_SUCCESS.getAndSet(true);
//                }
//            }
//        });
//        // 对通道关闭进行监听
//        cf.channel().closeFuture().sync();
//    }
//    public class ArtisanNettyClientHandler extends ChannelInboundHandlerAdapter {
//        private ArtisanNettyClient artisanNettyClient;
//        public ArtisanNettyClientHandler(ArtisanNettyClient artisanNettyClient) {
//            this.artisanNettyClient = artisanNettyClient;
//        }
//        /**
//         * 当客户端连接服务器完成就会触发该方法
//         *
//         * @param ctx
//         * @throws Exception
//         */
//        @Override
//        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//            // 创建一个ByteBuf，包含"HelloServer"的字符串
//            //   ByteBuf buf = Unpooled.copiedBuffer("HelloServer".getBytes(CharsetUtil.UTF_8));
//            // 向服务器发送消息
//         //   System.out.println("channelActive");
//
//            CompletableFuture.runAsync(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        while(artisanNettyClient.CHANNER_SUCCESS.get()){
//                            String s=artisanNettyClient.readMessage();
//                            ctx.writeAndFlush(s);
//                        }
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//
//        }
//        /**
//         * 当通道有读取事件时会触发，即服务端发送数据给客户端
//         *
//         * @param ctx
//         * @param msg
//         * @throws Exception
//         */
//        @Override
//        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            // 获取消息内容
//            //   ByteBuf buf = (ByteBuf) msg;
//            // 打印服务端发送的消息
//          //  System.out.println("channelRead：" +msg);
//            // 打印服务端的地址
//          //  System.out.println("channelRead： " + ctx.channel().remoteAddress());
//        }
//        /**
//         * 当通道处于不活动状态时调用
//         *
//         * @param ctx
//         * @throws Exception
//         */
//        @Override
//        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//            // 打印提示信息
//         //   System.err.println("channelInactive 运行中断开重连。。。");
//
//            // 调用客户端的connect方法进行重连
//            artisanNettyClient.connect();
//        }
//        /**
//         * 当捕获到异常时调用
//         *
//         * @param ctx
//         * @param cause
//         * @throws Exception
//         */
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            // 打印异常堆栈信息
//            cause.printStackTrace();
//            // 关闭通道
//            ctx.close();
//        }
//    }
//}