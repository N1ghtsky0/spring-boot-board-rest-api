# Spring Boot Board REST API

OAuth2 소셜 로그인을 적용한 게시판 REST API 서버입니다.
Clean Architecture 원칙에 따라 설계되어 높은 확장성과 유지보수성을 제공합니다.

---

## 아키텍처 구조

```
src/main/java/xyz/jiwook/demo/springBootBoardRestApi/
│
├── domain/                     # 도메인 계층 (비즈니스 엔티티)
│   ├── member/                 # 회원 도메인
│   └── oauthaccount/           # OAuth 계정 도메인
│
├── application/                # 애플리케이션 계층 (유스케이스)
│   └── auth/                   # 인증 비즈니스 로직
│
├── infrastructure/             # 인프라 계층 (기술 구현체)
│   ├── jwt/                    # JWT 토큰 처리
│   └── oauth2/                 # OAuth2 프로토콜 구현
│
├── presentation/               # 프레젠테이션 계층 (API)
│   └── api/auth/               # 인증 API 엔드포인트
│
└── global/                     # 공통 요소
    ├── common/                 # 공통 클래스
    ├── exception/              # 예외 처리
    └── security/               # 보안 설정
```

### 의존성 방향
```
Presentation → Application → Domain ← Infrastructure
                                ↑
                             (의존)
```
- **Domain**: 순수 비즈니스 로직, 어디에도 의존하지 않음
- **Application**: 도메인을 조율하는 유스케이스
- **Infrastructure**: 기술적 세부사항 구현 (Domain 인터페이스 구현)
- **Presentation**: 외부 요청을 처리하고 Application 호출

---

## 상세 디렉토리 구조

### 1. Domain Layer (도메인 계층)

#### domain/member/
순수 회원 도메인 로직을 담당합니다.

- **Member.java** - 회원 엔티티
  - id: 회원 고유 식별자 (PK)
  - email: 이메일
  - sub: JWT 토큰 subject로 사용되는 고유 식별자

- **MemberRepository.java** - 회원 데이터 접근 인터페이스
  - existsBySub(): sub 중복 검사
  - findBySub(): sub로 회원 조회

- **MemberService.java** - 회원 도메인 서비스
  - createMember(): 새 회원 생성
  - getMemberBySub(): sub로 회원 조회
  - generateUniqueSub(): 고유한 UUID 생성

#### domain/oauthaccount/
OAuth 계정 도메인을 담당합니다.

- **OAuthAccount.java** - OAuth 계정 엔티티
  - providerName: OAuth 제공자 (google, kakao, naver)
  - providerId: 제공자별 사용자 ID
  - member: 연결된 회원 (ManyToOne)

- **OAuthAccountRepository.java** - OAuth 계정 데이터 접근 인터페이스
  - findByProviderNameAndProviderId(): 소셜 계정 조회
  - existsByProviderNameAndProviderId(): 소셜 계정 존재 여부 확인

- **OAuthAccountService.java** - OAuth 계정 도메인 서비스
  - linkOAuthAccount(): 소셜 계정을 회원과 연결
  - findOAuthAccount(): 소셜 계정 조회
  - isAccountRegistered(): 소셜 계정 등록 여부 확인

---

### 2. Application Layer (애플리케이션 계층)

#### application/auth/
인증 관련 비즈니스 플로우를 조율합니다.

- **OAuth2AuthenticationFacade.java** - 인증 플로우 조율 파사드
  - getAuthorizationUri(): OAuth2 인증 URL 생성
  - handleCallback(): OAuth2 콜백 처리 및 목적별 분기 (login/join/connect)
  - handleLogin(): 로그인 처리 (JWT 토큰 발급)
  - handleJoin(): 회원가입 처리
  - handleConnect(): 소셜 계정 연결 처리 (TODO)

- **OAuth2AuthorizationResponseUtils.java** - OAuth2 응답 처리 유틸리티
  - toMultiMap(): HTTP 파라미터 변환
  - isAuthorizationResponse(): 응답 유효성 검증
  - convert(): OAuth2AuthorizationResponse DTO 변환

#### application/auth/usecase/
각 비즈니스 케이스를 독립적으로 처리합니다.

- **OAuth2LoginUseCase.java** - 로그인 유스케이스
  - 소셜 계정으로 회원 조회
  - 회원의 sub 반환 (JWT 토큰 발급용)

- **OAuth2JoinUseCase.java** - 회원가입 유스케이스
  - 새 회원 생성
  - 소셜 계정 연결

#### application/auth/dto/
- **OAuth2UserInfoDto.java** - OAuth2 사용자 정보 DTO
  - providerName: 제공자 이름
  - providerId: 제공자 사용자 ID
  - email: 이메일

---

### 3. Infrastructure Layer (인프라 계층)

#### infrastructure/jwt/
JWT 토큰 생성 및 검증을 담당합니다.

- **JwtTokenProvider.java** - JWT 토큰 처리
  - generateAccessToken(): Access Token 생성 (30분 유효)
  - getSigningKey(): HMAC 서명 키 생성

#### infrastructure/oauth2/
OAuth2 프로토콜 구현체를 담당합니다.

