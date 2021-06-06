package main.java.client;

import java.net.URI;
import java.rmi.server.ExportException;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public class MyWebSocketClient extends WebSocketClient{

    public MyWebSocketClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.err.println("Connect Successfully");
    }

    @Override
    public void onMessage(String msg) {
        System.err.println("receive message: " + msg);
        JSONObject jsonObject = JSONObject.parseObject(msg);
        System.err.println(jsonObject.get("Task"));
        if(msg.equals("over")){
            this.close();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.err.println("Linking Close");
    }

    @Override
    public void onError(Exception e){
        e.printStackTrace();
        System.err.println("Connect Error");
    }
}
