package com.google.android.systemui.elmyra.sensors;

import android.provider.Settings;
import android.support.annotation.Nullable;
import java.io.PrintWriter;
import java.io.FileDescriptor;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.util.TypedValue;

import com.android.keyguard.KeyguardUpdateMonitor;
import android.net.Uri;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.util.AsyncSensorManager;
import android.hardware.SensorEvent;

import com.google.android.systemui.elmyra.ElmyraService;

import android.hardware.SensorManager;
import android.hardware.SensorEventListener;

import com.android.keyguard.KeyguardUpdateMonitorCallback;
import android.content.Context;
import com.android.systemui.Dumpable;

public class SLPIGestureSensor implements Dumpable, GestureSensor
{
    private Context mContext;
    private AssistGestureController mController;
    private boolean mIsListening;
    private ElmyraService.GestureListener mListener;
    private SensorEventLogger mLogger;
    private int[] mPrimaryLowerThreshold;
    private int[] mPrimarySensitivity;
    private int[] mPrimaryTimeWindow;
    private int[] mPrimaryUpperThreshold;
    private float mProgressDetectThreshold;
    private int mReconfigures;
    private int[] mSecondaryLowerThreshold;
    private int[] mSecondarySensitivity;
    private int[] mSecondaryTimeWindow;
    private int[] mSecondaryUpperThreshold;
    private android.hardware.Sensor mSensor;
    private SensorEventListener mSensorListener;
    private SensorManager mSensorManager;
    private android.hardware.Sensor mWakeSensor;
    private SensorEventListener mWakeSensorListener;
    