- **OAuth2AuthenticationService.java** - OAuth2 인증 서비스
  - createAuthorizationUri(): 인증 요청 URI 생성 및 state 저장
  - getAuthorizationRequestFromState(): state로 인증 요청 조회
  - processCallback(): 토큰 교환 및 사용자 정보 조회
  - validateRegistrationId(): OAuth2 제공자 유효성 검증

- **OAuth2StateRepository.java** - OAuth2 state 관리 (Redis)
  - save(): state 저장 (5분 TTL)
  - findAndRemove(): state 조회 및 삭제

- **OAuth2UserInfoService.java** - OAuth2 사용자 정보 추출
  - extractUserInfo(): 제공자별 사용자 정보 파싱 및 DTO 변환

#### infrastructure/oauth2/provider/
OAuth2 제공자별 사용자 정보 파싱을 담당합니다.

- **OAuth2UserInfo.java** - 추상 클래스
- **GoogleOAuth2UserInfo.java** - Google 사용자 정보 파싱
- **KakaoOAuth2UserInfo.java** - Kakao 사용자 정보 파싱
- **NaverOAuth2UserInfo.java** - Naver 사용자 정보 파싱

#### infrastructure/oauth2/dto/
- **OAuth2AuthorizationRequestDto.java** - 인증 요청 래퍼
  - authorizationRequest: OAuth2 인증 요청 객체
  - purpose: 요청 목적 (login/join/connect)

---

### 4. Presentation Layer (프레젠테이션 계층)

#### presentation/api/auth/
인증 관련 REST API 엔드포인트를 제공합니다.

- **AuthController.java** - 인증 API 컨트롤러
  - GET /api/auth/oauth2/authorization/{registrationId} - OAuth2 인증 URL 조회
    - Query: ?purpose=login|join|connect
  - GET /api/auth/oauth2/callback/{registrationId} - OAuth2 콜백 처리
    - 로그인 시: Authorization 헤더에 JWT 토큰 반환

---

### 5. Global Layer (공통 계층)

#### global/common/
- **ApiResponse.java** - 공통 API 응답 포맷
  - success: 성공 여부
  - data: 응답 데이터

- **MutableEntity.java** - 수정 가능 엔티티 추상 클래스
  - createdAt: 생성일시
  - updatedAt: 수정일시

- **ImmutableEntity.java** - 불변 엔티티 추상 클래스
  - createdAt: 생성일시

#### global/exception/
- **BusinessException.java** - 비즈니스 예외
- **GlobalExceptionHandler.java** - 전역 예외 처리
  - @RestControllerAdvice: 모든 컨트롤러 예외 처리
  - BusinessException, IllegalArgumentException, IllegalStateException 등 처리

#### global/security/
- **WebSecurityConfig.java** - Spring Security 설정
  - CSRF 비활성화
  - Stateless 세션 정책
  - 모든 요청 허용 (현재)

---

## 기술 스택

- **Java**: 21
- **Spring Boot**: 3.5.6
- **Spring Security OAuth2 Client**: OAuth2 소셜 로그인
- **Spring Data JPA**: 데이터 접근
- **Spring Data Redis**: State 관리
- **JWT (jjwt)**: 토큰 발급
- **MySQL**: 데이터베이스
- **Lombok**: 보일러플레이트 코드 제거

---

## 인증 플로우

### 로그인 플로우
```
1. 클라이언트 → GET /api/auth/oauth2/authorization/google?purpose=login
2. 서버 → OAuth2 인증 URL 반환
3. 사용자 → Google 로그인 및 동의
4. Google → GET /api/auth/oauth2/callback/google?code=xxx&state=yyy
5. 서버 → 토큰 교환 → 사용자 정보 조회 → JWT 발급
6. 클라이언트 ← Authorization 헤더에 JWT 토큰 반환
```

### 회원가입 플로우
```
1. 클라이언트 → GET /api/auth/oauth2/authorization/kakao?purpose=join
2-4. (동일)
5. 서버 → 새 회원 생성 → 소셜 계정 연결
6. 클라이언트 ← 성공 메시지 반환
```

---

## 설계 원칙

### 1. 단일 책임 원칙 (SRP)
- 각 클래스와 메서드는 하나의 명확한 책임만 가집니다.
- 예: MemberService는 회원 관리만, OAuthAccountService는 OAuth 계정 관리만

### 2. 의존성 역전 원칙 (DIP)
- 도메인은 인프라에 의존하지 않습니다.
- 인프라가 도메인의 인터페이스를 구현합니다.

### 3. 계층 분리
- 각 계층은 명확한 책임을 가지며 경계가 분명합니다.
- 상위 계층은 하위 계층에 의존하지만, 하위 계층은 상위 계층을 알지 못합니다.

### 4. 테스트 가능성
- 순수 함수와 의존성 주입으로 단위 테스트가 용이합니다.
- HttpServletRequest 같은 프레임워크 의존성은 최소화합니다.

---

## 향후 개선 계획

- [ ] RefreshToken 구현
- [ ] JWT 검증 필터 추가
- [ ] 게시판 도메인 구현
- [ ] 소셜 계정 연결 기능 구현
- [ ] 통합 테스트 작성
- [ ] API 문서화 (Swagger/OpenAPI)

---

## 라이선스

This project is licensed under the MIT License.
