package com.openmonet.utils;

import com.openmonet.context.ContextHolder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.openmonet.context.ContextHolder.replaceVarsIfPresent;

public class UrlUtil {
    public static final String PATTERN_API = "${protocol}://${env}.${domain}/${prefix}/${endpoint}";
    public static final String PATTERN_WEB = "${protocol}://${prefix}${defis}${env}.${domain}/${endpoint}";
    public static final String DEFAULT_PROTOCOL = "https";
    public static final String DEFAULT_PREFIX = "api";
    public static final String DEFAULT_DOMAIN = "p2pay-internal.work";

    /**
     * форматирование url
     *
     * @param path   url
     * @param prefix prefix
     * @return отформатированная url
     */
    public static String formatUrl(String pattern, String path, String prefix, String domain) {
        path = replaceVarsIfPresent(path);

        if (!path.startsWith("http")) {
            if (path.startsWith("/")) {
                path = path.replaceFirst("/", "");
            }
            path = pattern.replace("${endpoint}", path);
            path = path.replace("${protocol}", DEFAULT_PROTOCOL);

            if (domain != null && !domain.isEmpty()) {
                path = path.replace("${domain}", domain);
            } else {
                path = path.replace("${domain}", DEFAULT_DOMAIN);
            }

            if (prefix != null && !prefix.isEmpty()) {
                path = path.replace("${prefix}", prefix);
            } else {
                path = path.replace("${prefix}", DEFAULT_PREFIX);
            }

            String env = ContextHolder.getStand();
            String defis = env.equals("staging") ? "." : "-";

            path = path.replace("${env}", env);
            path = path.replace("${defis}", defis);
        }

        return path;
    }

    /**
     * Кодирует URL или параметры запроса для передачи запрещенных символов, например пробела в URL
     *
     * @return возвращает закодированную строку
     */
    public static String encode(String text) {
        try {
            return URLEncoder.encode(replaceVarsIfPresent(text), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
