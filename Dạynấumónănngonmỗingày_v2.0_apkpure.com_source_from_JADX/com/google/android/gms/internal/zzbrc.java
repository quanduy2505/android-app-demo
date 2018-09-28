package com.google.android.gms.internal;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.dynamic.zzd;
import me.wangyuwei.loadingview.C0801R;
import rx.internal.operators.OnSubscribeConcatMap;

public interface zzbrc extends IInterface {

    public static abstract class zza extends Binder implements zzbrc {

        private static class zza implements zzbrc {
            private IBinder zzrp;

            zza(IBinder iBinder) {
                this.zzrp = iBinder;
            }

            public IBinder asBinder() {
                return this.zzrp;
            }

            public int getResultCode() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void reset() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zzabh() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzabi() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzcd = com.google.android.gms.dynamic.zzd.zza.zzcd(obtain2.readStrongBinder());
                    return zzcd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzabj() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzcd = com.google.android.gms.dynamic.zzd.zza.zzcd(obtain2.readStrongBinder());
                    return zzcd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String zzabk() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    String readString = obtain2.readString();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzabl() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzcd = com.google.android.gms.dynamic.zzd.zza.zzcd(obtain2.readStrongBinder());
                    return zzcd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public zzd zzabm() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                    zzd zzcd = com.google.android.gms.dynamic.zzd.zza.zzcd(obtain2.readStrongBinder());
                    return zzcd;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean zzabn() throws RemoteException {
                boolean z = false;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    obtain2.recycle();
                    obtain.recycle();
                    return z;
                } catch (Throwable th) {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int zzabo() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    this.zzrp.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                    int readInt = obtain2.readInt();
                    return readInt;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zzay(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.zzrp.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zzjN(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    obtain.writeString(str);
                    this.zzrp.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void zzjO(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    obtain.writeString(str);
                    this.zzrp.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String zzjP(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.firebase.storage.network.INetworkRequest");
                    obtain.writeString(str);
                    this.zzrp.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    String readString = obtain2.readString();
                    return readString;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static zzbrc zzfL(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.firebase.storage.network.INetworkRequest");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof zzbrc)) ? new zza(iBinder) : (zzbrc) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            IBinder iBinder = null;
            String zzjP;
            zzd zzabi;
            int resultCode;
            switch (i) {
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzjN(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case OnSubscribeConcatMap.END /*2*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    reset();
                    parcel2.writeNoException();
                    return true;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzjO(parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzabh();
                    parcel2.writeNoException();
                    return true;
                case ConnectionResult.INVALID_ACCOUNT /*5*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzay(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzjP = zzjP(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeString(zzjP);
                    return true;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzabi = zzabi();
                    parcel2.writeNoException();
                    if (zzabi != null) {
                        iBinder = zzabi.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case ConnectionResult.INTERNAL_ERROR /*8*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzabi = zzabj();
                    parcel2.writeNoException();
                    if (zzabi != null) {
                        iBinder = zzabi.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzjP = zzabk();
                    parcel2.writeNoException();
                    parcel2.writeString(zzjP);
                    return true;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzabi = zzabl();
                    parcel2.writeNoException();
                    if (zzabi != null) {
                        iBinder = zzabi.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    zzabi = zzabm();
                    parcel2.writeNoException();
                    if (zzabi != null) {
                        iBinder = zzabi.asBinder();
                    }
                    parcel2.writeStrongBinder(iBinder);
                    return true;
                case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    resultCode = getResultCode();
                    parcel2.writeNoException();
                    parcel2.writeInt(resultCode);
                    return true;
                case ConnectionResult.CANCELED /*13*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    boolean zzabn = zzabn();
                    parcel2.writeNoException();
                    parcel2.writeInt(zzabn ? 1 : 0);
                    return true;
                case ConnectionResult.TIMEOUT /*14*/:
                    parcel.enforceInterface("com.google.firebase.storage.network.INetworkRequest");
                    resultCode = zzabo();
                    parcel2.writeNoException();
                    parcel2.writeInt(resultCode);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.firebase.storage.network.INetworkRequest");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    int getResultCode() throws RemoteException;

    void reset() throws RemoteException;

    void zzabh() throws RemoteException;

    zzd zzabi() throws RemoteException;

    zzd zzabj() throws RemoteException;

    String zzabk() throws RemoteException;

    zzd zzabl() throws RemoteException;

    zzd zzabm() throws RemoteException;

    boolean zzabn() throws RemoteException;

    int zzabo() throws RemoteException;

    void zzay(String str, String str2) throws RemoteException;

    void zzjN(String str) throws RemoteException;

    void zzjO(String str) throws RemoteException;

    String zzjP(String str) throws RemoteException;
}
