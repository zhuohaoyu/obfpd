package main.java.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class MyWebSocketServer extends WebSocketServer{
    Map<WebSocket, Integer> reflct = null;

    public MyWebSocketServer(int port) {
        super(new InetSocketAddress(port));
        reflct = new HashMap<WebSocket, Integer>();
    }

    @Override
    public void onClose(WebSocket ws, int arg1, String arg2, boolean arg3) {
        System.err.println("------------------onClose-------------------");
        reflct.remove(ws);
    }

    @Override
    public void onError(WebSocket ws, Exception e) {
        System.err.println("------------------onError-------------------");
        if(ws != null) {
        }
        e.getStackTrace();
    }

    @Override
    public void onMessage(WebSocket ws, String msg) {
        System.err.println("Receive Message: "+msg);
        for (WebSocket tws : reflct.keySet()) {
            if (tws.hashCode() != ws.hashCode())
                tws.send(msg);
        }
        System.err.println("HASH="+ws.hashCode());

        if(ws.isClosed()) {
        }
        else if (ws.isClosing()) {
            System.err.println("ws closing...");
        }
        else if (ws.isConnecting()) {
            System.err.println("ws opening...");
        }
        else if(ws.isOpen()) {
            System.err.println("ws opened...");
            System.err.println(msg);
        }
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake shake) {
        System.err.println("HASH="+ws.hashCode());
        System.err.println("-----------------onOpen--------------------"+ws.isOpen()+"--"+ws.getReadyState()+"--"+ws.getAttachment());
        reflct.put(ws, 1024);
        for(Iterator<String> it=shake.iterateHttpFields();it.hasNext();) {
            String key = it.next();
            System.err.println(key+":"+shake.getFieldValue(key));
        }
    }

    @Override
    public void onStart() {
        System.err.println("------------------onStart-------------------");
    }
}
