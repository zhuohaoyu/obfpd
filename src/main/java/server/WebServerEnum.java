package main.java.server;

public enum WebServerEnum {
    server;
    private static MyWebSocketServer socketServer = null;
    public static void init(MyWebSocketServer server) {
        socketServer = server;
        if (socketServer != null) {
            socketServer.start();
        }
    }
}
