package main.java.server;

public class SocketServerEngine {
    public static void main(String[] args) {
        WebServerEnum.server.init(new MyWebSocketServer(8090));
    }
}
