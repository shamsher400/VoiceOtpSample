package com.bng.bngvoiceotp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bng.magiccall.Utils.DebugLogManager;

class CallStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CallStateReceiver::", "onReceive");
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        //CallState callState = new BngVoiceOtpVerification();
       // callState.callState(state);
        BngVoiceOtpVerification.getInstance().callState(state);
    }
}
