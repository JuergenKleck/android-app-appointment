package com.juergenkleck.android.app.appointment;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import com.juergenkleck.android.app.appointment.storage.StoreData;

/**
 * Android App - Appointment
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class AppointmentWidgetProvider extends AppWidgetProvider {

    public static String REFRESH_ACTION = "com.juergenkleck.android.app.appointment.AppointmentWidgetProvider.REFRESH";
    public static String SETTINGS_ACTION = "com.juergenkleck.android.app.appointment.AppointmentWidgetProvider.SETTINGS";

    public static List<CalendarEntry> events = new ArrayList<>();
    public static StoreData storeData = new StoreData();

    public static int[] appIds;

    public AppointmentWidgetProvider() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        assert action != null;
        if (action.equals(REFRESH_ACTION)) {
            AppointmentWidgetUpdater.updateAppWidget(context);
        } else if (action.equals(SETTINGS_ACTION)) {
            Intent i = new Intent(context, AppointmentConfigurationActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        appIds = appWidgetIds;
        AppointmentWidgetUpdater.updateAppWidget(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}