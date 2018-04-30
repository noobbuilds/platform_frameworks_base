package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.os.Handler;

abstract class TransientGate extends Gate
{
    private final long mBlockDuration;
    private boolean mIsBlocking;
    private final Runnable mResetGate;
    private final Handler mResetGateHandler;
    
    TransientGate(final Context context, final long mBlockDuration) {
        super(context);
        this.mResetGate = new Runnable() {
            @Override
            public void run() {
                TransientGate.this.mIsBlocking = false;
                TransientGate.this.notifyListener();
            }
        };
        this.mBlockDuration = mBlockDuration;
        this.mResetGateHandler = new Handler(context.getMainLooper());
    }
    
    protected void block() {
        this.mIsBlocking = true;
        this.notifyListener();
        this.mResetGateHandler.removeCallbacks(this.mResetGate);
        this.mResetGateHandler.postDelayed(this.mResetGate, this.mBlockDuration);
    }
    
    @Override
    protected boolean isBlocked() {
        return this.mIsBlocking;
    }
}
