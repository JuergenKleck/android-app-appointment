package info.simplyapps.app.appointment;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class AppointmentUpdateService extends Service {

    public Intent savedIntent;
    private BackgroundThread background;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (background != null) {
            background.interrupt();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        savedIntent = intent;
        doThreadStart();
        AppointmentWidgetUpdater.updateAppWidget(getApplicationContext());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void doThreadStart() {
        background = new BackgroundThread();
        background.start();
    }

    private class BackgroundThread extends Thread {
        public void run() {
            AppointmentWidgetUpdater.updateAppWidget(getApplicationContext());
            stopSelf();
        }
    }
}