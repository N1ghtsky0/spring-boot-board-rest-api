package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.ErrorCode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OAuth2ErrorCode implements ErrorCode {
    INVALID_STATE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_PURPOSE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    BAD_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 서비스입니다.")
    ;

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
