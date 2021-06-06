package main.java.server;

public class SocketServerEngine {
    public static void main(String[] args) {
         new Thread(new Crawler(60 * 60 * 1000)).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                WebServerEnum.server.init(new MyWebSocketServer(8090));
            }
        }).start();

    }
}
