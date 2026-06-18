package com.zblog.service;

import com.zblog.common.SlugUtils;
import org.springframework.stereotype.Service;

@Service
public class SlugService {

    public String generate(String title) {
        return SlugUtils.generate(title);
    }

    public String ensureUnique(String baseSlug, java.util.function.Function<String, Boolean> existsChecker) {
        return SlugUtils.ensureUnique(baseSlug, existsChecker);
    }

    public boolean isReserved(String slug) {
        return SlugUtils.isReserved(slug);
    }
}
