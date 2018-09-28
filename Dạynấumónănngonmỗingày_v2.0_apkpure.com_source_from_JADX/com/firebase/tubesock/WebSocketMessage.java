package com.firebase.tubesock;

public class WebSocketMessage {
    private byte[] byteMessage;
    private byte opcode;
    private String stringMessage;

    public WebSocketMessage(byte[] message) {
        this.byteMessage = message;
        this.opcode = (byte) 2;
    }

    public WebSocketMessage(String message) {
        this.stringMessage = message;
        this.opcode = (byte) 1;
    }

    public boolean isText() {
        return this.opcode == (byte) 1;
    }

    public boolean isBinary() {
        return this.opcode == 2;
    }

    public byte[] getBytes() {
        return this.byteMessage;
    }

    public String getText() {
        return this.stringMessage;
    }
}
