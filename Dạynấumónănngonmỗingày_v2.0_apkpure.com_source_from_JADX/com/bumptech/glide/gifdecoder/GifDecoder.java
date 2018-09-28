package com.bumptech.glide.gifdecoder;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class GifDecoder {
    private static final Config BITMAP_CONFIG;
    private static final int DISPOSAL_BACKGROUND = 2;
    private static final int DISPOSAL_NONE = 1;
    private static final int DISPOSAL_PREVIOUS = 3;
    private static final int DISPOSAL_UNSPECIFIED = 0;
    private static final int INITIAL_FRAME_POINTER = -1;
    private static final int MAX_STACK_SIZE = 4096;
    private static final int NULL_CODE = -1;
    public static final int STATUS_FORMAT_ERROR = 1;
    public static final int STATUS_OK = 0;
    public static final int STATUS_OPEN_ERROR = 2;
    public static final int STATUS_PARTIAL_DECODE = 3;
    private static final String TAG;
    private int[] act;
    private BitmapProvider bitmapProvider;
    private final byte[] block;
    private byte[] data;
    private int framePointer;
    private GifHeader header;
    private byte[] mainPixels;
    private int[] mainScratch;
    private GifHeaderParser parser;
    private byte[] pixelStack;
    private short[] prefix;
    private Bitmap previousImage;
    private ByteBuffer rawData;
    private boolean savePrevious;
    private int status;
    private byte[] suffix;

    public interface BitmapProvider {
        Bitmap obtain(int i, int i2, Config config);

        void release(Bitmap bitmap);
    }

    static {
        TAG = GifDecoder.class.getSimpleName();
        BITMAP_CONFIG = Config.ARGB_8888;
    }

    public GifDecoder(BitmapProvider provider) {
        this.block = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY];
        this.bitmapProvider = provider;
        this.header = new GifHeader();
    }

    public int getWidth() {
        return this.header.width;
    }

    public int getHeight() {
        return this.header.height;
    }

    public byte[] getData() {
        return this.data;
    }

    public int getStatus() {
        return this.status;
    }

    public void advance() {
        this.framePointer = (this.framePointer + STATUS_FORMAT_ERROR) % this.header.frameCount;
    }

    public int getDelay(int n) {
        if (n < 0 || n >= this.header.frameCount) {
            return NULL_CODE;
        }
        return ((GifFrame) this.header.frames.get(n)).delay;
    }

    public int getNextDelay() {
        if (this.header.frameCount <= 0 || this.framePointer < 0) {
            return NULL_CODE;
        }
        return getDelay(this.framePointer);
    }

    public int getFrameCount() {
        return this.header.frameCount;
    }

    public int getCurrentFrameIndex() {
        return this.framePointer;
    }

    public void resetFrameIndex() {
        this.framePointer = NULL_CODE;
    }

    public int getLoopCount() {
        return this.header.loopCount;
    }

    public synchronized Bitmap getNextFrame() {
        Bitmap bitmap = null;
        synchronized (this) {
            if (this.header.frameCount <= 0 || this.framePointer < 0) {
                if (Log.isLoggable(TAG, STATUS_PARTIAL_DECODE)) {
                    Log.d(TAG, "unable to decode frame, frameCount=" + this.header.frameCount + " framePointer=" + this.framePointer);
                }
                this.status = STATUS_FORMAT_ERROR;
            }
            if (this.status != STATUS_FORMAT_ERROR && this.status != STATUS_OPEN_ERROR) {
                this.status = STATUS_OK;
                GifFrame currentFrame = (GifFrame) this.header.frames.get(this.framePointer);
                GifFrame previousFrame = null;
                int previousIndex = this.framePointer + NULL_CODE;
                if (previousIndex >= 0) {
                    previousFrame = (GifFrame) this.header.frames.get(previousIndex);
                }
                if (currentFrame.lct == null) {
                    this.act = this.header.gct;
                } else {
                    this.act = currentFrame.lct;
                    if (this.header.bgIndex == currentFrame.transIndex) {
                        this.header.bgColor = STATUS_OK;
                    }
                }
                int save = STATUS_OK;
                if (currentFrame.transparency) {
                    save = this.act[currentFrame.transIndex];
                    this.act[currentFrame.transIndex] = STATUS_OK;
                }
                if (this.act == null) {
                    if (Log.isLoggable(TAG, STATUS_PARTIAL_DECODE)) {
                        Log.d(TAG, "No Valid Color Table");
                    }
                    this.status = STATUS_FORMAT_ERROR;
                } else {
                    bitmap = setPixels(currentFrame, previousFrame);
                    if (currentFrame.transparency) {
                        this.act[currentFrame.transIndex] = save;
                    }
                }
            } else if (Log.isLoggable(TAG, STATUS_PARTIAL_DECODE)) {
                Log.d(TAG, "Unable to decode frame, status=" + this.status);
            }
        }
        return bitmap;
    }

    public int read(InputStream is, int contentLength) {
        int capacity = AccessibilityNodeInfoCompat.ACTION_COPY;
        if (is != null) {
            if (contentLength > 0) {
                capacity = contentLength + MAX_STACK_SIZE;
            }
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream(capacity);
                byte[] data = new byte[AccessibilityNodeInfoCompat.ACTION_COPY];
                while (true) {
                    int nRead = is.read(data, STATUS_OK, data.length);
                    if (nRead == NULL_CODE) {
                        break;
                    }
                    buffer.write(data, STATUS_OK, nRead);
                }
                buffer.flush();
                read(buffer.toByteArray());
            } catch (IOException e) {
                Log.w(TAG, "Error reading data from stream", e);
            }
        } else {
            this.status = STATUS_OPEN_ERROR;
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e2) {
                Log.w(TAG, "Error closing stream", e2);
            }
        }
        return this.status;
    }

    public void clear() {
        this.header = null;
        this.data = null;
        this.mainPixels = null;
        this.mainScratch = null;
        if (this.previousImage != null) {
            this.bitmapProvider.release(this.previousImage);
        }
        this.previousImage = null;
        this.rawData = null;
    }

    public void setData(GifHeader header, byte[] data) {
        this.header = header;
        this.data = data;
        this.status = STATUS_OK;
        this.framePointer = NULL_CODE;
        this.rawData = ByteBuffer.wrap(data);
        this.rawData.rewind();
        this.rawData.order(ByteOrder.LITTLE_ENDIAN);
        this.savePrevious = false;
        for (GifFrame frame : header.frames) {
            if (frame.dispose == STATUS_PARTIAL_DECODE) {
                this.savePrevious = true;
                break;
            }
        }
        this.mainPixels = new byte[(header.width * header.height)];
        this.mainScratch = new int[(header.width * header.height)];
    }

    private GifHeaderParser getHeaderParser() {
        if (this.parser == null) {
            this.parser = new GifHeaderParser();
        }
        return this.parser;
    }

    public int read(byte[] data) {
        this.data = data;
        this.header = getHeaderParser().setData(data).parseHeader();
        if (data != null) {
            this.rawData = ByteBuffer.wrap(data);
            this.rawData.rewind();
            this.rawData.order(ByteOrder.LITTLE_ENDIAN);
            this.mainPixels = new byte[(this.header.width * this.header.height)];
            this.mainScratch = new int[(this.header.width * this.header.height)];
            this.savePrevious = false;
            for (GifFrame frame : this.header.frames) {
                if (frame.dispose == STATUS_PARTIAL_DECODE) {
                    this.savePrevious = true;
                    break;
                }
            }
        }
        return this.status;
    }

    private Bitmap setPixels(GifFrame currentFrame, GifFrame previousFrame) {
        int c;
        int width = this.header.width;
        int height = this.header.height;
        int[] dest = this.mainScratch;
        if (previousFrame != null && previousFrame.dispose > 0) {
            if (previousFrame.dispose == STATUS_OPEN_ERROR) {
                c = STATUS_OK;
                if (!currentFrame.transparency) {
                    c = this.header.bgColor;
                }
                Arrays.fill(dest, c);
            } else if (previousFrame.dispose == STATUS_PARTIAL_DECODE && this.previousImage != null) {
                this.previousImage.getPixels(dest, STATUS_OK, width, STATUS_OK, STATUS_OK, width, height);
            }
        }
        decodeBitmapData(currentFrame);
        int pass = STATUS_FORMAT_ERROR;
        int inc = 8;
        int iline = STATUS_OK;
        for (int i = STATUS_OK; i < currentFrame.ih; i += STATUS_FORMAT_ERROR) {
            int line = i;
            if (currentFrame.interlace) {
                if (iline >= currentFrame.ih) {
                    pass += STATUS_FORMAT_ERROR;
                    switch (pass) {
                        case STATUS_OPEN_ERROR /*2*/:
                            iline = 4;
                            break;
                        case STATUS_PARTIAL_DECODE /*3*/:
                            iline = STATUS_OPEN_ERROR;
                            inc = 4;
                            break;
                        case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                            iline = STATUS_FORMAT_ERROR;
                            inc = STATUS_OPEN_ERROR;
                            break;
                    }
                }
                line = iline;
                iline += inc;
            }
            line += currentFrame.iy;
            if (line < this.header.height) {
                int k = line * this.header.width;
                int dx = k + currentFrame.ix;
                int dlim = dx + currentFrame.iw;
                if (this.header.width + k < dlim) {
                    dlim = k + this.header.width;
                }
                int sx = i * currentFrame.iw;
                while (dx < dlim) {
                    int sx2 = sx + STATUS_FORMAT_ERROR;
                    c = this.act[this.mainPixels[sx] & MotionEventCompat.ACTION_MASK];
                    if (c != 0) {
                        dest[dx] = c;
                    }
                    dx += STATUS_FORMAT_ERROR;
                    sx = sx2;
                }
            }
        }
        if (this.savePrevious && (currentFrame.dispose == 0 || currentFrame.dispose == STATUS_FORMAT_ERROR)) {
            if (this.previousImage == null) {
                this.previousImage = getNextBitmap();
            }
            this.previousImage.setPixels(dest, STATUS_OK, width, STATUS_OK, STATUS_OK, width, height);
        }
        Bitmap result = getNextBitmap();
        result.setPixels(dest, STATUS_OK, width, STATUS_OK, STATUS_OK, width, height);
        return result;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void decodeBitmapData(com.bumptech.glide.gifdecoder.GifFrame r25) {
        /*
        r24 = this;
        if (r25 == 0) goto L_0x0011;
    L_0x0002:
        r0 = r24;
        r0 = r0.rawData;
        r22 = r0;
        r0 = r25;
        r0 = r0.bufferFrameStart;
        r23 = r0;
        r22.position(r23);
    L_0x0011:
        if (r25 != 0) goto L_0x00c5;
    L_0x0013:
        r0 = r24;
        r0 = r0.header;
        r22 = r0;
        r0 = r22;
        r0 = r0.width;
        r22 = r0;
        r0 = r24;
        r0 = r0.header;
        r23 = r0;
        r0 = r23;
        r0 = r0.height;
        r23 = r0;
        r16 = r22 * r23;
    L_0x002d:
        r0 = r24;
        r0 = r0.mainPixels;
        r22 = r0;
        if (r22 == 0) goto L_0x0046;
    L_0x0035:
        r0 = r24;
        r0 = r0.mainPixels;
        r22 = r0;
        r0 = r22;
        r0 = r0.length;
        r22 = r0;
        r0 = r22;
        r1 = r16;
        if (r0 >= r1) goto L_0x0052;
    L_0x0046:
        r0 = r16;
        r0 = new byte[r0];
        r22 = r0;
        r0 = r22;
        r1 = r24;
        r1.mainPixels = r0;
    L_0x0052:
        r0 = r24;
        r0 = r0.prefix;
        r22 = r0;
        if (r22 != 0) goto L_0x0068;
    L_0x005a:
        r22 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = r22;
        r0 = new short[r0];
        r22 = r0;
        r0 = r22;
        r1 = r24;
        r1.prefix = r0;
    L_0x0068:
        r0 = r24;
        r0 = r0.suffix;
        r22 = r0;
        if (r22 != 0) goto L_0x007e;
    L_0x0070:
        r22 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = r22;
        r0 = new byte[r0];
        r22 = r0;
        r0 = r22;
        r1 = r24;
        r1.suffix = r0;
    L_0x007e:
        r0 = r24;
        r0 = r0.pixelStack;
        r22 = r0;
        if (r22 != 0) goto L_0x0094;
    L_0x0086:
        r22 = 4097; // 0x1001 float:5.741E-42 double:2.024E-320;
        r0 = r22;
        r0 = new byte[r0];
        r22 = r0;
        r0 = r22;
        r1 = r24;
        r1.pixelStack = r0;
    L_0x0094:
        r10 = r24.read();
        r22 = 1;
        r5 = r22 << r10;
        r12 = r5 + 1;
        r2 = r5 + 2;
        r17 = -1;
        r8 = r10 + 1;
        r22 = 1;
        r22 = r22 << r8;
        r7 = r22 + -1;
        r6 = 0;
    L_0x00ab:
        if (r6 >= r5) goto L_0x00d5;
    L_0x00ad:
        r0 = r24;
        r0 = r0.prefix;
        r22 = r0;
        r23 = 0;
        r22[r6] = r23;
        r0 = r24;
        r0 = r0.suffix;
        r22 = r0;
        r0 = (byte) r6;
        r23 = r0;
        r22[r6] = r23;
        r6 = r6 + 1;
        goto L_0x00ab;
    L_0x00c5:
        r0 = r25;
        r0 = r0.iw;
        r22 = r0;
        r0 = r25;
        r0 = r0.ih;
        r23 = r0;
        r16 = r22 * r23;
        goto L_0x002d;
    L_0x00d5:
        r3 = 0;
        r18 = r3;
        r20 = r3;
        r13 = r3;
        r9 = r3;
        r4 = r3;
        r11 = r3;
        r14 = 0;
    L_0x00df:
        r0 = r16;
        if (r14 >= r0) goto L_0x00f3;
    L_0x00e3:
        if (r9 != 0) goto L_0x0107;
    L_0x00e5:
        r9 = r24.readBlock();
        if (r9 > 0) goto L_0x0106;
    L_0x00eb:
        r22 = 3;
        r0 = r22;
        r1 = r24;
        r1.status = r0;
    L_0x00f3:
        r14 = r18;
    L_0x00f5:
        r0 = r16;
        if (r14 >= r0) goto L_0x0200;
    L_0x00f9:
        r0 = r24;
        r0 = r0.mainPixels;
        r22 = r0;
        r23 = 0;
        r22[r14] = r23;
        r14 = r14 + 1;
        goto L_0x00f5;
    L_0x0106:
        r3 = 0;
    L_0x0107:
        r0 = r24;
        r0 = r0.block;
        r22 = r0;
        r22 = r22[r3];
        r0 = r22;
        r0 = r0 & 255;
        r22 = r0;
        r22 = r22 << r4;
        r11 = r11 + r22;
        r4 = r4 + 8;
        r3 = r3 + 1;
        r9 = r9 + -1;
        r21 = r20;
    L_0x0121:
        if (r4 < r8) goto L_0x0207;
    L_0x0123:
        r6 = r11 & r7;
        r11 = r11 >> r8;
        r4 = r4 - r8;
        if (r6 != r5) goto L_0x0136;
    L_0x0129:
        r8 = r10 + 1;
        r22 = 1;
        r22 = r22 << r8;
        r7 = r22 + -1;
        r2 = r5 + 2;
        r17 = -1;
        goto L_0x0121;
    L_0x0136:
        if (r6 <= r2) goto L_0x0143;
    L_0x0138:
        r22 = 3;
        r0 = r22;
        r1 = r24;
        r1.status = r0;
        r20 = r21;
        goto L_0x00df;
    L_0x0143:
        if (r6 != r12) goto L_0x0148;
    L_0x0145:
        r20 = r21;
        goto L_0x00df;
    L_0x0148:
        r22 = -1;
        r0 = r17;
        r1 = r22;
        if (r0 != r1) goto L_0x0168;
    L_0x0150:
        r0 = r24;
        r0 = r0.pixelStack;
        r22 = r0;
        r20 = r21 + 1;
        r0 = r24;
        r0 = r0.suffix;
        r23 = r0;
        r23 = r23[r6];
        r22[r21] = r23;
        r17 = r6;
        r13 = r6;
        r21 = r20;
        goto L_0x0121;
    L_0x0168:
        r15 = r6;
        if (r6 < r2) goto L_0x017c;
    L_0x016b:
        r0 = r24;
        r0 = r0.pixelStack;
        r22 = r0;
        r20 = r21 + 1;
        r0 = (byte) r13;
        r23 = r0;
        r22[r21] = r23;
        r6 = r17;
        r21 = r20;
    L_0x017c:
        if (r6 < r5) goto L_0x019b;
    L_0x017e:
        r0 = r24;
        r0 = r0.pixelStack;
        r22 = r0;
        r20 = r21 + 1;
        r0 = r24;
        r0 = r0.suffix;
        r23 = r0;
        r23 = r23[r6];
        r22[r21] = r23;
        r0 = r24;
        r0 = r0.prefix;
        r22 = r0;
        r6 = r22[r6];
        r21 = r20;
        goto L_0x017c;
    L_0x019b:
        r0 = r24;
        r0 = r0.suffix;
        r22 = r0;
        r22 = r22[r6];
        r0 = r22;
        r13 = r0 & 255;
        r0 = r24;
        r0 = r0.pixelStack;
        r22 = r0;
        r20 = r21 + 1;
        r0 = (byte) r13;
        r23 = r0;
        r22[r21] = r23;
        r22 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = r22;
        if (r2 >= r0) goto L_0x01e1;
    L_0x01ba:
        r0 = r24;
        r0 = r0.prefix;
        r22 = r0;
        r0 = r17;
        r0 = (short) r0;
        r23 = r0;
        r22[r2] = r23;
        r0 = r24;
        r0 = r0.suffix;
        r22 = r0;
        r0 = (byte) r13;
        r23 = r0;
        r22[r2] = r23;
        r2 = r2 + 1;
        r22 = r2 & r7;
        if (r22 != 0) goto L_0x01e1;
    L_0x01d8:
        r22 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r0 = r22;
        if (r2 >= r0) goto L_0x01e1;
    L_0x01de:
        r8 = r8 + 1;
        r7 = r7 + r2;
    L_0x01e1:
        r17 = r15;
        r19 = r18;
    L_0x01e5:
        if (r20 <= 0) goto L_0x0201;
    L_0x01e7:
        r20 = r20 + -1;
        r0 = r24;
        r0 = r0.mainPixels;
        r22 = r0;
        r18 = r19 + 1;
        r0 = r24;
        r0 = r0.pixelStack;
        r23 = r0;
        r23 = r23[r20];
        r22[r19] = r23;
        r14 = r14 + 1;
        r19 = r18;
        goto L_0x01e5;
    L_0x0200:
        return;
    L_0x0201:
        r18 = r19;
        r21 = r20;
        goto L_0x0121;
    L_0x0207:
        r20 = r21;
        goto L_0x00df;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.gifdecoder.GifDecoder.decodeBitmapData(com.bumptech.glide.gifdecoder.GifFrame):void");
    }

    private int read() {
        try {
            return this.rawData.get() & MotionEventCompat.ACTION_MASK;
        } catch (Exception e) {
            this.status = STATUS_FORMAT_ERROR;
            return STATUS_OK;
        }
    }

    private int readBlock() {
        int blockSize = read();
        int n = STATUS_OK;
        if (blockSize > 0) {
            while (n < blockSize) {
                int count = blockSize - n;
                try {
                    this.rawData.get(this.block, n, count);
                    n += count;
                } catch (Exception e) {
                    Log.w(TAG, "Error Reading Block", e);
                    this.status = STATUS_FORMAT_ERROR;
                }
            }
        }
        return n;
    }

    private Bitmap getNextBitmap() {
        Bitmap result = this.bitmapProvider.obtain(this.header.width, this.header.height, BITMAP_CONFIG);
        if (result == null) {
            result = Bitmap.createBitmap(this.header.width, this.header.height, BITMAP_CONFIG);
        }
        setAlpha(result);
        return result;
    }

    @TargetApi(12)
    private static void setAlpha(Bitmap bitmap) {
        if (VERSION.SDK_INT >= 12) {
            bitmap.setHasAlpha(true);
        }
    }
}
