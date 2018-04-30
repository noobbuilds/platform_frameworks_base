package com.google.android.systemui.elmyra.sensors;

import android.support.annotation.Nullable;

import com.google.android.systemui.elmyra.ElmyraService;

public interface GestureSensor extends Sensor
{
    void setGestureListener(@Nullable final ElmyraService.GestureListener p0);
    
    public interface Listener
    {
        void onGestureDetected(final GestureSensor p0);
        
        void onGestureProgress(final GestureSensor p0, final float p1, final int p2);
    }
}
