package group.lab6.lab6.model;

//Статус экземпляра пластинки
public enum InstanceStatus {
    IN_STOCK("в наличии"),
    SOLD("продано");

    private final String rusName;

    InstanceStatus(String rusName) {
        this.rusName = rusName;
    }

    public String getRusName() {
        return rusName;
    }

    public static InstanceStatus fromDbValue(String dbValue) {
        if (dbValue == null) return null;
        return switch (dbValue) {
            case "in_stock" -> IN_STOCK;
            case "sold" -> SOLD;
            default -> throw new IllegalArgumentException("Unknown status: " + dbValue);
        };
    }

    public String toDbValue() {
        return switch (this) {
            case IN_STOCK -> "in_stock";
            case SOLD -> "sold";
        };
    }

    @Override
    public String toString() {
        return rusName;
    }
}