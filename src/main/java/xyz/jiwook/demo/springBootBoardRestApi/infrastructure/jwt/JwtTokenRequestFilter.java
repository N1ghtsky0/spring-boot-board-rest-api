package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.Member;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.MemberService;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenRequestFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("==========JwtTokenRequestFilter==========");
        log.info("request uri: {}", request.getRequestURI());
        Enumeration<String> headers = request.getHeaderNames();
        do {
            String headerName = headers.nextElement();
            log.info("[header] {}: {}", headerName, request.getHeader(headerName));
        }
        while (headers.hasMoreElements());
        request.getParameterMap().forEach((key, values) -> log.info("[parameter] {}: {}", key, values));

        final String accessToken = this.getAccessTokenFromRequest(request);
        if (accessToken != null) {
            final String tokenSubject = jwtTokenProvider.getSubjectFromJwtToken(accessToken);
            Member member = memberService.getMemberBySub(tokenSubject);

            UserDetails userDetails = new User(member.getSub(), "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("member Subject: {}", member.getSub());
            log.info("role: {}", authentication.getAuthorities());
        }
        log.info("=========================================");
        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }
}
