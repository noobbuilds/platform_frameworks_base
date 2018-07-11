package com.google.android.systemui.elmyra;

import android.content.Context;

import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.actions.CameraAction;
import com.google.android.systemui.elmyra.actions.DismissTimer;
import com.google.android.systemui.elmyra.actions.LaunchOpa;
import com.google.android.systemui.elmyra.actions.ServiceAction;
import com.google.android.systemui.elmyra.actions.SetupWizardAction;
import com.google.android.systemui.elmyra.actions.SilenceCall;
import com.google.android.systemui.elmyra.actions.SnoozeAlarm;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.feedback.HapticClick;
import com.google.android.systemui.elmyra.feedback.NavUndimEffect;
import com.google.android.systemui.elmyra.feedback.OpaHomeButton;
import com.google.android.systemui.elmyra.feedback.OpaLockscreen;
import com.google.android.systemui.elmyra.feedback.SquishyNavigationButtons;
import com.google.android.systemui.elmyra.gates.CameraVisibility;
import com.google.android.systemui.elmyra.gates.ChargingState;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup;
import com.google.android.systemui.elmyra.gates.KeyguardProximity;
import com.google.android.systemui.elmyra.gates.NavigationBarVisibility;
import com.google.android.systemui.elmyra.gates.SetupWizard;
import com.google.android.systemui.elmyra.gates.SystemKeyPress;
import com.google.android.systemui.elmyra.gates.TelephonyActivity;
import com.google.android.systemui.elmyra.gates.UsbState;
import com.google.android.systemui.elmyra.gates.VrMode;
import com.google.android.systemui.elmyra.gates.WakeMode;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.sensors.SLPIGestureSensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceConfigurationGoogle implements ServiceConfiguration {
    private List<Action> mActions = new ArrayList<>();
    private List<FeedbackEffect> mFeedbackEffects;
    private List<Gate> mGates;
    private GestureSensor mGestureSensor;

    public ServiceConfigurationGoogle(Context context) {
        context.getContentResolver();
        LaunchOpa launchOpa = new LaunchOpa(context, Arrays.asList(new OpaHomeButton(context), new OpaLockscreen(context)));
        List asList = Arrays.asList(new DismissTimer(context), new SnoozeAlarm(context), new SilenceCall(context), new ServiceAction(context, launchOpa));
        mActions.addAll(asList);
        mActions.add(new CameraAction(context, launchOpa));
        mActions.add(new SetupWizardAction(context, launchOpa));
        mActions.add(launchOpa);
        mFeedbackEffects = new ArrayList<>();
        mFeedbackEffects.add(new HapticClick(context));
        mFeedbackEffects.add(new SquishyNavigationButtons(context));
        mFeedbackEffects.add(new NavUndimEffect(context));
        mGates = new ArrayList<>();
        mGates.add(new WakeMode(context));
        mGates.add(new ChargingState(context));
        mGates.add(new UsbState(context));
        mGates.add(new KeyguardProximity(context));
        mGates.add(new SetupWizard(context));
        mGates.add(new NavigationBarVisibility(context, asList));
        mGates.add(new SystemKeyPress(context));
        mGates.add(new TelephonyActivity(context));
        mGates.add(new VrMode(context));
        mGates.add(new KeyguardDeferredSetup(context, asList));
        mGates.add(new CameraVisibility(context));
        mGestureSensor = new SLPIGestureSensor(context);
    }

    public List<Action> getActions() {
        return mActions;
    }

    public List<FeedbackEffect> getFeedbackEffects() {
        return mFeedbackEffects;
    }

    public List<Gate> getGates() {
        return mGates;
    }

    public GestureSensor getGestureSensor() {
        return mGestureSensor;
    }
}

