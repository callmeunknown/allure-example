package com.openmonet.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.openmonet.utils.ErrorMessage.getAssertError;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class DataUtils {

    /**
     * Генерирует набор русских, английских букв и цифр по маске
     * <p>
     * <br/> R - русская буква
     * <br/> D - цифра
     * <br/> W - английская буква
     * <br/> S - спецсимвол из списка <b>!"№;%:?*()_-=+/\|.,<>`~</b>
     *
     * @return - рандомная строка
     */
    public static String generateValueByMask(String mask) {
        StringBuilder result = new StringBuilder();
        char[] chars = mask.toCharArray();
        for (char aChar : chars) {
            switch (String.valueOf(aChar)) {
                case "R":
                    result.append(getRussianLetterUpperCase());
                    break;
                case "r":
                    result.append(getRussianLetterLowerCase());
                    break;
                case "D":
                    result.append(randomNumeric(1));
                    break;
                case "e":
                    result.append(randomAlphabetic(1).toLowerCase());
                    break;
                case "E":
                    result.append(randomAlphabetic(1).toUpperCase());
                    break;
                case "S":
                    result.append(getRandomSymbol(1));
                    break;
                default:
                    result.append(aChar);
                    break;
            }
        }
        result.trimToSize();
        return result.toString();
    }

    /**
     * рандомная русская буква в верхнем регистре
     *
     * @return рандомная русская буква
     */
    private static String getRussianLetterUpperCase() {
        return getRussianLetter(1040);
    }

    /**
     * рандомная русская буква в нижнем регистре
     *
     * @return рандомная русская буква
     */
    private static String getRussianLetterLowerCase() {
        return getRussianLetter(1072);
    }

    /**
     * Получает русскую букву в верхнем или нижнем регистре
     * https://i.voenmeh.ru/kafi5/Kam.loc/inform/UTF-8.htm
     *
     * @return  -   буква
     */
    private static String getRussianLetter(int leftLimit) {
        int rightLimit = leftLimit + 31;
        String res = "";
        int a = ThreadLocalRandom.current().nextInt(leftLimit, rightLimit + 1);
        char symbol = (char) a;
        res += symbol;
        return res;
    }


    private static String getRandomSymbol(int length) {
        return RandomStringUtils.random(length, "!№;%:?*()_-=+.,<>`~");
    }

    /**
     * Генерирует рандомный номер телефона по маске 7901*******
     *
     * @return -   номер телефона
     */
    public static long generatePhone() {
        final long PHONE_TEMPLATE = 79010000000L;
        final int leftLimit = 1;
        final int rightLimit = 9999999;

        long currentThreadId = Thread.currentThread().getId();
        long uniqueSeed = System.currentTimeMillis() + currentThreadId;
        Random rnd = new Random(uniqueSeed);
        int randomValue = leftLimit + (int) (rnd.nextDouble() * (rightLimit - leftLimit));

        long generatedPhone= PHONE_TEMPLATE + randomValue + currentThreadId;

        return generatedPhone;
    }

    public static String randomStringWithParam(int length, boolean ru, boolean en, boolean numbers, boolean symbols) {
        StringBuilder sb = new StringBuilder();
        List<String> type = new ArrayList<String>() {{
            if (ru) {
                add("R");
            }
            if (en) {
                add("W");
            }
            if (numbers) {
                add("D");
            }
            if (symbols) {
                add("S");
            }
        }};

        for (int i = 0; i < length; i++) {
            sb.append(generateValueByMask(type.get(new Random().nextInt(type.size()))));
        }
        return sb.toString();
    }

    /**
     * Форматирует число, если был передан формат
     *
     * @param number -   число
     * @param format -   формат
     * @return -   отформатированное число
     */
    public static String formatNumber(String number, String format) {
        if (NumberUtils.isParsable(number)) {
            if (format != null) {
                DecimalFormat df;
                Double d = Double.parseDouble(number);
                df = new DecimalFormat(format);
                df.setRoundingMode(RoundingMode.DOWN);
                return df.format(d);
            } else {
                return number;
            }
        } else {
            throw getAssertError("Provided string is not a number: %s", number);
        }
    }

    /**
     * Возвращает кол-во цифр в десятичной части числа
     *
     * @param n -   число
     * @return -   кол-во цифр в десятичной части числа
     */
    public static Integer getDecimalCount(String n) {
        return n.contains(".") ?
                n.replaceAll(".*\\.(?=\\d?)", "").length() :
                0;
    }
}
