package com.example.soumyaagarwal.customerapp.Model;

/**
 * Created by RajK on 16-05-2017.
 */

public class ChatMessage {
    private String commentString;
    private String senderUId;
    private String receiverUId;
    private String sendertimestamp;
    private String type;
    private String id;
    private String status;
    private String imgurl;
    private String mesenderlocal_storage;
    private String othersenderlocal_storage;
    private String receiverToken;
    private String chatref;
    private int percentUploaded;
    public String getChatref() {
        return chatref;
    }

    public void setChatref(String chatref) {
        this.chatref = chatref;
    }


    public String getReceiverToken() {
        return receiverToken;
    }

    public void setReceiverToken(String receiverToken) {
        this.receiverToken = receiverToken;
    }

    public String getSenderUId() {
        return senderUId;
    }

    public void setSenderUId(String senderUId) {
        this.senderUId = senderUId;
    }

    public String getReceiverUId() {
        return receiverUId;
    }

    public void setReceiverUId(String receiverUId) {
        this.receiverUId = receiverUId;
    }

    public String getSendertimestamp() {
        return sendertimestamp;
    }

    public void setSendertimestamp(String sendertimestamp) {
        this.sendertimestamp = sendertimestamp;
    }


    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getCommentString() {
        return commentString;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }


    public ChatMessage() {

    }

//for chat messages and photo both

    public ChatMessage(String senderUId, String receiverUId, String sendertimestamp, String type, String id, String status, String commentString, String receiverToken, String chatref) {
        this.senderUId = senderUId;
        this.receiverUId = receiverUId;
        this.sendertimestamp = sendertimestamp;
        this.type = type;
        this.id = id;
        this.status = status;
        this.receiverToken = receiverToken;
        this.chatref = chatref;

            this.commentString = commentString;

    }

    public ChatMessage(String senderUId, String receiverUId, String sendertimestamp, String type, String id, String status, String imgurl, String receiverToken, String chatref, int percentUploaded, String mesenderlocal_storage, String othersenderlocal_storage) {
        this.senderUId = senderUId;
        this.receiverUId = receiverUId;
        this.sendertimestamp = sendertimestamp;
        this.type = type;
        this.id = id;
        this.status = status;
        this.imgurl = imgurl;
        this.receiverToken = receiverToken;
        this.chatref = chatref;
        this.percentUploaded = percentUploaded;
        this.mesenderlocal_storage = mesenderlocal_storage;
        this.othersenderlocal_storage = othersenderlocal_storage;
    }

    public String getMesenderlocal_storage() {
        return mesenderlocal_storage;
    }

    public void setMesenderlocal_storage(String mesenderlocal_storage) {
        this.mesenderlocal_storage = mesenderlocal_storage;
    }

    public String getOthersenderlocal_storage() {
        return othersenderlocal_storage;
    }

    public void setOthersenderlocal_storage(String othersenderlocal_storage) {
        this.othersenderlocal_storage = othersenderlocal_storage;
    }

    public int getPercentUploaded() {
        return percentUploaded;
    }

    public void setPercentUploaded(int percentUploaded) {
        this.percentUploaded = percentUploaded;
    }
}
