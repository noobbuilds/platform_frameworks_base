package com.google.android.systemui.elmyra.actions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import java.util.function.Consumer;

import android.provider.Settings;
import android.provider.Settings.Secure;

import com.android.systemui.R;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import java.util.List;
import android.content.Context;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.gates.CameraVisibility;
import com.google.android.systemui.elmyra.sensors.GestureSensor;

public class CameraAction extends Action
{
    private String mCameraElmyraFeedbackResFlag;
    private String mCameraIntentAction;
    private String mCameraPackageName;
    private boolean mCameraShowing;
    private CameraVisibility mCameraVisibility;
    private Gate.Listener mCameraVisibilityListener;
    private String mDisableCameraKey;
    private boolean mIsCameraActionEnabled;
    private LaunchOpa mLaunchOpa;
    private UserContentObserver mSettingsObserver;
    private boolean mShowFeedbackForCamera;
    
    public CameraAction(final Context context, LaunchOpa mLaunchOpa) {
        super(context, null);
        this.mCameraVisibilityListener = new Gate.Listener() {
            @Override
            public void onGestureDetected(GestureSensor gestureSensor) {

            }

            @Override
            public void onGestureProgress(GestureSensor gestureSensor, float n, int n2) {

            }

            @Override
            public void onGateChanged(final Gate gate) {
                CameraAction.this.checkCameraIsShowing();
            }
        };
        Resources resources = context.getResources();
        (this.mCameraVisibility = new CameraVisibility(context)).activate();
        this.mCameraShowing = this.mCameraVisibility.isCameraShowing();
        this.mCameraPackageName = resources.getString(R.string.google_camera_app_package_name);
        this.mDisableCameraKey = resources.getString(R.string.secure_settings_disable_camera_action_key);
        this.mCameraIntentAction = resources.getString(R.string.google_camera_elmyra_intent_action);
        this.mCameraElmyraFeedbackResFlag = resources.getString(R.string.google_camera_elmyra_feedback_flag);
        this.mLaunchOpa = mLaunchOpa;
        this.mShowFeedbackForCamera = this.shouldShowFeedback();
        this.mIsCameraActionEnabled = this.getCameraActionEnabledStatus();
        this.mSettingsObserver = new UserContentObserver(context, Settings.Secure.getUriFor(this.mDisableCameraKey), new LambdaF((byte)0));
        this.mCameraVisibility.setListener(this.mCameraVisibilityListener);
    }
    
    private void checkCameraIsShowing() {
        boolean cameraShowing = this.mCameraVisibility.isCameraShowing();
        if (cameraShowing != this.mCameraShowing) {
            this.mShowFeedbackForCamera = this.shouldShowFeedback();
            this.mCameraShowing = cameraShowing;
            this.notifyListener();
        }
    }
    
    private boolean getCameraActionEnabledStatus() {
        boolean b = false;
        if (Settings.Secure.getInt(this.getContext().getContentResolver(), this.mDisableCameraKey, 0) == 0) {
            b = true;
        }
        return b;
    }
    
    private boolean shouldShowFeedback() {
        boolean b = false;
        try {
            Resources resourcesForApplication = this.getContext().getPackageManager().getResourcesForApplication(this.mCameraPackageName);
            b = (resourcesForApplication.getInteger(resourcesForApplication.getIdentifier(this.mCameraElmyraFeedbackResFlag, "integer", this.mCameraPackageName)) != 0);
            return b;
        }
        catch (PackageManager.NameNotFoundException ex) {
            return b;
        }
        catch (Resources.NotFoundException ex2) {
            return b;
        }
    }
    
    private void updateCameraActionEnabledStatus() {
        boolean cameraActionEnabledStatus = this.getCameraActionEnabledStatus();
        if (cameraActionEnabledStatus != this.mIsCameraActionEnabled) {
            this.mIsCameraActionEnabled = cameraActionEnabledStatus;
            this.notifyListener();
        }
    }
    
    @Override
    public boolean isAvailable() {
        return this.mIsCameraActionEnabled && this.mCameraShowing;
    }
    
    @Override
    public void onProgress(final float n, int n2) {
        this.updateFeedbackEffects(n, n2);
    }
    
    @Override
    public void onTrigger() {
        this.triggerFeedbackEffects();
        Intent intent = new Intent();
        intent.setAction(this.mCameraIntentAction);
        intent.setPackage(this.mCameraPackageName);
        intent.addFlags(intent.getFlags());
        this.getContext().sendBroadcast(intent);
    }
    
    @Override
    protected void triggerFeedbackEffects() {
        super.triggerFeedbackEffects();
        if (this.mShowFeedbackForCamera) {
            this.mLaunchOpa.triggerFeedbackEffects();
        }
    }
    
    @Override
    protected void updateFeedbackEffects(final float n, int n2) {
        super.updateFeedbackEffects(n, n2);
        if (this.mShowFeedbackForCamera) {
            this.mLaunchOpa.updateFeedbackEffects(n, n2);
        }
    }
}
