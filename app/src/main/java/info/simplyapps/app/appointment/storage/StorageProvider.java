package info.simplyapps.app.appointment.storage;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Class which stores data into the internal Storage of the Android system
 */
public class StorageProvider {

    private static final String FILENAME = "appointment_data";

    public StorageProvider() {
    }

    public static void persist(Context context, StoreData container) {
        try (FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(container);
        } catch (IOException ignored) {
        }
    }

    public static StoreData read(Context context) {
        FileInputStream fos = null;
        ObjectInputStream oos = null;
        StoreData container = null;
        try {
            fos = context.openFileInput(FILENAME);
            oos = new ObjectInputStream(fos);

            container = (StoreData) oos.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (fos != null)
                    fos.close();
            } catch (Exception ignored) {  }
        }
        return container;
    }

}
