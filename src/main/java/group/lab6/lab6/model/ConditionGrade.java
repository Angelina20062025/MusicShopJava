package group.lab6.lab6.model;

//Оценка состояния винила или обложки
public enum ConditionGrade {
    MINT("Идеальное"),
    NEAR_MINT("Почти идеальное"),
    VERY_GOOD("Очень хорошее"),
    GOOD("Хорошее"),
    FAIR("Удовлетворительное"),
    POOR("Плохое");

    private final String rusName;

    ConditionGrade(String rusName) {
        this.rusName = rusName;
    }

    public String getRusName() {
        return rusName;
    }

    public static ConditionGrade fromDbValue(String dbValue) {
        if (dbValue == null) return null;
        return switch (dbValue) {
            case "mint" -> MINT;
            case "near_mint" -> NEAR_MINT;
            case "very_good" -> VERY_GOOD;
            case "good" -> GOOD;
            case "fair" -> FAIR;
            case "poor" -> POOR;
            default -> throw new IllegalArgumentException("Unknown condition: " + dbValue);
        };
    }

    public String toDbValue() {
        return switch (this) {
            case MINT -> "mint";
            case NEAR_MINT -> "near_mint";
            case VERY_GOOD -> "very_good";
            case GOOD -> "good";
            case FAIR -> "fair";
            case POOR -> "poor";
        };
    }

    @Override
    public String toString() {
        return rusName;
    }
}