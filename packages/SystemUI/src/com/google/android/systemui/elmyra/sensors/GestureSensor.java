package com.google.android.systemui.elmyra.sensors;

import android.support.annotation.Nullable;
import com.google.android.systemui.elmyra.ElmyraService;
import com.google.android.systemui.elmyra.gates.Gate;

public interface GestureSensor extends Sensor {

    void setGestureListener(@Nullable ElmyraService.GestureListener p0);

    interface Listener {
        void onGestureDetected(GestureSensor p0);
        void onGestureProgress(GestureSensor p0, float p1, int p2);
    }
}
