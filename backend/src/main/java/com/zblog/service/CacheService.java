package com.zblog.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final Cache<String, String> pageCache = Caffeine.newBuilder()
        .maximumSize(500)
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build();

    public Optional<String> get(String key) {
        return Optional.ofNullable(pageCache.getIfPresent(key));
    }

    public void put(String key, String html) {
        pageCache.put(key, html);
    }

    public void evict(String key) {
        pageCache.invalidate(key);
    }

    public void evictPostRelated(String postSlug, String categorySlug, Set<String> tagSlugs) {
        evict("post:" + postSlug);
        evict("home:page:1");
        if (categorySlug != null) {
            evict("category:" + categorySlug + ":page:1");
        }
        evict("archive");
        if (tagSlugs != null) {
            tagSlugs.forEach(slug -> evict("tag:" + slug + ":page:1"));
        }
    }
}
