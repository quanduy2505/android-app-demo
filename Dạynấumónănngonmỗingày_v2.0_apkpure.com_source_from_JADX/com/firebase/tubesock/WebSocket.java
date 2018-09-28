package com.firebase.tubesock;

import android.support.v4.view.PointerIconCompat;
import com.google.android.gms.common.ConnectionResult;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.protocol.HTTP;
import rx.android.BuildConfig;
import rx.internal.operators.OnSubscribeConcatMap;

public class WebSocket {
    static final byte OPCODE_BINARY = (byte) 2;
    static final byte OPCODE_CLOSE = (byte) 8;
    static final byte OPCODE_NONE = (byte) 0;
    static final byte OPCODE_PING = (byte) 9;
    static final byte OPCODE_PONG = (byte) 10;
    static final byte OPCODE_TEXT = (byte) 1;
    private static final String THREAD_BASE_NAME = "TubeSock";
    private static final Charset UTF8;
    private static final AtomicInteger clientCount;
    private static ThreadInitializer intializer;
    private static ThreadFactory threadFactory;
    private final int clientId;
    private WebSocketEventHandler eventHandler;
    private final WebSocketHandshake handshake;
    private final Thread innerThread;
    private final WebSocketReceiver receiver;
    private volatile Socket socket;
    private volatile State state;
    private final URI url;
    private final WebSocketWriter writer;

    /* renamed from: com.firebase.tubesock.WebSocket.2 */
    class C05932 implements Runnable {
        C05932() {
        }

        public void run() {
            WebSocket.this.runReader();
        }
    }

