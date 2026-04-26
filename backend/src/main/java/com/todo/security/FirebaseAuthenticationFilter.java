package com.todo.security;

import com.google.firebase.auth.FirebaseToken;
import com.todo.service.TokenCacheService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filtro de autenticacao Firebase com cache.
 * Valida o Firebase ID Token presente no header Authorization.
 * Usa cache para evitar chamadas repetidas ao Firebase - ganho de ~200-500ms por request.
 */
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenCacheService tokenCacheService;

    public FirebaseAuthenticationFilter(TokenCacheService tokenCacheService) {
        this.tokenCacheService = tokenCacheService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);


            if (token != null) {
                // Usa cache para validar token (ganho de ~200-500ms por request)
                FirebaseToken decodedToken = tokenCacheService.validateToken(token);
                
                if (decodedToken != null) {
                    String uid = decodedToken.getUid();
                    String email = decodedToken.getEmail();


                    logger.debug("Token validado com sucesso para usuario: {}", email);

                    // Cria a autenticacao do Spring Security
                    UserPrincipal userPrincipal = new UserPrincipal(uid, email);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userPrincipal, null, new ArrayList<>());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao validar Firebase token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token do header Authorization.
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}