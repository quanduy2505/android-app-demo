package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

abstract class DecompressingEntity extends HttpEntityWrapper {
    private static final int BUFFER_SIZE = 2048;
    private InputStream content;

    abstract InputStream getDecompressingInputStream(InputStream inputStream) throws IOException;

    public DecompressingEntity(HttpEntity wrapped) {
        super(wrapped);
    }

    public InputStream getContent() throws IOException {
        if (!this.wrappedEntity.isStreaming()) {
            return getDecompressingInputStream(this.wrappedEntity.getContent());
        }
        if (this.content == null) {
            this.content = getDecompressingInputStream(this.wrappedEntity.getContent());
        }
        return this.content;
    }

    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = getContent();
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int l = instream.read(buffer);
                if (l == -1) {
                    break;
                }
                outstream.write(buffer, 0, l);
            }
        } finally {
            instream.close();
        }
    }
}
