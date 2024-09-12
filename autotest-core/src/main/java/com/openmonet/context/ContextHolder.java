package com.openmonet.context;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openmonet.configurations.StandConfig;
import com.openmonet.keys.StorageKeys;
import com.openmonet.utils.VariableUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для хранения переменных теста
 * Синтаксис %{var_name}%
 */
public class ContextHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextHolder.class);
    private static final ThreadLocal<Map<String, Object>> THREAD = new ThreadLocal<>();

    private static Map<String, Object> getThread() {
        Map<String, Object> vault = THREAD.get();
        if (vault == null) {
            vault = new HashMap<>();
            THREAD.set(vault);
        }
        return vault;
    }

    /**
     * Возвращает название стенда, переданного через аргумент stand
     *
     * @return -   стенд
     */
    public static String getStand() {
        return StandConfig.config().stand();
    }

    /**
     * Возвращает название mysql подключения, переданного через аргумент mysqlConnectionUrl
     *
     * @return -  mysqlConnectionUrl
     */
    public static String getMysqlConnectionUrl () {
        return StandConfig.config().mysqlConnectionUrl();
    }


    public static Map<String, Object> asMap() {
        return getThread();
    }

    public static void put(String key, Object value) {
        getThread().put(key, value);
        LOGGER.debug(String.format("Сохранена переменная: %s=%s", key, value));
    }

    public static <T> T getValue(String key) {
        return (T) getThread().get(key);
    }

    public static <T> T remove(String key) {
        return (T) getThread().remove(key);
    }

    public static void clear() {
        getThread().clear();
        THREAD.remove();
    }

    public static String replaceVarsIfPresent(String text) {
        if (text == null) {
            return "";
        } else {
            return VariableUtil.replaceVars(text, asMap());
        }
    }

    public static String replaceVarsOrReturnNull(String text) {
        String replacedValue = replaceVarsIfPresent(text);
        if (!replacedValue.isEmpty()) {
            return replacedValue;
        } else {
            return null;
        }
    }


    /**
     * Возвращает значение продового токена из хранилища
     * @return
     */
    public static String getGuestToken() {
        String token = getValue(StorageKeys.PROD_TOKEN);
        if (token == null) {
            throw new AssertionError("Не сохранено значение для гостевого токена");
        }
        return token;
    }

    /**
     * Возвращает значение авторизованного токена из хранилища
     * @return
     */
    public static String getAccessToken() {
        String token = getValue(StorageKeys.ACCESS_TOKEN);
        if (token == null) {
            throw new AssertionError("Не сохранено значение для авторизованного токена");
        }
        return getValue(StorageKeys.ACCESS_TOKEN);
    }
}