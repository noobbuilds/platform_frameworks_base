package com.google.android.systemui.elmyra.sensors;

import java.io.PrintWriter;
import android.hardware.SensorEvent;
import java.util.ArrayList;
import android.content.Context;
import android.support.annotation.Nullable;
import android.os.Handler;
import java.util.List;

class BufferedEventLogger implements SensorEventLogger
{
    private final SensorEventBuffer mEventBuffer;
    private final List<SensorEventBuffer.Sample[]> mEventSnapshots;
    private final SensorEventBuffer mRawBuffer;
    private final List<SensorEventBuffer.Sample[]> mRawSnapshots;
    private final int mSnapshotCapacity;
    private final int mSnapshotDelay;
    @Nullable
    private final Handler mSnapshotHandler;
    
    BufferedEventLogger(final Context context, final int n, final int n2, final int mSnapshotCapacity, final int mSnapshotDelay) {
        this.mEventBuffer = new SensorEventBuffer(n);
        this.mRawBuffer = new SensorEventBuffer(n2);
        this.mEventSnapshots = new ArrayList<SensorEventBuffer.Sample[]>(mSnapshotCapacity);
        this.mRawSnapshots = new ArrayList<SensorEventBuffer.Sample[]>(mSnapshotCapacity);
        this.mSnapshotCapacity = mSnapshotCapacity;
        this.mSnapshotDelay = mSnapshotDelay;
        Handler mSnapshotHandler;
        if (this.mSnapshotDelay > 0) {
            mSnapshotHandler = new Handler(context.getMainLooper());
        }
        else {
            mSnapshotHandler = null;
        }
        this.mSnapshotHandler = mSnapshotHandler;
    }
    
    private void commitSnapshot() {
        this.mEventSnapshots.add(this.mEventBuffer.toArray());
        this.mRawSnapshots.add(this.mRawBuffer.toArray());
        if (this.mEventSnapshots.size() > this.mSnapshotCapacity) {
            this.mEventSnapshots.remove(0);
        }
        if (this.mRawSnapshots.size() > this.mSnapshotCapacity) {
            this.mRawSnapshots.remove(0);
        }
        this.mEventBuffer.clear();
        this.mRawBuffer.clear();
    }
    
    private static String formatEventSample(final SensorEventBuffer.Sample sample) {
        return String.format("%d\t%d\t%f", sample.timestamp, (int)sample.values[0], sample.values[1]);
    }
    
    private static String formatRawSample(final SensorEventBuffer.Sample sample) {
        return String.format("%d\t%d\t%d\t%d\t%d\t%d\t%d", sample.timestamp, (int)sample.values[1], (int)sample.values[2], (int)sample.values[3], (int)sample.values[4], (int)sample.values[5], (int)sample.values[6]);
    }
    
    @Override
    public void addGestureEvent(final SensorEvent sensorEvent) {
        this.mEventBuffer.add(sensorEvent);
    }
    
    @Override
    public void addRawEvent(final SensorEvent sensorEvent) {
        this.mRawBuffer.add(sensorEvent);
    }
    
    @Override
    public void captureSnapshot() {
        if (this.mSnapshotHandler != null) {
           // this.mSnapshotHandler.postDelayed((Runnable)new -$Lambda$04K0rzRg9y1RIz5gf-uV2wEbe-c(this), (long)this.mSnapshotDelay);
        }
        else {
            this.commitSnapshot();
        }
    }
    
    @Override
    public void dumpSnapshots(final PrintWriter printWriter) {
        if (this.isLoggingGestureEvents()) {
            printWriter.println("  Events:");
            for (int i = 0; i < this.mEventSnapshots.size(); ++i) {
                final SensorEventBuffer.Sample[] array = this.mEventSnapshots.get(i);
                for (int j = 0; j < array.length; ++j) {
                    printWriter.print("    ");
                    printWriter.println(formatEventSample(array[j]));
                }
                printWriter.println();
            }
        }
        if (this.isLoggingRawEvents()) {
            printWriter.println("  Samples:");
            for (int k = 0; k < this.mRawSnapshots.size(); ++k) {
                final SensorEventBuffer.Sample[] array2 = this.mRawSnapshots.get(k);
                for (int l = 0; l < array2.length; ++l) {
                    printWriter.print("    ");
                    printWriter.println(formatRawSample(array2[l]));
                }
                printWriter.println();
            }
        }
    }
    
    public boolean isLoggingGestureEvents() {
        boolean b = false;
        if (this.mEventBuffer.capacity() > 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean isLoggingRawEvents() {
        boolean b = false;
        if (this.mRawBuffer.capacity() > 0) {
            b = true;
        }
        return b;
    }
}
