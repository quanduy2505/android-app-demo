package com.facebook.share.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ShareMediaContent extends ShareContent<ShareMediaContent, Builder> {
    public static final Creator<ShareMediaContent> CREATOR;
    private final List<ShareMedia> media;

    /* renamed from: com.facebook.share.model.ShareMediaContent.1 */
    static class C04701 implements Creator<ShareMediaContent> {
        C04701() {
        }

        public ShareMediaContent createFromParcel(Parcel in) {
            return new ShareMediaContent(in);
        }

        public ShareMediaContent[] newArray(int size) {
            return new ShareMediaContent[size];
        }
    }

    public static class Builder extends com.facebook.share.model.ShareContent.Builder<ShareMediaContent, Builder> {
        private final List<ShareMedia> media;

        public Builder() {
            this.media = new ArrayList();
        }

        public Builder addMedium(@Nullable ShareMedia medium) {
            if (medium != null) {
                ShareMedia mediumToAdd;
                if (medium instanceof SharePhoto) {
                    mediumToAdd = new com.facebook.share.model.SharePhoto.Builder().readFrom((SharePhoto) medium).build();
                } else if (medium instanceof ShareVideo) {
                    mediumToAdd = new com.facebook.share.model.ShareVideo.Builder().readFrom((ShareVideo) medium).build();
                } else {
                    throw new IllegalArgumentException("medium must be either a SharePhoto or ShareVideo");
                }
                this.media.add(mediumToAdd);
            }
            return this;
        }

        public Builder addMedia(@Nullable List<ShareMedia> media) {
            if (media != null) {
                for (ShareMedia medium : media) {
                    addMedium(medium);
                }
            }
            return this;
        }

        public ShareMediaContent build() {
            return new ShareMediaContent();
        }

        public Builder readFrom(ShareMediaContent model) {
            if (model == null) {
                return this;
            }
            return ((Builder) super.readFrom((ShareContent) model)).addMedia(model.getMedia());
        }

        public Builder setMedia(@Nullable List<ShareMedia> media) {
            this.media.clear();
            addMedia(media);
            return this;
        }
    }

    private ShareMediaContent(Builder builder) {
        super((com.facebook.share.model.ShareContent.Builder) builder);
        this.media = Collections.unmodifiableList(builder.media);
    }

    ShareMediaContent(Parcel in) {
        super(in);
        this.media = Arrays.asList((ShareMedia[]) in.readParcelableArray(ShareMedia.class.getClassLoader()));
    }

    @Nullable
    public List<ShareMedia> getMedia() {
        return this.media;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeParcelableArray((ShareMedia[]) this.media.toArray(), flags);
    }

    static {
        CREATOR = new C04701();
    }
}
