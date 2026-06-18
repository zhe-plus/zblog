package com.zblog.common;

import java.util.Set;
import java.util.UUID;

public class FileUtils {

    private static final Set<String> ALLOWED_MIME = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml",
        "application/pdf", "text/plain", "text/markdown", "application/zip", "application/json"
    );

    private static final Set<String> BLOCKED_EXT = Set.of(
        "exe", "php", "jsp", "asp", "aspx", "sh", "bash", "py", "rb", "pl", "cgi", "war", "jar", "class"
    );

    private static final long DEFAULT_MAX_SIZE = 10 * 1024 * 1024; // 10MB

    public static boolean isAllowedMimeType(String mimeType) {
        return mimeType != null && ALLOWED_MIME.contains(mimeType.toLowerCase());
    }

    public static boolean isBlockedExtension(String filename) {
        if (filename == null) return true;
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) return false;
        String ext = filename.substring(dotIndex + 1).toLowerCase();
        return BLOCKED_EXT.contains(ext);
    }

    public static boolean isFileSizeExceeded(long fileSize, long maxSize) {
        return fileSize > maxSize;
    }

    public static boolean isFileSizeExceeded(long fileSize) {
        return isFileSizeExceeded(fileSize, DEFAULT_MAX_SIZE);
    }

    public static String generateStorageFilename(String originalFilename) {
        String ext = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1) {
            ext = originalFilename.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + ext;
    }

    public static long getDefaultMaxSize() {
        return DEFAULT_MAX_SIZE;
    }
}
