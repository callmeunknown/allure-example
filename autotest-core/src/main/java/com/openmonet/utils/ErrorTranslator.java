package com.openmonet.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ErrorTranslator extends Exception {

    /**
     * принимает на вход ошибку
     * проверяет тип ошибки, вызвает соответствующий типу метод
     * возвращает текст "переведенной" ошибку
     */
    public static String errorMessageValue(Throwable e) {
        String errText = ExceptionUtils.getRootCauseMessage(e);
        String errValue = errText.split(":")[0];
        if (errValue.equals("NoSuchElementException")) {
            return noSuchElementException(errText);
        }
        if (errValue.equals("ElementShould")) {
            return elementShould(errText);
        }
        return errText;
    }

    /**
     * Переводит ошибки типа elementShould
     */
    private static String elementShould(String errText) {
        /**
         * Element should be exact text"
         */
        if (StringUtils.substringAfter(errText, ":").trim().startsWith("Element should be exact text")) {
            String locatorText = errText.substring(errText.indexOf("{") + 1, errText.lastIndexOf("}"));
            String expectedText = errText.substring(errText.indexOf("text case sensitive '") + 21, errText.lastIndexOf("' ") - 1);
            String errorText = String.format("Элемент с локатором - '%s' должен содержать текст '%s' но не содержит его", locatorText, expectedText);
            return (errorText);
        }
        /**
         * Element should be enabled
         */
        if (StringUtils.substringAfter(errText, ":").trim().startsWith("Element should be enabled")) {
            String locatorText = errText.substring(errText.indexOf("{") + 1, errText.lastIndexOf("}"));
            String errorText = String.format("Элемент с локатором - '%s' должен быть доступен", locatorText);
            return (errorText);
        }

        /**
         * Element should be visible
         */
        if (StringUtils.substringAfter(errText, ":").trim().startsWith("Element should be visible")) {
            String locatorText = errText.substring(errText.indexOf("{") + 1, errText.lastIndexOf("}"));
            String errorText = String.format("Элемент с локатором - '%s' должен быть видимый", locatorText);
            return (errorText);
        }

        /**
         * Element should be disable
         */
        if (StringUtils.substringAfter(errText, ":").trim().startsWith("Element should be disable")) {
            String locatorText = errText.substring(errText.indexOf("{") + 1, errText.lastIndexOf("}"));
            String errorText = String.format("Элемент с локатором - '%s' должен быть недоступен", locatorText);
            return (errorText);
        }

        /**
         * Element should have attribute
         */
        if (StringUtils.substringAfter(errText, ":").trim().startsWith("Element should have attribute")) {
            String locatorText = errText.substring(errText.indexOf("{") + 1, errText.lastIndexOf("}"));
            String attributeName = StringUtils.substringBetween(errText, "have attribute", "=").trim();
            String expectedValue = StringUtils.substringBetween(errText, "=\"", "\"");
            String actualValue = StringUtils.substringBetween(errText, "Actual value: value=\"", "\"");
            String errorText = String.format("Аттрибут '%s' элемента с локатором '%s' должен содержать значение '%s', а содержит '%s'", attributeName, locatorText, expectedValue, actualValue);

            return (errorText);
        }
        return (errText);
    }

    /**
     * Переводит ошибки типа NoSuchElementException
     */
    private static String noSuchElementException(String errText) {
        String locatorText = errText.substring(errText.indexOf("{") + 1, errText.lastIndexOf("}") - 1);
        return (String.format("не найден элемент интерфейса с локатором - '%s'", locatorText));
    }

    /**
     * конструктор новой Throwable ошибки
     */
    public ErrorTranslator(String message, Throwable cause) {
        super(message, cause);
    }

}