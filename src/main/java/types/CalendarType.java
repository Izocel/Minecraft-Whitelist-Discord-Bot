package types;

public enum CalendarType {
    REFERRAL("referral"),
    TREASURE("treasure"),
    LEVEL("level");

    private final String type;

    CalendarType(String color) {
        this.type = color;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return getType();
    }
}
