package com.facebook.share.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.facebook.share.model.ShareMedia.Type;

public final class ShareVideo extends ShareMedia {
    public static final Creator<ShareVideo> CREATOR;
    private final Uri localUrl;

    /* renamed from: com.facebook.share.model.ShareVideo.1 */
    static class C04761 implements Creator<ShareVideo> {
        C04761() {
        }

        public ShareVideo createFromParcel(Parcel source) {
            return new ShareVideo(source);
        }

        public ShareVideo[] newArray(int size) {
            return new ShareVideo[size];
        }
    }

    public static final class Builder extends com.facebook.share.model.ShareMedia.Builder<ShareVideo, Builder> {
        private Uri localUrl;

        public Builder setLocalUrl(@Nullable Uri localUrl) {
            this.localUrl = localUrl;
            return this;
        }

        public ShareVideo build() {
            return new ShareVideo();
        }

        public Builder readFrom(ShareVideo model) {
            if (model == null) {
                return this;
            }
            return ((Builder) super.readFrom((ShareMedia) model)).setLocalUrl(model.getLocalUrl());
        }

        Builder readFrom(Parcel parcel) {
            return readFrom((ShareVideo) parcel.readParcelable(ShareVideo.class.getClassLoader()));
        }
    }

    private ShareVideo(Builder builder) {
        super((com.facebook.share.model.ShareMedia.Builder) builder);
        this.localUrl = builder.localUrl;
    }

    ShareVideo(Parcel in) {
        super(in);
        this.localUrl = (Uri) in.readParcelable(Uri.class.getClassLoader());
    }

    @Nullable
    public Uri getLocalUrl() {
        return this.localUrl;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeParcelable(this.localUrl, 0);
    }

    static {
        CREATOR = new C04761();
    }

    public Type getMediaType() {
        return Type.VIDEO;
    }
}
