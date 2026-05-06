package com.workingraccoon.backend.util;

public class XssUtils {

    public static String sanitize(String value) {
        if (value == null) return null;
        return value
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("/", "&#x2F;");
    }
}