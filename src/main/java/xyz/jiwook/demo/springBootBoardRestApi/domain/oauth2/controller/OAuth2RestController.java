package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.service.OAuth2AuthorizationService;
import xyz.jiwook.demo.springBootBoardRestApi.global.common.HttpResponseVO;
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.BusinessException;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class OAuth2RestController {
    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    @GetMapping("/oauth/authorization/{registrationId}")
    public ResponseEntity<HttpResponseVO> getAuthorizationUri(@PathVariable String registrationId, HttpServletRequest request) {
        try {
            String authorizationUri = oAuth2AuthorizationService.getAuthorizationUri(request, registrationId);
            return ResponseEntity.ok(HttpResponseVO.success(Map.of("result", authorizationUri)));
        } catch (BusinessException e) {
            return ResponseEntity.ok(HttpResponseVO.fail("지원되지 않는 소셜 로그인 제공자입니다."));
        }
    }

}
