package com.zblog.enums;

public enum CommentStatus {
    PENDING("pending"),
    APPROVED("approved"),
    SPAM("spam");

    private final String value;

    CommentStatus(String value) { this.value = value; }

    public String getValue() { return value; }

    public static CommentStatus fromValue(String value) {
        for (CommentStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Invalid comment status: " + value);
    }
}
