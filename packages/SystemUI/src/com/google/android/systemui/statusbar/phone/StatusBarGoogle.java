package com.google.android.systemui.statusbar.phone;

import android.content.IntentFilter;
import com.android.systemui.statusbar.notification.InflationException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import com.android.systemui.statusbar.phone.StatusBar;

public class StatusBarGoogle extends StatusBar
{
    private boolean mShouldBroadcastNotifications;
    private BroadcastReceiver mWallpaperChangedReceiver;

    public StatusBarGoogle() {
        this.mWallpaperChangedReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (intent.getAction().equals("android.intent.action.WALLPAPER_CHANGED")) {
                    StatusBarGoogle.this.checkNotificationBroadcastSupport();
                }
            }
        };
    }

    private void checkNotificationBroadcastSupport() {
        this.mShouldBroadcastNotifications = false;
        final WallpaperManager wallpaperManager = (WallpaperManager)this.mContext.getSystemService((Class)WallpaperManager.class);
        if (wallpaperManager == null) {
            return;
        }
        final WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
        if (wallpaperInfo == null) {
            return;
        }
        if (wallpaperInfo.getComponent().getClassName().startsWith("com.breel.wallpapers.imprint")) {
            this.mShouldBroadcastNotifications = true;
        }
    }

    @Override
    public void addNotification(final StatusBarNotification statusBarNotification, final NotificationListenerService.RankingMap notificationListenerService$RankingMap) throws InflationException {
        super.addNotification(statusBarNotification, notificationListenerService$RankingMap);
        if (this.mShouldBroadcastNotifications) {
            final Intent intent = new Intent();
            intent.setPackage("com.breel.wallpapers");
            intent.setAction("com.breel.wallpapers.NOTIFICATION_RECEIVED");
            this.mContext.sendBroadcast(intent, "com.breel.wallpapers.notifications");
        }
    }

    @Override
    public void start() {
        super.start();
        this.mContext.registerReceiver(this.mWallpaperChangedReceiver, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"));
        this.checkNotificationBroadcastSupport();
    }
}

