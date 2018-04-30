package com.google.android.systemui.elmyra.sensors;

import java.io.PrintWriter;
import android.hardware.SensorEvent;

interface SensorEventLogger
{
    void addGestureEvent(final SensorEvent p0);
    
    void addRawEvent(final SensorEvent p0);
    
    void captureSnapshot();
    
    void dumpSnapshots(final PrintWriter p0);
    
    boolean isLoggingRawEvents();
}
