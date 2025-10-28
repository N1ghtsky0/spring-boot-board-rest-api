package xyz.jiwook.demo.springBootBoardRestApi.domain.oauthAccount;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.ErrorCode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OAuthAccountErrorCode implements ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "소셜계정정보를 찾을 수 없습니다."),
    ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 가입된 소셜계정입니다.")
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
