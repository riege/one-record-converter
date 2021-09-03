package com.riege.onerecord.converter;

public class ValidationMessage {

    public enum Severity {
        HINT, WARNING, ERROR;
    }

    private final Severity severity;
    private final String message;

    public ValidationMessage(Severity severity, String message) {
        this.severity = severity;
        this.message = message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public boolean isHint() {
        return severity == Severity.HINT;
    }

    public boolean isWarning() {
        return severity == Severity.WARNING;
    }

    public boolean isError() {
        return severity == Severity.ERROR;
    }

    public boolean isSeverity(Severity compareTo) {
        return severity == compareTo;
    }

    public String getMessage() {
        return message;
    }

}
