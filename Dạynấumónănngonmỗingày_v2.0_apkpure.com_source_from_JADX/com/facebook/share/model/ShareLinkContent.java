package com.facebook.share.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;

public final class ShareLinkContent extends ShareContent<ShareLinkContent, Builder> {
    public static final Creator<ShareLinkContent> CREATOR;
    private final String contentDescription;
    private final String contentTitle;
    private final Uri imageUrl;
    private final String quote;

    /* renamed from: com.facebook.share.model.ShareLinkContent.1 */
    static class C04691 implements Creator<ShareLinkContent> {
        C04691() {
        }

        public ShareLinkContent createFromParcel(Parcel in) {
            return new ShareLinkContent(in);
        }

        public ShareLinkContent[] newArray(int size) {
            return new ShareLinkContent[size];
        }
    }

    public static final class Builder extends com.facebook.share.model.ShareContent.Builder<ShareLinkContent, Builder> {
        private String contentDescription;
        private String contentTitle;
        private Uri imageUrl;
        private String quote;

        public Builder setContentDescription(@Nullable String contentDescription) {
            this.contentDescription = contentDescription;
            return this;
        }

        public Builder setContentTitle(@Nullable String contentTitle) {
            this.contentTitle = contentTitle;
            return this;
        }

        public Builder setImageUrl(@Nullable Uri imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder setQuote(@Nullable String quote) {
            this.quote = quote;
            return this;
        }

        public ShareLinkContent build() {
            return new ShareLinkContent();
        }

        public Builder readFrom(ShareLinkContent model) {
            if (model == null) {
                return this;
            }
            return ((Builder) super.readFrom((ShareContent) model)).setContentDescription(model.getContentDescription()).setImageUrl(model.getImageUrl()).setContentTitle(model.getContentTitle()).setQuote(model.getQuote());
        }
    }

    private ShareLinkContent(Builder builder) {
        super((com.facebook.share.model.ShareContent.Builder) builder);
        this.contentDescription = builder.contentDescription;
        this.contentTitle = builder.contentTitle;
        this.imageUrl = builder.imageUrl;
        this.quote = builder.quote;
    }

    ShareLinkContent(Parcel in) {
        super(in);
        this.contentDescription = in.readString();
        this.contentTitle = in.readString();
        this.imageUrl = (Uri) in.readParcelable(Uri.class.getClassLoader());
        this.quote = in.readString();
    }

    public String getContentDescription() {
        return this.contentDescription;
    }

    @Nullable
    public String getContentTitle() {
        return this.contentTitle;
    }

    @Nullable
    public Uri getImageUrl() {
        return this.imageUrl;
    }

    @Nullable
    public String getQuote() {
        return this.quote;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(this.contentDescription);
        out.writeString(this.contentTitle);
        out.writeParcelable(this.imageUrl, 0);
        out.writeString(this.quote);
    }

    static {
        CREATOR = new C04691();
    }
}
