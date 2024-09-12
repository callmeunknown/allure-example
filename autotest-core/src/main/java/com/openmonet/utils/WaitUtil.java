package com.openmonet.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WaitUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(WaitUtil.class);

    /**
     * Рекурсивный метод слипа потока.
     * maxSleepTimeout - максимально возможное время одного слипа - 30 сек (можно поменять)
     * нужно для того, чтобы в случае(к примеру) ожидания 5 минут поток не спал просто 5 мин, а 6 раз по 30 секунд
     *
     * @param totalSleepSeconds - время ожидания
     */
    public static void waitSeconds(double totalSleepSeconds) {
        LOGGER.debug("Ожидание {} секунд", totalSleepSeconds);
        double maxSleepTimeout = 30d;
        try {
            if (totalSleepSeconds > maxSleepTimeout) {
                TimeUnit.MILLISECONDS.sleep((long) (maxSleepTimeout * 1000));
                totalSleepSeconds = totalSleepSeconds - maxSleepTimeout;
                waitSeconds(totalSleepSeconds);
            } else {
                TimeUnit.MILLISECONDS.sleep((long) (totalSleepSeconds * 1000));
            }
        } catch (InterruptedException interrupt) {
            LOGGER.error("проблема со sleep потока" + interrupt);
        }
    }
}
