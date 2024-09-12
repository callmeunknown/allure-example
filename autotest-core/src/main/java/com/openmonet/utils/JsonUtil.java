package com.openmonet.utils;

import com.bettercloud.vault.json.Json;
import com.bettercloud.vault.json.ParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import io.qameta.allure.Allure;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;
import static com.openmonet.utils.ErrorMessage.*;

public class JsonUtil {

    /**
     * извлекает данные по json path из json
     *
     * @param body     json
     * @param jsonPath json path
     * @return значение из json
     */
    public static String getFieldFromJson(String body, String jsonPath) {
        String val;
        try {
            val = parseJsonPath(body, replaceVarsIfPresent(jsonPath));
        } catch (InvalidJsonException e) {
            Allure.addAttachment("INVALID JSON", "application/json", body, ".txt");
            throw new InvalidJsonException(INVALID_JSON);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(getErrorMessage(ILLEGAL_JSON, body));
        } catch (PathNotFoundException pathNotFoundException) {
            throw new AssertionError(String.format("\nНе найдено значение по jsonpath.\nВыражение для поиска:%s\nJson:\n%s\n", jsonPath, body));
        } catch (InvalidPathException invalidPathException) {
            throw new AssertionError(String.format("\nУказан невалидный  jsonpath.\nВыражение для поиска:%s\nJson:\n%s\n", jsonPath, body));
        }
        return val;
    }

    /**
     * Ищет значение в переданном json по jsonpath
     * @param body  -   json-тело
     * @param jsonPath  -   выражение для поиска
     * @return  -   найденное значение
     */
    private static String parseJsonPath(String body, String jsonPath) {
        String value;
        Configuration jacksonConfig = Configuration.builder()
                .mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonProvider())
                .build();
        JsonNode jsonNode = JsonPath.using(jacksonConfig).parse(body).read(replaceVarsIfPresent(jsonPath), JsonNode.class);
        if (jsonNode != null) {
            value = formatJsonNode(jsonNode.toString());
        } else {
            value = "null";
        }
        return value;
    }

    /**
     * Убирает кавычки внутри массива у всех элементов
     * @param jsonNode  -   нода-массив
     * @return  -   отформатированный массив
     */
    private static String formatJsonNode(String jsonNode) {
        Iterable<String> notFormatted = Splitter.on(CharMatcher.anyOf(",[]")).omitEmptyStrings().split(jsonNode);
        StringBuilder formattedNode = null;
        boolean isArray = false;
        for (String line: notFormatted) {
            line = line.replaceAll("[\\\\][\"]", "\"");
            String lineResult = line.replaceAll("^[\"]", "").replaceAll("[\"]$","");
            if (formattedNode == null) {
                formattedNode = new StringBuilder();
            } else {
                isArray = true;
                formattedNode.append(",");
            }
            formattedNode.append(lineResult);
        }
        if(isArray) {
            formattedNode = new StringBuilder().append("[").append(formattedNode).append("]");
        }
        return formattedNode == null ? "" : formattedNode.toString();
    }

    public static List<String> getFieldsListFromJson(String body, String jsonPath) {
        Configuration jacksonConfig = Configuration.builder()
                .mappingProvider(new JacksonMappingProvider())
                .jsonProvider(new JacksonJsonProvider())
                .build();
        List node;
        try {
            node = JsonPath.using(jacksonConfig).parse(body).read(replaceVarsIfPresent(jsonPath), List.class);
        } catch (InvalidJsonException e) {
            Allure.addAttachment("INVALID JSON", "application/json", body, ".txt");
            throw new InvalidJsonException(INVALID_JSON);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(getErrorMessage(ILLEGAL_JSON, body));
        }

        return node;
    }

    /**
     * парсит к utf-8
     *
     * @param text текст
     * @return текст в utf-8 (насколько это возможно)
     */
    public static String jsonToUtf(String text) {
        try {
            return Json.parse(text).toString();
        } catch (ParseException e) {
            return new String(text.getBytes(), StandardCharsets.UTF_8);
        }
    }
}
