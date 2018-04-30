package com.google.android.systemui.elmyra.gates;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.content.Context;

import com.google.android.systemui.elmyra.sensors.GestureSensor;

public abstract class Gate
{
    private boolean mActive;
    private final Context mContext;
    @Nullable
    private Listener mListener;
    private final Handler mNotifyHandler;
    
    public Gate(final Context mContext) {
        this.mContext = mContext;
        this.mNotifyHandler = new Handler(mContext.getMainLooper());
        this.mActive = false;
    }
    
    public void activate() {
        if (!this.isActive()) {
            this.mActive = true;
            this.onActivate();
        }
    }
    
    public void deactivate() {
        if (this.isActive()) {
            this.mActive = false;
            this.onDeactivate();
        }
    }
    
    protected Context getContext() {
        return this.mContext;
    }
    
    public final boolean isActive() {
        return this.mActive;
    }
    
    protected abstract boolean isBlocked();
    
    public final boolean isBlocking() {
        return this.isActive() && this.isBlocked();
    }
    
    protected void notifyListener() {
        if (this.isActive() && this.mListener != null) {
       //     this.mNotifyHandler.post((Runnable)new -$Lambda$gEUpfLPh0uNo7ifmfUWGSpFHiQQ((byte)2, this));
        }
    }
    
    protected abstract void onActivate();
    
    protected abstract void onDeactivate();
    
    public void setListener(@Nullable final Listener mListener) {
        this.mListener = mListener;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    public interface Listener
    {
        void onGestureDetected(GestureSensor gestureSensor);

        void onGestureProgress(GestureSensor gestureSensor, float n, int n2);

        void onGateChanged(final Gate p0);
    }
}
