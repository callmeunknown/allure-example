package com.openmonet.listener;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int counter = 0;

    private final String SYSTEM_ENV_NAME = "rerunCount";
    private final int RETRY_COUNT = Integer.parseInt(System.getProperty(SYSTEM_ENV_NAME, "0"));

    @Override
    public boolean retry(ITestResult result) {
        if (counter < maxRetryCount()) {
            counter++;
            return true;
        }
        return false;
    }

    private int maxRetryCount() {
        int maxRetryCount = 2;
        return Math.min(RETRY_COUNT, maxRetryCount);
    }
}