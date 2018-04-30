package com.google.android.systemui.elmyra.gates;

import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;

public class SystemKeyPress extends TransientGate
{
    private final int[] mBlockingKeys;
    private final CommandQueue mCommandQueue;
    private final CommandQueue.Callbacks mCommandQueueCallbacks;
    
    public SystemKeyPress(final Context context) {
        super(context, context.getResources().getInteger(R.integer.elmyra_system_key_gate_duration));
        this.mCommandQueueCallbacks = new CommandQueue.Callbacks() {
            @Override
            public void handleSystemKey(final int n) {
                for (int i = 0; i < SystemKeyPress.this.mBlockingKeys.length; ++i) {
                    if (SystemKeyPress.this.mBlockingKeys[i] == n) {
                        SystemKeyPress.this.block();
                        break;
                    }
                }
            }
        };
        this.mBlockingKeys = context.getResources().getIntArray(R.array.elmyra_blocking_system_keys);
        this.mCommandQueue = SysUiServiceProvider.getComponent(context, CommandQueue.class);
    }
    
    @Override
    protected void onActivate() {
        this.mCommandQueue.addCallbacks(this.mCommandQueueCallbacks);
    }
    
    @Override
    protected void onDeactivate() {
        this.mCommandQueue.removeCallbacks(this.mCommandQueueCallbacks);
    }
}
