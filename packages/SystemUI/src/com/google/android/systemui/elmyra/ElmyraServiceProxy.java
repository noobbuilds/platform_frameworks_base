package com.google.android.systemui.elmyra;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ElmyraServiceProxy extends Service {
    private static List<ElmyraServiceListener> mElmyraServiceListeners;
    private IElmyraService.Stub mBinder;

    public ElmyraServiceProxy() {
        mElmyraServiceListeners = new ArrayList<>();
        mBinder = new IElmyraService.Stub() {

            public void launchAssistant() {
                checkPermission("com.google.android.elmyra.permission.CONFIGURE_ASSIST_GESTURE");
                try {
                    for (int size = mElmyraServiceListeners.size() - 1; size >= 0; size--) {
                        IElmyraServiceListener listener = mElmyraServiceListeners.get(size).getListener();
                        if (listener == null) {
                            mElmyraServiceListeners.remove(size);
                        } else {
                            listener.launchAssistant();
                        }
                    }
                } catch (Throwable e) {
                    Log.e("Elmyra/ElmyraServiceProxy", "Error launching assistant", e);
                }
            }

            public void registerListener(IBinder iBinder, IBinder iBinder2) {
                checkPermission("com.google.android.elmyra.permission.CONFIGURE_ASSIST_GESTURE");
                if (iBinder == null) {
                    Log.w("Elmyra/ElmyraServiceProxy", "token should not be null");
                } else if (iBinder2 == null) {
                    for (int i = 0; i < mElmyraServiceListeners.size(); i++) {
                        if (iBinder.equals(mElmyraServiceListeners.get(i).getToken())) {
                            mElmyraServiceListeners.get(i).unlinkToDeath();
                            mElmyraServiceListeners.remove(i);
                            return;
                        }
                    }
                } else {
                    mElmyraServiceListeners.add(new ElmyraServiceListener(iBinder, IElmyraServiceListener.Stub.asInterface(iBinder2)));
                }
            }

            public void registerSettingsListener(IBinder iBinder, IBinder iBinder2) {
                checkPermission("com.google.android.elmyra.permission.CONFIGURE_ASSIST_GESTURE");
                try {
                    for (int size = mElmyraServiceListeners.size() - 1; size >= 0; size--) {
                        IElmyraServiceListener listener = mElmyraServiceListeners.get(size).getListener();
                        if (listener == null) {
                            mElmyraServiceListeners.remove(size);
                        } else {
                            listener.setListener(iBinder, iBinder2);
                        }
                    }
                } catch (Throwable e) {
                    Log.e("Elmyra/ElmyraServiceProxy", "Action isn't connected", e);
                }
            }
        };
    }

    private void checkPermission(String str) {
        enforceCallingOrSelfPermission(str, "Must have " + str + " permission");
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return START_STICKY_COMPATIBILITY;
    }

    private class ElmyraServiceListener implements IBinder.DeathRecipient {
        private IElmyraServiceListener mListener;
        private IBinder mToken;

        ElmyraServiceListener(IBinder iBinder, IElmyraServiceListener iElmyraServiceListener) {
            mToken = iBinder;
            mListener = iElmyraServiceListener;
            linkToDeath();
        }

        private void linkToDeath() {
            if (mToken != null) {
                try {
                    mToken.linkToDeath(this, 0);
                } catch (Throwable e) {
                    Log.e("Elmyra/ElmyraServiceProxy", "Unable to linkToDeath", e);
                }
            }
        }

        public void binderDied() {
            Log.w("Elmyra/ElmyraServiceProxy", "ElmyraServiceListener binder died");
            mToken = null;
            mListener = null;
        }

        public IElmyraServiceListener getListener() {
            return mListener;
        }

        public IBinder getToken() {
            return mToken;
        }

        void unlinkToDeath() {
            if (mToken != null) {
                mToken.unlinkToDeath(this, 0);
            }
        }
    }
}
