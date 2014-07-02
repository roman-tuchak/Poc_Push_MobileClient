package com.pushclient.app;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Check application setting about notifications
        if (!isNotifyInvoice(context)) return;

        //Statement does nothing. Wait for different type of incoming messages
        if (isNotifyPrediction(context));

        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
        Log.i(MainActivity.TAG, "Incoming message in receiver");
    }

    // Check Preferences for Invoice notifications
    private boolean isNotifyInvoice(Context context) {
        boolean invoiceFlag = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivity.INVOICE_SWITCH_KEY, true);
        Log.v(MainActivity.TAG, "Invoices switch is: " + invoiceFlag);
        return invoiceFlag;
    }

    /**
     * If Preferences for Invoice notification return false, method return false,
     * because Prediction switch depends from Invoices switch.
    */
    private boolean isNotifyPrediction(Context context) {
        if (isNotifyInvoice(context)) {
            boolean predictFlag = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(SettingsActivity.PREDICT_SWITCH_KEY, true);
            Log.v(MainActivity.TAG, "Prediction switch is: " + predictFlag);
            return predictFlag;
        }
        return false;
    }
}
