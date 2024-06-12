package org.gauravagrwl.financeData.exceptions;

public class FinanceAppException extends RuntimeException {

    public FinanceAppException(String message) {
        super(message);
    }

    public FinanceAppException(String format, Object... args) {
        super(String.format(format, args));
    }
}
