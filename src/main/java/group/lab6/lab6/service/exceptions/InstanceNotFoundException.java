package group.lab6.lab6.service.exceptions;

public class InstanceNotFoundException extends BusinessException {

    public InstanceNotFoundException(String message) {
        super(message);
    }

    public InstanceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}