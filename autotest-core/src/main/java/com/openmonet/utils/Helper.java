package com.openmonet.utils;

public class Helper {
    public static String getRunningMethodName(int lvl) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= lvl) {
            return stackTrace[lvl].getMethodName();
        } else {
            return "none method";
        }
    }
}
