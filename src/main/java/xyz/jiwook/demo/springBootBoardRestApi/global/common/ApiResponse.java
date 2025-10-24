package xyz.jiwook.demo.springBootBoardRestApi.global.common;

import java.util.Map;

public record ApiResponse(boolean success, Map<String, Object> data) {
    public static ApiResponse success(Map<String, Object> data) {
        return new ApiResponse(true, data);
    }

    public static ApiResponse fail(String message) {
        return new ApiResponse(false, Map.of("message", message));
    }
}
