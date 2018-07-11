package com.google.android.systemui.elmyra;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IElmyraServiceListener extends IInterface {

    void launchAssistant() throws RemoteException;

    void setListener(IBinder iBinder, IBinder iBinder2) throws RemoteException;

    public static abstract class Stub extends Binder implements IElmyraServiceListener {

        public Stub() {
            attachInterface(this, "com.google.android.systemui.elmyra.IElmyraServiceListener");
        }

        public static IElmyraServiceListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.systemui.elmyra.IElmyraServiceListener");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IElmyraServiceListener)) ? new Proxy(iBinder) : (IElmyraServiceListener) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    setListener(parcel.readStrongBinder(), parcel.readStrongBinder());
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    launchAssistant();
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IElmyraServiceListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void launchAssistant() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    this.mRemote.transact(2, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void setListener(IBinder iBinder, IBinder iBinder2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeStrongBinder(iBinder2);
                    this.mRemote.transact(1, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}

