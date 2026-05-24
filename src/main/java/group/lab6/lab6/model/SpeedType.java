package group.lab6.lab6.model;

//Скорость вращения пластинки
public enum SpeedType {
    RPM_33("33 об/мин"),
    RPM_45("45 об/мин"),
    RPM_78("78 об/мин");

    private final String displayName;

    SpeedType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SpeedType fromDbValue(String dbValue) {
        if (dbValue == null) return null;
        return switch (dbValue) {
            case "33" -> RPM_33;
            case "45" -> RPM_45;
            case "78" -> RPM_78;
            default -> throw new IllegalArgumentException("Unknown speed: " + dbValue);
        };
    }

    public String toDbValue() {
        return switch (this) {
            case RPM_33 -> "33";
            case RPM_45 -> "45";
            case RPM_78 -> "78";
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}