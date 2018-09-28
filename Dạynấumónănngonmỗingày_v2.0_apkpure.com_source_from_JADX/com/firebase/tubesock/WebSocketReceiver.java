package com.firebase.tubesock;

import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import me.wangyuwei.loadingview.C0801R;

class WebSocketReceiver {
    private WebSocketEventHandler eventHandler;
    private DataInputStream input;
    private byte[] inputHeader;
    private Builder pendingBuilder;
    private volatile boolean stop;
    private WebSocket websocket;

    WebSocketReceiver(WebSocket websocket) {
        this.input = null;
        this.websocket = null;
        this.eventHandler = null;
        this.inputHeader = new byte[C0801R.styleable.AppCompatTheme_spinnerStyle];
        this.stop = false;
        this.websocket = websocket;
    }

    void setInput(DataInputStream input) {
        this.input = input;
    }

    void run() {
        this.eventHandler = this.websocket.getEventHandler();
        while (!this.stop) {
            try {
                int offset = 0 + read(this.inputHeader, 0, 1);
                boolean fin = (this.inputHeader[0] & TransportMediator.FLAG_KEY_MEDIA_NEXT) != 0;
                if ((this.inputHeader[0] & C0801R.styleable.AppCompatTheme_spinnerStyle) != 0) {
                    throw new WebSocketException("Invalid frame received");
                }
                byte opcode = (byte) (this.inputHeader[0] & 15);
                offset += read(this.inputHeader, offset, 1);
                byte length = this.inputHeader[1];
                long payload_length = 0;
                if (length < 126) {
                    payload_length = (long) length;
                } else if (length == 126) {
                    offset += read(this.inputHeader, offset, 2);
                    payload_length = (long) (((this.inputHeader[2] & MotionEventCompat.ACTION_MASK) << 8) | (this.inputHeader[3] & MotionEventCompat.ACTION_MASK));
                } else if (length == 127) {
                    payload_length = parseLong(this.inputHeader, (offset + read(this.inputHeader, offset, 8)) - 8);
                }
                byte[] payload = new byte[((int) payload_length)];
                read(payload, 0, (int) payload_length);
                if (opcode == 8) {
                    this.websocket.onCloseOpReceived();
                } else if (opcode == 10) {
                    continue;
                } else if (opcode == 1 || opcode == 2 || opcode == 9 || opcode == null) {
                    appendBytes(fin, opcode, payload);
                } else {
                    throw new WebSocketException("Unsupported opcode: " + opcode);
                }
            } catch (SocketTimeoutException e) {
            } catch (IOException ioe) {
                handleError(new WebSocketException("IO Error", ioe));
            } catch (WebSocketException e2) {
                handleError(e2);
            }
        }
    }

    private void appendBytes(boolean fin, byte opcode, byte[] data) {
        if (opcode == 9) {
            if (fin) {
                handlePing(data);
                return;
            }
            throw new WebSocketException("PING must not fragment across frames");
        } else if (this.pendingBuilder != null && opcode != null) {
            throw new WebSocketException("Failed to continue outstanding frame");
        } else if (this.pendingBuilder == null && opcode == null) {
            throw new WebSocketException("Received continuing frame, but there's nothing to continue");
        } else {
            if (this.pendingBuilder == null) {
                this.pendingBuilder = MessageBuilderFactory.builder(opcode);
            }
            if (!this.pendingBuilder.appendBytes(data)) {
                throw new WebSocketException("Failed to decode frame");
            } else if (fin) {
                WebSocketMessage message = this.pendingBuilder.toMessage();
                this.pendingBuilder = null;
                if (message == null) {
                    throw new WebSocketException("Failed to decode whole message");
                }
                this.eventHandler.onMessage(message);
            }
        }
    }

    private void handlePing(byte[] payload) {
        if (payload.length <= 125) {
            this.websocket.pong(payload);
            return;
        }
        throw new WebSocketException("PING frame too long");
    }

    private long parseLong(byte[] buffer, int offset) {
        return (((((((((long) buffer[offset + 0]) << 56) + (((long) (buffer[offset + 1] & MotionEventCompat.ACTION_MASK)) << 48)) + (((long) (buffer[offset + 2] & MotionEventCompat.ACTION_MASK)) << 40)) + (((long) (buffer[offset + 3] & MotionEventCompat.ACTION_MASK)) << 32)) + (((long) (buffer[offset + 4] & MotionEventCompat.ACTION_MASK)) << 24)) + ((long) ((buffer[offset + 5] & MotionEventCompat.ACTION_MASK) << 16))) + ((long) ((buffer[offset + 6] & MotionEventCompat.ACTION_MASK) << 8))) + ((long) ((buffer[offset + 7] & MotionEventCompat.ACTION_MASK) << 0));
    }

    private int read(byte[] buffer, int offset, int length) throws IOException {
        this.input.readFully(buffer, offset, length);
        return length;
    }

    void stopit() {
        this.stop = true;
    }

    boolean isRunning() {
        return !this.stop;
    }

    private void handleError(WebSocketException e) {
        stopit();
        this.websocket.handleReceiverError(e);
    }
}
