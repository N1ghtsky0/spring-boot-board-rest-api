package xyz.jiwook.demo.springBootBoardRestApi.global.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
