package group.lab6.lab6.model;

//Способ оплаты
public enum PaymentMethod {
    CASH("Наличные"),
    CARD("Карта"),
    ONLINE("Онлайн");

    private final String rusName;

    PaymentMethod(String rusName) {
        this.rusName = rusName;
    }

    public String getRusName() {
        return rusName;
    }

    public static PaymentMethod fromDbValue(String dbValue) {
        if (dbValue == null) return null;
        return switch (dbValue) {
            case "cash" -> CASH;
            case "card" -> CARD;
            case "online" -> ONLINE;
            default -> throw new IllegalArgumentException("Unknown payment method: " + dbValue);
        };
    }

    public String toDbValue() {
        return switch (this) {
            case CASH -> "cash";
            case CARD -> "card";
            case ONLINE -> "online";
        };
    }

    @Override
    public String toString() {
        return rusName;
    }
}