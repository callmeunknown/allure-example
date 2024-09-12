package com.openmonet.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String getDateByRussianDateFormat(String dates, String mask) {
        LocalDateTime localDateTime = null;
        for (String date: dates.split(";")) {
            localDateTime = setDateTime(localDateTime, date);
        }
        DateTimeFormatter dateTimeFormatter;
        try {
            dateTimeFormatter = mask == null ? FORMATTER : DateTimeFormatter.ofPattern(mask);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Не корректный шаблон форматирования даты: " + mask);
        }
        if (localDateTime == null) {
            throw new IllegalArgumentException("Не верный формат передачи аргументов: " + dates);
        }
        ZoneId zoneId = ZoneId.of("Europe/Moscow");
        return localDateTime.atZone(zoneId).format(dateTimeFormatter.withLocale(new Locale("ru")));
    }

    /**
     * Считает localDateTime по переданной дате словесно (плюс 1 год, минус 2 месяца и т.д)
     * Если localDateTime передается не null - прибавляет/вычитает от нее
     * @param localDateTime -   датавремя
     * @param date  -   нужная дата
     * @return  -   датавремя +/- нужное время
     */
    private static LocalDateTime setDateTime(LocalDateTime localDateTime, String date) {
        if (date.trim().startsWith("плюс") || date.trim().startsWith("минус")) {
            String sign = RegexUtil.getMatchValueByGroupNumber(date, "(плюс|минус)", 1);
            String year = RegexUtil.getMatchValueByGroupNumber(date, "(\\d+{1,10})+ (год(а)*|лет)", 1);
            Integer yearNumb = year != null ? Integer.valueOf(year) : null;

            String month = RegexUtil.getMatchValueByGroupNumber(date, "(\\d+{1,10})+ месяц(ев|а)*", 1);
            Integer monthNumb = month != null ? Integer.valueOf(month) : null;
            String day = RegexUtil.getMatchValueByGroupNumber(date, "(\\d+{1,10})+ (день|дней|дня)", 1);
            Integer dayNumb = day != null ? Integer.valueOf(day) : null;
            localDateTime = getCurrentDatePlusOrMinusDate(sign, yearNumb, monthNumb, dayNumb, localDateTime);
        }
        if (date.equalsIgnoreCase("сегодня")) {
            localDateTime = DateUtils.getCurrentDatePlusMinusDay("", 0);
        }
        return localDateTime;
    }


    /**
     * метод для получения текущей даты, текущей даты плюс какое-то количество дней, либо текущая дата минус какое-то количество дней
     */
    private static LocalDateTime getCurrentDatePlusMinusDay(String option, int day) {
        switch (option) {
            case "плюс":
                return LocalDate.now().plusDays(day).atStartOfDay();
            case "минус":
                return LocalDate.now().minusDays(day).atStartOfDay();
            default:
                return LocalDate.now().atStartOfDay();
        }
    }

    private static LocalDateTime getCurrentDatePlusOrMinusDate(String option, Integer year, Integer month, Integer day, LocalDateTime localDateTime) {
        LocalDateTime newDateTime = localDateTime == null? LocalDateTime.now(): localDateTime;
        switch (option) {
            case "плюс":
                return newDateTime.plusYears(year != null ? year : 0)
                        .plusMonths(month != null ? month : 0).plusDays(day != null ? day : 0);
            case "минус":
                return newDateTime.minusYears(year != null ? year : 0)
                        .minusMonths(month != null ? month : 0).minusDays(day != null ? day : 0);
            default:
                return LocalDate.now().atStartOfDay();
        }
    }
}
