package com.google.firebase.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Map;
import java.util.Map.Entry;

public final class RemoteMessage extends zza {
    public static final Creator<RemoteMessage> CREATOR;
    final int mVersionCode;
    private Map<String, String> zzabb;
    Bundle zzahb;
    private Notification zzcjr;

    public static class Builder {
        private final Map<String, String> zzabb;
        private final Bundle zzahb;

        public Builder(String str) {
            this.zzahb = new Bundle();
            this.zzabb = new ArrayMap();
            if (TextUtils.isEmpty(str)) {
                String str2 = "Invalid to: ";
                String valueOf = String.valueOf(str);
                throw new IllegalArgumentException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
            this.zzahb.putString("google.to", str);
        }

        public Builder addData(String str, String str2) {
            this.zzabb.put(str, str2);
            return this;
        }

        public RemoteMessage build() {
            Bundle bundle = new Bundle();
            for (Entry entry : this.zzabb.entrySet()) {
                bundle.putString((String) entry.getKey(), (String) entry.getValue());
            }
            bundle.putAll(this.zzahb);
            String token = FirebaseInstanceId.getInstance().getToken();
            if (token != null) {
                this.zzahb.putString("from", token);
            } else {
                this.zzahb.remove("from");
            }
            return new RemoteMessage(bundle);
        }

        public Builder clearData() {
            this.zzabb.clear();
            return this;
        }

        public Builder setCollapseKey(String str) {
            this.zzahb.putString("collapse_key", str);
            return this;
        }

        public Builder setData(Map<String, String> map) {
            this.zzabb.clear();
            this.zzabb.putAll(map);
            return this;
        }

        public Builder setMessageId(String str) {
            this.zzahb.putString("google.message_id", str);
            return this;
        }

        public Builder setMessageType(String str) {
            this.zzahb.putString("message_type", str);
            return this;
        }

        public Builder setTtl(int i) {
            this.zzahb.putString("google.ttl", String.valueOf(i));
            return this;
        }
    }

    public static class Notification {
        private final String mTag;
        private final String zzFU;
        private final String zzaPp;
        private final String zzalD;
        private final String zzcjs;
        private final String[] zzcjt;
        private final String zzcju;
        private final String[] zzcjv;
        private final String zzcjw;
        private final String zzcjx;
        private final String zzcjy;

        private Notification(Bundle bundle) {
            this.zzalD = zza.zzf(bundle, "gcm.n.title");
            this.zzcjs = zza.zzh(bundle, "gcm.n.title");
            this.zzcjt = zzj(bundle, "gcm.n.title");
            this.zzFU = zza.zzf(bundle, "gcm.n.body");
            this.zzcju = zza.zzh(bundle, "gcm.n.body");
            this.zzcjv = zzj(bundle, "gcm.n.body");
            this.zzcjw = zza.zzf(bundle, "gcm.n.icon");
            this.zzcjx = zza.zzU(bundle);
            this.mTag = zza.zzf(bundle, "gcm.n.tag");
            this.zzaPp = zza.zzf(bundle, "gcm.n.color");
            this.zzcjy = zza.zzf(bundle, "gcm.n.click_action");
        }

        private String[] zzj(Bundle bundle, String str) {
            Object[] zzi = zza.zzi(bundle, str);
            if (zzi == null) {
                return null;
            }
            String[] strArr = new String[zzi.length];
            for (int i = 0; i < zzi.length; i++) {
                strArr[i] = String.valueOf(zzi[i]);
            }
            return strArr;
        }

        public String getBody() {
            return this.zzFU;
        }

        public String[] getBodyLocalizationArgs() {
            return this.zzcjv;
        }

        public String getBodyLocalizationKey() {
            return this.zzcju;
        }

        public String getClickAction() {
            return this.zzcjy;
        }

        public String getColor() {
            return this.zzaPp;
        }

        public String getIcon() {
            return this.zzcjw;
        }

        public String getSound() {
            return this.zzcjx;
        }

        public String getTag() {
            return this.mTag;
        }

        public String getTitle() {
            return this.zzalD;
        }

        public String[] getTitleLocalizationArgs() {
            return this.zzcjt;
        }

        public String getTitleLocalizationKey() {
            return this.zzcjs;
        }
    }

    static {
        CREATOR = new zzc();
    }

    RemoteMessage(int i, Bundle bundle) {
        this.mVersionCode = i;
        this.zzahb = bundle;
    }

    RemoteMessage(Bundle bundle) {
        this(1, bundle);
    }

    public String getCollapseKey() {
        return this.zzahb.getString("collapse_key");
    }

    public Map<String, String> getData() {
        if (this.zzabb == null) {
            this.zzabb = new ArrayMap();
            for (String str : this.zzahb.keySet()) {
                Object obj = this.zzahb.get(str);
                if (obj instanceof String) {
                    String str2 = (String) obj;
                    if (!(str.startsWith("google.") || str.startsWith("gcm.") || str.equals("from") || str.equals("message_type") || str.equals("collapse_key"))) {
                        this.zzabb.put(str, str2);
                    }
                }
            }
        }
        return this.zzabb;
    }

    public String getFrom() {
        return this.zzahb.getString("from");
    }

    public String getMessageId() {
        String string = this.zzahb.getString("google.message_id");
        return string == null ? this.zzahb.getString("message_id") : string;
    }

    public String getMessageType() {
        return this.zzahb.getString("message_type");
    }

    public Notification getNotification() {
        if (this.zzcjr == null && zza.zzE(this.zzahb)) {
            this.zzcjr = new Notification(null);
        }
        return this.zzcjr;
    }

    public long getSentTime() {
        Object obj = this.zzahb.get("google.sent_time");
        if (obj instanceof Long) {
            return ((Long) obj).longValue();
        }
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                String valueOf = String.valueOf(obj);
                Log.w("FirebaseMessaging", new StringBuilder(String.valueOf(valueOf).length() + 19).append("Invalid sent time: ").append(valueOf).toString());
            }
        }
        return 0;
    }

    public String getTo() {
        return this.zzahb.getString("google.to");
    }

    public int getTtl() {
        Object obj = this.zzahb.get("google.ttl");
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                String valueOf = String.valueOf(obj);
                Log.w("FirebaseMessaging", new StringBuilder(String.valueOf(valueOf).length() + 13).append("Invalid TTL: ").append(valueOf).toString());
            }
        }
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }

    void zzL(Intent intent) {
        intent.putExtras(this.zzahb);
    }
}
