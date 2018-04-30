package com.google.android.systemui.elmyra.actions;

import java.util.NoSuchElementException;
import com.google.android.systemui.elmyra.IElmyraServiceListener;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.util.Log;
import android.content.ServiceConnection;
import android.content.ComponentName;
import com.google.android.systemui.elmyra.ElmyraServiceProxy;
import android.content.Intent;
import android.os.Binder;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import android.os.IBinder;
import com.google.android.systemui.elmyra.IElmyraServiceSettingsListener;
import com.google.android.systemui.elmyra.IElmyraService;
import android.os.IBinder.DeathRecipient;

public class ServiceAction extends Action implements IBinder.DeathRecipient
{
    private IElmyraService mElmyraService;
    private final ElmyraServiceConnection mElmyraServiceConnection;
    private final ElmyraServiceListener mElmyraServiceListener;
    protected IElmyraServiceSettingsListener mElmyraServiceSettingsListener;
    protected final LaunchOpa mLaunchOpa;
    private final IBinder mToken;
    
    public ServiceAction(final Context context, final LaunchOpa mLaunchOpa) {
        super(context, null);
        this.mToken = (IBinder)new Binder();
        this.mElmyraServiceConnection = new ElmyraServiceConnection((ElmyraServiceConnection)null);
        this.mElmyraServiceListener = new ElmyraServiceListener((ElmyraServiceListener)null);
        this.mLaunchOpa = mLaunchOpa;
        final Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, (Class)ElmyraServiceProxy.class));
        context.startService(intent);
        this.bindToElmyraServiceProxy();
    }
    
    private void bindToElmyraServiceProxy() {
        if (this.mElmyraService != null) {
            return;
        }
        try {
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName(this.getContext(), (Class)ElmyraServiceProxy.class));
            this.getContext().bindService(intent, (ServiceConnection)this.mElmyraServiceConnection, 1);
        }
        catch (SecurityException ex) {
            Log.e("Elmyra/ServiceAction", "Unable to bind to ElmyraServiceProxy", (Throwable)ex);
        }
    }
    
    public void binderDied() {
        Log.w("Elmyra/ServiceAction", "Binder died");
        this.mElmyraServiceSettingsListener = null;
        this.notifyListener();
    }
    
    @Override
    public boolean isAvailable() {
        return this.mElmyraServiceSettingsListener != null;
    }
    
    @Override
    public void onProgress(final float n, final int n2) {
        this.updateFeedbackEffects(n, n2);
        if (this.mElmyraServiceSettingsListener == null) {
            return;
        }
        try {
            this.mElmyraServiceSettingsListener.onGestureProgress(n, n2);
        }
        catch (RemoteException ex) {
            Log.e("Elmyra/ServiceAction", "Unable to send progress, setting listener to null", (Throwable)ex);
            this.mElmyraServiceSettingsListener = null;
            this.notifyListener();
        }
    }
    
    @Override
    public void onTrigger() {
        this.triggerFeedbackEffects();
        if (this.mElmyraServiceSettingsListener == null) {
            return;
        }
        try {
            this.mElmyraServiceSettingsListener.onGestureDetected();
        } catch (DeadObjectException ex2) {
            Log.e("Elmyra/ServiceAction", "Settings crashed or closed without unregistering, setting listener to null", (Throwable)ex2);
            this.mElmyraServiceSettingsListener = null;
            this.notifyListener();
        } catch (RemoteException ex) {
            Log.e("Elmyra/ServiceAction", "Unable to send onGestureDetected, setting listener to null", (Throwable)ex);
            this.mElmyraServiceSettingsListener = null;
            this.notifyListener();
        }
    }
    
    @Override
    protected void triggerFeedbackEffects() {
        super.triggerFeedbackEffects();
        this.mLaunchOpa.triggerFeedbackEffects();
    }
    
    @Override
    protected void updateFeedbackEffects(final float n, final int n2) {
        super.updateFeedbackEffects(n, n2);
        this.mLaunchOpa.updateFeedbackEffects(n, n2);
    }
    
    private class ElmyraServiceConnection implements ServiceConnection
    {
        public ElmyraServiceConnection(ElmyraServiceConnection elmyraServiceConnection) {

        }

        public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
            ServiceAction.this.mElmyraService = IElmyraService.Stub.asInterface(binder);
            try {
                ServiceAction.this.mElmyraService.registerListener(ServiceAction.this.mToken, (IBinder)ServiceAction.this.mElmyraServiceListener);
            }
            catch (RemoteException ex) {
                Log.e("Elmyra/ServiceAction", "Error registering listener", (Throwable)ex);
            }
        }
        
        public void onServiceDisconnected(final ComponentName componentName) {
            ServiceAction.this.mElmyraService = null;
        }
    }
    
    private class ElmyraServiceListener extends IElmyraServiceListener.Stub
    {
        public ElmyraServiceListener(ElmyraServiceListener elmyraServiceListener) {

        }

        public void launchAssistant() {
            if (ServiceAction.this.mLaunchOpa.isAvailable()) {
                ServiceAction.this.mLaunchOpa.launchOpa();
            }
        }
        
        public void setListener(final IBinder binder, final IBinder binder2) {
            if (binder2 == null && ServiceAction.this.mElmyraServiceSettingsListener == null) {
                return;
            }
            final IElmyraServiceSettingsListener interface1 = IElmyraServiceSettingsListener.Stub.asInterface(binder2);
            if (interface1 != ServiceAction.this.mElmyraServiceSettingsListener) {
                ServiceAction.this.mElmyraServiceSettingsListener = interface1;
                ServiceAction.this.notifyListener();
            }
            if (binder == null) {
                return;
            }
            Label_0066: {
                if (binder2 == null) {
                    break Label_0066;
                }
                while (true) {
                    try {
                        binder.unlinkToDeath(ServiceAction.this, 0);
                        return;
                    } catch (NoSuchElementException ex2) {}
                }
            }
        }
    }
}
