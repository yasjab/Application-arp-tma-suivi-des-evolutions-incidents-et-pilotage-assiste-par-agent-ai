package com.atos.arp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenRouterClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.model}")
    private String model;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    /** Nombre de tentatives en cas de 429 (rate limit) avant d'abandonner. */
    private static final int MAX_RETRIES = 2;
    /** Délai entre chaque tentative, en millisecondes. */
    private static final long RETRY_DELAY_MS = 1500;

    /** Appel simple sans tools -> texte direct (utilisé par /summary). */
    public String chat(List<Map<String, Object>> messages) {
        JsonNode response = chatRaw(messages, null);
        return response.path("choices").get(0).path("message").path("content").asText();
    }

    /** Appel complet, tools optionnels -> réponse brute (utilisé par /chat). */
    public JsonNode chatRaw(List<Map<String, Object>> messages, List<Map<String, Object>> tools) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        if (tools != null && !tools.isEmpty()) {
            body.put("tools", tools);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String rawResponse = null;
        HttpClientErrorException lastRateLimitError = null;

        for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
            try {
                rawResponse = restTemplate.postForObject(apiUrl, request, String.class);
                lastRateLimitError = null;
                break; // succès, on sort de la boucle
            } catch (HttpClientErrorException.TooManyRequests e) {
                lastRateLimitError = e;
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (HttpClientErrorException e) {
                // 401, 403, 404, etc. -> pas la peine de réessayer
                System.out.println("=== OPENROUTER ERROR === Status: " + e.getStatusCode()
                        + " | Body: " + e.getResponseBodyAsString());
                throw new OpenRouterException(
                        "Erreur d'authentification ou de configuration avec le service IA (" + e.getStatusCode() + ")."
                );
            } catch (HttpServerErrorException e) {
                // 500, 502, 503 côté OpenRouter -> service temporairement indisponible
                throw new OpenRouterException(
                        "Le service IA est temporairement indisponible. Réessaie dans quelques instants."
                );
            } catch (ResourceAccessException e) {
                // Timeout ou pas de réseau
                throw new OpenRouterException(
                        "Impossible de contacter le service IA (problème réseau). Réessaie dans quelques instants."
                );
            }
        }

        if (lastRateLimitError != null) {
            throw new OpenRouterException(
                    "Le modèle IA est actuellement surchargé (limite de requêtes atteinte). " +
                            "Réessaie dans quelques instants."
            );
        }

        JsonNode json;
        try {
            json = mapper.readTree(rawResponse);
        } catch (Exception e) {
            throw new OpenRouterException("Réponse OpenRouter illisible.");
        }

        // Le fournisseur peut renvoyer un 200 avec un corps d'erreur au lieu d'une vraie réponse
        if (json.has("error")) {
            String errMsg = json.path("error").path("message").asText("Erreur inconnue");
            throw new OpenRouterException(
                    "Le modèle IA est temporairement indisponible (" + errMsg + "). Réessaie dans quelques instants."
            );
        }

        if (!json.has("choices") || json.path("choices").isEmpty()) {
            throw new OpenRouterException("Réponse OpenRouter inattendue, pas de contenu généré.");
        }

        return json;
    }
}