package main.java.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import main.java.ui.ForumPanel;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class MyWebSocketServer extends WebSocketServer{
    static final int POST_MAX = 100000000;
    Map<WebSocket, String> reflct = null;

    public MyWebSocketServer(int port) {
        super(new InetSocketAddress(port));
        reflct = new HashMap<WebSocket, String>();
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
        DataBase db = new DataBase("test.db");
        System.err.println("Receive Message: "+msg);
        JSONObject jsonObject = JSONObject.parseObject(msg);

        System.err.println("ENTERING ON MESSAGE___________");

        if (jsonObject.get("Task").equals("Connect")) {
            jsonObject = new JSONObject();
            reflct.put(ws, jsonObject.get("userID").toString());
            ws.send(msg);
        }
        else if (jsonObject.get("Task").equals("createPOST")) {

            System.err.println("CALLING CREATE POST#1____________");

            String title = jsonObject.get("Title").toString();
            String topic = jsonObject.get("topic").toString();
            String userID = jsonObject.get("userID").toString();
            String content = jsonObject.get("content").toString();

            System.err.println("CALLING CREATE POST#2____________");

            boolean flag = db.newPost(title, content, topic, Integer.parseInt(userID));
            if (flag == false) {
                JSONObject fail = new JSONObject();
                fail.put("Task", "CreatePostFail");
                ws.send(fail.toString());
            }
        }
        else if (jsonObject.get("Task").equals("queryPOST")) {
            List<Map<String, String> > tmp = db.getRangePost(1, POST_MAX);
            for (Map<String, String> detail : tmp) {
                jsonObject = new JSONObject();
                jsonObject.put("Task", "queryPOST");
                jsonObject.put("postID", detail.get("id"));
                jsonObject.put("Title", detail.get("title"));
                jsonObject.put("userID", detail.get("poster_id"));
                ws.send(jsonObject.toString());
            }
            jsonObject = new JSONObject();
            jsonObject.put("Task", "queryPOSTFinished");
            ws.send(jsonObject.toString());
            System.err.println("queryPOST: " + tmp.size());
        }
        else if (jsonObject.get("Task").equals("createREPLY")) {
            String postID = jsonObject.get("postID").toString();
            String userID = jsonObject.get("userID").toString();
            String content = jsonObject.get("content").toString();

            System.err.println("CALLING CREATE reply-----");

            boolean flag = db.newReply(content, Integer.parseInt(postID), Integer.parseInt(userID));
            if (flag == false) {
                JSONObject fail = new JSONObject();
                fail.put("Task", "CreatePostFail");
                ws.send(fail.toString());
            }
        }
        else if (jsonObject.get("Task").equals("queryREPLY")) {
            String postID = jsonObject.get("postID").toString();
            List<Map<String, String> > tmp = db.getPostReply(Integer.parseInt(postID));
            for (Map<String, String> detail : tmp) {
                jsonObject = new JSONObject();
                jsonObject.put("Task", "queryREPLY");
                jsonObject.put("postID", detail.get("post_id"));
                jsonObject.put("content", detail.get("content"));
                jsonObject.put("posterID", detail.get("poster_id"));
                jsonObject.put("time", detail.get("time"));
                ws.send(jsonObject.toString());
            }
            jsonObject = new JSONObject();
            jsonObject.put("Task", "queryREPLYFinished");
            ws.send(jsonObject.toString());
            System.err.println("queryREPLY: " + tmp.size());
        }
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
        db.close();
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake shake) {
        System.err.println("HASH="+ws.hashCode());
        System.err.println("-----------------onOpen--------------------"+ws.isOpen()+"--"+ws.getReadyState()+"--"+ws.getAttachment());

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
