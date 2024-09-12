package com.openmonet.utils;

import static com.openmonet.utils.ErrorMessage.ILLEGAL_PARAMETER;
import static com.openmonet.utils.ErrorMessage.getErrorMessage;

public class ColorUtil {

    public enum Color {
        WHITE("rgba(255, 255, 255, 1)"),
        GREY("rgba(238, 241, 246, 1)"),
        BLUE("rgba(26, 118, 225, 1)");
        private final String rgba;
        Color(String rgba) {
            this.rgba = rgba;
        }
        public String getRgba() {
            return rgba;
        }
    }

    public static String getRgbaColor (String colorName) {
        switch (colorName.toLowerCase()) {
            case "белый":
                return Color.WHITE.rgba;
            case "серый":
                return Color.GREY.rgba;
            case "синий":
                return Color.BLUE.rgba;
            default:
                throw new IllegalArgumentException(getErrorMessage(ILLEGAL_PARAMETER, colorName, "colorName"));
        }
    }
}
