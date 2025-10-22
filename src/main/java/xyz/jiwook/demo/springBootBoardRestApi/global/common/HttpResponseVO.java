package xyz.jiwook.demo.springBootBoardRestApi.global.common;

import lombok.Data;

import java.util.Map;

@Data
public class HttpResponseVO {
    private boolean success;
    private Map<String, Object> data;

    private HttpResponseVO(boolean success, Map<String, Object> data) {
        this.success = success;
        this.data = data;
    }

    public static HttpResponseVO success(Map<String, Object> data) {
        return new HttpResponseVO(true, data);
    }

    public static HttpResponseVO fail(String message) {
        return new HttpResponseVO(false, Map.of("message", message));
    }
}
