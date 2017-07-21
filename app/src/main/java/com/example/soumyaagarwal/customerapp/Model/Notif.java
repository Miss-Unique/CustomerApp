package com.example.soumyaagarwal.customerapp.Model;

/**
 * Created by RajK on 18-07-2017.
 */

public class Notif {
    private String id,timestamp,type,senderId,receiverId,receiverFCMToken,content,taskId;

    public Notif() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverFCMToken() {
        return receiverFCMToken;
    }

    public void setReceiverFCMToken(String receiverFCMToken) {
        this.receiverFCMToken = receiverFCMToken;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Notif(String id, String timestamp, String type, String senderId, String receiverId, String receiverFCMToken, String content, String TaskId) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.receiverFCMToken = receiverFCMToken;
        this.content = content;
        this.taskId = TaskId;
    }
}
