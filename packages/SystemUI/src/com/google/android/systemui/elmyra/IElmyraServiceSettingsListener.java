package com.google.android.systemui.elmyra;

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IElmyraServiceSettingsListener extends IInterface
{
    void onGestureDetected() throws RemoteException;
    
    void onGestureProgress(final float p0, final int p1) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IElmyraServiceSettingsListener
    {
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
        }
        
        public static IElmyraServiceSettingsListener asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof IElmyraServiceSettingsListener) {
                return (IElmyraServiceSettingsListener)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
                    this.onGestureProgress(parcel.readFloat(), parcel.readInt());
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
                    this.onGestureDetected();
                    return true;
                }
            }
        }
        
        private static class Proxy implements IElmyraServiceSettingsListener
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void onGestureDetected() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
                    this.mRemote.transact(2, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void onGestureProgress(final float n, final int n2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
                    obtain.writeFloat(n);
                    obtain.writeInt(n2);
                    this.mRemote.transact(1, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
