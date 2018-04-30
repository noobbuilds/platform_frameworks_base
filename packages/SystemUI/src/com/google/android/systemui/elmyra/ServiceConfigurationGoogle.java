package com.google.android.systemui.elmyra;

import com.google.android.systemui.elmyra.sensors.SLPIGestureSensor;
import com.google.android.systemui.elmyra.gates.CameraVisibility;
import com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup;
import com.google.android.systemui.elmyra.gates.VrMode;
import com.google.android.systemui.elmyra.gates.TelephonyActivity;
import com.google.android.systemui.elmyra.gates.SystemKeyPress;
import com.google.android.systemui.elmyra.gates.NavigationBarVisibility;
import com.google.android.systemui.elmyra.gates.SetupWizard;
import com.google.android.systemui.elmyra.gates.KeyguardProximity;
import com.google.android.systemui.elmyra.gates.UsbState;
import com.google.android.systemui.elmyra.gates.ChargingState;
import com.google.android.systemui.elmyra.gates.WakeMode;
import com.google.android.systemui.elmyra.feedback.NavUndimEffect;
import com.google.android.systemui.elmyra.feedback.SquishyNavigationButtons;
import com.google.android.systemui.elmyra.feedback.HapticClick;
import com.google.android.systemui.elmyra.actions.SetupWizardAction;
import com.google.android.systemui.elmyra.actions.CameraAction;
import java.util.Collection;
import java.util.ArrayList;
import com.google.android.systemui.elmyra.actions.ServiceAction;
import com.google.android.systemui.elmyra.actions.SilenceCall;
import com.google.android.systemui.elmyra.actions.SnoozeAlarm;
import com.google.android.systemui.elmyra.actions.DismissTimer;
import com.google.android.systemui.elmyra.actions.LaunchOpa;
import java.util.Arrays;
import com.google.android.systemui.elmyra.feedback.OpaLockscreen;
import com.google.android.systemui.elmyra.feedback.OpaHomeButton;
import android.content.Context;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.List;

public class ServiceConfigurationGoogle implements ServiceConfiguration
{
    private final List<Action> mActions;
    private final List<FeedbackEffect> mFeedbackEffects;
    private final List<Gate> mGates;
    private final GestureSensor mGestureSensor;
    
    public ServiceConfigurationGoogle(final Context context) {
        context.getContentResolver();
        final LaunchOpa launchOpa = new LaunchOpa(context, Arrays.asList(new OpaHomeButton(context), new OpaLockscreen(context)));
        final List<Action> list = Arrays.asList(new DismissTimer(context), new SnoozeAlarm(context), new SilenceCall(context), new ServiceAction(context, launchOpa));
        (this.mActions = new ArrayList<Action>()).addAll(list);
        this.mActions.add(new CameraAction(context, launchOpa));
        this.mActions.add(new SetupWizardAction(context, launchOpa));
        this.mActions.add(launchOpa);
        (this.mFeedbackEffects = new ArrayList<FeedbackEffect>()).add(new HapticClick(context));
        this.mFeedbackEffects.add(new SquishyNavigationButtons(context));
        this.mFeedbackEffects.add(new NavUndimEffect(context));
        (this.mGates = new ArrayList<Gate>()).add(new WakeMode(context));
        this.mGates.add(new ChargingState(context));
        this.mGates.add(new UsbState(context));
        this.mGates.add(new KeyguardProximity(context));
        this.mGates.add(new SetupWizard(context));
        this.mGates.add(new NavigationBarVisibility(context, list));
        this.mGates.add(new SystemKeyPress(context));
        this.mGates.add(new TelephonyActivity(context));
        this.mGates.add(new VrMode(context));
        this.mGates.add(new KeyguardDeferredSetup(context, list));
        this.mGates.add(new CameraVisibility(context));
        this.mGestureSensor = new SLPIGestureSensor(context);
    }
    
    @Override
    public List<Action> getActions() {
        return this.mActions;
    }
    
    @Override
    public List<FeedbackEffect> getFeedbackEffects() {
        return this.mFeedbackEffects;
    }
    
    @Override
    public List<Gate> getGates() {
        return this.mGates;
    }
    
    @Override
    public GestureSensor getGestureSensor() {
        return this.mGestureSensor;
    }
}