    /* renamed from: com.firebase.tubesock.WebSocket.3 */
    static /* synthetic */ class C05943 {
        static final /* synthetic */ int[] $SwitchMap$com$firebase$tubesock$WebSocket$State;

        static {
            $SwitchMap$com$firebase$tubesock$WebSocket$State = new int[State.values().length];
            try {
                $SwitchMap$com$firebase$tubesock$WebSocket$State[State.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$firebase$tubesock$WebSocket$State[State.CONNECTING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$firebase$tubesock$WebSocket$State[State.CONNECTED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$firebase$tubesock$WebSocket$State[State.DISCONNECTING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$firebase$tubesock$WebSocket$State[State.DISCONNECTED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private enum State {
        NONE,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED
    }

    /* renamed from: com.firebase.tubesock.WebSocket.1 */
    static class C11261 implements ThreadInitializer {
        C11261() {
        }

        public void setName(Thread t, String name) {
            t.setName(name);
        }
    }

    static {
        clientCount = new AtomicInteger(0);
        UTF8 = Charset.forName(HTTP.UTF_8);
        threadFactory = Executors.defaultThreadFactory();
        intializer = new C11261();
    }

    static ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    static ThreadInitializer getIntializer() {
        return intializer;
    }

    public static void setThreadFactory(ThreadFactory threadFactory, ThreadInitializer intializer) {
        threadFactory = threadFactory;
        intializer = intializer;
    }

    public WebSocket(URI url) {
        this(url, null);
    }

    public WebSocket(URI url, String protocol) {
        this(url, protocol, null);
    }

    public WebSocket(URI url, String protocol, Map<String, String> extraHeaders) {
        this.state = State.NONE;
        this.socket = null;
        this.eventHandler = null;
        this.clientId = clientCount.incrementAndGet();
        this.innerThread = getThreadFactory().newThread(new C05932());
        this.url = url;
        this.handshake = new WebSocketHandshake(url, protocol, extraHeaders);
        this.receiver = new WebSocketReceiver(this);
        this.writer = new WebSocketWriter(this, THREAD_BASE_NAME, this.clientId);
    }

    public void setEventHandler(WebSocketEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    WebSocketEventHandler getEventHandler() {
        return this.eventHandler;
    }

    public synchronized void connect() {
        if (this.state != State.NONE) {
            this.eventHandler.onError(new WebSocketException("connect() already called"));
            close();
        } else {
            getIntializer().setName(getInnerThread(), "TubeSockReader-" + this.clientId);
            this.state = State.CONNECTING;
            getInnerThread().start();
        }
    }

    public synchronized void send(String data) {
        send(OPCODE_TEXT, data.getBytes(UTF8));
    }

    public synchronized void send(byte[] data) {
        send(OPCODE_BINARY, data);
    }

    synchronized void pong(byte[] data) {
        send(OPCODE_PONG, data);
    }

    private synchronized void send(byte opcode, byte[] data) {
        if (this.state != State.CONNECTED) {
            this.eventHandler.onError(new WebSocketException("error while sending data: not connected"));
        } else {
            try {
                this.writer.send(opcode, true, data);
            } catch (IOException e) {
                this.eventHandler.onError(new WebSocketException("Failed to send frame", e));
                close();
            }
        }
    }

    void handleReceiverError(WebSocketException e) {
        this.eventHandler.onError(e);
        if (this.state == State.CONNECTED) {
            close();
        }
        closeSocket();
    }

    public synchronized void close() {
        switch (C05943.$SwitchMap$com$firebase$tubesock$WebSocket$State[this.state.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                this.state = State.DISCONNECTED;
                break;
            case OnSubscribeConcatMap.END /*2*/:
                closeSocket();
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                sendCloseHandshake();
                break;
        }
    }

    void onCloseOpReceived() {
        closeSocket();
    }

    private synchronized void closeSocket() {
        if (this.state != State.DISCONNECTED) {
            this.receiver.stopit();
            this.writer.stopIt();
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.state = State.DISCONNECTED;
            this.eventHandler.onClose();
        }
    }

    private void sendCloseHandshake() {
        try {
            this.state = State.DISCONNECTING;
            this.writer.stopIt();
            this.writer.send(OPCODE_CLOSE, true, new byte[0]);
        } catch (IOException e) {
            this.eventHandler.onError(new WebSocketException("Failed to send close frame", e));
        }
    }

    private Socket createSocket() {
        String scheme = this.url.getScheme();
        String host = this.url.getHost();
        int port = this.url.getPort();
        if (scheme != null && scheme.equals("ws")) {
            if (port == -1) {
                port = 80;
            }
            try {
                return new Socket(host, port);
            } catch (UnknownHostException uhe) {
                throw new WebSocketException("unknown host: " + host, uhe);
            } catch (IOException ioe) {
                throw new WebSocketException("error while creating socket to " + this.url, ioe);
            }
        } else if (scheme == null || !scheme.equals("wss")) {
            throw new WebSocketException("unsupported protocol: " + scheme);
        } else {
            if (port == -1) {
                port = 443;
            }
            try {
                Socket socket = SSLSocketFactory.getDefault().createSocket(host, port);
                verifyHost((SSLSocket) socket, host);
                return socket;
            } catch (UnknownHostException uhe2) {
                throw new WebSocketException("unknown host: " + host, uhe2);
            } catch (IOException ioe2) {
                throw new WebSocketException("error while creating secure socket to " + this.url, ioe2);
            }
        }
    }

    private void verifyHost(SSLSocket socket, String host) throws SSLException {
        new StrictHostnameVerifier().verify(host, socket.getSession().getPeerCertificates()[0]);
    }

    public void blockClose() throws InterruptedException {
        if (this.writer.getInnerThread().getState() != java.lang.Thread.State.NEW) {
            this.writer.getInnerThread().join();
        }
        getInnerThread().join();
    }

    private void runReader() {
        try {
            Socket socket = createSocket();
            synchronized (this) {
                this.socket = socket;
                if (this.state == State.DISCONNECTED) {
                    try {
                        this.socket.close();
                        this.socket = null;
                        close();
                        return;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                DataInputStream input = new DataInputStream(socket.getInputStream());
                OutputStream output = socket.getOutputStream();
                output.write(this.handshake.getHandshake());
                boolean handshakeComplete = false;
                byte[] buffer = new byte[PointerIconCompat.TYPE_DEFAULT];
                int pos = 0;
                ArrayList<String> handshakeLines = new ArrayList();
                while (!handshakeComplete) {
                    int b = input.read();
                    if (b == -1) {
                        throw new WebSocketException("Connection closed before handshake was complete");
                    }
                    buffer[pos] = (byte) b;
                    pos++;
                    String line;
                    if (buffer[pos - 1] == 10 && buffer[pos - 2] == 13) {
                        line = new String(buffer, UTF8);
                        if (line.trim().equals(BuildConfig.VERSION_NAME)) {
                            handshakeComplete = true;
                        } else {
                            handshakeLines.add(line.trim());
                        }
                        buffer = new byte[PointerIconCompat.TYPE_DEFAULT];
                        pos = 0;
                    } else if (pos == 1000) {
                        line = new String(buffer, UTF8);
                        throw new WebSocketException("Unexpected long line in handshake: " + line);
                    }
                }
                this.handshake.verifyServerStatusLine((String) handshakeLines.get(0));
                handshakeLines.remove(0);
                HashMap<String, String> headers = new HashMap();
                Iterator i$ = handshakeLines.iterator();
                while (i$.hasNext()) {
                    String[] keyValue = ((String) i$.next()).split(": ", 2);
                    headers.put(keyValue[0], keyValue[1]);
                }
                this.handshake.verifyServerHandshakeHeaders(headers);
                this.writer.setOutput(output);
                this.receiver.setInput(input);
                this.state = State.CONNECTED;
                this.writer.getInnerThread().start();
                this.eventHandler.onOpen();
                this.receiver.run();
                close();
                return;
            }
        } catch (WebSocketException wse) {
            try {
                this.eventHandler.onError(wse);
            } finally {
                close();
            }
        } catch (IOException ioe) {
            this.eventHandler.onError(new WebSocketException("error while connecting: " + ioe.getMessage(), ioe));
            close();
        }
    }

    Thread getInnerThread() {
        return this.innerThread;
    }
}
