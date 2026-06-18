package com.zblog.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private T data;
    private Pagination pagination;

    public ApiResponse() {}

    public ApiResponse(T data) {
        this.data = data;
    }

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data);
    }

    public static <T> ApiResponse<T> of(T data, Pagination pagination) {
        ApiResponse<T> response = new ApiResponse<>(data);
        response.pagination = pagination;
        return response;
    }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Pagination getPagination() { return pagination; }
    public void setPagination(Pagination pagination) { this.pagination = pagination; }
}
