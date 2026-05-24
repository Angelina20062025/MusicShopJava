package group.lab6.lab6.model;

//Формат пластинки
public enum FormatType {
    LP("LP"),
    EP("EP"),
    SINGLE("Single"),
    MAXI_SINGLE("Maxi-Single");

    private final String displayName;

    FormatType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static FormatType fromDbValue(String dbValue) {
        if (dbValue == null) return null;
        return switch (dbValue) {
            case "LP" -> LP;
            case "EP" -> EP;
            case "Single" -> SINGLE;
            case "Maxi_Single" -> MAXI_SINGLE;
            default -> throw new IllegalArgumentException("Unknown format: " + dbValue);
        };
    }

    public String toDbValue() {
        return this.name();
    }

    @Override
    public String toString() {
        return displayName;
    }
}