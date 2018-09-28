package com.facebook.share.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public final class AppInviteContent implements ShareModel {
    public static final Creator<AppInviteContent> CREATOR;
    private final String applinkUrl;
    private final Destination destination;
    private final String previewImageUrl;
    private final String promoCode;
    private final String promoText;

    /* renamed from: com.facebook.share.model.AppInviteContent.1 */
    static class C04661 implements Creator<AppInviteContent> {
        C04661() {
        }

        public AppInviteContent createFromParcel(Parcel in) {
            return new AppInviteContent(in);
        }

        public AppInviteContent[] newArray(int size) {
            return new AppInviteContent[size];
        }
    }

    public static class Builder implements ShareModelBuilder<AppInviteContent, Builder> {
        private String applinkUrl;
        private Destination destination;
        private String previewImageUrl;
        private String promoCode;
        private String promoText;

        public enum Destination {
            FACEBOOK("facebook"),
            MESSENGER("messenger");
            
            private final String name;

            private Destination(String s) {
                this.name = s;
            }

            public boolean equalsName(String otherName) {
                return otherName == null ? false : this.name.equals(otherName);
            }

            public String toString() {
                return this.name;
            }
        }

        public Builder setApplinkUrl(String applinkUrl) {
            this.applinkUrl = applinkUrl;
            return this;
        }

        public Builder setPreviewImageUrl(String previewImageUrl) {
            this.previewImageUrl = previewImageUrl;
            return this;
        }

        public Builder setPromotionDetails(String promotionText, String promotionCode) {
            if (TextUtils.isEmpty(promotionText)) {
                if (!TextUtils.isEmpty(promotionCode)) {
                    throw new IllegalArgumentException("promotionCode cannot be specified without a valid promotionText");
                }
            } else if (promotionText.length() > 80) {
                throw new IllegalArgumentException("Invalid promotion text, promotionText needs to be between1 and 80 characters long");
            } else if (!isAlphanumericWithSpaces(promotionText)) {
                throw new IllegalArgumentException("Invalid promotion text, promotionText can only contain alphanumericcharacters and spaces.");
            } else if (!TextUtils.isEmpty(promotionCode)) {
                if (promotionCode.length() > 10) {
                    throw new IllegalArgumentException("Invalid promotion code, promotionCode can be between1 and 10 characters long");
                } else if (!isAlphanumericWithSpaces(promotionCode)) {
                    throw new IllegalArgumentException("Invalid promotion code, promotionCode can only contain alphanumeric characters and spaces.");
                }
            }
            this.promoCode = promotionCode;
            this.promoText = promotionText;
            return this;
        }

        public Builder setDestination(Destination destination) {
            this.destination = destination;
            return this;
        }

        public AppInviteContent build() {
            return new AppInviteContent();
        }

        public Builder readFrom(AppInviteContent content) {
            if (content == null) {
                return this;
            }
            return setApplinkUrl(content.getApplinkUrl()).setPreviewImageUrl(content.getPreviewImageUrl()).setPromotionDetails(content.getPromotionText(), content.getPromotionCode()).setDestination(content.getDestination());
        }

        private boolean isAlphanumericWithSpaces(String str) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (!Character.isDigit(c) && !Character.isLetter(c) && !Character.isSpaceChar(c)) {
                    return false;
                }
            }
            return true;
        }
    }

    private AppInviteContent(Builder builder) {
        this.applinkUrl = builder.applinkUrl;
        this.previewImageUrl = builder.previewImageUrl;
        this.promoCode = builder.promoCode;
        this.promoText = builder.promoText;
        this.destination = builder.destination;
    }

    AppInviteContent(Parcel in) {
        this.applinkUrl = in.readString();
        this.previewImageUrl = in.readString();
        this.promoText = in.readString();
        this.promoCode = in.readString();
        String destinationString = in.readString();
        if (destinationString.length() > 0) {
            this.destination = Destination.valueOf(destinationString);
        } else {
            this.destination = Destination.FACEBOOK;
        }
    }

    public String getApplinkUrl() {
        return this.applinkUrl;
    }

    public String getPreviewImageUrl() {
        return this.previewImageUrl;
    }

    public String getPromotionCode() {
        return this.promoCode;
    }

    public String getPromotionText() {
        return this.promoText;
    }

    public Destination getDestination() {
        if (this.destination != null) {
            return this.destination;
        }
        return Destination.FACEBOOK;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.applinkUrl);
        out.writeString(this.previewImageUrl);
        out.writeString(this.promoText);
        out.writeString(this.promoCode);
        out.writeString(this.destination.toString());
    }

    static {
        CREATOR = new C04661();
    }
}
