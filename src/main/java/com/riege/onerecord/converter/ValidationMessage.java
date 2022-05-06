package com.riege.onerecord.converter;

public class ValidationMessage {

    public enum Severity {
        HINT, WARNING, ERROR;
    }

    private final Severity severity;
    private final String group;
    private final String detail;

    public ValidationMessage(Severity severity, String message) {
        this(severity, null, message);
    }

    public ValidationMessage(Severity severity, String group, String detail) {
        this.severity = severity;
        this.group = group;
        this.detail = detail;
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

    public String getGroup() {
        return group;
    }

    public String getDetail() {
        return detail;
    }

    /**
     * Deprecated in version 1.0 use {@link #getGroup()} and {@link #getDetail()} instead
     *
     * @return validation message as string, prefixed with group
     */
    @Deprecated
    public String getMessage() {
        return group == null ? detail : "[" + group + "] " + detail;
    }

}
