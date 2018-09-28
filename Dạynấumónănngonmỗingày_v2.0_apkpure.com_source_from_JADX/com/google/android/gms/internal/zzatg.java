package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.common.util.zze;

public class zzatg extends zzats {
    private final zza zzbrD;
    private boolean zzbrE;

    @TargetApi(11)
    private class zza extends SQLiteOpenHelper {
        final /* synthetic */ zzatg zzbrF;

        zza(zzatg com_google_android_gms_internal_zzatg, Context context, String str) {
            this.zzbrF = com_google_android_gms_internal_zzatg;
            super(context, str, null, 1);
        }

        @WorkerThread
        public SQLiteDatabase getWritableDatabase() {
            try {
                return super.getWritableDatabase();
            } catch (SQLiteException e) {
                if (VERSION.SDK_INT < 11 || !(e instanceof SQLiteDatabaseLockedException)) {
                    this.zzbrF.zzJt().zzLa().log("Opening the local database failed, dropping and recreating it");
                    String zznV = this.zzbrF.zznV();
                    if (!this.zzbrF.getContext().getDatabasePath(zznV).delete()) {
                        this.zzbrF.zzJt().zzLa().zzj("Failed to delete corrupted local db file", zznV);
                    }
                    try {
                        return super.getWritableDatabase();
                    } catch (SQLiteException e2) {
                        this.zzbrF.zzJt().zzLa().zzj("Failed to open local database. Events will bypass local storage", e2);
                        return null;
                    }
                }
                throw e2;
            }
        }

        @WorkerThread
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            zzasu.zza(this.zzbrF.zzJt(), sQLiteDatabase);
        }

        @WorkerThread
        public void onOpen(SQLiteDatabase sQLiteDatabase) {
            if (VERSION.SDK_INT < 15) {
                Cursor rawQuery = sQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
                try {
                    rawQuery.moveToFirst();
                } finally {
                    rawQuery.close();
                }
            }
            zzasu.zza(this.zzbrF.zzJt(), sQLiteDatabase, "messages", "create table if not exists messages ( type INTEGER NOT NULL, entry BLOB NOT NULL)", "type,entry", null);
        }

        @WorkerThread
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    zzatg(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
        this.zzbrD = new zza(this, getContext(), zznV());
    }

