package com.firebase.tubesock;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.TransportMediator;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class WebSocketWriter {
    private WritableByteChannel channel;
    private boolean closeSent;
    private final Thread innerThread;
    private BlockingQueue<ByteBuffer> pendingBuffers;
    private final Random random;
    private volatile boolean stop;
    private WebSocket websocket;

    /* renamed from: com.firebase.tubesock.WebSocketWriter.1 */
    class C05951 implements Runnable {
        C05951() {
        }

        public void run() {
            WebSocketWriter.this.runWriter();
        }
    }

    WebSocketWriter(WebSocket websocket, String threadBaseName, int clientId) {
        this.random = new Random();
        this.stop = false;
        this.closeSent = false;
        this.innerThread = WebSocket.getThreadFactory().newThread(new C05951());
        WebSocket.getIntializer().setName(getInnerThread(), threadBaseName + "Writer-" + clientId);
        this.websocket = websocket;
        this.pendingBuffers = new LinkedBlockingQueue();
    }

    void setOutput(OutputStream output) {
        this.channel = Channels.newChannel(output);
    }

    private ByteBuffer frameInBuffer(byte opcode, boolean masking, byte[] data) throws IOException {
        int headerLength = 2;
        if (masking) {
            headerLength = 2 + 4;
        }
        int length = data.length;
        if (length >= TransportMediator.KEYCODE_MEDIA_PLAY) {
            if (length <= SupportMenu.USER_MASK) {
                headerLength += 2;
            } else {
                headerLength += 8;
            }
        }
        ByteBuffer frame = ByteBuffer.allocate(data.length + headerLength);
        frame.put((byte) (Byte.MIN_VALUE | opcode));
        if (length < TransportMediator.KEYCODE_MEDIA_PLAY) {
            if (masking) {
                length |= TransportMediator.FLAG_KEY_MEDIA_NEXT;
            }
            frame.put((byte) length);
        } else if (length <= SupportMenu.USER_MASK) {
            length_field = TransportMediator.KEYCODE_MEDIA_PLAY;
            if (masking) {
                length_field = TransportMediator.KEYCODE_MEDIA_PLAY | TransportMediator.FLAG_KEY_MEDIA_NEXT;
            }
            frame.put((byte) length_field);
            frame.putShort((short) length);
        } else {
            length_field = TransportMediator.KEYCODE_MEDIA_PAUSE;
            if (masking) {
                length_field = TransportMediator.KEYCODE_MEDIA_PAUSE | TransportMediator.FLAG_KEY_MEDIA_NEXT;
            }
            frame.put((byte) length_field);
            frame.putInt(0);
            frame.putInt(length);
        }
        if (masking) {
            byte[] mask = generateMask();
            frame.put(mask);
            for (int i = 0; i < data.length; i++) {
                frame.put((byte) (data[i] ^ mask[i % 4]));
            }
        }
        frame.flip();
        return frame;
    }

    private byte[] generateMask() {
        byte[] mask = new byte[4];
        this.random.nextBytes(mask);
        return mask;
    }

    synchronized void send(byte opcode, boolean masking, byte[] data) throws IOException {
        ByteBuffer frame = frameInBuffer(opcode, masking, data);
        if (!this.stop || (!this.closeSent && opcode == (byte) 8)) {
            if (opcode == (byte) 8) {
                this.closeSent = true;
            }
            this.pendingBuffers.add(frame);
        } else {
            throw new WebSocketException("Shouldn't be sending");
        }
    }

    private void writeMessage() throws InterruptedException, IOException {
        this.channel.write((ByteBuffer) this.pendingBuffers.take());
    }

    void stopIt() {
        this.stop = true;
    }

    private void handleError(WebSocketException e) {
        this.websocket.handleReceiverError(e);
    }

    private void runWriter() {
        while (!this.stop && !Thread.interrupted()) {
            try {
                writeMessage();
            } catch (IOException e) {
                handleError(new WebSocketException("IO Exception", e));
                return;
            } catch (InterruptedException e2) {
                return;
            }
        }
        for (int i = 0; i < this.pendingBuffers.size(); i++) {
            writeMessage();
        }
    }

    Thread getInnerThread() {
        return this.innerThread;
    }
}
