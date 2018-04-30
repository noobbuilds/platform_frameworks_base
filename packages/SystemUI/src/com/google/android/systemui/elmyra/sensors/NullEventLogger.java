package com.google.android.systemui.elmyra.sensors;

import java.io.PrintWriter;
import android.hardware.SensorEvent;

class NullEventLogger implements SensorEventLogger
{
    @Override
    public void addGestureEvent(final SensorEvent sensorEvent) {
    }
    
    @Override
    public void addRawEvent(final SensorEvent sensorEvent) {
    }
    
    @Override
    public void captureSnapshot() {
    }
    
    @Override
    public void dumpSnapshots(final PrintWriter printWriter) {
    }
    
    @Override
    public boolean isLoggingRawEvents() {
        return false;
    }
}
