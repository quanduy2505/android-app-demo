package com.firebase.client.utilities;

import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import com.google.android.gms.common.ConnectionResult;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Base64 {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int DECODE = 0;
    public static final int DONT_GUNZIP = 4;
    public static final int DO_BREAK_LINES = 8;
    public static final int ENCODE = 1;
    private static final byte EQUALS_SIGN = (byte) 61;
    private static final byte EQUALS_SIGN_ENC = (byte) -1;
    public static final int GZIP = 2;
    private static final int MAX_LINE_LENGTH = 76;
    private static final byte NEW_LINE = (byte) 10;
    public static final int NO_OPTIONS = 0;
    public static final int ORDERED = 32;
    private static final String PREFERRED_ENCODING = "US-ASCII";
    public static final int URL_SAFE = 16;
    private static final byte WHITE_SPACE_ENC = (byte) -5;
    private static final byte[] _ORDERED_ALPHABET;
    private static final byte[] _ORDERED_DECODABET;
    private static final byte[] _STANDARD_ALPHABET;
    private static final byte[] _STANDARD_DECODABET;
    private static final byte[] _URL_SAFE_ALPHABET;
    private static final byte[] _URL_SAFE_DECODABET;

    /* renamed from: com.firebase.client.utilities.Base64.1 */
    static class C05851 extends ObjectInputStream {
        final /* synthetic */ ClassLoader val$loader;

        C05851(java.io.InputStream x0, ClassLoader classLoader) {
            this.val$loader = classLoader;
            super(x0);
        }

        public Class<?> resolveClass(ObjectStreamClass streamClass) throws IOException, ClassNotFoundException {
            Class<?> c = Class.forName(streamClass.getName(), Base64.$assertionsDisabled, this.val$loader);
            if (c == null) {
                return super.resolveClass(streamClass);
            }
            return c;
        }
    }

    public static class InputStream extends FilterInputStream {
        private boolean breakLines;
        private byte[] buffer;
        private int bufferLength;
        private byte[] decodabet;
        private boolean encode;
        private int lineLength;
        private int numSigBytes;
        private int options;
        private int position;

        public InputStream(java.io.InputStream in) {
            this(in, Base64.NO_OPTIONS);
        }

        public InputStream(java.io.InputStream in, int options) {
            boolean z = true;
            super(in);
            this.options = options;
            this.breakLines = (options & Base64.DO_BREAK_LINES) > 0 ? true : Base64.$assertionsDisabled;
            if ((options & Base64.ENCODE) <= 0) {
                z = Base64.$assertionsDisabled;
            }
            this.encode = z;
            this.bufferLength = this.encode ? Base64.DONT_GUNZIP : 3;
            this.buffer = new byte[this.bufferLength];
            this.position = -1;
            this.lineLength = Base64.NO_OPTIONS;
            this.decodabet = Base64.getDecodabet(options);
        }

        public int read() throws IOException {
            int b;
            if (this.position < 0) {
                int i;
                if (this.encode) {
                    byte[] b3 = new byte[3];
                    int numBinaryBytes = Base64.NO_OPTIONS;
                    for (i = Base64.NO_OPTIONS; i < 3; i += Base64.ENCODE) {
                        b = this.in.read();
                        if (b < 0) {
                            break;
                        }
                        b3[i] = (byte) b;
                        numBinaryBytes += Base64.ENCODE;
                    }
                    if (numBinaryBytes <= 0) {
                        return -1;
                    }
                    Base64.encode3to4(b3, Base64.NO_OPTIONS, numBinaryBytes, this.buffer, Base64.NO_OPTIONS, this.options);
                    this.position = Base64.NO_OPTIONS;
                    this.numSigBytes = Base64.DONT_GUNZIP;
                } else {
                    byte[] b4 = new byte[Base64.DONT_GUNZIP];
                    i = Base64.NO_OPTIONS;
                    while (i < Base64.DONT_GUNZIP) {
                        do {
                            b = this.in.read();
                            if (b < 0) {
                                break;
                            }
                        } while (this.decodabet[b & TransportMediator.KEYCODE_MEDIA_PAUSE] <= -5);
                        if (b < 0) {
                            break;
                        }
                        b4[i] = (byte) b;
                        i += Base64.ENCODE;
                    }
                    if (i == Base64.DONT_GUNZIP) {
                        this.numSigBytes = Base64.decode4to3(b4, Base64.NO_OPTIONS, this.buffer, Base64.NO_OPTIONS, this.options);
                        this.position = Base64.NO_OPTIONS;
                    } else if (i == 0) {
                        return -1;
                    } else {
                        throw new IOException("Improperly padded Base64 input.");
                    }
                }
            }
            if (this.position < 0) {
                throw new IOException("Error in Base64 code reading stream.");
            } else if (this.position >= this.numSigBytes) {
                return -1;
            } else {
                if (this.encode && this.breakLines && this.lineLength >= Base64.MAX_LINE_LENGTH) {
                    this.lineLength = Base64.NO_OPTIONS;
                    return 10;
                }
                this.lineLength += Base64.ENCODE;
                byte[] bArr = this.buffer;
                int i2 = this.position;
                this.position = i2 + Base64.ENCODE;
                b = bArr[i2];
                if (this.position >= this.bufferLength) {
                    this.position = -1;
                }
                return b & MotionEventCompat.ACTION_MASK;
            }
        }

        public int read(byte[] dest, int off, int len) throws IOException {
            int i = Base64.NO_OPTIONS;
            while (i < len) {
                int b = read();
                if (b >= 0) {
                    dest[off + i] = (byte) b;
                    i += Base64.ENCODE;
                } else if (i == 0) {
                    return -1;
                } else {
                    return i;
                }
            }
            return i;
        }
    }

    public static class OutputStream extends FilterOutputStream {
        private byte[] b4;
        private boolean breakLines;
        private byte[] buffer;
        private int bufferLength;
        private byte[] decodabet;
        private boolean encode;
        private int lineLength;
        private int options;
        private int position;
        private boolean suspendEncoding;

        public OutputStream(java.io.OutputStream out) {
            this(out, Base64.ENCODE);
        }

        public OutputStream(java.io.OutputStream out, int options) {
            int i;
            boolean z = true;
            super(out);
            this.breakLines = (options & Base64.DO_BREAK_LINES) != 0 ? true : Base64.$assertionsDisabled;
            if ((options & Base64.ENCODE) == 0) {
                z = Base64.$assertionsDisabled;
            }
            this.encode = z;
            if (this.encode) {
                i = 3;
            } else {
                i = Base64.DONT_GUNZIP;
            }
            this.bufferLength = i;
            this.buffer = new byte[this.bufferLength];
            this.position = Base64.NO_OPTIONS;
            this.lineLength = Base64.NO_OPTIONS;
            this.suspendEncoding = Base64.$assertionsDisabled;
            this.b4 = new byte[Base64.DONT_GUNZIP];
            this.options = options;
            this.decodabet = Base64.getDecodabet(options);
        }

        public void write(int theByte) throws IOException {
            if (this.suspendEncoding) {
                this.out.write(theByte);
            } else if (this.encode) {
                r1 = this.buffer;
                r2 = this.position;
                this.position = r2 + Base64.ENCODE;
                r1[r2] = (byte) theByte;
                if (this.position >= this.bufferLength) {
                    this.out.write(Base64.encode3to4(this.b4, this.buffer, this.bufferLength, this.options));
                    this.lineLength += Base64.DONT_GUNZIP;
                    if (this.breakLines && this.lineLength >= Base64.MAX_LINE_LENGTH) {
                        this.out.write(10);
                        this.lineLength = Base64.NO_OPTIONS;
                    }
                    this.position = Base64.NO_OPTIONS;
                }
            } else if (this.decodabet[theByte & TransportMediator.KEYCODE_MEDIA_PAUSE] > Base64.WHITE_SPACE_ENC) {
                r1 = this.buffer;
                r2 = this.position;
                this.position = r2 + Base64.ENCODE;
                r1[r2] = (byte) theByte;
                if (this.position >= this.bufferLength) {
                    this.out.write(this.b4, Base64.NO_OPTIONS, Base64.decode4to3(this.buffer, Base64.NO_OPTIONS, this.b4, Base64.NO_OPTIONS, this.options));
                    this.position = Base64.NO_OPTIONS;
                }
            } else if (this.decodabet[theByte & TransportMediator.KEYCODE_MEDIA_PAUSE] != Base64.WHITE_SPACE_ENC) {
                throw new IOException("Invalid character in Base64 data.");
            }
        }

        public void write(byte[] theBytes, int off, int len) throws IOException {
            if (this.suspendEncoding) {
                this.out.write(theBytes, off, len);
                return;
            }
            for (int i = Base64.NO_OPTIONS; i < len; i += Base64.ENCODE) {
                write(theBytes[off + i]);
            }
        }

        public void flushBase64() throws IOException {
            if (this.position <= 0) {
                return;
            }
            if (this.encode) {
                this.out.write(Base64.encode3to4(this.b4, this.buffer, this.position, this.options));
                this.position = Base64.NO_OPTIONS;
                return;
            }
            throw new IOException("Base64 input not properly padded.");
        }

        public void close() throws IOException {
            flushBase64();
            super.close();
            this.buffer = null;
            this.out = null;
        }

        public void suspendEncoding() throws IOException {
            flushBase64();
            this.suspendEncoding = true;
        }

        public void resumeEncoding() {
            this.suspendEncoding = Base64.$assertionsDisabled;
        }
    }

    static {
        boolean z;
        if (Base64.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        _STANDARD_ALPHABET = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};
        _STANDARD_DECODABET = new byte[]{(byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, WHITE_SPACE_ENC, WHITE_SPACE_ENC, (byte) -9, (byte) -9, WHITE_SPACE_ENC, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, WHITE_SPACE_ENC, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 62, (byte) -9, (byte) -9, (byte) -9, (byte) 63, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 59, (byte) 60, EQUALS_SIGN, (byte) -9, (byte) -9, (byte) -9, EQUALS_SIGN_ENC, (byte) -9, (byte) -9, (byte) -9, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, NEW_LINE, (byte) 11, (byte) 12, (byte) 13, (byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19, (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 26, (byte) 27, (byte) 28, (byte) 29, (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34, (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44, (byte) 45, (byte) 46, (byte) 47, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9};
        _URL_SAFE_ALPHABET = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 45, (byte) 95};
        _URL_SAFE_DECODABET = new byte[]{(byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, WHITE_SPACE_ENC, WHITE_SPACE_ENC, (byte) -9, (byte) -9, WHITE_SPACE_ENC, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, WHITE_SPACE_ENC, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 62, (byte) -9, (byte) -9, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 59, (byte) 60, EQUALS_SIGN, (byte) -9, (byte) -9, (byte) -9, EQUALS_SIGN_ENC, (byte) -9, (byte) -9, (byte) -9, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, NEW_LINE, (byte) 11, (byte) 12, (byte) 13, (byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19, (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 63, (byte) -9, (byte) 26, (byte) 27, (byte) 28, (byte) 29, (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34, (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44, (byte) 45, (byte) 46, (byte) 47, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9};
        _ORDERED_ALPHABET = new byte[]{(byte) 45, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 95, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122};
        _ORDERED_DECODABET = new byte[]{(byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, WHITE_SPACE_ENC, WHITE_SPACE_ENC, (byte) -9, (byte) -9, WHITE_SPACE_ENC, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, WHITE_SPACE_ENC, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 0, (byte) -9, (byte) -9, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, NEW_LINE, (byte) -9, (byte) -9, (byte) -9, EQUALS_SIGN_ENC, (byte) -9, (byte) -9, (byte) -9, (byte) 11, (byte) 12, (byte) 13, (byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19, (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) 26, (byte) 27, (byte) 28, (byte) 29, (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34, (byte) 35, (byte) 36, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) 37, (byte) -9, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44, (byte) 45, (byte) 46, (byte) 47, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 59, (byte) 60, EQUALS_SIGN, (byte) 62, (byte) 63, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9, (byte) -9};
    }

    private static final byte[] getAlphabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE) {
            return _URL_SAFE_ALPHABET;
        }
        if ((options & ORDERED) == ORDERED) {
            return _ORDERED_ALPHABET;
        }
        return _STANDARD_ALPHABET;
    }

    private static final byte[] getDecodabet(int options) {
        if ((options & URL_SAFE) == URL_SAFE) {
            return _URL_SAFE_DECODABET;
        }
        if ((options & ORDERED) == ORDERED) {
            return _ORDERED_DECODABET;
        }
        return _STANDARD_DECODABET;
    }

    private Base64() {
    }

    private static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options) {
        encode3to4(threeBytes, NO_OPTIONS, numSigBytes, b4, NO_OPTIONS, options);
        return b4;
    }

    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset, int options) {
        int i;
        int i2 = NO_OPTIONS;
        byte[] ALPHABET = getAlphabet(options);
        if (numSigBytes > 0) {
            i = (source[srcOffset] << 24) >>> DO_BREAK_LINES;
        } else {
            i = NO_OPTIONS;
        }
        int i3 = (numSigBytes > ENCODE ? (source[srcOffset + ENCODE] << 24) >>> URL_SAFE : NO_OPTIONS) | i;
        if (numSigBytes > GZIP) {
            i2 = (source[srcOffset + GZIP] << 24) >>> 24;
        }
        int inBuff = i3 | i2;
        switch (numSigBytes) {
            case ENCODE /*1*/:
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + ENCODE] = ALPHABET[(inBuff >>> 12) & 63];
                destination[destOffset + GZIP] = EQUALS_SIGN;
                destination[destOffset + 3] = EQUALS_SIGN;
                break;
            case GZIP /*2*/:
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + ENCODE] = ALPHABET[(inBuff >>> 12) & 63];
                destination[destOffset + GZIP] = ALPHABET[(inBuff >>> 6) & 63];
                destination[destOffset + 3] = EQUALS_SIGN;
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                destination[destOffset] = ALPHABET[inBuff >>> 18];
                destination[destOffset + ENCODE] = ALPHABET[(inBuff >>> 12) & 63];
                destination[destOffset + GZIP] = ALPHABET[(inBuff >>> 6) & 63];
                destination[destOffset + 3] = ALPHABET[inBuff & 63];
                break;
        }
        return destination;
    }

    public static void encode(ByteBuffer raw, ByteBuffer encoded) {
        byte[] raw3 = new byte[3];
        byte[] enc4 = new byte[DONT_GUNZIP];
        while (raw.hasRemaining()) {
            int rem = Math.min(3, raw.remaining());
            raw.get(raw3, NO_OPTIONS, rem);
            encode3to4(enc4, raw3, rem, NO_OPTIONS);
            encoded.put(enc4);
        }
    }

    public static void encode(ByteBuffer raw, CharBuffer encoded) {
        byte[] raw3 = new byte[3];
        byte[] enc4 = new byte[DONT_GUNZIP];
        while (raw.hasRemaining()) {
            int rem = Math.min(3, raw.remaining());
            raw.get(raw3, NO_OPTIONS, rem);
            encode3to4(enc4, raw3, rem, NO_OPTIONS);
            for (int i = NO_OPTIONS; i < DONT_GUNZIP; i += ENCODE) {
                encoded.put((char) (enc4[i] & MotionEventCompat.ACTION_MASK));
            }
        }
    }

    public static String encodeObject(Serializable serializableObject) throws IOException {
        return encodeObject(serializableObject, NO_OPTIONS);
    }

    public static String encodeObject(Serializable serializableObject, int options) throws IOException {
        GZIPOutputStream gzos;
        IOException e;
        Throwable th;
        if (serializableObject == null) {
            throw new NullPointerException("Cannot serialize a null object.");
        }
        ByteArrayOutputStream baos = null;
        java.io.OutputStream b64os = null;
        GZIPOutputStream gzos2 = null;
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            try {
                java.io.OutputStream b64os2 = new OutputStream(baos2, options | ENCODE);
                if ((options & GZIP) != 0) {
                    try {
                        gzos = new GZIPOutputStream(b64os2);
                    } catch (IOException e2) {
                        e = e2;
                        b64os = b64os2;
                        baos = baos2;
                        try {
                            throw e;
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        b64os = b64os2;
                        baos = baos2;
                        try {
                            oos.close();
                        } catch (Exception e3) {
                        }
                        try {
                            gzos2.close();
                        } catch (Exception e4) {
                        }
                        try {
                            b64os.close();
                        } catch (Exception e5) {
                        }
                        try {
                            baos.close();
                        } catch (Exception e6) {
                        }
                        throw th;
                    }
                    try {
                        oos = new ObjectOutputStream(gzos);
                        gzos2 = gzos;
                    } catch (IOException e7) {
                        e = e7;
                        gzos2 = gzos;
                        b64os = b64os2;
                        baos = baos2;
                        throw e;
                    } catch (Throwable th4) {
                        th = th4;
                        gzos2 = gzos;
                        b64os = b64os2;
                        baos = baos2;
                        oos.close();
                        gzos2.close();
                        b64os.close();
                        baos.close();
                        throw th;
                    }
                }
                oos = new ObjectOutputStream(b64os2);
                oos.writeObject(serializableObject);
                try {
                    oos.close();
                } catch (Exception e8) {
                }
                try {
                    gzos2.close();
                } catch (Exception e9) {
                }
                try {
                    b64os2.close();
                } catch (Exception e10) {
                }
                try {
                    baos2.close();
                } catch (Exception e11) {
                }
                try {
                    return new String(baos2.toByteArray(), PREFERRED_ENCODING);
                } catch (UnsupportedEncodingException e12) {
                    return new String(baos2.toByteArray());
                }
            } catch (IOException e13) {
                e = e13;
                baos = baos2;
                throw e;
            } catch (Throwable th5) {
                th = th5;
                baos = baos2;
                oos.close();
                gzos2.close();
                b64os.close();
                baos.close();
                throw th;
            }
        } catch (IOException e14) {
            e = e14;
            throw e;
        }
    }

    public static String encodeBytes(byte[] source) {
        String encoded = null;
        try {
            encoded = encodeBytes(source, NO_OPTIONS, source.length, NO_OPTIONS);
        } catch (IOException ex) {
            if (!$assertionsDisabled) {
                throw new AssertionError(ex.getMessage());
            }
        }
        if ($assertionsDisabled || encoded != null) {
            return encoded;
        }
        throw new AssertionError();
    }

    public static String encodeBytes(byte[] source, int options) throws IOException {
        return encodeBytes(source, NO_OPTIONS, source.length, options);
    }

    public static String encodeBytes(byte[] source, int off, int len) {
        String encoded = null;
        try {
            encoded = encodeBytes(source, off, len, NO_OPTIONS);
        } catch (IOException ex) {
            if (!$assertionsDisabled) {
                throw new AssertionError(ex.getMessage());
            }
        }
        if ($assertionsDisabled || encoded != null) {
            return encoded;
        }
        throw new AssertionError();
    }

    public static String encodeBytes(byte[] source, int off, int len, int options) throws IOException {
        byte[] encoded = encodeBytesToBytes(source, off, len, options);
        try {
            return new String(encoded, PREFERRED_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return new String(encoded);
        }
    }

    public static byte[] encodeBytesToBytes(byte[] source) {
        byte[] encoded = null;
        try {
            encoded = encodeBytesToBytes(source, NO_OPTIONS, source.length, NO_OPTIONS);
        } catch (IOException ex) {
            if (!$assertionsDisabled) {
                throw new AssertionError("IOExceptions only come from GZipping, which is turned off: " + ex.getMessage());
            }
        }
        return encoded;
    }

    public static byte[] encodeBytesToBytes(byte[] source, int off, int len, int options) throws IOException {
        IOException e;
        Throwable th;
        if (source == null) {
            throw new NullPointerException("Cannot serialize a null array.");
        } else if (off < 0) {
            throw new IllegalArgumentException("Cannot have negative offset: " + off);
        } else if (len < 0) {
            throw new IllegalArgumentException("Cannot have length offset: " + len);
        } else if (off + len > source.length) {
            Object[] objArr = new Object[3];
            objArr[NO_OPTIONS] = Integer.valueOf(off);
            objArr[ENCODE] = Integer.valueOf(len);
            objArr[GZIP] = Integer.valueOf(source.length);
            throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", objArr));
        } else if ((options & GZIP) != 0) {
            ByteArrayOutputStream baos = null;
            GZIPOutputStream gzos = null;
            OutputStream b64os = null;
            try {
                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                try {
                    OutputStream b64os2 = new OutputStream(baos2, options | ENCODE);
                    try {
                        GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(b64os2);
                        try {
                            gZIPOutputStream.write(source, off, len);
                            gZIPOutputStream.close();
                            try {
                                gZIPOutputStream.close();
                            } catch (Exception e2) {
                            }
                            try {
                                b64os2.close();
                            } catch (Exception e3) {
                            }
                            try {
                                baos2.close();
                            } catch (Exception e4) {
                            }
                            return baos2.toByteArray();
                        } catch (IOException e5) {
                            e = e5;
                            b64os = b64os2;
                            gzos = gZIPOutputStream;
                            baos = baos2;
                            try {
                                throw e;
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            b64os = b64os2;
                            gzos = gZIPOutputStream;
                            baos = baos2;
                            try {
                                gzos.close();
                            } catch (Exception e6) {
                            }
                            try {
                                b64os.close();
                            } catch (Exception e7) {
                            }
                            try {
                                baos.close();
                            } catch (Exception e8) {
                            }
                            throw th;
                        }
                    } catch (IOException e9) {
                        e = e9;
                        b64os = b64os2;
                        baos = baos2;
                        throw e;
                    } catch (Throwable th4) {
                        th = th4;
                        b64os = b64os2;
                        baos = baos2;
                        gzos.close();
                        b64os.close();
                        baos.close();
                        throw th;
                    }
                } catch (IOException e10) {
                    e = e10;
                    baos = baos2;
                    throw e;
                } catch (Throwable th5) {
                    th = th5;
                    baos = baos2;
                    gzos.close();
                    b64os.close();
                    baos.close();
                    throw th;
                }
            } catch (IOException e11) {
                e = e11;
                throw e;
            }
        } else {
            boolean breakLines = (options & DO_BREAK_LINES) != 0 ? true : $assertionsDisabled;
            int encLen = ((len / 3) * DONT_GUNZIP) + (len % 3 > 0 ? DONT_GUNZIP : NO_OPTIONS);
            if (breakLines) {
                encLen += encLen / MAX_LINE_LENGTH;
            }
            byte[] outBuff = new byte[encLen];
            int d = NO_OPTIONS;
            int e12 = NO_OPTIONS;
            int len2 = len - 2;
            int lineLength = NO_OPTIONS;
            while (d < len2) {
                encode3to4(source, d + off, 3, outBuff, e12, options);
                lineLength += DONT_GUNZIP;
                if (breakLines && lineLength >= MAX_LINE_LENGTH) {
                    outBuff[e12 + DONT_GUNZIP] = NEW_LINE;
                    e12 += ENCODE;
                    lineLength = NO_OPTIONS;
                }
                d += 3;
                e12 += DONT_GUNZIP;
            }
            if (d < len) {
                encode3to4(source, d + off, len - d, outBuff, e12, options);
                e12 += DONT_GUNZIP;
            }
            if (e12 > outBuff.length - 1) {
                return outBuff;
            }
            Object finalOut = new byte[e12];
            System.arraycopy(outBuff, NO_OPTIONS, finalOut, NO_OPTIONS, e12);
            return finalOut;
        }
    }

    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, int options) {
        if (source == null) {
            throw new NullPointerException("Source array was null.");
        } else if (destination == null) {
            throw new NullPointerException("Destination array was null.");
        } else if (srcOffset < 0 || srcOffset + 3 >= source.length) {
            r3 = new Object[GZIP];
            r3[NO_OPTIONS] = Integer.valueOf(source.length);
            r3[ENCODE] = Integer.valueOf(srcOffset);
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and still process four bytes.", r3));
        } else if (destOffset < 0 || destOffset + GZIP >= destination.length) {
            r3 = new Object[GZIP];
            r3[NO_OPTIONS] = Integer.valueOf(destination.length);
            r3[ENCODE] = Integer.valueOf(destOffset);
            throw new IllegalArgumentException(String.format("Destination array with length %d cannot have offset of %d and still store three bytes.", r3));
        } else {
            byte[] DECODABET = getDecodabet(options);
            if (source[srcOffset + GZIP] == EQUALS_SIGN) {
                destination[destOffset] = (byte) ((((DECODABET[source[srcOffset]] & MotionEventCompat.ACTION_MASK) << 18) | ((DECODABET[source[srcOffset + ENCODE]] & MotionEventCompat.ACTION_MASK) << 12)) >>> URL_SAFE);
                return ENCODE;
            } else if (source[srcOffset + 3] == EQUALS_SIGN) {
                outBuff = (((DECODABET[source[srcOffset]] & MotionEventCompat.ACTION_MASK) << 18) | ((DECODABET[source[srcOffset + ENCODE]] & MotionEventCompat.ACTION_MASK) << 12)) | ((DECODABET[source[srcOffset + GZIP]] & MotionEventCompat.ACTION_MASK) << 6);
                destination[destOffset] = (byte) (outBuff >>> URL_SAFE);
                destination[destOffset + ENCODE] = (byte) (outBuff >>> DO_BREAK_LINES);
                return GZIP;
            } else {
                outBuff = ((((DECODABET[source[srcOffset]] & MotionEventCompat.ACTION_MASK) << 18) | ((DECODABET[source[srcOffset + ENCODE]] & MotionEventCompat.ACTION_MASK) << 12)) | ((DECODABET[source[srcOffset + GZIP]] & MotionEventCompat.ACTION_MASK) << 6)) | (DECODABET[source[srcOffset + 3]] & MotionEventCompat.ACTION_MASK);
                destination[destOffset] = (byte) (outBuff >> URL_SAFE);
                destination[destOffset + ENCODE] = (byte) (outBuff >> DO_BREAK_LINES);
                destination[destOffset + GZIP] = (byte) outBuff;
                return 3;
            }
        }
    }

    public static byte[] decode(byte[] source) throws IOException {
        return decode(source, NO_OPTIONS, source.length, NO_OPTIONS);
    }

    public static byte[] decode(byte[] source, int off, int len, int options) throws IOException {
        if (source == null) {
            throw new NullPointerException("Cannot decode null source array.");
        } else if (off < 0 || off + len > source.length) {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and process %d bytes.", new Object[]{Integer.valueOf(source.length), Integer.valueOf(off), Integer.valueOf(len)}));
        } else if (len == 0) {
            return new byte[NO_OPTIONS];
        } else {
            if (len < DONT_GUNZIP) {
                throw new IllegalArgumentException("Base64-encoded string must have at least four characters, but length specified was " + len);
            }
            int b4Posn;
            byte[] DECODABET = getDecodabet(options);
            byte[] outBuff = new byte[((len * 3) / DONT_GUNZIP)];
            int outBuffPosn = NO_OPTIONS;
            byte[] b4 = new byte[DONT_GUNZIP];
            int i = off;
            int b4Posn2 = NO_OPTIONS;
            while (i < off + len) {
                byte sbiDecode = DECODABET[source[i] & MotionEventCompat.ACTION_MASK];
                if (sbiDecode >= -5) {
                    if (sbiDecode >= -1) {
                        b4Posn = b4Posn2 + ENCODE;
                        b4[b4Posn2] = source[i];
                        if (b4Posn > 3) {
                            outBuffPosn += decode4to3(b4, NO_OPTIONS, outBuff, outBuffPosn, options);
                            b4Posn = NO_OPTIONS;
                            if (source[i] == 61) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        b4Posn = b4Posn2;
                    }
                    i += ENCODE;
                    b4Posn2 = b4Posn;
                } else {
                    Object[] objArr = new Object[GZIP];
                    objArr[NO_OPTIONS] = Integer.valueOf(source[i] & MotionEventCompat.ACTION_MASK);
                    objArr[ENCODE] = Integer.valueOf(i);
                    throw new IOException(String.format("Bad Base64 input character decimal %d in array position %d", objArr));
                }
            }
            b4Posn = b4Posn2;
            byte[] out = new byte[outBuffPosn];
            System.arraycopy(outBuff, NO_OPTIONS, out, NO_OPTIONS, outBuffPosn);
            return out;
        }
    }

    public static byte[] decode(String s) throws IOException {
        return decode(s, NO_OPTIONS);
    }

    public static byte[] decode(String s, int options) throws IOException {
        byte[] bytes;
        Throwable th;
        if (s == null) {
            throw new NullPointerException("Input string was null.");
        }
        try {
            bytes = s.getBytes(PREFERRED_ENCODING);
        } catch (UnsupportedEncodingException e) {
            bytes = s.getBytes();
        }
        bytes = decode(bytes, NO_OPTIONS, bytes.length, options);
        boolean dontGunzip = (options & DONT_GUNZIP) != 0 ? true : $assertionsDisabled;
        if (bytes != null && bytes.length >= DONT_GUNZIP && !dontGunzip && 35615 == ((bytes[NO_OPTIONS] & MotionEventCompat.ACTION_MASK) | ((bytes[ENCODE] << DO_BREAK_LINES) & MotionEventCompat.ACTION_POINTER_INDEX_MASK))) {
            ByteArrayInputStream bais = null;
            GZIPInputStream gzis = null;
            ByteArrayOutputStream baos = null;
            byte[] buffer = new byte[ItemAnimator.FLAG_MOVED];
            try {
                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                try {
                    ByteArrayInputStream bais2 = new ByteArrayInputStream(bytes);
                    try {
                        GZIPInputStream gzis2 = new GZIPInputStream(bais2);
                        while (true) {
                            try {
                                int length = gzis2.read(buffer);
                                if (length < 0) {
                                    break;
                                }
                                baos2.write(buffer, NO_OPTIONS, length);
                            } catch (IOException e2) {
                                baos = baos2;
                                gzis = gzis2;
                                bais = bais2;
                            } catch (Throwable th2) {
                                th = th2;
                                baos = baos2;
                                gzis = gzis2;
                                bais = bais2;
                            }
                        }
                        bytes = baos2.toByteArray();
                        try {
                            baos2.close();
                        } catch (Exception e3) {
                        }
                        try {
                            gzis2.close();
                        } catch (Exception e4) {
                        }
                        try {
                            bais2.close();
                        } catch (Exception e5) {
                        }
                    } catch (IOException e6) {
                        baos = baos2;
                        bais = bais2;
                        try {
                            baos.close();
                        } catch (Exception e7) {
                        }
                        try {
                            gzis.close();
                        } catch (Exception e8) {
                        }
                        try {
                            bais.close();
                        } catch (Exception e9) {
                        }
                        return bytes;
                    } catch (Throwable th3) {
                        th = th3;
                        baos = baos2;
                        bais = bais2;
                        try {
                            baos.close();
                        } catch (Exception e10) {
                        }
                        try {
                            gzis.close();
                        } catch (Exception e11) {
                        }
                        try {
                            bais.close();
                        } catch (Exception e12) {
                        }
                        throw th;
                    }
                } catch (IOException e13) {
                    baos = baos2;
                    baos.close();
                    gzis.close();
                    bais.close();
                    return bytes;
                } catch (Throwable th4) {
                    th = th4;
                    baos = baos2;
                    baos.close();
                    gzis.close();
                    bais.close();
                    throw th;
                }
            } catch (IOException e14) {
                baos.close();
                gzis.close();
                bais.close();
                return bytes;
            } catch (Throwable th5) {
                th = th5;
                baos.close();
                gzis.close();
                bais.close();
                throw th;
            }
        }
        return bytes;
    }

    public static Object decodeToObject(String encodedObject) throws IOException, ClassNotFoundException {
        return decodeToObject(encodedObject, NO_OPTIONS, null);
    }

    public static Object decodeToObject(String encodedObject, int options, ClassLoader loader) throws IOException, ClassNotFoundException {
        IOException e;
        Throwable th;
        ClassNotFoundException e2;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(decode(encodedObject, options));
            if (loader == null) {
                try {
                    ois = new ObjectInputStream(bais);
                } catch (IOException e3) {
                    e = e3;
                    byteArrayInputStream = bais;
                    try {
                        throw e;
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (ClassNotFoundException e4) {
                    e2 = e4;
                    byteArrayInputStream = bais;
                    throw e2;
                } catch (Throwable th3) {
                    th = th3;
                    byteArrayInputStream = bais;
                    try {
                        byteArrayInputStream.close();
                    } catch (Exception e5) {
                    }
                    try {
                        ois.close();
                    } catch (Exception e6) {
                    }
                    throw th;
                }
            }
            ois = new C05851(bais, loader);
            Object obj = ois.readObject();
            try {
                bais.close();
            } catch (Exception e7) {
            }
            try {
                ois.close();
            } catch (Exception e8) {
            }
            return obj;
        } catch (IOException e9) {
            e = e9;
            throw e;
        } catch (ClassNotFoundException e10) {
            e2 = e10;
            throw e2;
        }
    }

    public static void encodeToFile(byte[] dataToEncode, String filename) throws IOException {
        IOException e;
        Throwable th;
        if (dataToEncode == null) {
            throw new NullPointerException("Data to encode was null.");
        }
        OutputStream bos = null;
        try {
            OutputStream bos2 = new OutputStream(new FileOutputStream(filename), ENCODE);
            try {
                bos2.write(dataToEncode);
                try {
                    bos2.close();
                } catch (Exception e2) {
                }
            } catch (IOException e3) {
                e = e3;
                bos = bos2;
                try {
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                th = th3;
                bos = bos2;
                try {
                    bos.close();
                } catch (Exception e4) {
                }
                throw th;
            }
        } catch (IOException e5) {
            e = e5;
            throw e;
        }
    }

    public static void decodeToFile(String dataToDecode, String filename) throws IOException {
        IOException e;
        Throwable th;
        OutputStream bos = null;
        try {
            OutputStream bos2 = new OutputStream(new FileOutputStream(filename), NO_OPTIONS);
            try {
                bos2.write(dataToDecode.getBytes(PREFERRED_ENCODING));
                try {
                    bos2.close();
                } catch (Exception e2) {
                }
            } catch (IOException e3) {
                e = e3;
                bos = bos2;
                try {
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                th = th3;
                bos = bos2;
                try {
                    bos.close();
                } catch (Exception e4) {
                }
                throw th;
            }
        } catch (IOException e5) {
            e = e5;
            throw e;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] decodeFromFile(java.lang.String r12) throws java.io.IOException {
        /*
        r3 = 0;
        r0 = 0;
        r5 = new java.io.File;	 Catch:{ IOException -> 0x0038 }
        r5.<init>(r12);	 Catch:{ IOException -> 0x0038 }
        r2 = 0;
        r6 = 0;
        r7 = 0;
        r8 = r5.length();	 Catch:{ IOException -> 0x0038 }
        r10 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r8 <= 0) goto L_0x003f;
    L_0x0015:
        r8 = new java.io.IOException;	 Catch:{ IOException -> 0x0038 }
        r9 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0038 }
        r9.<init>();	 Catch:{ IOException -> 0x0038 }
        r10 = "File is too big for this convenience method (";
        r9 = r9.append(r10);	 Catch:{ IOException -> 0x0038 }
        r10 = r5.length();	 Catch:{ IOException -> 0x0038 }
        r9 = r9.append(r10);	 Catch:{ IOException -> 0x0038 }
        r10 = " bytes).";
        r9 = r9.append(r10);	 Catch:{ IOException -> 0x0038 }
        r9 = r9.toString();	 Catch:{ IOException -> 0x0038 }
        r8.<init>(r9);	 Catch:{ IOException -> 0x0038 }
        throw r8;	 Catch:{ IOException -> 0x0038 }
    L_0x0038:
        r4 = move-exception;
    L_0x0039:
        throw r4;	 Catch:{ all -> 0x003a }
    L_0x003a:
        r8 = move-exception;
    L_0x003b:
        r0.close();	 Catch:{ Exception -> 0x006d }
    L_0x003e:
        throw r8;
    L_0x003f:
        r8 = r5.length();	 Catch:{ IOException -> 0x0038 }
        r8 = (int) r8;	 Catch:{ IOException -> 0x0038 }
        r2 = new byte[r8];	 Catch:{ IOException -> 0x0038 }
        r1 = new com.firebase.client.utilities.Base64$InputStream;	 Catch:{ IOException -> 0x0038 }
        r8 = new java.io.BufferedInputStream;	 Catch:{ IOException -> 0x0038 }
        r9 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0038 }
        r9.<init>(r5);	 Catch:{ IOException -> 0x0038 }
        r8.<init>(r9);	 Catch:{ IOException -> 0x0038 }
        r9 = 0;
        r1.<init>(r8, r9);	 Catch:{ IOException -> 0x0038 }
    L_0x0056:
        r8 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r7 = r1.read(r2, r6, r8);	 Catch:{ IOException -> 0x0072, all -> 0x006f }
        if (r7 < 0) goto L_0x0060;
    L_0x005e:
        r6 = r6 + r7;
        goto L_0x0056;
    L_0x0060:
        r3 = new byte[r6];	 Catch:{ IOException -> 0x0072, all -> 0x006f }
        r8 = 0;
        r9 = 0;
        java.lang.System.arraycopy(r2, r8, r3, r9, r6);	 Catch:{ IOException -> 0x0072, all -> 0x006f }
        r1.close();	 Catch:{ Exception -> 0x006b }
    L_0x006a:
        return r3;
    L_0x006b:
        r8 = move-exception;
        goto L_0x006a;
    L_0x006d:
        r9 = move-exception;
        goto L_0x003e;
    L_0x006f:
        r8 = move-exception;
        r0 = r1;
        goto L_0x003b;
    L_0x0072:
        r4 = move-exception;
        r0 = r1;
        goto L_0x0039;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.firebase.client.utilities.Base64.decodeFromFile(java.lang.String):byte[]");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String encodeFromFile(java.lang.String r12) throws java.io.IOException {
        /*
        r4 = 0;
        r0 = 0;
        r5 = new java.io.File;	 Catch:{ IOException -> 0x0046 }
        r5.<init>(r12);	 Catch:{ IOException -> 0x0046 }
        r8 = r5.length();	 Catch:{ IOException -> 0x0046 }
        r8 = (double) r8;	 Catch:{ IOException -> 0x0046 }
        r10 = 4608983858650965606; // 0x3ff6666666666666 float:2.720083E23 double:1.4;
        r8 = r8 * r10;
        r10 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r8 = r8 + r10;
        r8 = (int) r8;	 Catch:{ IOException -> 0x0046 }
        r9 = 40;
        r8 = java.lang.Math.max(r8, r9);	 Catch:{ IOException -> 0x0046 }
        r2 = new byte[r8];	 Catch:{ IOException -> 0x0046 }
        r6 = 0;
        r7 = 0;
        r1 = new com.firebase.client.utilities.Base64$InputStream;	 Catch:{ IOException -> 0x0046 }
        r8 = new java.io.BufferedInputStream;	 Catch:{ IOException -> 0x0046 }
        r9 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0046 }
        r9.<init>(r5);	 Catch:{ IOException -> 0x0046 }
        r8.<init>(r9);	 Catch:{ IOException -> 0x0046 }
        r9 = 1;
        r1.<init>(r8, r9);	 Catch:{ IOException -> 0x0046 }
    L_0x0030:
        r8 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r7 = r1.read(r2, r6, r8);	 Catch:{ IOException -> 0x0054, all -> 0x0051 }
        if (r7 < 0) goto L_0x003a;
    L_0x0038:
        r6 = r6 + r7;
        goto L_0x0030;
    L_0x003a:
        r4 = new java.lang.String;	 Catch:{ IOException -> 0x0054, all -> 0x0051 }
        r8 = 0;
        r9 = "US-ASCII";
        r4.<init>(r2, r8, r6, r9);	 Catch:{ IOException -> 0x0054, all -> 0x0051 }
        r1.close();	 Catch:{ Exception -> 0x004d }
    L_0x0045:
        return r4;
    L_0x0046:
        r3 = move-exception;
    L_0x0047:
        throw r3;	 Catch:{ all -> 0x0048 }
    L_0x0048:
        r8 = move-exception;
    L_0x0049:
        r0.close();	 Catch:{ Exception -> 0x004f }
    L_0x004c:
        throw r8;
    L_0x004d:
        r8 = move-exception;
        goto L_0x0045;
    L_0x004f:
        r9 = move-exception;
        goto L_0x004c;
    L_0x0051:
        r8 = move-exception;
        r0 = r1;
        goto L_0x0049;
    L_0x0054:
        r3 = move-exception;
        r0 = r1;
        goto L_0x0047;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.firebase.client.utilities.Base64.encodeFromFile(java.lang.String):java.lang.String");
    }

    public static void encodeFileToFile(String infile, String outfile) throws IOException {
        IOException e;
        Throwable th;
        String encoded = encodeFromFile(infile);
        java.io.OutputStream out = null;
        try {
            java.io.OutputStream out2 = new BufferedOutputStream(new FileOutputStream(outfile));
            try {
                out2.write(encoded.getBytes(PREFERRED_ENCODING));
                try {
                    out2.close();
                } catch (Exception e2) {
                }
            } catch (IOException e3) {
                e = e3;
                out = out2;
                try {
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                th = th3;
                out = out2;
                try {
                    out.close();
                } catch (Exception e4) {
                }
                throw th;
            }
        } catch (IOException e5) {
            e = e5;
            throw e;
        }
    }

    public static void decodeFileToFile(String infile, String outfile) throws IOException {
        IOException e;
        Throwable th;
        byte[] decoded = decodeFromFile(infile);
        java.io.OutputStream out = null;
        try {
            java.io.OutputStream out2 = new BufferedOutputStream(new FileOutputStream(outfile));
            try {
                out2.write(decoded);
                try {
                    out2.close();
                } catch (Exception e2) {
                }
            } catch (IOException e3) {
                e = e3;
                out = out2;
                try {
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                th = th3;
                out = out2;
                try {
                    out.close();
                } catch (Exception e4) {
                }
                throw th;
            }
        } catch (IOException e5) {
            e = e5;
            throw e;
        }
    }
}
