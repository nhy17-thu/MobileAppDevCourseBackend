package com.mobilecourse.backend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.controllers.ChatController;
import com.mobilecourse.backend.controllers.UserController;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;


@ServerEndpoint(value="/websocket/{sid}", configurator=GetHttpSessionConfigurator.class)
@Component
public class WebSocketServer {

    public static Hashtable<String, WebSocketServer> getWebSocketTable() {
        return webSocketTable;
    }

    private static Hashtable<String, WebSocketServer> webSocketTable = new Hashtable<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //用于标识客户端的sid
    private String sid = "";

    //推荐在连接的时候进行检查，防止有人冒名连接
    // todo: 连接的时候检查HttpSession：是否已经登陆？
    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("sid") String sid) {
        this.session = session;
        this.sid = sid;
        webSocketTable.put(sid, this);
        HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        System.out.println("Current httpSession's uid: " + httpSession.getAttribute("uid"));

        try {
            System.out.println("uid:" + sid + " 成功连接websocket");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", "uid: " + sid + "已成功连接到聊天服务器！");
            jsonObject.put("senderId", null);
            jsonObject.put("senderName", null);
            sendMessage(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 在关闭连接时移除对应连接
    @OnClose
    public void onClose() {
        webSocketTable.remove(this.sid);
    }

    /*
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            System.out.println(sid + "发来消息：" + message);
            sendMessage("收到消息：" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    // 收到消息的时候：将聊天记录添加到数据库
    @OnMessage
    public void onMessage(String message, Session session) {
        // 要求message格式：（content不为空）
        // {
        //      "receiverId": "xxx",
        //      "content": "xxx"
        // }
        JSONObject inputMessageJSON = JSON.parseObject(message);
        ChatController chatController = new ChatController();
        int senderId = Integer.parseInt(sid);
        int receiverId = inputMessageJSON.getIntValue("receiverId");
        String content = inputMessageJSON.getString("content");
        chatController.addChatMessage(senderId, receiverId, content);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", content);
        jsonObject.put("senderId", sid);
        UserController userController = new UserController();
        jsonObject.put("senderName", userController.getUserByUid(Integer.parseInt(sid)).getName());

        // 如果receiver在线，则将消息直接发给他（利用webSocketTable进行查询）
        WebSocketServer receiverSocket = webSocketTable.get(String.valueOf(receiverId));
        try {
            if (receiverSocket != null) {
                receiverSocket.sendMessage(jsonObject.toJSONString());
            }
        } catch (IOException e) {
            // 如果发送失败，等待1秒尝试重发
            e.printStackTrace();
            try {
                TimeUnit.SECONDS.sleep(1);
                receiverSocket.sendMessage(jsonObject.toJSONString());
            } catch (IOException error) {
                // 若重发还是失败，则断开socket连接
                error.printStackTrace();
                webSocketTable.remove(String.valueOf(receiverId));
            } catch (InterruptedException interruption) {
                // 手动中断当前线程
                Thread.currentThread().interrupt();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        webSocketTable.remove(this.sid);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