    @WorkerThread
    @TargetApi(11)
    private boolean zza(int i, byte[] bArr) {
        zzJe();
        zzmq();
        if (this.zzbrE) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShareConstants.MEDIA_TYPE, Integer.valueOf(i));
        contentValues.put("entry", bArr);
        zzJv().zzKt();
        int i2 = 0;
        int i3 = 5;
        while (i2 < 5) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                sQLiteDatabase = getWritableDatabase();
                if (sQLiteDatabase == null) {
                    this.zzbrE = true;
                    if (sQLiteDatabase != null) {
                        sQLiteDatabase.close();
                    }
                    return false;
                }
                sQLiteDatabase.beginTransaction();
                long j = 0;
                Cursor rawQuery = sQLiteDatabase.rawQuery("select count(1) from messages", null);
                if (rawQuery != null && rawQuery.moveToFirst()) {
                    j = rawQuery.getLong(0);
                }
                if (j >= 100000) {
                    zzJt().zzLa().log("Data loss, local db full");
                    j = (100000 - j) + 1;
                    long delete = (long) sQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[]{Long.toString(j)});
                    if (delete != j) {
                        zzJt().zzLa().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(j), Long.valueOf(delete), Long.valueOf(j - delete));
                    }
                }
                sQLiteDatabase.insertOrThrow("messages", null, contentValues);
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                return true;
            } catch (SQLiteFullException e) {
                zzJt().zzLa().zzj("Error writing entry to local database", e);
                this.zzbrE = true;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                i2++;
            } catch (SQLiteException e2) {
                if (VERSION.SDK_INT < 11 || !(e2 instanceof SQLiteDatabaseLockedException)) {
                    if (sQLiteDatabase != null) {
                        if (sQLiteDatabase.inTransaction()) {
                            sQLiteDatabase.endTransaction();
                        }
                    }
                    zzJt().zzLa().zzj("Error writing entry to local database", e2);
                    this.zzbrE = true;
                } else {
                    SystemClock.sleep((long) i3);
                    i3 += 20;
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                i2++;
            } catch (Throwable th) {
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
            }
        }
        zzJt().zzLc().log("Failed to write entry to local database");
        return false;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    SQLiteDatabase getWritableDatabase() {
        if (VERSION.SDK_INT < 11 || this.zzbrE) {
            return null;
        }
        SQLiteDatabase writableDatabase = this.zzbrD.getWritableDatabase();
        if (writableDatabase != null) {
            return writableDatabase;
        }
        this.zzbrE = true;
        return null;
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    boolean zzKP() {
        return getContext().getDatabasePath(zznV()).exists();
    }

    public boolean zza(zzatb com_google_android_gms_internal_zzatb) {
        if (VERSION.SDK_INT < 11) {
            return false;
        }
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzatb.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= AccessibilityNodeInfoCompat.ACTION_SET_SELECTION) {
            return zza(0, marshall);
        }
        zzJt().zzLc().log("Event is too long for local database. Sending event directly to service");
        return false;
    }

    public boolean zza(zzaub com_google_android_gms_internal_zzaub) {
        if (VERSION.SDK_INT < 11) {
            return false;
        }
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzaub.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= AccessibilityNodeInfoCompat.ACTION_SET_SELECTION) {
            return zza(1, marshall);
        }
        zzJt().zzLc().log("User property too long for local database. Sending directly to service");
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.TargetApi(11)
    public java.util.List<com.google.android.gms.common.internal.safeparcel.zza> zzls(int r14) {
        /*
        r13 = this;
        r13.zzmq();
        r13.zzJe();
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 11;
        if (r0 >= r1) goto L_0x000e;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        return r0;
    L_0x000e:
        r0 = r13.zzbrE;
        if (r0 == 0) goto L_0x0014;
    L_0x0012:
        r0 = 0;
        goto L_0x000d;
    L_0x0014:
        r10 = new java.util.ArrayList;
        r10.<init>();
        r0 = r13.zzKP();
        if (r0 != 0) goto L_0x0021;
    L_0x001f:
        r0 = r10;
        goto L_0x000d;
    L_0x0021:
        r9 = 5;
        r0 = 0;
        r11 = r0;
    L_0x0024:
        r0 = 5;
        if (r11 >= r0) goto L_0x018e;
    L_0x0027:
        r1 = 0;
        r0 = r13.getWritableDatabase();	 Catch:{ SQLiteFullException -> 0x01a3, SQLiteException -> 0x01a0 }
        if (r0 != 0) goto L_0x0038;
    L_0x002e:
        r1 = 1;
        r13.zzbrE = r1;	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        if (r0 == 0) goto L_0x0036;
    L_0x0033:
        r0.close();
    L_0x0036:
        r0 = 0;
        goto L_0x000d;
    L_0x0038:
        r0.beginTransaction();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r1 = "messages";
        r2 = 3;
        r2 = new java.lang.String[r2];	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r3 = 0;
        r4 = "rowid";
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r3 = 1;
        r4 = "type";
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r3 = 2;
        r4 = "entry";
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = "rowid asc";
        r8 = java.lang.Integer.toString(r14);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r6 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r2 = -1;
    L_0x005f:
        r1 = r6.moveToNext();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        if (r1 == 0) goto L_0x013a;
    L_0x0065:
        r1 = 0;
        r4 = r6.getLong(r1);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r1 = 1;
        r1 = r6.getInt(r1);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r2 = 2;
        r3 = r6.getBlob(r2);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        if (r1 != 0) goto L_0x00cd;
    L_0x0076:
        r2 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r1 = 0;
        r7 = r3.length;	 Catch:{ zza -> 0x0095 }
        r2.unmarshall(r3, r1, r7);	 Catch:{ zza -> 0x0095 }
        r1 = 0;
        r2.setDataPosition(r1);	 Catch:{ zza -> 0x0095 }
        r1 = com.google.android.gms.internal.zzatb.CREATOR;	 Catch:{ zza -> 0x0095 }
        r1 = r1.createFromParcel(r2);	 Catch:{ zza -> 0x0095 }
        r1 = (com.google.android.gms.internal.zzatb) r1;	 Catch:{ zza -> 0x0095 }
        r2.recycle();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        if (r1 == 0) goto L_0x0093;
    L_0x0090:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
    L_0x0093:
        r2 = r4;
        goto L_0x005f;
    L_0x0095:
        r1 = move-exception;
        r1 = r13.zzJt();	 Catch:{ all -> 0x00a8 }
        r1 = r1.zzLa();	 Catch:{ all -> 0x00a8 }
        r3 = "Failed to load event from local database";
        r1.log(r3);	 Catch:{ all -> 0x00a8 }
        r2.recycle();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r2 = r4;
        goto L_0x005f;
    L_0x00a8:
        r1 = move-exception;
        r2.recycle();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        throw r1;	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
    L_0x00ad:
        r1 = move-exception;
        r12 = r1;
        r1 = r0;
        r0 = r12;
    L_0x00b1:
        r2 = r13.zzJt();	 Catch:{ all -> 0x019e }
        r2 = r2.zzLa();	 Catch:{ all -> 0x019e }
        r3 = "Error reading entries from local database";
        r2.zzj(r3, r0);	 Catch:{ all -> 0x019e }
        r0 = 1;
        r13.zzbrE = r0;	 Catch:{ all -> 0x019e }
        if (r1 == 0) goto L_0x01a6;
    L_0x00c3:
        r1.close();
        r0 = r9;
    L_0x00c7:
        r1 = r11 + 1;
        r11 = r1;
        r9 = r0;
        goto L_0x0024;
    L_0x00cd:
        r2 = 1;
        if (r1 != r2) goto L_0x012b;
    L_0x00d0:
        r7 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r2 = 0;
        r1 = 0;
        r8 = r3.length;	 Catch:{ zza -> 0x0109 }
        r7.unmarshall(r3, r1, r8);	 Catch:{ zza -> 0x0109 }
        r1 = 0;
        r7.setDataPosition(r1);	 Catch:{ zza -> 0x0109 }
        r1 = com.google.android.gms.internal.zzaub.CREATOR;	 Catch:{ zza -> 0x0109 }
        r1 = r1.createFromParcel(r7);	 Catch:{ zza -> 0x0109 }
        r1 = (com.google.android.gms.internal.zzaub) r1;	 Catch:{ zza -> 0x0109 }
        r7.recycle();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
    L_0x00e9:
        if (r1 == 0) goto L_0x0093;
    L_0x00eb:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        goto L_0x0093;
    L_0x00ef:
        r1 = move-exception;
        r12 = r1;
        r1 = r0;
        r0 = r12;
    L_0x00f3:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x019e }
        r3 = 11;
        if (r2 < r3) goto L_0x0170;
    L_0x00f9:
        r2 = r0 instanceof android.database.sqlite.SQLiteDatabaseLockedException;	 Catch:{ all -> 0x019e }
        if (r2 == 0) goto L_0x0170;
    L_0x00fd:
        r2 = (long) r9;	 Catch:{ all -> 0x019e }
        android.os.SystemClock.sleep(r2);	 Catch:{ all -> 0x019e }
        r0 = r9 + 20;
    L_0x0103:
        if (r1 == 0) goto L_0x00c7;
    L_0x0105:
        r1.close();
        goto L_0x00c7;
    L_0x0109:
        r1 = move-exception;
        r1 = r13.zzJt();	 Catch:{ all -> 0x011c }
        r1 = r1.zzLa();	 Catch:{ all -> 0x011c }
        r3 = "Failed to load user property from local database";
        r1.log(r3);	 Catch:{ all -> 0x011c }
        r7.recycle();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r1 = r2;
        goto L_0x00e9;
    L_0x011c:
        r1 = move-exception;
        r7.recycle();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        throw r1;	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
    L_0x0121:
        r1 = move-exception;
        r12 = r1;
        r1 = r0;
        r0 = r12;
    L_0x0125:
        if (r1 == 0) goto L_0x012a;
    L_0x0127:
        r1.close();
    L_0x012a:
        throw r0;
    L_0x012b:
        r1 = r13.zzJt();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r1 = r1.zzLa();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r2 = "Unknown record type in local database";
        r1.log(r2);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        goto L_0x0093;
    L_0x013a:
        r6.close();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r1 = "messages";
        r4 = "rowid <= ?";
        r5 = 1;
        r5 = new java.lang.String[r5];	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r6 = 0;
        r2 = java.lang.Long.toString(r2);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r5[r6] = r2;	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r1 = r0.delete(r1, r4, r5);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r2 = r10.size();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        if (r1 >= r2) goto L_0x0162;
    L_0x0155:
        r1 = r13.zzJt();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r1 = r1.zzLa();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r2 = "Fewer entries removed from local database than expected";
        r1.log(r2);	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
    L_0x0162:
        r0.setTransactionSuccessful();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        r0.endTransaction();	 Catch:{ SQLiteFullException -> 0x00ad, SQLiteException -> 0x00ef, all -> 0x0121 }
        if (r0 == 0) goto L_0x016d;
    L_0x016a:
        r0.close();
    L_0x016d:
        r0 = r10;
        goto L_0x000d;
    L_0x0170:
        if (r1 == 0) goto L_0x017b;
    L_0x0172:
        r2 = r1.inTransaction();	 Catch:{ all -> 0x019e }
        if (r2 == 0) goto L_0x017b;
    L_0x0178:
        r1.endTransaction();	 Catch:{ all -> 0x019e }
    L_0x017b:
        r2 = r13.zzJt();	 Catch:{ all -> 0x019e }
        r2 = r2.zzLa();	 Catch:{ all -> 0x019e }
        r3 = "Error reading entries from local database";
        r2.zzj(r3, r0);	 Catch:{ all -> 0x019e }
        r0 = 1;
        r13.zzbrE = r0;	 Catch:{ all -> 0x019e }
        r0 = r9;
        goto L_0x0103;
    L_0x018e:
        r0 = r13.zzJt();
        r0 = r0.zzLc();
        r1 = "Failed to read events from database in reasonable time";
        r0.log(r1);
        r0 = 0;
        goto L_0x000d;
    L_0x019e:
        r0 = move-exception;
        goto L_0x0125;
    L_0x01a0:
        r0 = move-exception;
        goto L_0x00f3;
    L_0x01a3:
        r0 = move-exception;
        goto L_0x00b1;
    L_0x01a6:
        r0 = r9;
        goto L_0x00c7;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzatg.zzls(int):java.util.List<com.google.android.gms.common.internal.safeparcel.zza>");
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
    }

    String zznV() {
        return zzJv().zzKj();
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }
}
