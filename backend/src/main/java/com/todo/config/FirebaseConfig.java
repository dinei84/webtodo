package com.todo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Configuração do Firebase Admin SDK.
 * Inicializa a conexão com o Firebase usando o arquivo de credenciais da Service Account.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials.path}")
    private Resource firebaseCredentials;

    @Value("${FIREBASE_CREDENTIALS_JSON:}")
    private String firebaseCredentialsJson;

    /**
     * Inicializa o Firebase App na inicialização da aplicação.
     */
    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream credentialsStream = resolveCredentialsStream();
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                        .build();

                FirebaseApp.initializeApp(options);
                logger.info("Firebase Admin SDK inicializado com sucesso");
            }
        } catch (IOException e) {
            logger.error("Erro ao inicializar Firebase Admin SDK", e);
            throw new RuntimeException("Falha ao inicializar Firebase", e);
        }
    }

    private InputStream resolveCredentialsStream() throws IOException {
        if (firebaseCredentialsJson != null && !firebaseCredentialsJson.isBlank()) {
            logger.info("Inicializando Firebase Admin SDK com credenciais vindas da variável FIREBASE_CREDENTIALS_JSON");
            return new ByteArrayInputStream(firebaseCredentialsJson.getBytes(StandardCharsets.UTF_8));
        }

        logger.info("Inicializando Firebase Admin SDK com credenciais do caminho configurado: {}", firebaseCredentials);
        return firebaseCredentials.getInputStream();
    }

    /**
     * Bean do FirebaseAuth para validação de tokens.
     */
    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    /**
     * Bean do Firestore para acesso ao banco de dados.
     */
    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
