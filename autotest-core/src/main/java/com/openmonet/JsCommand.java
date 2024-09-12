package com.openmonet;

/**
 * Список JavaScript команд
 */
public enum JsCommand {
    REMOVE_FOCUS("arguments[0].blur();");

    private String js;

    JsCommand(String js) {
        this.js = js;
    }

    public String getJS() {
        return js;
    }
}
