package com.facebook.share.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.facebook.share.model.ShareModel;
import com.facebook.share.model.ShareModelBuilder;

public class LikeContent implements ShareModel {
    public static final Creator<LikeContent> CREATOR;
    private final String objectId;
    private final String objectType;

    /* renamed from: com.facebook.share.internal.LikeContent.1 */
    static class C04571 implements Creator<LikeContent> {
        C04571() {
        }

        public LikeContent createFromParcel(Parcel in) {
            return new LikeContent(in);
        }

        public LikeContent[] newArray(int size) {
            return new LikeContent[size];
        }
    }

    public static class Builder implements ShareModelBuilder<LikeContent, Builder> {
        private String objectId;
        private String objectType;

        public Builder setObjectId(String objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder setObjectType(String objectType) {
            this.objectType = objectType;
            return this;
        }

        public LikeContent build() {
            return new LikeContent();
        }

        public Builder readFrom(LikeContent content) {
            if (content == null) {
                return this;
            }
            return setObjectId(content.getObjectId()).setObjectType(content.getObjectType());
        }
    }

    private LikeContent(Builder builder) {
        this.objectId = builder.objectId;
        this.objectType = builder.objectType;
    }

    LikeContent(Parcel in) {
        this.objectId = in.readString();
        this.objectType = in.readString();
    }

    public String getObjectId() {
        return this.objectId;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.objectId);
        out.writeString(this.objectType);
    }

    static {
        CREATOR = new C04571();
    }
}
