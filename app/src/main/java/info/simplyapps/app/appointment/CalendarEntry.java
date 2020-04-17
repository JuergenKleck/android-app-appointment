package info.simplyapps.app.appointment;

public class CalendarEntry {

    public String name;
    public String start;
    public String end;

    public CalendarEntry(String name, String start, String end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        return CalendarEntry.class.isInstance(o) ? CalendarEntry.class.cast(o).name.equals(this.name) && CalendarEntry.class.cast(o).start.equals(this.start) : false;
    }


}
