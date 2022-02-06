package com.juergenkleck.android.app.appointment;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Android App - Appointment
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class AppointmentBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                || action.equals(Intent.ACTION_TIME_CHANGED)
                || action.equals(AppointmentWidgetProvider.REFRESH_ACTION)) {
            final Intent intent1 = new Intent(context, AppointmentUpdateService.class);
            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            context.startService(intent);
        }
    }

}