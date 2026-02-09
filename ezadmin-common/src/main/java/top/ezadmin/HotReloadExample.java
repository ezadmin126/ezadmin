package top.ezadmin;

/**
 * 热加载功能使用示例
 *
 * 该示例展示如何在开发环境中启用配置文件和模板的热加载功能
 */
public class HotReloadExample {

    /**
     * 启用热加载的配置示例
     */
    public static void enableHotReload() {
        // 1. 获取配置实例
        EzBootstrapConfig config = EzBootstrap.config();

        // 2. 设置 sqlCache 为 false 启用热加载
        config.setSqlCache(false);

        // 3. 初始化 EzBootstrap
        EzBootstrap.getInstance().init(config);

        System.out.println("热加载已启用！现在你可以：");
        System.out.println("1. 修改 src/main/resources/topezadmin/config/layui/dsl/list/*.json");
        System.out.println("2. 修改 src/main/resources/topezadmin/config/layui/dsl/form/*.json");
        System.out.println("3. 修改 src/main/resources/topezadmin/config/layui/dsl/component/*.html");
        System.out.println("无需重新编译，刷新页面即可看到效果！");
    }

    /**
     * Spring Boot 配置示例
     */
    public static class SpringBootConfig {
        /*
        @Configuration
        public class EzAdminConfiguration {

            @Value("${spring.profiles.active:prod}")
            private String activeProfile;

            @Bean
            public EzBootstrapConfig ezBootstrapConfig() {
                EzBootstrapConfig config = new EzBootstrapConfig();

                // 根据环境配置热加载
                if ("dev".equals(activeProfile) || "development".equals(activeProfile)) {
                    config.setSqlCache(false);  // 开发环境启用热加载
                    System.out.println("开发环境：热加载已启用");
                } else {
                    config.setSqlCache(true);   // 生产环境使用缓存
                    System.out.println("生产环境：使用缓存模式");
                }

                // 其他配置...
                return config;
            }

            @Bean
            public EzBootstrap ezBootstrap(EzBootstrapConfig config) {
                EzBootstrap bootstrap = EzBootstrap.getInstance();
                bootstrap.init(config);
                return bootstrap;
            }
        }
        */
    }

    /**
     * 通过 JVM 参数启用热加载
     *
     * 启动时添加参数：
     * -Dezadmin.hotreload=true
     */
    public static void enableHotReloadBySystemProperty() {
        String hotReload = System.getProperty("ezadmin.hotreload", "false");
        boolean enableHotReload = Boolean.parseBoolean(hotReload);

        EzBootstrapConfig config = EzBootstrap.config();
        config.setSqlCache(!enableHotReload);  // 注意：sqlCache=false 时启用热加载

        if (enableHotReload) {
            System.out.println("通过系统属性启用了热加载");
        }
    }

    /**
     * 手动指定项目根路径
     *
     * 当系统无法自动检测项目路径时使用
     */
    public static void setProjectRoot(String projectPath) {
        // 设置项目根路径
        System.setProperty("ezadmin.project.root", projectPath);

        // 启用热加载
        EzBootstrapConfig config = EzBootstrap.config();
        config.setSqlCache(false);
        EzBootstrap.getInstance().init(config);

        System.out.println("项目根路径设置为: " + projectPath);
        System.out.println("热加载已启用");
    }

    /**
     * 演示运行时切换热加载模式
     */
    public static void toggleHotReload() {
        EzBootstrapConfig config = EzBootstrap.config();

        // 获取当前状态
        boolean currentHotReload = !config.isSqlCache();

        // 切换状态
        config.setSqlCache(!config.isSqlCache());

        // 重新初始化
        EzBootstrap.getInstance().init(config);

        System.out.println("热加载模式从 " + currentHotReload + " 切换到 " + !currentHotReload);

        // 清理缓存
        EzBootstrap.clear();
    }

    public static void main(String[] args) {
        // 示例1：启用热加载
        enableHotReload();

        // 示例2：通过系统属性控制
        // enableHotReloadBySystemProperty();

        // 示例3：手动设置项目路径
        // setProjectRoot("/Users/yourname/projects/ezadmin");

        // 示例4：运行时切换
        // toggleHotReload();
    }
}