package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.http.protocol.HTTP;

public class StringEntity extends AbstractHttpEntity implements Cloneable {
    protected final byte[] content;

    public StringEntity(String string, String mimeType, String charset) throws UnsupportedEncodingException {
        if (string == null) {
            throw new IllegalArgumentException("Source string may not be null");
        }
        if (mimeType == null) {
            mimeType = HTTP.PLAIN_TEXT_TYPE;
        }
        if (charset == null) {
            charset = HTTP.ISO_8859_1;
        }
        this.content = string.getBytes(charset);
        setContentType(new StringBuffer().append(mimeType).append(HTTP.CHARSET_PARAM).append(charset).toString());
    }

    public StringEntity(String string, String charset) throws UnsupportedEncodingException {
        this(string, null, charset);
    }

    public StringEntity(String string) throws UnsupportedEncodingException {
        this(string, null);
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return (long) this.content.length;
    }

    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        outstream.write(this.content);
        outstream.flush();
    }

    public boolean isStreaming() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
