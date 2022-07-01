package fr.luzog.pl.fkx.utils;

import java.text.DecimalFormat;

public enum DayMoment {

    SUNRISE(0), SM(1500),
    MORNING(3000), MM(7500),
    MIDDAY(6000), MA(8500),
    AFTERNOON(9000), AS(10500),
    SUNSET(12000), SE(13500),
    EVENING(15000), EM(16500),
    MIDNIGHT(18000), MN(19500),
    NIGHT(21000), NS(22500);

    private int hour;

    DayMoment(int i) {
        setHour(i);
    }

    public int getHour() {
        return hour;
    }

    private void setHour(int hour) {
        this.hour = hour;
    }

    public String getFormattedName() {
        return name().length() == 1 ? name().toUpperCase() : name().toUpperCase().charAt(0) + name().toLowerCase().substring(1);
    }

    public String getFormattedTime() {
        DecimalFormat df = new DecimalFormat("00");
        return df.format((int) (hour / 1200)) + ":" + df.format((int) ((hour % 1200) / 20));
    }

    @Override
    public String toString() {
        return name().length() == 1 ? name().toLowerCase() : name().toUpperCase().charAt(0) + name().toLowerCase().substring(1);
    }

    public static DayMoment match(String s) {
        for (DayMoment d : DayMoment.values())
            if (d.toString().equalsIgnoreCase(s))
                return d;
        return null;
    }

    public static DayMoment getByHour(int hour) {
        for (DayMoment dm : values())
            if (dm.getHour() == hour)
                return dm;
        return null;
    }

    public static DayMoment getApproxByHour(int hour) {
        DayMoment m = null;
        int diff = Integer.MAX_VALUE;
        for (DayMoment dm : values())
            if (Math.abs(dm.getHour() - hour) < diff) {
                diff = Math.abs(dm.getHour() - hour);
                m = dm;
            }
        return m;
    }

}
