package main.java.client;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MyTest {
    public static WebSocketClient client;
    public static void main(String[] args) {
        try {
            client = new MyWebSocketClient(new URI("ws://localhost:8090"),new Draft_6455());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        client.connect();
        try {
            while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                Thread.sleep(1);
            }
            client.send("orzyzh");
        }
        catch (Exception e) {
            System.out.println("hahaha: " + e);
        }
    }
}
