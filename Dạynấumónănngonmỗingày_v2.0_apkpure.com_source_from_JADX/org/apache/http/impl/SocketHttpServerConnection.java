package org.apache.http.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import org.apache.http.HttpInetConnection;
import org.apache.http.impl.io.SocketInputBuffer;
import org.apache.http.impl.io.SocketOutputBuffer;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class SocketHttpServerConnection extends AbstractHttpServerConnection implements HttpInetConnection {
    private volatile boolean open;
    private volatile Socket socket;

    public SocketHttpServerConnection() {
        this.socket = null;
    }

    protected void assertNotOpen() {
        if (this.open) {
            throw new IllegalStateException("Connection is already open");
        }
    }

    protected void assertOpen() {
        if (!this.open) {
            throw new IllegalStateException("Connection is not open");
        }
    }

    protected SessionInputBuffer createHttpDataReceiver(Socket socket, int buffersize, HttpParams params) throws IOException {
        return createSessionInputBuffer(socket, buffersize, params);
    }

    protected SessionOutputBuffer createHttpDataTransmitter(Socket socket, int buffersize, HttpParams params) throws IOException {
        return createSessionOutputBuffer(socket, buffersize, params);
    }

    protected SessionInputBuffer createSessionInputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
        return new SocketInputBuffer(socket, buffersize, params);
    }

    protected SessionOutputBuffer createSessionOutputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
        return new SocketOutputBuffer(socket, buffersize, params);
    }

    protected void bind(Socket socket, HttpParams params) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        } else if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            this.socket = socket;
            int buffersize = HttpConnectionParams.getSocketBufferSize(params);
            init(createHttpDataReceiver(socket, buffersize, params), createHttpDataTransmitter(socket, buffersize, params), params);
            this.open = true;
        }
    }

    protected Socket getSocket() {
        return this.socket;
    }

    public boolean isOpen() {
        return this.open;
    }

    public InetAddress getLocalAddress() {
        if (this.socket != null) {
            return this.socket.getLocalAddress();
        }
        return null;
    }

    public int getLocalPort() {
        if (this.socket != null) {
            return this.socket.getLocalPort();
        }
        return -1;
    }

    public InetAddress getRemoteAddress() {
        if (this.socket != null) {
            return this.socket.getInetAddress();
        }
        return null;
    }

    public int getRemotePort() {
        if (this.socket != null) {
            return this.socket.getPort();
        }
        return -1;
    }

    public void setSocketTimeout(int timeout) {
        assertOpen();
        if (this.socket != null) {
            try {
                this.socket.setSoTimeout(timeout);
            } catch (SocketException e) {
            }
        }
    }

    public int getSocketTimeout() {
        int i = -1;
        if (this.socket != null) {
            try {
                i = this.socket.getSoTimeout();
            } catch (SocketException e) {
            }
        }
        return i;
    }

    public void shutdown() throws IOException {
        this.open = false;
        Socket tmpsocket = this.socket;
        if (tmpsocket != null) {
            tmpsocket.close();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void close() throws java.io.IOException {
        /*
        r3 = this;
        r2 = 0;
        r1 = r3.open;
        if (r1 != 0) goto L_0x0006;
    L_0x0005:
        return;
    L_0x0006:
        r3.open = r2;
        r3.open = r2;
        r0 = r3.socket;
        r3.doFlush();	 Catch:{ all -> 0x0019 }
        r0.shutdownOutput();	 Catch:{ IOException -> 0x001e, UnsupportedOperationException -> 0x0022 }
    L_0x0012:
        r0.shutdownInput();	 Catch:{ IOException -> 0x0020, UnsupportedOperationException -> 0x0022 }
    L_0x0015:
        r0.close();
        goto L_0x0005;
    L_0x0019:
        r1 = move-exception;
        r0.close();
        throw r1;
    L_0x001e:
        r1 = move-exception;
        goto L_0x0012;
    L_0x0020:
        r1 = move-exception;
        goto L_0x0015;
    L_0x0022:
        r1 = move-exception;
        goto L_0x0015;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.SocketHttpServerConnection.close():void");
    }
}
