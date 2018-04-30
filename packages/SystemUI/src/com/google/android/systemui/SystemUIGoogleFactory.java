package com.google.android.systemui;

import com.android.systemui.assist.AssistManager;
import android.util.ArrayMap;
import com.android.systemui.statusbar.phone.ScrimController;
import java.util.function.Consumer;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import android.view.View;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.VendorServices;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import android.content.Context;
import com.android.systemui.SystemUIFactory;

public class SystemUIGoogleFactory extends SystemUIFactory
{
    @Override
    public <T> T createInstance(final Class<T> clazz) {
        if (clazz == VendorServices.class) {
            return (T)new GoogleServices();
        }
        return super.createInstance(clazz);
    }
    
    @Override
    public ScrimController createScrimController(final LightBarController lightBarController, final ScrimView scrimView, final ScrimView scrimView2, final View view, final LockscreenWallpaper lockscreenWallpaper, final Consumer<Boolean> consumer) {
        return new LiveWallpaperScrimController(lightBarController, scrimView, scrimView2, view, lockscreenWallpaper, consumer);
    }
    
    @Override
    public void injectDependencies(final ArrayMap<Object, Dependency.DependencyProvider> arrayMap, final Context context) {
     //   arrayMap.put((Object)AssistManager.class, (Object)new -$Lambda$yGxM5dv_0zHU_R4mrOfgsIRyMY4$1(context));
     //   arrayMap.put((Object)Dependency.LEAK_REPORT_EMAIL, (Object)-$Lambda$yGxM5dv_0zHU_R4mrOfgsIRyMY4.$INST$0);
    }
}
