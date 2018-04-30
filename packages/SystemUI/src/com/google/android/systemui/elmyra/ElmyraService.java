package com.google.android.systemui.elmyra;

import android.os.SystemClock;
import android.metrics.LogMaker;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.util.Log;

import java.util.ArrayList;

import android.os.PowerManager;
import com.android.internal.logging.MetricsLogger;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import android.content.Context;
import java.util.List;
import com.google.android.systemui.elmyra.actions.Action;
import com.android.systemui.Dumpable;

public class ElmyraService implements Dumpable
{
    private Action.Listener mActionListener;
    private List<Action> mActions;
    private Context mContext;
    private List<FeedbackEffect> mFeedbackEffects;
    private Gate.Listener mGateListener;
    private List<Gate> mGates;
    private GestureListener mGestureListener;
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
            public void onActionAvailabilityChanged(final Action action) {
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
            public void onGateChanged(final Gate gate) {
                ElmyraService.this.updateSensorListener();
            }
        };
        this.mGestureListener = new GestureListener((GestureListener)null);
        this.mContext = mContext;
        this.mLogger = new MetricsLogger();
        this.mPowerManager = (PowerManager)this.mContext.getSystemService(mContext.POWER_SERVICE);
        this.mWakeLock = this.mPowerManager.newWakeLock(1, ":Elmyra/ElmyraService");
     //   (this.mActions = new ArrayList<Action>(serviceConfiguration.getActions())).forEach(new Lam((byte)0, this));
        this.mFeedbackEffects = new ArrayList<FeedbackEffect>(serviceConfiguration.getFeedbackEffects());
      //  (this.mGates = new ArrayList<Gate>(serviceConfiguration.getGates())).forEach(new -$Lambda$D9j-tHk1MyQ2FfK7LyzHoKNWdp4((byte)1, this));
        this.mGestureSensor = serviceConfiguration.getGestureSensor();
        if (this.mGestureSensor != null) {
            this.mGestureSensor.setGestureListener(this.mGestureListener);
        }
        this.updateSensorListener();
    }
    
    private void activateGates() {
        for (int i = 0; i < this.mGates.size(); ++i) {
            this.mGates.get(i).activate();
        }
    }
    
    private Gate blockingGate() {
        for (int i = 0; i < this.mGates.size(); ++i) {
            if (this.mGates.get(i).isBlocking()) {
                return this.mGates.get(i);
            }
        }
        return null;
    }
    
    private void deactivateGates() {
        for (int i = 0; i < this.mGates.size(); ++i) {
            this.mGates.get(i).deactivate();
        }
    }
    
    private Action firstAvailableAction() {
        for (int i = 0; i < this.mActions.size(); ++i) {
            if (this.mActions.get(i).isAvailable()) {
                return this.mActions.get(i);
            }
        }
        return null;
    }
    
    private void startListening() {
        if (this.mGestureSensor != null && (this.mGestureSensor.isListening() ^ true)) {
            this.mGestureSensor.startListening();
        }
    }
    
    private void stopListening() {
        if (this.mGestureSensor != null && this.mGestureSensor.isListening()) {
            this.mGestureSensor.stopListening();
            for (int i = 0; i < this.mFeedbackEffects.size(); ++i) {
                this.mFeedbackEffects.get(i).onRelease();
            }
            Action updateActiveAction = this.updateActiveAction();
            if (updateActiveAction != null) {
                updateActiveAction.onProgress(0.0f, 0);
            }
        }
    }
    
    private Action updateActiveAction() {
        Action firstAvailableAction = this.firstAvailableAction();
        if (this.mLastActiveAction != null && firstAvailableAction != this.mLastActiveAction) {
            Log.i("Elmyra/ElmyraService", "Switching action from " + this.mLastActiveAction + " to " + firstAvailableAction);
            this.mLastActiveAction.onProgress(0.0f, 0);
        }
        return this.mLastActiveAction = firstAvailableAction;
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, PrintWriter printWriter, String[] array) {
        printWriter.println(ElmyraService.class.getSimpleName() + " state:");
        printWriter.println("  Gates:");
        for (int i = 0; i < this.mGates.size(); ++i) {
            printWriter.print("    ");
            if (this.mGates.get(i).isActive()) {
                String s;
                if (this.mGates.get(i).isBlocking()) {
                    s = "X ";
                }
                else {
                    s = "O ";
                }
                printWriter.print(s);
            }
            else {
                printWriter.print("- ");
            }
            printWriter.println(this.mGates.get(i).toString());
        }
        printWriter.println("  Actions:");
        for (int j = 0; j < this.mActions.size(); ++j) {
            printWriter.print("    ");
            String s2;
            if (this.mActions.get(j).isAvailable()) {
                s2 = "O ";
            }
            else {
                s2 = "X ";
            }
            printWriter.print(s2);
            printWriter.println(this.mActions.get(j).toString());
        }
        printWriter.println("  Active: " + this.mLastActiveAction);
        if (this.mGestureSensor instanceof Dumpable) {
            ((Dumpable)this.mGestureSensor).dump(fileDescriptor, printWriter, array);
        }
    }
    
    protected void updateSensorListener() {
        Action updateActiveAction = this.updateActiveAction();
        if (updateActiveAction == null) {
            Log.i("Elmyra/ElmyraService", "No available actions");
            this.deactivateGates();
            this.stopListening();
            return;
        }
        this.activateGates();
        Gate blockingGate = this.blockingGate();
        if (blockingGate != null) {
            Log.i("Elmyra/ElmyraService", "Gated by " + blockingGate);
            this.stopListening();
            return;
        }
        Log.i("Elmyra/ElmyraService", "Unblocked; current action: " + updateActiveAction);
        this.startListening();
    }
    
    public class GestureListener implements Gate.Listener
    {
        public GestureListener(GestureListener gestureListener) {

        }

        @Override
        public void onGestureDetected(final GestureSensor gestureSensor) {
            ElmyraService.this.mWakeLock.acquire(2000L);
            boolean interactive = ElmyraService.this.mPowerManager.isInteractive();
            LogMaker setType = new LogMaker(999).setType(4);
            int subtype;
            if (interactive) {
                subtype = 1;
            }
            else {
                subtype = 2;
            }
            LogMaker setSubtype = setType.setSubtype(subtype);
            long latency;
            if (interactive) {
                latency = SystemClock.uptimeMillis() - ElmyraService.this.mLastPrimedGesture;
            }
            else {
                latency = 0L;
            }
            LogMaker setLatency = setSubtype.setLatency(latency);
            ElmyraService.this.mLastPrimedGesture = 0L;
            Action actionUpdate = ElmyraService.this.updateActiveAction();
            if (actionUpdate != null) {
                Log.i("Elmyra/ElmyraService", "Triggering " + actionUpdate);
                actionUpdate.onTrigger();
                for (int i = 0; i < ElmyraService.this.mFeedbackEffects.size(); ++i) {
                    ((FeedbackEffect)ElmyraService.this.mFeedbackEffects.get(i)).onResolve();
                }
                setLatency.setPackageName(actionUpdate.getClass().getName());
            }
            ElmyraService.this.mLogger.write(setLatency);
        }
        
        @Override
        public void onGestureProgress(final GestureSensor gestureSensor, float n, int n2) {
            Action actionUpdate = ElmyraService.this.updateActiveAction();
            if (actionUpdate != null) {
                actionUpdate.onProgress(n, n2);
                for (int i = 0; i < ElmyraService.this.mFeedbackEffects.size(); ++i) {
                    ((FeedbackEffect)ElmyraService.this.mFeedbackEffects.get(i)).onProgress(n, n2);
                }
            }
            if (n2 != ElmyraService.this.mLastStage) {
                long uptimeMillis = SystemClock.uptimeMillis();
                if (n2 == 2) {
                    ElmyraService.this.mLogger.action(998);
                    ElmyraService.this.mLastPrimedGesture = uptimeMillis;
                }
                else if (n2 == 0 && ElmyraService.this.mLastPrimedGesture != 0L) {
                    ElmyraService.this.mLogger.write(new LogMaker(997).setType(4).setLatency(uptimeMillis - ElmyraService.this.mLastPrimedGesture));
                }
                ElmyraService.this.mLastStage = n2;
            }
        }

        @Override
        public void onGateChanged(Gate p0) {

        }
    }
}
