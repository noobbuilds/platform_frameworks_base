package com.google.android.systemui.elmyra;

import android.os.Parcel;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IElmyraServiceListener extends IInterface
{
    void launchAssistant() throws RemoteException;
    
    void setListener(final IBinder p0, final IBinder p1) throws RemoteException;
    
    public abstract static class Stub extends Binder implements IElmyraServiceListener
    {
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.android.systemui.elmyra.IElmyraServiceListener");
        }
        
        public static IElmyraServiceListener asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.android.systemui.elmyra.IElmyraServiceListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof IElmyraServiceListener) {
                return (IElmyraServiceListener)queryLocalInterface;
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
                    parcel2.writeString("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    this.setListener(parcel.readStrongBinder(), parcel.readStrongBinder());
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    this.launchAssistant();
                    return true;
                }
            }
        }
        
        private static class Proxy implements IElmyraServiceListener
        {
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            @Override
            public void launchAssistant() throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    this.mRemote.transact(2, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
            
            @Override
            public void setListener(final IBinder binder, final IBinder binder2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraServiceListener");
                    obtain.writeStrongBinder(binder);
                    obtain.writeStrongBinder(binder2);
                    this.mRemote.transact(1, obtain, (Parcel)null, 1);
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}
