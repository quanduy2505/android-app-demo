package com.bumptech.glide.gifdecoder;

import java.util.ArrayList;
import java.util.List;

public class GifHeader {
    int bgColor;
    int bgIndex;
    GifFrame currentFrame;
    int frameCount;
    List<GifFrame> frames;
    int[] gct;
    boolean gctFlag;
    int gctSize;
    int height;
    int loopCount;
    int pixelAspect;
    int status;
    int width;

    public GifHeader() {
        this.gct = null;
        this.status = 0;
        this.frameCount = 0;
        this.frames = new ArrayList();
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getNumFrames() {
        return this.frameCount;
    }

    public int getStatus() {
        return this.status;
    }
}
