package com.openmonet.utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.openmonet.context.ContextHolder;
import com.openmonet.corecommonstep.gherkintables.MathOperationModel;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;
import static com.openmonet.utils.ErrorMessage.*;

public class CompareUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareUtil.class);
    private static final String STRING_TYPE = "string";
    private static final String INT_TYPE = "int";
    private static final String DOUBLE_TYPE = "double";
    private static final String BOOLEAN_TYPE = "boolean";


    /**
     * Метод сравнения 2ух значение по переданному оператору.
     * В зависимости от оператора вызывает либо сравнение чисел, либо сравнение неизвестных типов (стринг или число)
     *
     * @param firstValue  -   первое значение
     * @param secondValue -   второе значение
     * @param operator    -   оператор
     * @return -   результат сравнения
     */
    public static boolean compare(String firstValue, String secondValue, String operator) {
        switch (operator.toLowerCase()) {
            case "strong equals":
                return strongEquals(firstValue, secondValue);
            case "equals":
                return equals(firstValue, secondValue);
            case "not equals":
                return !equals(firstValue, secondValue);
            case "strong contains":
                return checkStrongContains(firstValue, secondValue);
            case "contains":
                return checkContains(firstValue, secondValue);
            case "not contains":
                return !checkContains(firstValue, secondValue);
            case ">":
                return compareNumbers(firstValue, secondValue) > 0;
            case "<":
                return compareNumbers(firstValue, secondValue) < 0;
            case ">=":
                return compareNumbers(firstValue, secondValue) >= 0;
            case "<=":
                return compareNumbers(firstValue, secondValue) <= 0;
            default:
                throw new IllegalArgumentException(getErrorMessage(ILLEGAL_PARAMETER, operator, "compare"));
        }
    }

    /**
     * Проверяет тип переменной и сравнивает по маске
     * @param actualValue   -   значение переменной
     * @param expectedType  -   ожидаемый тип переменной    (пока только boolean)
     * @param operator  -   оператор сравнения (только равно/не равно)
     * @return  -   результат сравнения
     */
    public static boolean compareType(String actualValue, String expectedType, String operator) {
        switch (operator.toLowerCase()) {
            case "equals":
            case "not equals":
                break;
            default:
                throw new IllegalArgumentException(getErrorMessage(ILLEGAL_PARAMETER, operator, "compareType"));
        }
        boolean isMatchType = false;
        switch (expectedType.toLowerCase()) {
            case "boolean":
                isMatchType = actualValue.equals("true") || actualValue.equals("false");
                break;
            default:
                throw new IllegalArgumentException(getErrorMessage(ILLEGAL_PARAMETER, expectedType, "compareType"));
        }
        return operator.equalsIgnoreCase("equals") == isMatchType;
    }

    /**
     * Проверяет, являются ли оба переданных значения массивами
     * @param firstValue    -   первое значение
     * @param secondValue   -   второе значение
     * @return  -   являются ли они массивами
     */
    private static boolean isValuesArray(String firstValue, String secondValue) {
        String regex = "^\\[(.*)\\]$";
        return Pattern.matches(regex, firstValue) && Pattern.matches(regex, secondValue);
    }

    /**
     * Проверяет, содержит ли первый елемент все значения второго элемента
     * @param firstValue    -   первый элемент
     * @param secondValue   -   второй элемент
     * @return  -   содержит ли
     */
    private static boolean checkStrongContains(String firstValue, String secondValue) {
        if (isValuesArray(firstValue, secondValue)) {
            List<Object> firstArray = Arrays.asList(parseStringToArray(firstValue));
            Iterable<String> secondIterable = Splitter.on(CharMatcher.anyOf(",[]")).omitEmptyStrings().split(secondValue);
            return StreamSupport.stream(secondIterable.spliterator(), false).allMatch(firstArray::contains);
        } else {
            return firstValue.contains(secondValue);
        }
    }

    /**
     * Проверяет, содержит ли первый елемент значения второго элемента
     * @param firstValue    -   первый элемент
     * @param secondValue   -   второй элемент
     * @return  -   содержит ли
     */
    private static boolean checkContains(String firstValue, String secondValue) {
        if (isValuesArray(firstValue, secondValue)) {
            List<Object> firstArray = Arrays.asList(parseStringToArray(firstValue));
            Iterable<String> secondIterable = Splitter.on(CharMatcher.anyOf(",[]")).omitEmptyStrings().split(secondValue);
            return StreamSupport.stream(secondIterable.spliterator(), false).anyMatch(firstArray::contains);
        } else {
            firstValue = firstValue.replace("[", "").replace("]", "");
            secondValue = secondValue.replace("[", "").replace("]", "");
            return firstValue.contains(secondValue);
        }
    }

    /**
     * Проверяет, строго равны ли элементы
     * @param firstValue    -   первый элемент
     * @param secondValue   -   второй элемент
     * @return  -   равны ли
     */
    private static boolean strongEquals(String firstValue, String secondValue) {
        if (isValuesArray(firstValue, secondValue)) {
            Object[] firstArray = parseStringToArray(firstValue);
            Object[] secondArray = parseStringToArray(secondValue);
            return Arrays.equals(firstArray, secondArray);
        } else {
            return firstValue.equals(secondValue);
        }
    }

    /**
     * Проверяет, равны ли элементы
     * @param firstValue    -   первый элемент
     * @param secondValue   -   второй элемент
     * @return  -   равны ли
     */
    private static boolean equals(String firstValue, String secondValue) {
        if (isValuesArray(firstValue, secondValue)) {
            Object[] firstArray = parseStringToArray(firstValue);
            Object[] secondArray = parseStringToArray(secondValue);
            Arrays.sort(firstArray);
            Arrays.sort(secondArray);
            return Arrays.equals(firstArray, secondArray);
        } else {
            if (isNumeric(firstValue, secondValue)) {
                firstValue = firstValue.replace(",", ".");
                secondValue = secondValue.replace(",", ".");
                return compareNumbers(firstValue, secondValue) == 0;
            }
            firstValue = firstValue.replace("[", "").replace("]", "");
            secondValue = secondValue.replace("[", "").replace("]", "");
            return firstValue.equals(secondValue);
        }
    }

    /**
     * Сравнивает 2 числа, переданных как стринг
     *
     * @param firstValue  -   первое число
     * @param secondValue -   второе число
     * @return -   результата сравнения compareTo() как чисел
     */
    private static int compareNumbers(String firstValue, String secondValue) {
        if (isNumeric(firstValue, secondValue)) {
            firstValue = firstValue.replace(",", ".");
            secondValue = secondValue.replace(",", ".");
            Double number1 = new Double(firstValue);
            Double number2 = new Double(secondValue);
            return number1.compareTo(number2);
        } else {
            throw new IllegalArgumentException(String.format("\nНевозможно преобразовать значения %s или %s к числовому типу при сравнении.\n", firstValue, secondValue));
        }
    }

    /***
     * Проверяет является ли переданный текст числом
     * @param strNum    -   текст
     * @return  -   число ли текст
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null || strNum.startsWith("+")) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
            NumberFormat.getInstance().parse(strNum);
        } catch (NumberFormatException | ParseException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Проверяет, являются ли переданные значения числами
     *
     * @param var1 -   значение 1
     * @param var2 -   значение 2
     * @return -   оба ли значения числа
     */
    public static boolean isNumeric(String var1, String var2) {
        var1 = var1.replace(",", ".");
        var2 = var2.replace(",", ".");
        return NumberUtils.isParsable(var1) && NumberUtils.isParsable(var2);
    }


    /**
     * Переводит из строки в массив
     *
     * @param value -   строка
     * @return -   массив
     */
    private static Object[] parseStringToArray(String value) {
        Iterable<String> iterable = Splitter.on(CharMatcher.anyOf(",[]")).omitEmptyStrings().split(value);
        return StreamSupport.stream(iterable.spliterator(), false).toArray();
    }

    /**
     * Выполняет математическую операцию с первым значение и сохраняет в переменную
     *
     * @param mathOperationModel -   модель для математической операции
     */
    public static void performMathOperationsAndSaveVariable(MathOperationModel mathOperationModel) {
        String newValue = DataUtils.formatNumber(performMathOperation(mathOperationModel), mathOperationModel.format);
        ContextHolder.put(mathOperationModel.variableName, newValue);
        LOGGER.info("Saved var: {}={}", mathOperationModel.variableName, newValue);
    }

    /**≥
     * Выполняет математическую операцию с первым значение и возвращает результат
     *
     * @param mathOperationModel -   модель для математической операции
     * @return -   результат математической операции
     */
    public static String performMathOperation(MathOperationModel mathOperationModel) {
        Assert.assertNotNull(mathOperationModel.firstValue, getErrorMessage("Value for operation wasn't provided. Make sure that the \"first_value\" column was passed in the datatable"));
        String operations = replaceVarsIfPresent(mathOperationModel.operation);
        String operator = String.valueOf(operations.charAt(0));
        String number = operations.substring(1);
        String oldValue = replaceVarsIfPresent(mathOperationModel.firstValue);
        isNumeric(oldValue, number);
        boolean numbersAreInt = mathOperationModel.valuesType.equals(INT_TYPE);
        String newVale;
        switch (operator) {
            case "+":
                newVale = numbersAreInt ?
                        String.valueOf(Long.parseLong(oldValue) + Long.parseLong(number)) :
                        String.valueOf(Double.parseDouble(oldValue) + Double.parseDouble(number));
                break;
            case "-":
                newVale = numbersAreInt ?
                        String.valueOf(Long.parseLong(oldValue) - Long.parseLong(number)) :
                        String.valueOf(Double.parseDouble(oldValue) - Double.parseDouble(number));
                break;
            case "/":
                newVale = numbersAreInt ?
                        String.valueOf(Long.parseLong(oldValue) / Long.parseLong(number)) :
                        String.valueOf(Double.parseDouble(oldValue) / Double.parseDouble(number));
                break;
            case "*":
                newVale = numbersAreInt ?
                        String.valueOf(Long.parseLong(oldValue) * Long.parseLong(number)) :
                        String.valueOf(Double.parseDouble(oldValue) * Double.parseDouble(number));
                break;
            default:
                throw getAssertError("Only 4 operator for math operation can be used: '+','-', '/' or '*");
        }
        if (!numbersAreInt && (operator.equals("+") || operator.equals("-"))) {
            return removeFloatingComma(formatDoubleAfterPlusOrMinus(oldValue, number, newVale));
        } else {
            return removeFloatingComma(newVale);
        }
    }

    private static String removeFloatingComma(String value) {
        return value.replace(",",".");
    }

    /**
     * Убирает "floating point", который мог появиться после сложения или вычитания
     *
     * @param oldValue -   старое значение
     * @param number   -   число, которое прибавили или вычли
     * @param newVale  -   результат мат. операции
     * @return -   отформатированное значение
     */
    public static String formatDoubleAfterPlusOrMinus(String oldValue, String number, String newVale) {
        double number1 = Double.parseDouble(oldValue);
        double number2 = Double.parseDouble(number);
        int decimalMaxCount = NumberUtils.max(DataUtils.getDecimalCount(Double.toString(number1)), DataUtils.getDecimalCount(Double.toString(number2)));
        if (DataUtils.getDecimalCount(newVale) > decimalMaxCount + 1) {
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(decimalMaxCount + 1);
            df.setRoundingMode(RoundingMode.DOWN);
            newVale = df.format(Double.parseDouble(newVale));
        }
        return newVale;
    }
}
