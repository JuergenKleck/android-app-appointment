package info.simplyapps.app.appointment;

import java.util.Comparator;


public class EventComparator implements Comparator<CalendarEntry> {

    public int compare(CalendarEntry lhs, CalendarEntry rhs) {
        return lhs.start.compareTo(rhs.start);
    }

}
