package kd.cosmic;

import kd.cosmic.server.Launcher;

/**
 * 启动本地应用程序(微服务节点)
 */
public class Application {

    public static void main(String[] args) {
        Launcher cosmic = new Launcher();

        cosmic.setClusterNumber("cosmic");
        cosmic.setTenantNumber("ierp");
        cosmic.setServerIP("127.0.0.1");

        cosmic.setAppName("cosmic-GJTT-kXHUoONg");
        cosmic.setWebPath("E:/yymWorkspace/cosmic-server/webapp/static-file-service");


        cosmic.setStartWithQing(false);
        cosmic.setConfigUrl("127.0.0.1:2181","zookeeper","zjywan123");

        cosmic.start();
    }
}