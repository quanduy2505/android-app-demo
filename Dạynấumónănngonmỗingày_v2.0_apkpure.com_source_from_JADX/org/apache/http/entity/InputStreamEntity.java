package org.apache.http.entity;

import android.support.v4.media.session.PlaybackStateCompat;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamEntity extends AbstractHttpEntity {
    private static final int BUFFER_SIZE = 2048;
    private final InputStream content;
    private final long length;

    public InputStreamEntity(InputStream instream, long length) {
        if (instream == null) {
            throw new IllegalArgumentException("Source input stream may not be null");
        }
        this.content = instream;
        this.length = length;
    }

    public boolean isRepeatable() {
        return false;
    }

    public long getContentLength() {
        return this.length;
    }

    public InputStream getContent() throws IOException {
        return this.content;
    }

    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = this.content;
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int l;
            if (this.length < 0) {
                while (true) {
                    l = instream.read(buffer);
                    if (l == -1) {
                        break;
                    }
                    outstream.write(buffer, 0, l);
                }
            } else {
                long remaining = this.length;
                while (remaining > 0) {
                    l = instream.read(buffer, 0, (int) Math.min(PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH, remaining));
                    if (l == -1) {
                        break;
                    }
                    outstream.write(buffer, 0, l);
                    remaining -= (long) l;
                }
            }
            instream.close();
        } catch (Throwable th) {
            instream.close();
        }
    }

    public boolean isStreaming() {
        return true;
    }

    public void consumeContent() throws IOException {
        this.content.close();
    }
}
