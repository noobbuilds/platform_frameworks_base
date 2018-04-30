package com.google.android.systemui;

import com.android.systemui.R;
import com.google.android.systemui.elmyra.ServiceConfiguration;
import com.google.android.systemui.elmyra.ElmyraService;
import com.google.android.systemui.elmyra.ServiceConfigurationGoogle;
import com.google.android.systemui.elmyra.ElmyraContext;
import com.google.android.systemui.ambientmusic.AmbientIndicationService;
import com.google.android.systemui.ambientmusic.AmbientIndicationContainer;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.Dumpable;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.ArrayList;
import com.android.systemui.VendorServices;

public class GoogleServices extends VendorServices
{
    private ArrayList<Object> mServices;

    public GoogleServices() {
        this.mServices = new ArrayList<Object>();
    }

    private void addService(final Object o) {
        if (o != null) {
            this.mServices.add(o);
        }
    }

    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        for (int i = 0; i < this.mServices.size(); ++i) {
            if (this.mServices.get(i) instanceof Dumpable) {
                ((Dumpable)this.mServices.get(i)).dump(fileDescriptor, printWriter, array);
            }
        }
    }

    @Override
    public void start() {
        final StatusBar statusBar = SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        final AmbientIndicationContainer ambientIndicationContainer = (AmbientIndicationContainer)statusBar.getStatusBarWindow().findViewById(R.id.ambient_indication_container);
        ambientIndicationContainer.initializeView(statusBar);
        this.addService(new AmbientIndicationService(this.mContext, ambientIndicationContainer));
        if (new ElmyraContext(this.mContext).isAvailable()) {
            this.addService(new ElmyraService(this.mContext, new ServiceConfigurationGoogle(this.mContext)));
        }
    }
}

