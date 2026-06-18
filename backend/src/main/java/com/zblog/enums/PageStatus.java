package com.zblog.enums;

public enum PageStatus {
    DRAFT("draft"),
    PUBLIC("public");

    private final String value;

    PageStatus(String value) { this.value = value; }

    public String getValue() { return value; }

    public static PageStatus fromValue(String value) {
        for (PageStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid page status: " + value);
    }
}
