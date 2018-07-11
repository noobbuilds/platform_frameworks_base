package com.google.android.systemui.elmyra;

import android.content.Context;
import android.metrics.LogMaker;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dumpable;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.actions.LambdaF;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.sensors.GestureSensor;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ElmyraService implements Dumpable {
    private Action.Listener mActionListener;
    ;
    private List<Action> mActions;
    private Context mContext;
    private List<FeedbackEffect> mFeedbackEffects;
    private Gate.Listener mGateListener;
    private List<Gate> mGates;
    private GestureSensor.Listener mGestureListener = new GestureListener();
    private GestureSensor mGestureSensor;
    private Action mLastActiveAction;
    private long mLastPrimedGesture;
    private int mLastStage;
    private MetricsLogger mLogger;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    public ElmyraService(final Context mContext, ServiceConfiguration serviceConfiguration) {
        this.mActionListener = new Action.Listener() {
            @Override
            public void onActionAvailabilityChanged(Action action) {
                ElmyraService.this.updateSensorListener();
            }
        };

        this.mGateListener = new Gate.Listener() {
            @Override
            public void onGestureDetected(GestureSensor gestureSensor) {

            }

            @Override
            public void onGestureProgress(GestureSensor gestureSensor, float n, int n2) {

            }

            @Override
            public void onGateChanged(Gate gate) {
                ElmyraService.this.updateSensorListener();
            }
        };

        GestureListener mGestureListener = new GestureListener();
        mLogger = new MetricsLogger();
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (mPowerManager != null) {
            mWakeLock = mPowerManager.newWakeLock(1, ":Elmyra/ElmyraService");
        }
        byte id = 0;
        mActions.forEach(new LambdaF(id));
        mFeedbackEffects = new ArrayList<>(serviceConfiguration.getFeedbackEffects());
        byte id2 = 1;
        (mGates = new ArrayList<Gate>(serviceConfiguration.getGates())).forEach(new LambdaF(id2));
        mGestureSensor = serviceConfiguration.getGestureSensor();
        if (mGestureSensor != null) {
            mGestureSensor.setGestureListener(mGestureListener);
        }
        updateSensorListener();
    }


    private void activateGates() {
        for (int i = 0; i < mGates.size(); ++i) {
            mGates.get(i).activate();
        }
    }

    private Gate blockingGate() {
        for (int i = 0; i < mGates.size(); ++i) {
            if (mGates.get(i).isBlocking()) {
                return mGates.get(i);
            }
        }
        return null;
    }

    private void deactivateGates() {
        for (int i = 0; i < mGates.size(); ++i) {
            mGates.get(i).deactivate();
        }
    }

    private Action firstAvailableAction() {
        for (int i = 0; i < mActions.size(); ++i) {
            if (mActions.get(i).isAvailable()) {
                return mActions.get(i);
            }
        }
        return null;
    }

    private void startListening() {
        if (mGestureSensor != null && (!mGestureSensor.isListening())) {
            mGestureSensor.startListening();
        }
    }

    private void stopListening() {
        if (mGestureSensor != null && mGestureSensor.isListening()) {
            mGestureSensor.stopListening();
            for (int i = 0; i < mFeedbackEffects.size(); ++i) {
                mFeedbackEffects.get(i).onRelease();
            }
            Action updateActiveAction = updateActiveAction();
            if (updateActiveAction != null) {
                updateActiveAction.onProgress(0.0f, 0);
            }
        }
    }

    private Action updateActiveAction() {
        Action firstAvailableAction = firstAvailableAction();
        if (mLastActiveAction != null && firstAvailableAction != mLastActiveAction) {
            Log.i("Elmyra/ElmyraService", "Switching action from " + mLastActiveAction + " to " + firstAvailableAction);
            mLastActiveAction.onProgress(0.0f, 0);
        }
        return mLastActiveAction = firstAvailableAction;
    }

    @Override
    public void dump(final FileDescriptor fileDescriptor, PrintWriter printWriter, String[] array) {
        printWriter.println(ElmyraService.class.getSimpleName() + " state:");
        printWriter.println("  Gates:");
        for (int i = 0; i < mGates.size(); ++i) {
            printWriter.print("    ");
            if (mGates.get(i).isActive()) {
                String s;
                if (mGates.get(i).isBlocking()) {
                    s = "X ";
                } else {
                    s = "O ";
                }
                printWriter.print(s);
            } else {
                printWriter.print("- ");
            }
            printWriter.println(mGates.get(i).toString());
        }
        printWriter.println("  Actions:");
        for (int j = 0; j < mActions.size(); ++j) {
            printWriter.print("    ");
            String s2;
            if (mActions.get(j).isAvailable()) {
                s2 = "O ";
            } else {
                s2 = "X ";
            }
            printWriter.print(s2);
            printWriter.println(mActions.get(j).toString());
        }
        printWriter.println("  Active: " + mLastActiveAction);
        if (mGestureSensor instanceof Dumpable) {
            ((Dumpable) mGestureSensor).dump(fileDescriptor, printWriter, array);
        }
    }

    protected void updateSensorListener() {
        Action updateActiveAction = updateActiveAction();
        if (updateActiveAction == null) {
            Log.i("Elmyra/ElmyraService", "No available actions");
            deactivateGates();
            stopListening();
            return;
        }
        activateGates();
        Gate blockingGate = blockingGate();
        if (blockingGate != null) {
            Log.i("Elmyra/ElmyraService", "Gated by " + blockingGate);
            stopListening();
            return;
        }
        Log.i("Elmyra/ElmyraService", "Unblocked; current action: " + updateActiveAction);
        startListening();
    }

    public class GestureListener implements GestureSensor.Listener {
        public GestureListener() {

        }

        public void onGestureDetected(final GestureSensor gestureSensor) {
            ElmyraService.this.mWakeLock.acquire(2000L);
            boolean interactive = ElmyraService.this.mPowerManager.isInteractive();
            LogMaker setType = new LogMaker(999).setType(4);
            int subtype;
            if (interactive) {
                subtype = 1;
            } else {
                subtype = 2;
            }
            LogMaker setSubtype = setType.setSubtype(subtype);
            long latency;
            if (interactive) {
                latency = SystemClock.uptimeMillis() - ElmyraService.this.mLastPrimedGesture;
            } else {
                latency = 0L;
            }
            LogMaker setLatency = setSubtype.setLatency(latency);
            ElmyraService.this.mLastPrimedGesture = 0L;
            Action actionUpdate = ElmyraService.this.updateActiveAction();
            if (actionUpdate != null) {
                Log.i("Elmyra/ElmyraService", "Triggering " + actionUpdate);
                actionUpdate.onTrigger();
                for (int i = 0; i < ElmyraService.this.mFeedbackEffects.size(); ++i) {
                    ElmyraService.this.mFeedbackEffects.get(i).onResolve();
                }
                setLatency.setPackageName(actionUpdate.getClass().getName());
            }
            ElmyraService.this.mLogger.write(setLatency);
        }

        public void onGestureProgress(final GestureSensor gestureSensor, float n, int n2) {
            Action actionUpdate = ElmyraService.this.updateActiveAction();
            if (actionUpdate != null) {
                actionUpdate.onProgress(n, n2);
                for (int i = 0; i < ElmyraService.this.mFeedbackEffects.size(); ++i) {
                    ElmyraService.this.mFeedbackEffects.get(i).onProgress(n, n2);
                }
            }

            if (n2 != ElmyraService.this.mLastStage) {
                long uptimeMillis = SystemClock.uptimeMillis();
                if (n2 == 2) {
                    ElmyraService.this.mLogger.action(998);
                    ElmyraService.this.mLastPrimedGesture = uptimeMillis;
                } else if (n2 == 0 && ElmyraService.this.mLastPrimedGesture != 0L) {
                    ElmyraService.this.mLogger.write(new LogMaker(997).setType(4).setLatency(uptimeMillis - ElmyraService.this.mLastPrimedGesture));
                }
                ElmyraService.this.mLastStage = n2;
            }
        }

    }
}
