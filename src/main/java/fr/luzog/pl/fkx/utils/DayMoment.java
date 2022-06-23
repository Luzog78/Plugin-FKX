package fr.luzog.pl.fkx.utils;

public enum DayMoment {

    SUNRISE(0), MORNING(3000), MIDDAY(6000), AFTERNOON(9000), SUNSET(12000), EVENING(15000), MIDNIGHT(18000), NIGHT(21000);

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
