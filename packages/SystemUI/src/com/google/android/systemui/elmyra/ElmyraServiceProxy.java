package com.google.android.systemui.elmyra;

import android.annotation.SuppressLint;
import android.os.IBinder.DeathRecipient;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import android.app.Service;

public class ElmyraServiceProxy extends Service
{
    private final IElmyraService.Stub mBinder;
    private List<ElmyraServiceListener> mElmyraServiceListeners;
    
    public ElmyraServiceProxy() {
        this.mElmyraServiceListeners = new ArrayList<ElmyraServiceListener>();
        this.mBinder = new IElmyraService.Stub() {
            public void launchAssistant() {
                ElmyraServiceProxy.this.checkPermission("com.google.android.elmyra.permission.CONFIGURE_ASSIST_GESTURE");
                try {
                    for (int i = ElmyraServiceProxy.this.mElmyraServiceListeners.size() - 1; i >= 0; --i) {
                        final IElmyraServiceListener listener = ((ElmyraServiceListener)ElmyraServiceProxy.this.mElmyraServiceListeners.get(i)).getListener();
                        if (listener == null) {
                            ElmyraServiceProxy.this.mElmyraServiceListeners.remove(i);
                        }
                        else {
                            listener.launchAssistant();
                        }
                    }
                }
                catch (RemoteException ex) {
                    Log.e("Elmyra/ElmyraServiceProxy", "Error launching assistant", (Throwable)ex);
                }
            }
            
            public void registerListener(final IBinder binder, final IBinder binder2) {
                ElmyraServiceProxy.this.checkPermission("com.google.android.elmyra.permission.CONFIGURE_ASSIST_GESTURE");
                if (binder == null) {
                    Log.w("Elmyra/ElmyraServiceProxy", "token should not be null");
                    return;
                }
                if (binder2 == null) {
                    for (int i = 0; i < ElmyraServiceProxy.this.mElmyraServiceListeners.size(); ++i) {
                        if (binder.equals(((ElmyraServiceListener)ElmyraServiceProxy.this.mElmyraServiceListeners.get(i)).getToken())) {
                            ((ElmyraServiceListener)ElmyraServiceProxy.this.mElmyraServiceListeners.get(i)).unlinkToDeath();
                            ElmyraServiceProxy.this.mElmyraServiceListeners.remove(i);
                            break;
                        }
                    }
                }
                else {
                    ElmyraServiceProxy.this.mElmyraServiceListeners.add(new ElmyraServiceListener(binder, IElmyraServiceListener.Stub.asInterface(binder2)));
                }
            }
            
            public void registerSettingsListener(final IBinder binder, final IBinder binder2) {
                ElmyraServiceProxy.this.checkPermission("com.google.android.elmyra.permission.CONFIGURE_ASSIST_GESTURE");
                try {
                    for (int i = ElmyraServiceProxy.this.mElmyraServiceListeners.size() - 1; i >= 0; --i) {
                        final IElmyraServiceListener listener = ((ElmyraServiceListener)ElmyraServiceProxy.this.mElmyraServiceListeners.get(i)).getListener();
                        if (listener == null) {
                            ElmyraServiceProxy.this.mElmyraServiceListeners.remove(i);
                        }
                        else {
                            listener.setListener(binder, binder2);
                        }
                    }
                }
                catch (RemoteException ex) {
                    Log.e("Elmyra/ElmyraServiceProxy", "Action isn't connected", (Throwable)ex);
                }
            }
        };
    }
    
    private void checkPermission(final String s) {
        this.enforceCallingOrSelfPermission(s, "Must have " + s + " permission");
    }
    
    public IBinder onBind(final Intent intent) {
        return (IBinder)this.mBinder;
    }
    
    @SuppressLint("WrongConstant")
    public int onStartCommand(final Intent intent, final int n, final int n2) {
        return 0;
    }
    
    private class ElmyraServiceListener implements IBinder.DeathRecipient
    {
        private IElmyraServiceListener mListener;
        private IBinder mToken;
        
        ElmyraServiceListener(final IBinder mToken, final IElmyraServiceListener mListener) {
            this.mToken = mToken;
            this.mListener = mListener;
            this.linkToDeath();
        }
        
        private void linkToDeath() {
            if (this.mToken == null) {
                return;
            }
            try {
                this.mToken.linkToDeath((IBinder.DeathRecipient)this, 0);
            }
            catch (RemoteException ex) {
                Log.e("Elmyra/ElmyraServiceProxy", "Unable to linkToDeath", (Throwable)ex);
            }
        }
        
        public void binderDied() {
            Log.w("Elmyra/ElmyraServiceProxy", "ElmyraServiceListener binder died");
            this.mToken = null;
            this.mListener = null;
        }
        
        public IElmyraServiceListener getListener() {
            return this.mListener;
        }
        
        public IBinder getToken() {
            return this.mToken;
        }
        
        public void unlinkToDeath() {
            if (this.mToken != null) {
                this.mToken.unlinkToDeath((IBinder.DeathRecipient)this, 0);
            }
        }
    }
}
