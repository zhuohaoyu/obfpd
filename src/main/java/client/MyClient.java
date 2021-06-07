package main.java.client;

import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MyClient {
    public WebSocketClient client;
    public String user;
    public class ReConntect implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (client.isClosed()) {
                    try {
                        client.reconnect();
                        while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                            Thread.sleep(1);
                        }
                        JSONObject jsonobj = new JSONObject();
                        jsonobj.put("Task", "Reconnect");
                        client.send(jsonobj.toString());
                    } catch (Exception e) {
                        System.err.println("onReconnect: " + e);
                    }
                }
                try {
                    Thread.sleep(5000);
                }
                catch (Exception e) {  }
            }
        }
    }
    public void send(JSONObject msg) {
        System.err.println("Send JSONObject: " + msg.toString());
        client.send(msg.toString());
    }
    public MyClient(String __user) {
        user = __user;
        try {
            client = new MyWebSocketClient(new URI("ws://localhost:8090"),new Draft_6455());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            client.connect();
            while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                Thread.sleep(1);
            }
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("Task", "Connect");
            jsonobj.put("userID", user);
            client.send(jsonobj.toString());
        }
        catch (Exception e) {
            System.err.println(e);
            System.exit(0);
        }
        new Thread(new ReConntect()).start();
    }
}
