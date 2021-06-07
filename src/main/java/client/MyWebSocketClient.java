package main.java.client;

import java.net.URI;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import main.java.App;
import main.java.ui.ForumPanel;
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
        if (jsonObject.get("Task").equals(ClientConstants.createPOST)) {
            Map<String, String> mp = new HashMap<>();
            mp.put("Title", jsonObject.get("Title").toString());
            mp.put("userID", jsonObject.get("userID").toString());
            mp.put("postID", jsonObject.get("postID").toString());
            ForumPanel.post.add(mp);
            System.err.println("list: " + ForumPanel.post + "  size = " + ForumPanel.post.size());
        }
        else if (jsonObject.get("Task").equals(ClientConstants.queryPOST)) {
            Map<String, String> mp = new HashMap<>();
            mp.put("Title", jsonObject.get("Title").toString());
            mp.put("userID", jsonObject.get("userID").toString());
            mp.put("postID", jsonObject.get("postID").toString());
            ForumPanel.post.add(mp);
            System.err.println("list: " + ForumPanel.post + "  size = " + ForumPanel.post.size());
        }
        else if (jsonObject.get("Task").equals(ClientConstants.queryREPLY)) {
            Map<String, String> mp = new HashMap<>();
            mp.put("content", jsonObject.get("content").toString());
            mp.put("time", jsonObject.get("time").toString());
            mp.put("posterID", jsonObject.get("posterID").toString());
            mp.put("postID", jsonObject.get("postID").toString());
            ForumPanel.reply.add(mp);
        }
        else if (jsonObject.get("Task").equals(ClientConstants.queryREPLYFinished)) {
            ForumPanel.isQueryREPLYFinished = true;
        }
        else if (jsonObject.get("Task").equals(ClientConstants.queryPOSTFinished)) {
            ForumPanel.isQueryPOSTFinished = true;
        }
        else if (jsonObject.get("Task").equals(ClientConstants.queryUPDATE)) {
            int sum = Integer.parseInt(jsonObject.get("size").toString());
            for (int i = 0; i < sum; ++i) {
                Map<String, String> mp = new HashMap<String, String>();
                mp.put("time", jsonObject.get("time" + Integer.toString(i)).toString());
                mp.put("course_name", jsonObject.get("course_name" + Integer.toString(i)).toString());
                mp.put("homework_name", jsonObject.get("homework_name" + Integer.toString(i)).toString());
                mp.put("type", jsonObject.get("type" + Integer.toString(i)).toString());
                mp.put("ole", jsonObject.get("old" + Integer.toString(i)).toString());
                mp.put("new", jsonObject.get("new" + Integer.toString(i)).toString());
                App.update.add(mp);
            }
            System.err.println(App.update);
        }
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
        System.exit(0);
    }
}
