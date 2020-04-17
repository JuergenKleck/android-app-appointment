package info.simplyapps.app.appointment.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import info.simplyapps.app.appointment.Calendars;

public class StoreData implements Serializable {

    private static final long serialVersionUID = 5696810296031292823L;

    public List<Calendars> calendarList;

    public StoreData() {
        calendarList = new ArrayList<>();
    }

}
