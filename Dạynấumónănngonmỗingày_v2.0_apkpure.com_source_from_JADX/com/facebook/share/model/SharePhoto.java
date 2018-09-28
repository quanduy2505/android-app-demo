package com.facebook.share.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.facebook.share.model.ShareMedia.Type;
import java.util.ArrayList;
import java.util.List;

public final class SharePhoto extends ShareMedia {
    public static final Creator<SharePhoto> CREATOR;
    private final Bitmap bitmap;
    private final String caption;
    private final Uri imageUrl;
    private final boolean userGenerated;

    /* renamed from: com.facebook.share.model.SharePhoto.1 */
    static class C04741 implements Creator<SharePhoto> {
        C04741() {
        }

        public SharePhoto createFromParcel(Parcel source) {
            return new SharePhoto(source);
        }

        public SharePhoto[] newArray(int size) {
            return new SharePhoto[size];
        }
    }

    public static final class Builder extends com.facebook.share.model.ShareMedia.Builder<SharePhoto, Builder> {
        private Bitmap bitmap;
        private String caption;
        private Uri imageUrl;
        private boolean userGenerated;

        public Builder setBitmap(@Nullable Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public Builder setImageUrl(@Nullable Uri imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder setUserGenerated(boolean userGenerated) {
            this.userGenerated = userGenerated;
            return this;
        }

        public Builder setCaption(@Nullable String caption) {
            this.caption = caption;
            return this;
        }

        Uri getImageUrl() {
            return this.imageUrl;
        }

        Bitmap getBitmap() {
            return this.bitmap;
        }

        public SharePhoto build() {
            return new SharePhoto();
        }

        public Builder readFrom(SharePhoto model) {
            if (model == null) {
                return this;
            }
            return ((Builder) super.readFrom((ShareMedia) model)).setBitmap(model.getBitmap()).setImageUrl(model.getImageUrl()).setUserGenerated(model.getUserGenerated()).setCaption(model.getCaption());
        }

        Builder readFrom(Parcel parcel) {
            return readFrom((SharePhoto) parcel.readParcelable(SharePhoto.class.getClassLoader()));
        }

        static void writePhotoListTo(Parcel out, int parcelFlags, List<SharePhoto> photos) {
            ShareMedia[] array = new ShareMedia[photos.size()];
            for (int i = 0; i < photos.size(); i++) {
                array[i] = (ShareMedia) photos.get(i);
            }
            out.writeParcelableArray(array, parcelFlags);
        }

        static List<SharePhoto> readPhotoListFrom(Parcel in) {
            List<ShareMedia> media = com.facebook.share.model.ShareMedia.Builder.readListFrom(in);
            List<SharePhoto> photos = new ArrayList();
            for (ShareMedia medium : media) {
                if (medium instanceof SharePhoto) {
                    photos.add((SharePhoto) medium);
                }
            }
            return photos;
        }
    }

    private SharePhoto(Builder builder) {
        super((com.facebook.share.model.ShareMedia.Builder) builder);
        this.bitmap = builder.bitmap;
        this.imageUrl = builder.imageUrl;
        this.userGenerated = builder.userGenerated;
        this.caption = builder.caption;
    }

    SharePhoto(Parcel in) {
        super(in);
        this.bitmap = (Bitmap) in.readParcelable(Bitmap.class.getClassLoader());
        this.imageUrl = (Uri) in.readParcelable(Uri.class.getClassLoader());
        this.userGenerated = in.readByte() != null;
        this.caption = in.readString();
    }

    @Nullable
    public Bitmap getBitmap() {
        return this.bitmap;
    }

    @Nullable
    public Uri getImageUrl() {
        return this.imageUrl;
    }

    public boolean getUserGenerated() {
        return this.userGenerated;
    }

    public String getCaption() {
        return this.caption;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        int i = 0;
        super.writeToParcel(out, flags);
        out.writeParcelable(this.bitmap, 0);
        out.writeParcelable(this.imageUrl, 0);
        if (this.userGenerated) {
            i = 1;
        }
        out.writeByte((byte) i);
        out.writeString(this.caption);
    }

    static {
        CREATOR = new C04741();
    }

    public Type getMediaType() {
        return Type.PHOTO;
    }
}