    public SLPIGestureSensor(final Context mContext) {
        this.mReconfigures = 0;
        this.mSensorListener = (SensorEventListener)new SensorEventListener() {
            public void onAccuracyChanged(final android.hardware.Sensor sensor, int n) {
            }
            
            public void onSensorChanged(final SensorEvent sensorEvent) {
                SLPIGestureSensor.this.onSensorEvent(sensorEvent);
            }
        };
        this.mWakeSensorListener = (SensorEventListener)new SensorEventListener() {
            public void onAccuracyChanged(final android.hardware.Sensor sensor, int n) {
            }
            
            public void onSensorChanged(final SensorEvent sensorEvent) {
            }
        };
        Listener mGestureListener = new Listener() {
            @Override
            public void onGestureDetected(final GestureSensor gestureSensor) {
                if (SLPIGestureSensor.this.mListener != null) {
                    SLPIGestureSensor.this.mListener.onGestureDetected(gestureSensor);
                }
                SLPIGestureSensor.this.mLogger.captureSnapshot();
            }

            @Override
            public void onGestureProgress(final GestureSensor gestureSensor, float n, int n2) {
                if (SLPIGestureSensor.this.mListener != null) {
                    SLPIGestureSensor.this.mListener.onGestureProgress(gestureSensor, n, n2);
                }
            }
        };
        KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            @Override
            public void onFinishedGoingToSleep(final int n) {
                SLPIGestureSensor.this.mController.onGestureProgress(0.0f);
            }

            @Override
            public void onStartedWakingUp() {
                SLPIGestureSensor.this.mController.onGestureProgress(0.0f);
            }
        };
        final ContentResolver contentResolver = mContext.getContentResolver();
        this.mContext = mContext;
        this.mSensorManager = Dependency.get(AsyncSensorManager.class);
        final Resources resources = mContext.getResources();
        final String string = resources.getString(R.string.elmyra_sensor_string_type);
        for (final android.hardware.Sensor sensor : this.mSensorManager.getSensorList(-1)) {
            if (sensor.getStringType().equals(string)) {
                if (sensor.isWakeUpSensor()) {
                    this.mWakeSensor = sensor;
                }
                else {
                    this.mSensor = sensor;
                }
            }
            if (this.mSensor != null && this.mWakeSensor != null) {
                break;
            }
        }
        if (this.mSensor == null) {
            Log.e("Elmyra/SLPIGestureSensor", "Could not find sensor " + string);
        }
        if (this.mWakeSensor == null) {
            Log.e("Elmyra/SLPIGestureSensor", "Could not find wake sensor " + string);
        }
        this.mController = new AssistGestureController(mContext, this).setGestureListener(mGestureListener);
        String ELFUCKGOOGLEMYRA = "assist_gesture_sensitivity";
        Settings.System.getUriFor(ELFUCKGOOGLEMYRA);
        final Uri ELFUCKGOOGLEMYRA_URI =
                Settings.System.getUriFor(ELFUCKGOOGLEMYRA);
  //      UserContentObserver mSettingsObserver = new UserContentObserver(Settings.System.getUriFor(contentResolver, "assist_gesture_sensitivity", 0) == 1);
        KeyguardUpdateMonitor.getInstance(mContext).registerCallback(mKeyguardUpdateMonitorCallback);
        int n = 1;
  //        if (Build.IS_DEBUGGABLE) {
  //          n = 1;
  //      }
  //      else {
  //          n = 0;
  //      }
        if (Settings.System.getInt(contentResolver, "systemui.google.elmyra_logging_enabled", n) != 0) {
            this.mLogger = new BufferedEventLogger(mContext, resources.getInteger(R.integer.elmyra_history_event_capacity), resources.getInteger(R.integer.elmyra_history_raw_duration) * 50, resources.getInteger(R.integer.elmyra_history_snapshot_capacity), resources.getInteger(R.integer.elmyra_history_snapshot_delay));
        }
        else {
            this.mLogger = new NullEventLogger();
        }
        final TypedValue typedValue = new TypedValue();
        resources.getValue(R.dimen.elmyra_progress_detect_threshold, typedValue, true);
        this.mProgressDetectThreshold = typedValue.getFloat();
        this.mPrimarySensitivity = resources.getIntArray(R.array.elmyra_primary_sensitivity);
        this.mPrimaryUpperThreshold = resources.getIntArray(R.array.elmyra_primary_upper_threshold);
        this.mPrimaryLowerThreshold = resources.getIntArray(R.array.elmyra_primary_lower_threshold);
        this.mPrimaryTimeWindow = resources.getIntArray(R.array.elmyra_primary_time_window);
        this.mSecondarySensitivity = resources.getIntArray(R.array.elmyra_secondary_sensitivity);
        this.mSecondaryUpperThreshold = resources.getIntArray(R.array.elmyra_secondary_upper_threshold);
        this.mSecondaryLowerThreshold = resources.getIntArray(R.array.elmyra_secondary_lower_threshold);
        this.mSecondaryTimeWindow = resources.getIntArray(R.array.elmyra_secondary_time_window);
    }
    
    private float calculateFraction(final float n, float n2, float n3) {
        return (n2 - n) * n3 + n;
    }
    
    private float calculateFraction(final int[] array, float n) {
        return this.calculateFraction(array[0], array[1], n);
    }
    
    private void onSensorEvent(final SensorEvent sensorEvent) {
        final int round = Math.round(sensorEvent.values[0]);
        final float max = Math.max(sensorEvent.values[1], sensorEvent.values[2]);
        switch (round & 0xFF) {
            case 16: {
                this.mLogger.addRawEvent(sensorEvent);
                break;
            }
            case 49: {
                this.mController.onGestureProgress(max);
                this.mLogger.addGestureEvent(sensorEvent);
                break;
            }
            case 65: {
                this.mController.onGestureDetected();
                this.mLogger.addGestureEvent(sensorEvent);
                break;
            }
            case 240: {
                if (Math.round(sensorEvent.values[1]) == 1) {
                    this.updateEventMask();
                    this.updateSensitivity();
                    ++this.mReconfigures;
                    Log.w("Elmyra/SLPIGestureSensor", "*** reconfigure event, count = " + this.mReconfigures);
                    break;
                }
                break;
            }
        }
    }
    
    private void setEventMask(final int n, int n2) {
        if (this.mSensor == null) {
            Log.w("Elmyra/SLPIGestureSensor", "Error: setEventMask() - no elmyra sensor!");
            return;
        }
       // this.mSensorManager.setOperationParameter(SensorAdditionalInfo.createCustomInfo(this.mSensor, 268435457, new float[] { n, n2 }));
    }
    
    private void updateEventMask() {
        boolean b;
        if (this.mLogger.isLoggingRawEvents()) {
            b = true;
        }
        else {
            b = false;
        }
        this.setEventMask((b ? 1 : 0) | 0x80 | 0x8 | 0x10, 16);
    }
    
    private void updateSensitivity() {
        final float floatForUser = Settings.Secure.getFloat(this.mContext.getContentResolver(), "assist_gesture_sensitivity", 0.5f);
        if (this.mSensor == null) {
            Log.w("Elmyra/SLPIGestureSensor", "Error: updateSensitivity(): no elmyra sensor!");
            return;
        }
        float n = 0.0f;
        Label_0053: {
            if (floatForUser >= 0.0f) {
                n = floatForUser;
                if (floatForUser <= 1.0f) {
                    break Label_0053;
                }
            }
            n = 0.5f;
        }
        final float n2 = 1.0f - n;
      //  this.mSensorManager.setOperationParameter(SensorAdditionalInfo.createCustomInfo(this.mSensor, 268435456, new float[] { n, this.mProgressDetectThreshold, 5.0f, this.calculateFraction(this.mPrimarySensitivity, n2), this.calculateFraction(this.mPrimaryUpperThreshold, n2), this.calculateFraction(this.mPrimaryLowerThreshold, n2), this.calculateFraction(this.mPrimaryTimeWindow, n2), this.calculateFraction(this.mSecondarySensitivity, n2), this.calculateFraction(this.mSecondaryUpperThreshold, n2), this.calculateFraction(this.mSecondaryLowerThreshold, n2), this.calculateFraction(this.mSecondaryTimeWindow, n2) }));
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, PrintWriter printWriter, String[] array) {
        printWriter.println(SLPIGestureSensor.class.getSimpleName() + " state:");
        printWriter.println("  mSensor: " + this.mSensor);
        printWriter.println("  mWakeSensor: " + this.mWakeSensor);
        printWriter.println("  mIsListening: " + this.mIsListening);
        printWriter.println("  mReconfigures: " + this.mReconfigures);
        this.mLogger.dumpSnapshots(printWriter);
    }
    
    @Override
    public boolean isListening() {
        return this.mIsListening;
    }
    
    @Override
    public void setGestureListener(@Nullable ElmyraService.GestureListener mListener) {
        this.mListener = mListener;
    }
    
    @Override
    public void startListening() {
        if (!this.mIsListening) {
            if (this.mSensor != null) {
                this.mSensorManager.registerListener(this.mSensorListener, this.mSensor, 20000);
            }
            if (this.mWakeSensor != null) {
                this.mSensorManager.registerListener(this.mWakeSensorListener, this.mWakeSensor, 20000);
            }
            this.mIsListening = true;
            this.updateEventMask();
            this.updateSensitivity();
        }
    }
    
    @Override
    public void stopListening() {
        if (this.mIsListening) {
            if (this.mSensor != null) {
                this.mSensorManager.unregisterListener(this.mSensorListener, this.mSensor);
            }
            if (this.mWakeSensor != null) {
                this.mSensorManager.unregisterListener(this.mWakeSensorListener, this.mWakeSensor);
            }
            this.mIsListening = false;
        }
    }
}
