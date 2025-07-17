package com.ap.stardew.models.enums;

public enum WeekDay {
    SUNDAY("SUN"),
    MONDAY("MON"),
    TUESDAY("TUE"),
    WEDNESDAY("WED"),
    THURSDAY("THU"),
    FRIDAY("FRI"),
    SATURDAY("SAT");

    private final String shortName;

    WeekDay(String shortName) {
        this.shortName = shortName;
    }

    public static WeekDay getWeekDay(int day) {
        switch (day) {
            case 1 -> {
                return SUNDAY;
            }
            case 2 -> {
                return MONDAY;
            }
            case 3 -> {
                return TUESDAY;
            }
            case 4 -> {
                return WEDNESDAY;
            }
            case 5 -> {
                return THURSDAY;
            }
            case 6 -> {
                return FRIDAY;
            }
            case 0 -> {
                return SATURDAY;
            }
        }

        return null;
    }

    public String getShortName() {
        return shortName;
    }
}
