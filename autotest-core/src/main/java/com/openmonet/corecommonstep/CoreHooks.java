package com.openmonet.corecommonstep;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openmonet.context.ContextHolder;

import static com.openmonet.keys.StorageKeys.API_URL;
import static com.openmonet.keys.StorageKeys.WEB_URL;
import static com.openmonet.utils.UrlUtil.*;

public class CoreHooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreHooks.class);

    @Before
    public void coreBefore() {
        ContextHolder.put(API_URL, formatUrl(PATTERN_API, "", null, null));
        LOGGER.info("переменная apiUrl - '{}'", ContextHolder.getValue(API_URL).toString());
        ContextHolder.put("accessToken", System.getProperty("accessToken"));

        ContextHolder.put(WEB_URL, formatUrl(PATTERN_WEB, "", null, null));
        LOGGER.info("переменная webUrl - '{}'", ContextHolder.getValue(WEB_URL).toString());
    }

    @After
    public void coreTearDown() {
        ContextHolder.clear();
    }
}
