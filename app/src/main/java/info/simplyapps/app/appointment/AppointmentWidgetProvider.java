package info.simplyapps.app.appointment;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import info.simplyapps.app.appointment.storage.StoreData;

public class AppointmentWidgetProvider extends AppWidgetProvider {

    public static String REFRESH_ACTION = "info.simplyapps.app.appointment.AppointmentWidgetProvider.REFRESH";
    public static String SETTINGS_ACTION = "info.simplyapps.app.appointment.AppointmentWidgetProvider.SETTINGS";

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