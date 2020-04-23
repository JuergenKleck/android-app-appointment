package info.simplyapps.app.appointment;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TableLayout;

import info.simplyapps.app.appointment.storage.StorageProvider;
import info.simplyapps.appengine.screens.GenericScreenTemplate;

public class AppointmentConfigurationActivity extends GenericScreenTemplate {

    private TableLayout lTable;
    private int mAppWidgetId;

    @Override
    public int getScreenLayout() {
        return R.layout.configure;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void prepareStorage(Context context) {

    }

    @Override
    public void onPermissionResult(String permission, boolean granted) {
        if (granted) {
            if (AppointmentWidgetProvider.storeData.calendarList.isEmpty()) {
                AppointmentWidgetUpdater.getCalendars(getApplicationContext());
            }
            updateLists();
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionHelper.checkPermission(getApplicationContext(), this, Manifest.permission.READ_CALENDAR, Boolean.TRUE);

        lTable = findViewById(R.id.tableLayoutCalendars);
        Button bAdd = findViewById(R.id.btn_close);
        bAdd.setOnClickListener(onButtonClose);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (AppointmentWidgetProvider.storeData.calendarList.isEmpty()) {
            AppointmentWidgetUpdater.getCalendars(getApplicationContext());
        }
        updateLists();
    }

    public void updateLists() {
        lTable.removeAllViews();
        for (Calendars entry : AppointmentWidgetProvider.storeData.calendarList) {
            View row;
            // ROW INFLATION
            LayoutInflater inflater = (LayoutInflater) this.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            row = inflater.inflate(R.layout.calendaritem, lTable, false);
            if (row != null) {
                CheckBox txtField;

                // Get reference to TextView
                txtField = row.findViewById(R.id.calendarlistitem_textview);
                //set value into the list
                txtField.setText(entry.name);
                txtField.setOnClickListener(mCheckboxSelectListener);
                txtField.setChecked(entry.enabled);
                lTable.addView(row);
            }
        }
    }


    public static OnClickListener mCheckboxSelectListener = v -> {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        CheckBox child = (CheckBox) vwParentRow.getChildAt(0);
        String selected = child.getText().toString();
        for (Calendars cal : AppointmentWidgetProvider.storeData.calendarList) {
            if (cal.name.equals(selected)) {
                cal.enabled = child.isChecked();
            }
        }
    };

    OnClickListener onButtonClose = new OnClickListener() {
        public void onClick(View v) {
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            StorageProvider.persist(getApplicationContext(), AppointmentWidgetProvider.storeData);
            AppointmentWidgetUpdater.updateAppWidget(getApplicationContext(), mAppWidgetId);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.appprovider);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);
        super.onDestroy();
    }


}