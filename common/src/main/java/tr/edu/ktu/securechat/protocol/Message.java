package tr.edu.ktu.securechat.protocol;

import tr.edu.ktu.securechat.crypto.CommunicationMode;
import tr.edu.ktu.securechat.crypto.KeyExchangeMethod;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private CommunicationMode mode;
    private KeyExchangeMethod kexMethod;
    private String senderId;
    private String receiverId;
    private byte[] iv;
    private byte[] payload;
    private long timestamp;

    public Message() {
        this.timestamp = System.currentTimeMillis();
    }

    public Message(MessageType type, CommunicationMode mode, String senderId, String receiverId, byte[] payload) {
        this();
        this.type = type;
        this.mode = mode;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public CommunicationMode getMode() {
        return mode;
    }

    public void setMode(CommunicationMode mode) {
        this.mode = mode;
    }

    public KeyExchangeMethod getKexMethod() {
        return kexMethod;
    }

    public void setKexMethod(KeyExchangeMethod kexMethod) {
        this.kexMethod = kexMethod;
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

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", mode=" + mode +
                ", kexMethod=" + kexMethod +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", payloadLen=" + (payload != null ? payload.length : 0) +
                ", timestamp=" + timestamp +
                '}';
    }
}