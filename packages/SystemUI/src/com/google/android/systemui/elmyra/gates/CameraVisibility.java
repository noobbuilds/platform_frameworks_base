package com.google.android.systemui.elmyra.gates;

import android.app.ITaskStackListener;
import android.app.ActivityManager.RunningTaskInfo;
import java.util.List;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.res.Resources.NotFoundException;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.app.TaskStackListener;
import android.content.pm.PackageManager;
import android.app.IActivityManager;

import com.android.systemui.R;
import com.google.android.systemui.elmyra.sensors.GestureSensor;

public class CameraVisibility extends Gate
{
    private ActivityManager mActivityManager;
    private boolean mCameraHandlesElmyraAction;
    private String mCameraHandlesElmyraResFlag;
    private String mCameraPackageName;
    private boolean mCameraShowing;
    private Listener mGateListener;
    private KeyguardVisibility mKeyguardGate;
    private PackageManager mPackageManager;
    private PowerState mPowerState;
    private TaskStackListener mTaskStackListener;
    private Handler mUpdateHandler;
    private Context context;

    public CameraVisibility(final Context context) {
        super(context);
        this.mTaskStackListener = new TaskStackListener() {
            public void onTaskStackChanged() {
          //      CameraVisibility.this.mUpdateHandler.post((Runnable)new -$Lambda$gEUpfLPh0uNo7ifmfUWGSpFHiQQ((byte)0, this));
            }
        };
        this.mGateListener = new Listener() {
            @Override
            public void onGestureDetected(GestureSensor gestureSensor) {

            }

            @Override
            public void onGestureProgress(GestureSensor gestureSensor, float n, int n2) {

            }

            @Override
            public void onGateChanged(final Gate gate) {
            //    CameraVisibility.this.mUpdateHandler.post((Runnable)new -$Lambda$gEUpfLPh0uNo7ifmfUWGSpFHiQQ((byte)1, this));
            }
        };
        this.mPackageManager = context.getPackageManager();
        this.mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        this.mKeyguardGate = new KeyguardVisibility(context);
        this.mPowerState = new PowerState(context);
        this.mKeyguardGate.setListener(this.mGateListener);
        this.mPowerState.setListener(this.mGateListener);
        Resources resources = context.getResources();
        this.mCameraPackageName = resources.getString(R.string.google_camera_app_package_name);
        this.mCameraHandlesElmyraResFlag = resources.getString(R.string.google_camera_handled_elmyra_flag);
        this.mUpdateHandler = new Handler(context.getMainLooper());
    }
    
    private boolean cameraCanHandleElmyraAction() {
        boolean b = false;
        try {
            Resources resourcesForApplication = this.getContext().getPackageManager().getResourcesForApplication(this.mCameraPackageName);
            if (resourcesForApplication.getInteger(resourcesForApplication.getIdentifier(this.mCameraHandlesElmyraResFlag, "integer", this.mCameraPackageName)) != 0) {
                b = true;
            }
            return b;
        }
        catch (PackageManager.NameNotFoundException ex) {}
        catch (Resources.NotFoundException ex2) {
      //      goto Label_0045;
        }
        return b;
    }
    
    private boolean isCameraInForeground() {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean b = false;
        try {
            UserHandle currentUser = UserHandle.getUserHandleForUid(1000);
            PackageManager mPackageManager = this.mPackageManager;
            String mCameraPackageName = this.mCameraPackageName;
            int id;
            if (currentUser != null) {
                id = currentUser.describeContents();
            }
            else {
                id = 0;
            }
            int uid = mPackageManager.getApplicationInfo(mCameraPackageName, 0).uid;
            List runningAppProcesses = this.mActivityManager.getRunningAppProcesses();
            RunningAppProcessInfo RunningAppProcessInfo = null;
            Block_6: {
                for (int i = 0; i < runningAppProcesses.size(); ++i) {
                    RunningAppProcessInfo = activityManager.getRunningAppProcesses().get(i);
                    if (RunningAppProcessInfo.uid == uid && RunningAppProcessInfo.processName.equalsIgnoreCase(this.mCameraPackageName)) {
                        break Block_6;
                    }
                }
                //goto Label_0149;
            }
            if (RunningAppProcessInfo.importance == 100) {
                b = true;
            }
            return b;
        } catch (NameNotFoundException ex2) {
        //    goto Label_0149;
        }
        return b;
    }
    
    private boolean isCameraTopActivity() {
        //   List tasks = ActivityManager.getService().getTasks(1, 0);
        //   return !tasks.isEmpty() && tasks.get(0).topActivity.getPackageName().equalsIgnoreCase(this.mCameraPackageName);
        return false;
    }
    
    private void updateCameraIsShowing() {
        boolean cameraShowing = this.isCameraShowing();
        if (cameraShowing) {
            this.mCameraHandlesElmyraAction = this.cameraCanHandleElmyraAction();
        }
        if (this.mCameraShowing != cameraShowing) {
            this.mCameraShowing = cameraShowing;
            this.notifyListener();
        }
    }
    
    @Override
    protected boolean isBlocked() {
        return !this.mCameraHandlesElmyraAction && this.mCameraShowing;
    }
    
    public boolean isCameraShowing() {
        return this.isCameraTopActivity() && this.isCameraInForeground() && (!this.mPowerState.isBlocking());
    }
    
    @Override
    protected void onActivate() {
        this.mKeyguardGate.activate();
        this.mPowerState.activate();
        this.mCameraHandlesElmyraAction = this.cameraCanHandleElmyraAction();
        this.mCameraShowing = this.isCameraShowing();
        //        this.mActivityManager.registerTaskStackListener((ITaskStackListener)this.mTaskStackListener);
    }
    
    @Override
    protected void onDeactivate() {
        this.mKeyguardGate.deactivate();
        this.mPowerState.deactivate();
        //       this.mActivityManager.unregisterTaskStackListener((ITaskStackListener)this.mTaskStackListener);
    }
}
