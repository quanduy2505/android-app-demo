package com.google.android.gms.internal;

import android.support.v4.view.PointerIconCompat;
import com.facebook.internal.NativeProtocol;
import com.google.android.gms.common.internal.zzac;
import org.apache.http.HttpStatus;

public final class zzatd {
    public static zza<Boolean> zzbqS;
    public static zza<Boolean> zzbqT;
    public static zza<Boolean> zzbqU;
    public static zza<String> zzbqV;
    public static zza<Long> zzbqW;
    public static zza<Long> zzbqX;
    public static zza<Long> zzbqY;
    public static zza<String> zzbqZ;
    public static zza<Long> zzbrA;
    public static zza<String> zzbra;
    public static zza<Integer> zzbrb;
    public static zza<Integer> zzbrc;
    public static zza<Integer> zzbrd;
    public static zza<Integer> zzbre;
    public static zza<Integer> zzbrf;
    public static zza<Integer> zzbrg;
    public static zza<Integer> zzbrh;
    public static zza<Integer> zzbri;
    public static zza<Integer> zzbrj;
    public static zza<Integer> zzbrk;
    public static zza<String> zzbrl;
    public static zza<Long> zzbrm;
    public static zza<Long> zzbrn;
    public static zza<Long> zzbro;
    public static zza<Long> zzbrp;
    public static zza<Long> zzbrq;
    public static zza<Long> zzbrr;
    public static zza<Long> zzbrs;
    public static zza<Long> zzbrt;
    public static zza<Long> zzbru;
    public static zza<Long> zzbrv;
    public static zza<Integer> zzbrw;
    public static zza<Long> zzbrx;
    public static zza<Integer> zzbry;
    public static zza<Integer> zzbrz;

    public static final class zza<V> {
        private final String zzAH;
        private final V zzaeZ;
        private final zzabs<V> zzafa;

        private zza(String str, zzabs<V> com_google_android_gms_internal_zzabs_V, V v) {
            zzac.zzw(com_google_android_gms_internal_zzabs_V);
            this.zzafa = com_google_android_gms_internal_zzabs_V;
            this.zzaeZ = v;
            this.zzAH = str;
        }

        static zza<Integer> zzB(String str, int i) {
            return zzo(str, i, i);
        }

        static zza<String> zzV(String str, String str2) {
            return zzk(str, str2, str2);
        }

        static zza<Long> zzb(String str, long j, long j2) {
            return new zza(str, zzabs.zza(str, Long.valueOf(j2)), Long.valueOf(j));
        }

        static zza<Boolean> zzb(String str, boolean z, boolean z2) {
            return new zza(str, zzabs.zzj(str, z2), Boolean.valueOf(z));
        }

        static zza<Long> zzh(String str, long j) {
            return zzb(str, j, j);
        }

        static zza<String> zzk(String str, String str2, String str3) {
            return new zza(str, zzabs.zzA(str, str3), str2);
        }

        static zza<Boolean> zzl(String str, boolean z) {
            return zzb(str, z, z);
        }

        static zza<Integer> zzo(String str, int i, int i2) {
            return new zza(str, zzabs.zza(str, Integer.valueOf(i2)), Integer.valueOf(i));
        }

        public V get() {
            return this.zzaeZ;
        }

        public V get(V v) {
            return v != null ? v : this.zzaeZ;
        }

        public String getKey() {
            return this.zzAH;
        }
    }

    static {
        zzbqS = zza.zzl("measurement.service_enabled", true);
        zzbqT = zza.zzl("measurement.service_client_enabled", true);
        zzbqU = zza.zzl("measurement.log_installs_enabled", false);
        zzbqV = zza.zzk("measurement.log_tag", "FA", "FA-SVC");
        zzbqW = zza.zzh("measurement.ad_id_cache_time", 10000);
        zzbqX = zza.zzh("measurement.monitoring.sample_period_millis", 86400000);
        zzbqY = zza.zzb("measurement.config.cache_time", 86400000, 3600000);
        zzbqZ = zza.zzV("measurement.config.url_scheme", "https");
        zzbra = zza.zzV("measurement.config.url_authority", "app-measurement.com");
        zzbrb = zza.zzB("measurement.upload.max_bundles", 100);
        zzbrc = zza.zzB("measurement.upload.max_batch_size", NativeProtocol.MESSAGE_GET_ACCESS_TOKEN_REQUEST);
        zzbrd = zza.zzB("measurement.upload.max_bundle_size", NativeProtocol.MESSAGE_GET_ACCESS_TOKEN_REQUEST);
        zzbre = zza.zzB("measurement.upload.max_events_per_bundle", PointerIconCompat.TYPE_DEFAULT);
        zzbrf = zza.zzB("measurement.upload.max_events_per_day", 100000);
        zzbrg = zza.zzB("measurement.upload.max_error_events_per_day", PointerIconCompat.TYPE_DEFAULT);
        zzbrh = zza.zzB("measurement.upload.max_public_events_per_day", 50000);
        zzbri = zza.zzB("measurement.upload.max_conversions_per_day", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        zzbrj = zza.zzB("measurement.upload.max_realtime_events_per_day", 10);
        zzbrk = zza.zzB("measurement.store.max_stored_events_per_app", 100000);
        zzbrl = zza.zzV("measurement.upload.url", "https://app-measurement.com/a");
        zzbrm = zza.zzh("measurement.upload.backoff_period", 43200000);
        zzbrn = zza.zzh("measurement.upload.window_interval", 3600000);
        zzbro = zza.zzh("measurement.upload.interval", 3600000);
        zzbrp = zza.zzh("measurement.upload.realtime_upload_interval", 10000);
        zzbrq = zza.zzh("measurement.upload.minimum_delay", 500);
        zzbrr = zza.zzh("measurement.alarm_manager.minimum_interval", 60000);
        zzbrs = zza.zzh("measurement.upload.stale_data_deletion_interval", 86400000);
        zzbrt = zza.zzh("measurement.upload.refresh_blacklisted_config_interval", 604800000);
        zzbru = zza.zzh("measurement.upload.initial_upload_delay_time", 15000);
        zzbrv = zza.zzh("measurement.upload.retry_time", 1800000);
        zzbrw = zza.zzB("measurement.upload.retry_count", 6);
        zzbrx = zza.zzh("measurement.upload.max_queue_time", 2419200000L);
        zzbry = zza.zzB("measurement.lifetimevalue.max_currency_tracked", 4);
        zzbrz = zza.zzB("measurement.audience.filter_result_max_count", HttpStatus.SC_OK);
        zzbrA = zza.zzh("measurement.service_client.idle_disconnect_millis", 5000);
    }
}
