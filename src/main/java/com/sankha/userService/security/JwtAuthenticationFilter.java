package com.sankha.userService.security;

import com.sankha.userService.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION = "Authorization";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);
        if (checkValidHeader(request, response, filterChain, authorization)) return;
        String jwt = authorization.substring(7);
        String username = jwtService.extractUsername(jwt);
        validateCredentials(request, username, jwt);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        filterChain.doFilter(request, response);
    }

    private void validateCredentials(HttpServletRequest request, String username, String jwt) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
            if (jwtService.validateToken(jwt, userDetails)) {
                saveSecurityContext(request, username);
            }
    }

    private static boolean checkValidHeader(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String authorization) throws IOException, ServletException {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private void saveSecurityContext(HttpServletRequest request, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        System.out.println("userDetails = " + userDetails);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                null, userDetails.getAuthorities()
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

}
