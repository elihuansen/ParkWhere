package io.parkwhere.exceptions;

public class MultipleFirstBlockException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Multiple 'First Block' rates detected.";
    }
}
