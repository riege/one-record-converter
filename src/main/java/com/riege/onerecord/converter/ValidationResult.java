package com.riege.onerecord.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationResult {

    private List<ValidationMessage> messageList = new ArrayList<>();

    /**
     * @Deprecated in version 0.10, use {@link #addHint(String, String)} instead
     */
    @Deprecated
    public void addHint(String message) {
        messageList.add(new ValidationMessage(ValidationMessage.Severity.HINT, message));
    }

    public void addHint(String group, String detail) {
        messageList.add(new ValidationMessage(ValidationMessage.Severity.HINT, group, detail));
    }

    /**
     * @Deprecated in version 0.10, use {@link #addWarning(String, String)} instead
     */
    @Deprecated
    public void addWarning(String message) {
        messageList.add(new ValidationMessage(ValidationMessage.Severity.WARNING, message));
    }

    public void addWarning(String group, String detail) {
        messageList.add(new ValidationMessage(ValidationMessage.Severity.WARNING, group, detail));
    }

    /**
     * @Deprecated in version 0.10, use {@link #addError(String, String)} instead
     */
    @Deprecated
    public void addError(String message) {
        messageList.add(new ValidationMessage(ValidationMessage.Severity.ERROR, message));
    }

    public void addError(String group, String detail) {
        messageList.add(new ValidationMessage(ValidationMessage.Severity.ERROR, group, detail));
    }

    public List<ValidationMessage> getHints() {
        return messageList.stream()
            .filter(ValidationMessage::isHint)
            .collect(Collectors.toList());
    }

    public List<ValidationMessage> getWarnings() {
        return messageList.stream()
            .filter(ValidationMessage::isWarning)
            .collect(Collectors.toList());
    }

    public List<ValidationMessage> getErrors() {
        return messageList.stream()
            .filter(ValidationMessage::isError)
            .collect(Collectors.toList());
    }

}
