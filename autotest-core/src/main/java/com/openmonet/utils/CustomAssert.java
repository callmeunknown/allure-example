package com.openmonet.utils;

import org.testng.asserts.SoftAssert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class CustomAssert extends SoftAssert {

    /**
     * вывод всех ошибок без повторений
     * @param message сообщение перед всеми ошибками
     */
    @Override
    public void assertAll(String message) {
        HashSet<String> set = getErrorMessages(message);
        if (set != null && !set.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            set.forEach(p -> builder.append(p).append(",\n"));
            String errorMessages = builder.toString();
            errorMessages = errorMessages.substring(0, errorMessages.lastIndexOf(","));
            throw new AssertionError(message + errorMessages);
        }
    }

    /**
     * получение коллекции ошибок без повторений
     * @param message сообщение перед всеми ошибками
     * @return коллекция ошибок без повторений и без message
     */
    public HashSet<String> getErrorMessages(String message) {
        try {
            super.assertAll(message);
        } catch (AssertionError error) {
            String errorMessage = error.getMessage();
            List<String> errors = Arrays.stream(errorMessage.split(",\n"))
                    .map(p -> {
                        p = p.replaceAll("\t", "").replaceAll("\n", "");
                        if (p.startsWith(message)) {
                            p = p.replace(message, "");
                        }
                        return p;
                    }).collect(Collectors.toList());
            HashSet<String> errorsSet = new HashSet<>();
            errors.forEach(p -> errorsSet.add(p.trim()));
            return errorsSet;
        }
        return null;
    }
}
