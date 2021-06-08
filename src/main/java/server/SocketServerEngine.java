package main.java.server;

public class SocketServerEngine {
    public static void main(String[] args) {
        new Thread(new Crawler(5 * 60 * 60 * 1000)).start();
        new Thread(() -> WebServerEnum.server.init(new MyWebSocketServer(8090))).start();

    }
}
