package com.zblog.enums;

public enum ErrorCode {
    // General
    INVALID_INPUT("INVALID_INPUT", "输入参数无效"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "资源不存在"),
    RESOURCE_GONE("RESOURCE_GONE", "资源已删除"),
    INTERNAL_ERROR("INTERNAL_ERROR", "服务内部错误"),
    RATE_LIMITED("RATE_LIMITED", "请求频率过高"),

    // Auth
    UNAUTHORIZED("UNAUTHORIZED", "未登录或会话已过期"),
    FORBIDDEN("FORBIDDEN", "无权限访问"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "用户名或密码错误"),
    ACCOUNT_LOCKED("ACCOUNT_LOCKED", "账户已锁定，请15分钟后重试"),
    PASSWORD_TOO_WEAK("PASSWORD_TOO_WEAK", "密码强度不足（≥8位，含字母和数字）"),
    OLD_PASSWORD_INCORRECT("OLD_PASSWORD_INCORRECT", "原密码错误"),
    CSRF_TOKEN_INVALID("CSRF_TOKEN_INVALID", "CSRF 令牌无效"),

    // Post
    POST_NOT_FOUND("POST_NOT_FOUND", "文章不存在"),
    POST_SLUG_DUPLICATE("POST_SLUG_DUPLICATE", "文章 Slug 已被占用"),
    POST_TITLE_EMPTY("POST_TITLE_EMPTY", "文章标题不能为空"),
    POST_INVALID_STATUS("POST_INVALID_STATUS", "无效的文章状态"),
    POST_SCHEDULE_IN_PAST("POST_SCHEDULE_IN_PAST", "定时发布时间不能是过去的时间"),

    // Category/Tag
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "分类不存在"),
    CATEGORY_SLUG_DUPLICATE("CATEGORY_SLUG_DUPLICATE", "分类 Slug 已被占用"),
    TAG_NOT_FOUND("TAG_NOT_FOUND", "标签不存在"),
    TAG_SLUG_DUPLICATE("TAG_SLUG_DUPLICATE", "标签 Slug 已被占用"),

    // Media
    MEDIA_NOT_FOUND("MEDIA_NOT_FOUND", "媒体文件不存在"),
    MEDIA_SIZE_EXCEEDED("MEDIA_SIZE_EXCEEDED", "文件大小超过限制"),
    MEDIA_TYPE_NOT_ALLOWED("MEDIA_TYPE_NOT_ALLOWED", "不支持的文件类型"),
    MEDIA_REFERENCED("MEDIA_REFERENCED", "文件被文章引用，无法删除"),

    // Comment
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "评论不存在"),
    COMMENT_SPAM_DETECTED("COMMENT_SPAM_DETECTED", "评论被识别为垃圾内容"),

    // Install
    INSTALL_ALREADY_COMPLETED("INSTALL_ALREADY_COMPLETED", "系统已安装"),
    INSTALL_DB_CONNECTION_FAILED("INSTALL_DB_CONNECTION_FAILED", "数据库连接失败"),
    INSTALL_TOKEN_INVALID("INSTALL_TOKEN_INVALID", "安装令牌无效或已过期"),

    // Slug
    SLUG_RESERVED("SLUG_RESERVED", "该 Slug 为系统保留字，请更换"),

    // Import/Export
    EXPORT_FAILED("EXPORT_FAILED", "数据导出失败"),
    IMPORT_FORMAT_INVALID("IMPORT_FORMAT_INVALID", "导入文件格式无效");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
