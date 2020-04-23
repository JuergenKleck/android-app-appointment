package info.simplyapps.app.appointment;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import info.simplyapps.app.appointment.storage.StorageProvider;
import info.simplyapps.app.appointment.storage.StoreData;

class AppointmentWidgetUpdater {

    static void updateAppWidget(Context context) {
        AppWidgetManager gm = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = AppointmentWidgetProvider.appIds;
        final int N = appWidgetIds != null ? appWidgetIds.length : 0;
        for (int i = 0; i < N; i++) {
            updateTheWidget(context, gm, appWidgetIds[i]);
        }
    }

    static void updateAppWidget(Context context, int appWidgetId) {
        AppWidgetManager gm = AppWidgetManager.getInstance(context);
        updateTheWidget(context, gm, appWidgetId);
    }

    private static void updateTheWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appprovider);

        final Intent refreshIntent = new Intent(context, AppointmentWidgetProvider.class);
        refreshIntent.setAction(AppointmentWidgetProvider.REFRESH_ACTION);
        final Intent settingsIntent = new Intent(context, AppointmentWidgetProvider.class);
        settingsIntent.setAction(AppointmentWidgetProvider.SETTINGS_ACTION);
        settingsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0,
                refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final PendingIntent settingsPendingIntent = PendingIntent.getBroadcast(context, 0,
                settingsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.refresh, refreshPendingIntent);
        views.setOnClickPendingIntent(R.id.settings, settingsPendingIntent);


        appWidgetManager.updateAppWidget(appWidgetId, views);

        if (AppointmentWidgetProvider.storeData.calendarList.isEmpty()) {
            getCalendars(context);
        }
        getEvents(context);
        setText(views);

        views.setTextViewText(R.id.textView1, context.getString(R.string.appointments) + " (" + AppointmentWidgetProvider.events.size() + ")");

        appWidgetManager.updateAppWidget(appWidgetId, views);
        views.setImageViewResource(R.id.refresh, R.drawable.refresh_anim);
    }

    private static void processCalendars(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME};
            Uri calendarUri = Uri.parse("content://com.android.calendar/calendars");
            Cursor managedCursor = context.getContentResolver().query(calendarUri, projection, null, null, null);    //all calendars
            //Cursor managedCursor = context.getContentResolver().query(calendarUri, projection, "selected=1", null, null);   //active calendars
            assert managedCursor != null;
            if (managedCursor.moveToFirst()) {
                String calName;
                String calId;
                int nameCol = managedCursor.getColumnIndex(projection[1]);
                int idCol = managedCursor.getColumnIndex(projection[0]);
                do {
                    calName = managedCursor.getString(nameCol);
                    calId = managedCursor.getString(idCol);
                    Calendars cal = new Calendars(calName, calId);
                    if (!AppointmentWidgetProvider.storeData.calendarList.contains(cal)) {
                        AppointmentWidgetProvider.storeData.calendarList.add(cal);
                    }
                } while (managedCursor.moveToNext());
            }
        }
    }

    static void getCalendars(Context context) {
        AppointmentWidgetProvider.storeData = StorageProvider.read(context);
        if (AppointmentWidgetProvider.storeData == null) {
            AppointmentWidgetProvider.storeData = new StoreData();
        }

        processCalendars(context);

        StorageProvider.persist(context, AppointmentWidgetProvider.storeData);
    }

    private static void processEvents(Context context) {
        String[] projection = new String[]{"title", "dtstart", "dtend", "rdate", "duration"};
        String selection = "calendar_id=? AND dtstart>=?";// AND dtend<=?";
        String sortOrder = "dtstart ASC, dtend ASC";
        Uri eventUri = Uri.parse("content://com.android.calendar/events");
        addAppointments(context, projection, selection, sortOrder, eventUri);
    }

    /**
     * Add appointments to the widget
     */
    private static void addAppointments(Context context, String[] projection, String selection,
                                        String sortOrder, Uri eventUri) {

        String[] selectionArgs;
        synchronized (AppointmentWidgetProvider.events) {
            AppointmentWidgetProvider.events.clear();
            String starttime = Long.toString(GregorianCalendar.getInstance().getTimeInMillis());
            String endtime = Long.toString(getFutureTime());
            for (Calendars calendar : AppointmentWidgetProvider.storeData.calendarList) {
                if (calendar.enabled) {
                    selectionArgs = new String[]{calendar.id, starttime};//, endtime };
                    Cursor managedCursor = context.getContentResolver().query(eventUri, projection, selection, selectionArgs, sortOrder);
                    if (managedCursor.moveToFirst()) {
                        String title;
                        String begin;
                        String end;
                        String recurring;
                        String duration;
                        int colTitle = managedCursor.getColumnIndex(projection[0]);
                        int colBegin = managedCursor.getColumnIndex(projection[1]);
                        int colEnd = managedCursor.getColumnIndex(projection[1]);
                        int colRecurring = managedCursor.getColumnIndex(projection[3]);
                        int colDuration = managedCursor.getColumnIndex(projection[4]);
                        do {
                            title = managedCursor.getString(colTitle);
                            begin = managedCursor.getString(colBegin);
                            end = managedCursor.getString(colEnd);
                            recurring = managedCursor.getString(colRecurring);
                            duration = managedCursor.getString(colDuration);
                            if (/*isBeforeToday(begin) || */isFarInFuture(begin)) {
                                continue;
                            }
                            CalendarEntry event = new CalendarEntry(title, begin, end);
                            if (!AppointmentWidgetProvider.events.contains(event)) {
                                AppointmentWidgetProvider.events.add(event);
                            }
                        } while (managedCursor.moveToNext());
                    }
                }
            }
            if (AppointmentWidgetProvider.events.size() > 0) {
                Collections.sort(AppointmentWidgetProvider.events, new EventComparator());
            }
        }
    }

    private static void getEvents(Context context) {
        processEvents(context);
    }

    private static void setText(RemoteViews views) {

        views.setTextViewText(R.id.textRow1, "");
        views.setTextViewText(R.id.textRow2, "");
        views.setTextViewText(R.id.textRow3, "");
        views.setTextViewText(R.id.textRow4, "");

        int cnt = 0;
        for (CalendarEntry entry : AppointmentWidgetProvider.events) {

            StringBuilder displayText = new StringBuilder();
            String title = entry.name;
            String begin = getDateTimeStr(entry.start);
            displayText.append(title + " (" + begin + ")");
            switch (cnt) {
                case 0:
                    views.setTextViewText(R.id.textRow1, displayText.toString());
                    break;
                case 1:
                    views.setTextViewText(R.id.textRow2, displayText.toString());
                    break;
                case 2:
                    views.setTextViewText(R.id.textRow3, displayText.toString());
                    break;
                case 3:
                    views.setTextViewText(R.id.textRow4, displayText.toString());
                    break;
                default:
                    return;
            }
            cnt++;
        }
    }

    private static boolean isBeforeToday(String time) {
        Date dTime = new Date(Long.parseLong(time));
        return Calendar.getInstance().getTimeInMillis() > dTime.getTime();
    }

    private static boolean isFarInFuture(String time) {
        Date dTime = new Date(Long.parseLong(time));
        return getFutureTime() < dTime.getTime();
    }

    private static long getFutureTime() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 14);
        return c.getTimeInMillis();
    }

    private static String getDateTimeStr(String timeMillis) {
        DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
        Date time = new Date(Long.parseLong(timeMillis));
        return sdf.format(time);
    }

}
