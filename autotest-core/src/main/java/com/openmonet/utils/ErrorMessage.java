package com.openmonet.utils;

/**
 * Класс для сообщений ошибок
 */
public class ErrorMessage {

    //region общие ошибки внутри проекта
    public static final String NOT_VALID_VALUE_IN_CONTEXT = "\nНе задано значение для переменной '%s'!\n в контексте по переменной '%s', ее значение: '%s'";
    public static final String VARIABLE_NOT_VALID = "Переменная '%s' ссылается на значение '%s'";
    public static final String NO_SUCH_FILE = "Не найден файл '%s' в директории '%s'";
    public static final String FOUND_MORE_ONE_FILE = "Найдено более 1 файла с наименованием %s по пути: ";
    public static final String FRAGMENT_ERROR = "Фрагмент '%s' должны были заменить, но что-то пошло не так!";
    public static final String FOUND_MORE_ONE_FRAGMENT = "Найдено более одного фрагмента с наименованием и путем:\n%s";
    public static final String FRAGMENT_NOT_EXIST = "Нет такого фрагмента с названием - '%s'.";
    public static final String LANGUAGE_IS_NOT_SAME = "Язык фрагмента не совпадает с языком сценария! Сценарий: '%s', Фрагмент: '%s'";
    public static final String CHECK_FEATURE_SYNTAX = "\nПроверьте синтаксис фича-файла!:\n%s\n";
    public static final String FEATURE_KEYWORD_NOT_EXIST = "Нет кейворда Функционал!";
    public static final String DATA_TAG_MORE_ONE = "Есть два тега '%s' в сценарии '%s'. Необходимо указать только один тег для данных!\n";
    public static final String ERRORS_NOT_FOUND = "Не найдены следующие ошибки: '%s'\n Ожидаемые ошибки: \n%s\nФактические ошибки: \n%s\n";
    public static final String SOMETHING_WRONG = "Что-то пошло не так!\n Ожидаемые ошибки: \n%s\nФактические ошибки: \n%s\n";
    public static final String NOT_FOUND_VARS_FOR_DYNAMIC_FRAGMENTS = "Следующие переменные не были переданы в динамический фрагмент из сценария '%s' для шага '%s': %s\n";
    public static final String FEATURE_PARSE_EXCEPTION = "Что-то пошло не так при парсинге фичи!\n%s";
    public static final String QUITE_NOT_EQUALS = "Сравнивались два значения для переменной '%s'\n" +
            "Ожидаемое: '%s'\nФактическое: '%s'\nОператор сравнения: '%s'\n";
    public static final String ACTUAL_VALUE_NOT_EQUAL_EXPECTED = "\nАктуальное значение не соответствует ожидаемому значению." +
            "\nАктуальное значение - '%s'.\nОжидаемое значение - '%s'.\n";
    public static final String NOT_FOUND_VALUE_BY_REGEX = "По регулярному выражению ничего не найдено. " +
            "Регулярное выражение '%s', номер группы '%s'.\n";
    public static final String VALUE_NOT_FOUND_BY_PATH = "По пути '%s' не найдено значение в файле '%s'.\n";
    public static final String UNEXPECTED_VALUE_TYPE = "Неожиданный тип значения: %s.\n";
    //endregion

    //region API-запросы
    public static final String INVALID_JSON = "Невалидный json.\nСам json лежит в аттаче.";
    public static final String ERROR_CONVERTING_STRING_TO_JSON = "\n\tОшибка конвертации строки в JSON объект.\nСодержимое json: %s\n";
    public static final String UNKNOWN_HOST = "Неизвестный хост - %s.\n";
    public static final String WRONG_STATUS = "\nОшибка выполнения запроса в методе.\n\tОжидаемый статус код: %s\n\tПолученный статус код: %s";
    public static final String ILLEGAL_PARAMETER = "\nПараметр '%s' является неверным для метода '%s'.\n";
    public static final String HEADER_NOT_FOUND = "В ответе на запрос нет хидера \"%s\". \n Список хидеров из ответа: %s";
    public static final String ILLEGAL_JSON = "json имеет невалидное значение '%s'";
    //endregion

    //region WEB
    public static final String URL_NOT_EQUAL_ACTUAL = "Ожидаемый url '%s' не равен текущему '%s'";
    public static final String URL_NOT_CONTAINS_TEXT = "Текущий url '%s' не содержит в себе '%s'";
    public static final String URL_NO_MATCHES = "Текущий url '%s' не совпадает с шаблоном '%s'.";

    public static final String BROWSER_TAB_NUMBER_MORE_THAN_TABS = "Номер '%d' переданной вкладки больше, чем вкладок";
    public static final String TEXT_IS_NOT_PRESENT = "\nТекст не найден: \n %s\n";
    public static final String PERIODIC_CLICK_ELEMENT_TEXT_IS_NOT_PRESENT = "Нажатие на элемент было выполнено '%s' раз и не найден элемент '%s' в течение '%s' секунд";
    public static final String ELEMENT_IS_NOT_DISPLAYED = "\nЭлемент \"%s\" не отображен\n";
    public static final String ATTRIBUTE_ACTUAL_VALUE_NOT_EQUAL_EXPECTED = "\nАктуальное значение атрибута \"%s\" не соответствует ожидаемому значению." +
            "\nАктуальное значение - '%s'.\nОжидаемое значение - '%s'.\n";
    //endregion


    public static String getErrorMessage(String template, Object... vars) {
        return String.format(template, vars);
    }

    public static AssertionError getAssertError(String errorMessage, Object... vars) {
        return new AssertionError(getErrorMessage(errorMessage, vars));
    }

}