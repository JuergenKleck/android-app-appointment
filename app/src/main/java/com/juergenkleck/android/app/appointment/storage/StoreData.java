package com.juergenkleck.android.app.appointment.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.juergenkleck.android.app.appointment.Calendars;

/**
 * Android App - Appointment
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class StoreData implements Serializable {

    private static final long serialVersionUID = 5696810296031292823L;

    public List<Calendars> calendarList;

    public StoreData() {
        calendarList = new ArrayList<>();
    }

}
