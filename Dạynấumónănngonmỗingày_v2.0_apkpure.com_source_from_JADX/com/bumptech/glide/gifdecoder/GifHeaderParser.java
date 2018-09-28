package com.bumptech.glide.gifdecoder;

import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import me.wangyuwei.loadingview.C0801R;
import rx.android.BuildConfig;
import rx.internal.operators.OnSubscribeConcatMap;

public class GifHeaderParser {
    static final int DEFAULT_FRAME_DELAY = 10;
    private static final int MAX_BLOCK_SIZE = 256;
    static final int MIN_FRAME_DELAY = 3;
    public static final String TAG = "GifHeaderParser";
    private final byte[] block;
    private int blockSize;
    private GifHeader header;
    private ByteBuffer rawData;

    public GifHeaderParser() {
        this.block = new byte[MAX_BLOCK_SIZE];
        this.blockSize = 0;
    }

    public GifHeaderParser setData(byte[] data) {
        reset();
        if (data != null) {
            this.rawData = ByteBuffer.wrap(data);
            this.rawData.rewind();
            this.rawData.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            this.rawData = null;
            this.header.status = 2;
        }
        return this;
    }

    public void clear() {
        this.rawData = null;
        this.header = null;
    }

    private void reset() {
        this.rawData = null;
        Arrays.fill(this.block, (byte) 0);
        this.header = new GifHeader();
        this.blockSize = 0;
    }

    public GifHeader parseHeader() {
        if (this.rawData == null) {
            throw new IllegalStateException("You must call setData() before parseHeader()");
        } else if (err()) {
            return this.header;
        } else {
            readHeader();
            if (!err()) {
                readContents();
                if (this.header.frameCount < 0) {
                    this.header.status = 1;
                }
            }
            return this.header;
        }
    }

    private void readContents() {
        boolean done = false;
        while (!done && !err()) {
            switch (read()) {
                case C0801R.styleable.AppCompatTheme_actionModeCopyDrawable /*33*/:
                    switch (read()) {
                        case OnSubscribeConcatMap.BOUNDARY /*1*/:
                            skip();
                            break;
                        case 249:
                            this.header.currentFrame = new GifFrame();
                            readGraphicControlExt();
                            break;
                        case 254:
                            skip();
                            break;
                        case MotionEventCompat.ACTION_MASK /*255*/:
                            readBlock();
                            String app = BuildConfig.VERSION_NAME;
                            for (int i = 0; i < 11; i++) {
                                app = app + ((char) this.block[i]);
                            }
                            if (!app.equals("NETSCAPE2.0")) {
                                skip();
                                break;
                            } else {
                                readNetscapeExt();
                                break;
                            }
                        default:
                            skip();
                            break;
                    }
                case C0801R.styleable.AppCompatTheme_dialogPreferredPadding /*44*/:
                    if (this.header.currentFrame == null) {
                        this.header.currentFrame = new GifFrame();
                    }
                    readBitmap();
                    break;
                case C0801R.styleable.AppCompatTheme_toolbarStyle /*59*/:
                    done = true;
                    break;
                default:
                    this.header.status = 1;
                    break;
            }
        }
    }

    private void readGraphicControlExt() {
        boolean z = true;
        read();
        int packed = read();
        this.header.currentFrame.dispose = (packed & 28) >> 2;
        if (this.header.currentFrame.dispose == 0) {
            this.header.currentFrame.dispose = 1;
        }
        GifFrame gifFrame = this.header.currentFrame;
        if ((packed & 1) == 0) {
            z = false;
        }
        gifFrame.transparency = z;
        int delayInHundredthsOfASecond = readShort();
        if (delayInHundredthsOfASecond < MIN_FRAME_DELAY) {
            delayInHundredthsOfASecond = DEFAULT_FRAME_DELAY;
        }
        this.header.currentFrame.delay = delayInHundredthsOfASecond * DEFAULT_FRAME_DELAY;
        this.header.currentFrame.transIndex = read();
        read();
    }

