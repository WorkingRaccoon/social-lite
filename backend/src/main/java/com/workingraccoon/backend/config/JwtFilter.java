package com.workingraccoon.backend.config;

import com.workingraccoon.backend.service.TokenBlacklistService;
import com.workingraccoon.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        System.out.println("JwtFilter 收到 Authorization: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Token 驗證結果: " + jwtUtil.validateToken(token));
            System.out.println("是否在黑名單: " + blacklistService.isBlacklisted(token));
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        if (blacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token 已失效，請重新登入");
            return;
        }

        if (jwtUtil.validateToken(token)) {
            String phone = jwtUtil.extractPhone(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(phone, null, List.of());
            authentication.setDetails(
                    new org.springframework.security.web.authentication.WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("SecurityContext 設定完成，使用者：" + phone);
        } else {
            System.out.println("Token 驗證失敗");
        }

        if (jwtUtil.validateToken(token)) {
            String phone = jwtUtil.extractPhone(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(phone, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}