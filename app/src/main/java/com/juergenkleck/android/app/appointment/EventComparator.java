package com.juergenkleck.android.app.appointment;

import java.util.Comparator;

/**
 * Android App - Appointment
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class EventComparator implements Comparator<CalendarEntry> {

    public int compare(CalendarEntry lhs, CalendarEntry rhs) {
        return lhs.start.compareTo(rhs.start);
    }

}
