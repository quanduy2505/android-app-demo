package com.facebook.share.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class AppGroupCreationContent implements ShareModel {
    public static final Creator<AppGroupCreationContent> CREATOR;
    private final String description;
    private final String name;
    private AppGroupPrivacy privacy;

    /* renamed from: com.facebook.share.model.AppGroupCreationContent.1 */
    static class C04651 implements Creator<AppGroupCreationContent> {
        C04651() {
        }

        public AppGroupCreationContent createFromParcel(Parcel in) {
            return new AppGroupCreationContent(in);
        }

        public AppGroupCreationContent[] newArray(int size) {
            return new AppGroupCreationContent[size];
        }
    }

    public enum AppGroupPrivacy {
        Open,
        Closed
    }

    public static class Builder implements ShareModelBuilder<AppGroupCreationContent, Builder> {
        private String description;
        private String name;
        private AppGroupPrivacy privacy;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setAppGroupPrivacy(AppGroupPrivacy privacy) {
            this.privacy = privacy;
            return this;
        }

        public AppGroupCreationContent build() {
            return new AppGroupCreationContent();
        }

        public Builder readFrom(AppGroupCreationContent content) {
            if (content == null) {
                return this;
            }
            return setName(content.getName()).setDescription(content.getDescription()).setAppGroupPrivacy(content.getAppGroupPrivacy());
        }
    }

    private AppGroupCreationContent(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.privacy = builder.privacy;
    }

    AppGroupCreationContent(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.privacy = (AppGroupPrivacy) in.readSerializable();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public AppGroupPrivacy getAppGroupPrivacy() {
        return this.privacy;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeString(this.description);
        out.writeSerializable(this.privacy);
    }

    static {
        CREATOR = new C04651();
    }
}
