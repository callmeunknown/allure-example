package com.openmonet.utils;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VariableUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(VariableUtil.class);

    /**
     * замена контекстных переменных на значения
     *
     * @param preBody текст
     * @param vars    контекстные переменные
     * @return значение
     */
    public static String replaceVars(String preBody, Map<String, Object> vars) {
        StringBuilder replacedText = new StringBuilder(preBody);
        StringBuilder replacedVars = new StringBuilder();

        final String patternStartVar = "%{";
        final String patternEndVar = "}%";

        while (true) {
            int fi = replacedText.indexOf(patternStartVar);
            if (fi == -1) {
                break;
            }
            int li = replacedText.indexOf(patternEndVar, fi);
            if (li == -1) {
                throw new AssertionError("\nПроверьте синтаксис!\nНет закрывающего символа '}%' в тексте: \n" + replacedText.toString());
            }
            String var = replacedText.substring(fi + patternStartVar.length(), li);
            if (vars.containsKey(var)) {
                checkVariables(var, vars);
                replacedText.replace(fi, li + 2, vars.get(var).toString());
                replacedVars
                        .append(patternStartVar)
                        .append(var)
                        .append(patternEndVar)
                        .append("->")
                        .append(vars.get(var).toString())
                        .append("\n");
                LOGGER.debug(replacedVars.toString());
            } else {
                break;
            }
        }
        return replacedText.toString();
    }

    /**
     * проверка есть эта переменная в контексте
     *
     * @param var  наименование переменой
     * @param vars контекст переменных
     */
    private static void checkVariables(String var, Map<String, Object> vars) {
        if (RegexUtil.getMatch(vars.get(var).toString(), ".*%\\{(\\w*)}%.*")) {
            checkVariable(".*%\\{(\\w*)}%.*", 1, vars, var, 2, 2);
        }
    }

    /**
     * проверка есть ли переменная в пуле контекста и не ссылается ли она на себя саму
     *
     * @param regex      регулярное выражение для указания контектной переменной
     * @param regexNumb  номер группы для нахоэжения по регулярке
     * @param variables  пул переменных контекста
     * @param variable   переменная
     * @param firstIndex с какого символа идет наименование переменной
     * @param lastIndex  сколько символов занимает обозначение переменной в конце
     */
    private static void checkVariable(String regex, int regexNumb, Map<String, Object> variables, String variable, int firstIndex, int lastIndex) {
        String nameVar = RegexUtil.getMatchValueByGroupNumber(variables.get(variable).toString(), regex, regexNumb);
        if (nameVar != null) {
            // то что вообще есть переменная
            if (!variables.containsKey(nameVar)) {
                throw new IllegalArgumentException(ErrorMessage.getErrorMessage(ErrorMessage.NOT_VALID_VALUE_IN_CONTEXT, nameVar, variable, variables.get(variable).toString()));
            }
            // проверка что переменная не ссылается сама на себя
            String value = variables.getOrDefault(nameVar, "").toString();
            if (!value.isEmpty() && value.substring(firstIndex, value.length() - lastIndex).equals(variable)) {
                throw new IllegalArgumentException(ErrorMessage.getErrorMessage(ErrorMessage.VARIABLE_NOT_VALID, variable, variables.get(nameVar).toString()));
            }
        }
    }

    /**
     * убирает лишние символы (обычно для json)
     *
     * @param in текст
     * @return текст
     */
    public static String extractBrackets(Object in) {
        if (in == null) {
            return "null";
        }
        String op = in.toString();
        String replace;
        String replace2;
        if (op.startsWith("[") && op.endsWith("]") && !op.contains(",")) {
            replace = StringUtils.replace(StringUtils.replace(op, "[", ""), "]", "");
            if (op.startsWith("[\"") && op.endsWith("\"]")) {
                replace2 = StringUtils.replace(op, "[\"","");
                replace2 = StringUtils.replace(replace2, "\"]","");
                replace2 = StringUtils.replace(replace2, "\\\"","\"");
                try {
                    if (replace2.matches("^[0-9]+$")) {
                        new BigDecimal(replace2);
                        return replace2;
                    } else {
                        return replace2;
                    }
                } catch (NumberFormatException n) {
                    return replace;
                }
            } else {
                return replace;
            }
        }
        if (op.startsWith("[[") && op.endsWith("]]") || op.startsWith("[") && op.endsWith("]")) {
            op = op.replaceFirst("\\[", "");
            int lastIndex = op.lastIndexOf("]");
            return op.substring(0, lastIndex);
        }
        return op;
    }

    /**
     * метод сплитит по символу splitSymbol
     * убирает лишние пробелы по бокам, а также кавычки и повторяющиеся элементы(distinct) в получившемся списке
     * далее сортирует
     * и отдает строку с разделяющим символом concatSymbol
     *
     * @param string       строка
     * @param splitSymbol  разделяющий символ по которому делается список
     * @param concatSymbol разделяющий символ который позволяет сделать строку с раздеелнием элементов
     * @return строка сортированная и без дублированных значений
     */
    public static String sorting(String string, String splitSymbol, String concatSymbol) {
        List<String> stringList = Arrays.stream(string.split(splitSymbol)).map(String::trim)
                .map(p -> p.replaceAll("\"", "")).distinct().collect(Collectors.toList());
        Collections.sort(stringList);
        StringBuilder stringBuilder = new StringBuilder();
        stringList.forEach(p -> stringBuilder.append(p.concat(concatSymbol)));
        return stringBuilder.toString();
    }
}