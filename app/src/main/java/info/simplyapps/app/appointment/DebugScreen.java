package info.simplyapps.app.appointment;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;


public class DebugScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Button button = new Button(getApplicationContext());
        button.setText("Update");
        button.setOnClickListener(v -> AppointmentWidgetUpdater.updateAppWidget(getApplicationContext()));

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.addView(button);
        this.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

    }

}