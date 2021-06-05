## 论坛数据传输

将json转成String类型进行传输，客户端调用client.send进行数据传输。需要重写MyWebsocketClient中的onMessage对收到的信息进行处理。 json的关键字需要都是字符串。

+ 创建帖子
```
客户端传输：
"task":"createPost"
"title":""
"courseID":""

客户端接受：
"task":"createPostSucc"
```

+ 请求帖子
```
客户端传输：
“task”:"queryPost"
"first":""
"last":""

客户端接受：
"task":"queryPost"
"postID":""
"title":""
"course":""
"time":""
...
"
```

+ 发送评论
```
客户端传输：
"task":"createReply"
"content":""
"postID":""

客户端接受：
"task":"createReplySucc"
```

+ 获取评论
```
客户端传输：
"task":"queryReply"
"first":""
"last":""

客户端接受：
"task":"queryReply"
"content":""
"postID":""
"time":""
...
```