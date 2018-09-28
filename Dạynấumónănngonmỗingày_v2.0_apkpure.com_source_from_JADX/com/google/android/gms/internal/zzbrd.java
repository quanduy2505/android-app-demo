package com.google.android.gms.internal;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.dynamic.zzd;
import me.wangyuwei.loadingview.C0801R;
import rx.internal.operators.OnSubscribeConcatMap;

public interface zzbrd extends IInterface {

    public static abstract class zza extends Binder implements zzbrd {

        private static class zza implements zzbrd {
            private IBinder zzrp;

            zza(IBinder iBinder) {
                this.zzrp = iBinder;
            }

            public IBinder asBinder() {
                return this.zzrp;
            }

            public String zzA(Uri uri) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrp.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                    String readString = obtain2.readString();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String zzB(Uri uri) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.zzrp.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    String readString = obtain2.readString();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    this.zzrp.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbrc zzfL = com.google.android.gms.internal.zzbrc.zza.zzfL(obtain2.readStrongBinder());
                    return zzfL;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    obtain.writeLong(j);
                    this.zzrp.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbrc zzfL = com.google.android.gms.internal.zzbrc.zza.zzfL(obtain2.readStrongBinder());
                    return zzfL;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, zzd com_google_android_gms_dynamic_zzd2) throws RemoteException {
                IBinder iBinder = null;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (com_google_android_gms_dynamic_zzd2 != null) {
                        iBinder = com_google_android_gms_dynamic_zzd2.asBinder();
                    }
                    obtain.writeStrongBinder(iBinder);
                    this.zzrp.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbrc zzfL = com.google.android.gms.internal.zzbrc.zza.zzfL(obtain2.readStrongBinder());
                    return zzfL;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, zzd com_google_android_gms_dynamic_zzd2, String str) throws RemoteException {
                IBinder iBinder = null;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    if (com_google_android_gms_dynamic_zzd2 != null) {
                        iBinder = com_google_android_gms_dynamic_zzd2.asBinder();
                    }
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeString(str);
                    this.zzrp.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbrc zzfL = com.google.android.gms.internal.zzbrc.zza.zzfL(obtain2.readStrongBinder());
                    return zzfL;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    obtain.writeString(str);
                    this.zzrp.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbrc zzfL = com.google.android.gms.internal.zzbrc.zza.zzfL(obtain2.readStrongBinder());
                    return zzfL;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, String str, zzd com_google_android_gms_dynamic_zzd2, long j, int i, boolean z) throws RemoteException {
                IBinder iBinder = null;
                int i2 = 1;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    obtain.writeString(str);
                    if (com_google_android_gms_dynamic_zzd2 != null) {
                        iBinder = com_google_android_gms_dynamic_zzd2.asBinder();
                    }
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeLong(j);
                    obtain.writeInt(i);
                    if (!z) {
                        i2 = 0;
                    }
                    obtain.writeInt(i2);
                    this.zzrp.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbrc zzfL = com.google.android.gms.internal.zzbrc.zza.zzfL(obtain2.readStrongBinder());
                    return zzfL;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String zzabp() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    this.zzrp.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    String readString = obtain2.readString();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbrc zzb(Uri uri, zzd com_google_android_gms_dynamic_zzd) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    this.zzrp.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbrc zzfL = com.google.android.gms.internal.zzbrc.zza.zzfL(obtain2.readStrongBinder());
                    return zzfL;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbrc zzb(Uri uri, zzd com_google_android_gms_dynamic_zzd, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    obtain.writeString(str);
                    this.zzrp.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbrc zzfL = com.google.android.gms.internal.zzbrc.zza.zzfL(obtain2.readStrongBinder());
                    return zzfL;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzbrc zzc(Uri uri, zzd com_google_android_gms_dynamic_zzd, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequestFactory");
                    if (uri != null) {
                        obtain.writeInt(1);
                        uri.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(com_google_android_gms_dynamic_zzd != null ? com_google_android_gms_dynamic_zzd.asBinder() : null);
                    obtain.writeString(str);
                    this.zzrp.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    zzbrc zzfL = com.google.android.gms.internal.zzbrc.zza.zzfL(obtain2.readStrongBinder());
                    return zzfL;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzbrd zzfM(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.firebase.storage.network.INetworkRequestFactory");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzbrd)) ? new zza(iBinder) : (zzbrd) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            zzbrc zza;
            String zzabp;
            switch (i) {
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zza = zza(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null, com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza != null ? zza.asBinder() : null);
                    return true;
                case OnSubscribeConcatMap.END /*2*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zza = zzb(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null, com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza != null ? zza.asBinder() : null);
                    return true;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zza = zza(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null, com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), parcel.readLong());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza != null ? zza.asBinder() : null);
                    return true;
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zza = zza(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null, com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza != null ? zza.asBinder() : null);
                    return true;
                case ConnectionResult.INVALID_ACCOUNT /*5*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zza = zza(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null, com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), parcel.readString(), com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), parcel.readLong(), parcel.readInt(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza != null ? zza.asBinder() : null);
                    return true;
                case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zza = zzb(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null, com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza != null ? zza.asBinder() : null);
                    return true;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zza = zzc(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null, com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza != null ? zza.asBinder() : null);
                    return true;
                case ConnectionResult.INTERNAL_ERROR /*8*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zza = zza(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null, com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza != null ? zza.asBinder() : null);
                    return true;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zza = zza(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null, com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()), com.google.android.gms.dynamic.zzd.zza.zzcd(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(zza != null ? zza.asBinder() : null);
                    return true;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zzabp = zzabp();
                    parcel2.writeNoException();
                    parcel2.writeString(zzabp);
                    return true;
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zzabp = zzA(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeString(zzabp);
                    return true;
                case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequestFactory");
                    zzabp = zzB(parcel.readInt() != 0 ? (Uri) Uri.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    parcel2.writeString(zzabp);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.firebase.storage.network.INetworkRequestFactory");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    String zzA(Uri uri) throws RemoteException;

    String zzB(Uri uri) throws RemoteException;

    zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd) throws RemoteException;

    zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, long j) throws RemoteException;

    zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, zzd com_google_android_gms_dynamic_zzd2) throws RemoteException;

    zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, zzd com_google_android_gms_dynamic_zzd2, String str) throws RemoteException;

    zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, String str) throws RemoteException;

    zzbrc zza(Uri uri, zzd com_google_android_gms_dynamic_zzd, String str, zzd com_google_android_gms_dynamic_zzd2, long j, int i, boolean z) throws RemoteException;

    String zzabp() throws RemoteException;

    zzbrc zzb(Uri uri, zzd com_google_android_gms_dynamic_zzd) throws RemoteException;

    zzbrc zzb(Uri uri, zzd com_google_android_gms_dynamic_zzd, String str) throws RemoteException;

    zzbrc zzc(Uri uri, zzd com_google_android_gms_dynamic_zzd, String str) throws RemoteException;
}
