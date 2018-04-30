package com.google.android.systemui;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class WallpaperPermissionsReceiver extends BroadcastReceiver
{
    private static final String[] WALLPAPER_PACKAGES;
    
    static {
        WALLPAPER_PACKAGES = new String[] { "com.ustwo.lwp", "com.breel.geswallpapers", "com.breel.wallpapers" };
    }
    
    public void onReceive(final Context p0, final Intent p1) { throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
