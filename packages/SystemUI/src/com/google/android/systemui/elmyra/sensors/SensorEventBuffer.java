package com.google.android.systemui.elmyra.sensors;

import android.hardware.SensorEvent;

class SensorEventBuffer
{
    private int mFrontIndex;
    private int mSampleCount;
    private final Sample[] mSamples;
    
    SensorEventBuffer(final int n) {
        this.mSamples = new Sample[n];
    }
    
    public void add(final SensorEvent sensorEvent) {
        if (this.mSamples.length == 0) {
            return;
        }
        final int n = (this.mFrontIndex + this.mSampleCount) % this.mSamples.length;
        if (this.mSampleCount == this.mSamples.length) {
            this.mFrontIndex = (this.mFrontIndex + 1) % this.mSamples.length;
            --this.mSampleCount;
        }
        if (this.mSamples[n] == null) {
            this.mSamples[n] = new Sample(sensorEvent);
        }
        else {
            this.mSamples[n].assign(sensorEvent);
        }
        ++this.mSampleCount;
    }
    
    public int capacity() {
        return this.mSamples.length;
    }
    
    public void clear() {
        this.mSampleCount = 0;
        this.mFrontIndex = 0;
    }
    
    public Sample get(int n) {
        final int n2 = n += this.mFrontIndex;
        if (n2 >= this.mSamples.length) {
            n = n2 - this.mSamples.length;
        }
        return this.mSamples[n];
    }
    
    public int size() {
        return this.mSampleCount;
    }
    
    public Sample[] toArray() {
        final Sample[] array = new Sample[this.size()];
        for (int i = 0; i < this.size(); ++i) {
            array[i] = new Sample(this.get(i));
        }
        return array;
    }
    
    class Sample
    {
        public long timestamp;
        public float[] values;
        
        Sample(final SensorEvent sensorEvent) {
            this.assign(sensorEvent);
        }
        
        Sample(final Sample sample) {
            this.timestamp = sample.timestamp;
            this.values = new float[sample.values.length];
            System.arraycopy(sample.values, 0, this.values, 0, this.values.length);
        }
        
        private void assign(final SensorEvent sensorEvent) {
            this.timestamp = sensorEvent.timestamp;
            if (this.values == null || this.values.length != sensorEvent.values.length) {
                this.values = new float[sensorEvent.values.length];
            }
            System.arraycopy(sensorEvent.values, 0, this.values, 0, this.values.length);
        }
    }
}
