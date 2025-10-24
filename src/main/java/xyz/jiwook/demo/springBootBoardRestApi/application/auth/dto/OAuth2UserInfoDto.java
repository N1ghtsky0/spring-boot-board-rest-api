package xyz.jiwook.demo.springBootBoardRestApi.application.auth.dto;

public record OAuth2UserInfoDto(String providerName, String providerId, String email) {
}
