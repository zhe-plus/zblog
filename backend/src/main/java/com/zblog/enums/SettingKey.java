package com.zblog.enums;

public enum SettingKey {
    SITE_TITLE("site_title", ValueType.STRING, "My Blog"),
    SITE_DESCRIPTION("site_description", ValueType.STRING, "又一个 zblog 站点"),
    COPYRIGHT_TEXT("copyright_text", ValueType.STRING, ""),
    META_KEYWORDS("meta_keywords", ValueType.STRING, ""),
    META_DESCRIPTION("meta_description", ValueType.STRING, ""),
    CUSTOM_HEAD_CODE("custom_head_code", ValueType.STRING, ""),
    CUSTOM_FOOT_CODE("custom_footer_code", ValueType.STRING, ""),
    ANALYTICS_ID("analytics_id", ValueType.STRING, ""),
    SITE_URL("site_url", ValueType.STRING, "http://localhost:8080"),

    POSTS_PER_PAGE("posts_per_page", ValueType.INTEGER, "10"),
    RSS_POST_COUNT("rss_post_count", ValueType.INTEGER, "20"),

    COMMENTS_ENABLED("comments_enabled", ValueType.BOOLEAN, "false"),
    SHOW_FULL_CONTENT("show_full_content", ValueType.BOOLEAN, "false"),

    SOCIAL_LINKS("social_links", ValueType.JSON, "[]"),
    NAVIGATION_LINKS("navigation_links", ValueType.JSON, "[]");

    public enum ValueType { STRING, INTEGER, BOOLEAN, JSON }

    private final String key;
    private final ValueType type;
    private final String defaultValue;

    SettingKey(String key, ValueType type, String defaultValue) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getKey() { return key; }
    public ValueType getType() { return type; }
    public String getDefaultValue() { return defaultValue; }

    public static SettingKey fromKey(String key) {
        for (SettingKey sk : values()) {
            if (sk.key.equals(key)) return sk;
        }
        return null;
    }
}
