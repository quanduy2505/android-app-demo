package org.apache.http.util;

import java.io.UnsupportedEncodingException;
import org.apache.http.protocol.HTTP;

public final class EncodingUtils {
    public static String getString(byte[] data, int offset, int length, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        } else if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        } else {
            try {
                return new String(data, offset, length, charset);
            } catch (UnsupportedEncodingException e) {
                return new String(data, offset, length);
            }
        }
    }

    public static String getString(byte[] data, String charset) {
        if (data != null) {
            return getString(data, 0, data.length, charset);
        }
        throw new IllegalArgumentException("Parameter may not be null");
    }

    public static byte[] getBytes(String data, String charset) {
        if (data == null) {
            throw new IllegalArgumentException("data may not be null");
        } else if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        } else {
            try {
                return data.getBytes(charset);
            } catch (UnsupportedEncodingException e) {
                return data.getBytes();
            }
        }
    }

    public static byte[] getAsciiBytes(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return data.getBytes(HTTP.US_ASCII);
        } catch (UnsupportedEncodingException e) {
            throw new Error("HttpClient requires ASCII support");
        }
    }

    public static String getAsciiString(byte[] data, int offset, int length) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return new String(data, offset, length, HTTP.US_ASCII);
        } catch (UnsupportedEncodingException e) {
            throw new Error("HttpClient requires ASCII support");
        }
    }

    public static String getAsciiString(byte[] data) {
        if (data != null) {
            return getAsciiString(data, 0, data.length);
        }
        throw new IllegalArgumentException("Parameter may not be null");
    }

    private EncodingUtils() {
    }
}
