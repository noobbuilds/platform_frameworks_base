package com.google.android.systemui;

import android.app.WallpaperInfo;
import android.os.RemoteException;
import android.app.IWallpaperManager.Stub;
import android.os.ServiceManager;
import android.app.ActivityManager;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import android.view.View;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.phone.LightBarController;
import java.util.Collection;
import java.util.Collections;
import android.app.IWallpaperManager;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import android.content.ComponentName;
import android.util.ArraySet;
import com.android.systemui.statusbar.phone.ScrimController;

public class LiveWallpaperScrimController extends ScrimController
{
    private static ArraySet<ComponentName> REDUCED_SCRIM_WALLPAPERS;
    private int mCurrentUser;
    private final LockscreenWallpaper mLockscreenWallpaper;
    private final IWallpaperManager mWallpaperManager;
    
    static {
        LiveWallpaperScrimController.REDUCED_SCRIM_WALLPAPERS = new ArraySet((ArraySet) Collections.singleton(new ComponentName("com.breel.geswallpapers", "com.breel.geswallpapers.wallpapers.EarthWallpaperService")));
    }

    public static class UserHelper {
        public UserHelper() {
        }

        public int getCurrentUser() {
            java.lang.reflect.Method getCurrentUser = null;
            try {
                getCurrentUser = ActivityManager.class.getDeclaredMethod("getCurrentUser");
                getCurrentUser.setAccessible(true);
                return (int) getCurrentUser.invoke(null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return -1;
        }
    }

    LiveWallpaperScrimController(final LightBarController lightBarController, final ScrimView scrimView, final ScrimView scrimView2, final View view, final LockscreenWallpaper mLockscreenWallpaper, final Consumer<Boolean> consumer) {
        super(lightBarController, scrimView, scrimView2, view, consumer);
        UserHelper userHelper = new UserHelper();
        this.mCurrentUser =  userHelper.getCurrentUser();
        this.mWallpaperManager = IWallpaperManager.Stub.asInterface(ServiceManager.getService("wallpaper"));
        this.mLockscreenWallpaper = mLockscreenWallpaper;
    }
    
    private boolean isReducedScrimWallpaperSet() {
        boolean b = false;
        try {
            final WallpaperInfo wallpaperInfo = this.mWallpaperManager.getWallpaperInfo(this.mCurrentUser);
            if (wallpaperInfo != null && LiveWallpaperScrimController.REDUCED_SCRIM_WALLPAPERS.contains((Object)wallpaperInfo.getComponent())) {
                if (this.mLockscreenWallpaper.getBitmap() == null) {
                    b = true;
                }
                return b;
            }
        }
        catch (RemoteException ex) {}
        return false;
    }
    
    private void updateScrimValues() {
        if (this.isReducedScrimWallpaperSet()) {
            this.setScrimBehindValues(0.25f, 0.05f);
        }
        else {
            this.setScrimBehindValues(0.45f, 0.2f);
        }
    }
    
    @Override
    public void setCurrentUser(final int mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
        this.updateScrimValues();
    }
    
    @Override
    public void setKeyguardShowing(final boolean keyguardShowing) {
        super.setKeyguardShowing(keyguardShowing);
        if (keyguardShowing) {
            this.updateScrimValues();
        }
    }
}
