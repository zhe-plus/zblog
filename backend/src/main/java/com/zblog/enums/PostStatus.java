package com.zblog.enums;

public enum PostStatus {
    DRAFT("draft"),
    PRIVATE("private"),
    PUBLIC("public");

    private final String value;

    PostStatus(String value) { this.value = value; }

    public String getValue() { return value; }

    public static PostStatus fromValue(String value) {
        for (PostStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid post status: " + value);
    }
}
