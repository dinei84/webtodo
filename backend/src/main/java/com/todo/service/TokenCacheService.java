package com.todo.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service para cache de tokens Firebase.
 * Evita chamar verifyIdToken() em cada requisição - ganho de ~200-500ms por request.
 */
@Service
public class TokenCacheService {

    private static final Logger logger = LoggerFactory.getLogger(TokenCacheService.class);
    
    // Cache: token hash -> (FirebaseToken, expiration time)
    // TTL: 10 minutos (Firebase tokens expiram em 1 hora, mas we cache mais curto por segurança)
    private final Cache<String, CachedToken> tokenCache;
    private final FirebaseAuth firebaseAuth;

    public TokenCacheService(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        this.tokenCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats()
                .build();
        
        logger.info("TokenCacheService inicializado com TTL de 10 minutos");
    }

    /**
     * Valida token com cache.
     * Se o token já foi validado recentemente, retorna do cache.
     * Caso contrário, valida com Firebase e armazena no cache.
     */
    public FirebaseToken validateToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        
        String tokenHash = hashToken(token);
        CachedToken cached = tokenCache.getIfPresent(tokenHash);
        
        if (cached != null && !cached.isExpired()) {
            logger.debug("Token validado via cache");
            return cached.getToken();
        }
        
        // Cache miss - valida com Firebase
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
            long expiresAt = System.currentTimeMillis() + (10 * 60 * 1000);
            CachedToken newCached = new CachedToken(decodedToken, expiresAt);
            tokenCache.put(tokenHash, newCached);
            
            logger.debug("Token validado com Firebase e adicionado ao cache");
            return decodedToken;
        } catch (Exception e) {
            logger.warn("Falha na validacao do token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Invalida um token no cache (ex: logout).
     */
    public void invalidateToken(String token) {
        if (token != null) {
            tokenCache.invalidate(hashToken(token));
            logger.debug("Token invalidado do cache");
        }
    }

    /**
     * Invalida token pelo hash.
     */
    public void invalidateTokenByHash(String tokenHash) {
        tokenCache.invalidate(tokenHash);
    }

    /**
     * Limpa todo o cache.
     */
    public void clearCache() {
        tokenCache.invalidateAll();
        logger.info("Cache de tokens limpo");
    }

    /**
     * Retorna estatísticas do cache.
     */
    public String getCacheStats() {
        return tokenCache.stats().toString();
    }

    /**
     * Gera hash do token para uso como chave no cache.
     */
    private String hashToken(String token) {
        return token.length() > 64 ? token.substring(0, 64) : token;
    }

    /**
     * Wrapper para token cacheado com informações de expiração.
     */
    private static class CachedToken {
        private final FirebaseToken token;
        private final long expiresAt;
        public CachedToken(FirebaseToken token, long expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }

        public FirebaseToken getToken() {
            return token;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}