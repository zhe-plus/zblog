package com.zblog.common;

import java.util.Set;
import java.util.function.Function;

public class SlugUtils {

    private static final Set<String> RESERVED = Set.of(
        "search", "archive", "rss.xml", "sitemap.xml", "robots.txt",
        "feed", "admin", "api", "install", "login", "logout",
        "page", "post", "category", "tag", "uploads", "static", "health"
    );

    public static String generate(String title) {
        if (title == null || title.isBlank()) return "untitled";
        String slug = title.toLowerCase().trim()
            .replaceAll("[^a-z0-9\\u4e00-\\u9fff\\-\\s]", "")
            .replaceAll("[\\u4e00-\\u9fff]", "-")
            .replaceAll("\\s+", "-")
            .replaceAll("-{2,}", "-")
            .replaceAll("^-|-$", "");
        return slug.isEmpty() ? "untitled" : slug;
    }

    public static String ensureUnique(String baseSlug, Function<String, Boolean> exists) {
        if (!exists.apply(baseSlug)) return baseSlug;
        int suffix = 1;
        String candidate;
        do {
            candidate = baseSlug + "-" + (suffix++);
        } while (exists.apply(candidate));
        return candidate;
    }

    public static boolean isReserved(String slug) {
        return slug != null && RESERVED.contains(slug.toLowerCase());
    }
}
