package com.juergenkleck.android.app.appointment;

import java.io.Serializable;

/**
 * Android App - Appointment
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class Calendars implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2642379643455015190L;

    public String name;
    public String id;
    public boolean enabled = false;

    public Calendars(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        return Calendars.class.isInstance(o) && Calendars.class.cast(o).name != null ? Calendars.class.cast(o).name.equals(this.name) : false;
    }

}