    private void readBitmap() {
        boolean lctFlag;
        boolean z = true;
        this.header.currentFrame.ix = readShort();
        this.header.currentFrame.iy = readShort();
        this.header.currentFrame.iw = readShort();
        this.header.currentFrame.ih = readShort();
        int packed = read();
        if ((packed & TransportMediator.FLAG_KEY_MEDIA_NEXT) != 0) {
            lctFlag = true;
        } else {
            lctFlag = false;
        }
        int lctSize = (int) Math.pow(2.0d, (double) ((packed & 7) + 1));
        GifFrame gifFrame = this.header.currentFrame;
        if ((packed & 64) == 0) {
            z = false;
        }
        gifFrame.interlace = z;
        if (lctFlag) {
            this.header.currentFrame.lct = readColorTable(lctSize);
        } else {
            this.header.currentFrame.lct = null;
        }
        this.header.currentFrame.bufferFrameStart = this.rawData.position();
        skipImageData();
        if (!err()) {
            GifHeader gifHeader = this.header;
            gifHeader.frameCount++;
            this.header.frames.add(this.header.currentFrame);
        }
    }

    private void readNetscapeExt() {
        do {
            readBlock();
            if (this.block[0] == (byte) 1) {
                this.header.loopCount = ((this.block[2] & MotionEventCompat.ACTION_MASK) << 8) | (this.block[1] & MotionEventCompat.ACTION_MASK);
            }
            if (this.blockSize <= 0) {
                return;
            }
        } while (!err());
    }

    private void readHeader() {
        String id = BuildConfig.VERSION_NAME;
        for (int i = 0; i < 6; i++) {
            id = id + ((char) read());
        }
        if (id.startsWith("GIF")) {
            readLSD();
            if (this.header.gctFlag && !err()) {
                this.header.gct = readColorTable(this.header.gctSize);
                this.header.bgColor = this.header.gct[this.header.bgIndex];
                return;
            }
            return;
        }
        this.header.status = 1;
    }

    private void readLSD() {
        this.header.width = readShort();
        this.header.height = readShort();
        int packed = read();
        this.header.gctFlag = (packed & TransportMediator.FLAG_KEY_MEDIA_NEXT) != 0;
        this.header.gctSize = 2 << (packed & 7);
        this.header.bgIndex = read();
        this.header.pixelAspect = read();
    }

    private int[] readColorTable(int ncolors) {
        int[] tab = null;
        byte[] c = new byte[(ncolors * MIN_FRAME_DELAY)];
        try {
            this.rawData.get(c);
            tab = new int[MAX_BLOCK_SIZE];
            int j = 0;
            int i = 0;
            while (i < ncolors) {
                int j2 = j + 1;
                int r = c[j] & MotionEventCompat.ACTION_MASK;
                j = j2 + 1;
                int g = c[j2] & MotionEventCompat.ACTION_MASK;
                j2 = j + 1;
                int i2 = i + 1;
                tab[i] = ((ViewCompat.MEASURED_STATE_MASK | (r << 16)) | (g << 8)) | (c[j] & MotionEventCompat.ACTION_MASK);
                j = j2;
                i = i2;
            }
        } catch (BufferUnderflowException e) {
            if (Log.isLoggable(TAG, MIN_FRAME_DELAY)) {
                Log.d(TAG, "Format Error Reading Color Table", e);
            }
            this.header.status = 1;
        }
        return tab;
    }

    private void skipImageData() {
        read();
        skip();
    }

    private void skip() {
        int blockSize;
        do {
            blockSize = read();
            this.rawData.position(this.rawData.position() + blockSize);
        } while (blockSize > 0);
    }

    private int readBlock() {
        this.blockSize = read();
        int n = 0;
        if (this.blockSize > 0) {
            int count = 0;
            while (n < this.blockSize) {
                try {
                    count = this.blockSize - n;
                    this.rawData.get(this.block, n, count);
                    n += count;
                } catch (Exception e) {
                    if (Log.isLoggable(TAG, MIN_FRAME_DELAY)) {
                        Log.d(TAG, "Error Reading Block n: " + n + " count: " + count + " blockSize: " + this.blockSize, e);
                    }
                    this.header.status = 1;
                }
            }
        }
        return n;
    }

    private int read() {
        try {
            return this.rawData.get() & MotionEventCompat.ACTION_MASK;
        } catch (Exception e) {
            this.header.status = 1;
            return 0;
        }
    }

    private int readShort() {
        return this.rawData.getShort();
    }

    private boolean err() {
        return this.header.status != 0;
    }
}
