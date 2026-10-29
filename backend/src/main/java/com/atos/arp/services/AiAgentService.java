package com.atos.arp.services;

import com.atos.arp.models.AgentChatRequest;
import com.atos.arp.models.AgentChatResponse;
import com.atos.arp.models.TicketStatsDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiAgentService {

    private final TicketStatsService ticketStatsService;
    private final OpenRouterClient openRouterClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AiAgentService(TicketStatsService ticketStatsService, OpenRouterClient openRouterClient) {
        this.ticketStatsService = ticketStatsService;
        this.openRouterClient = openRouterClient;
    }

    public List<TicketStatsDto> getRawStats() {
        return ticketStatsService.getStatsByEntite();
    }

    public AgentChatResponse getSummary() {
        try {
            List<TicketStatsDto> stats = ticketStatsService.getStatsByEntite();

            String statsAsText = stats.stream()
                    .map(s -> "- %s : %d en cours, %d DI livrées, %d FC livrées, %d annulées, %.1f%% livrées"
                            .formatted(s.entite(), s.enCours(), s.diLivrees(), s.fcLivrees(), s.annulees(), s.pourcentageLivrees()))
                    .collect(Collectors.joining("\n"));

            String systemPrompt = """
                Tu es un assistant d'analyse pour une équipe technique. On te donne des statistiques
                réelles de tickets par entité. Ne les modifie jamais, ne calcule pas de nouveaux chiffres.
                Réponds en français, de façon synthétique (5-6 lignes max), en mettant en avant les entités
                en difficulté et les points positifs.
                """;

            List<Map<String, Object>> messages = List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", "Statistiques tickets par entité :\n" + statsAsText)
            );

            return new AgentChatResponse(openRouterClient.chat(messages));

        } catch (OpenRouterException e) {
            return new AgentChatResponse(e.getMessage());
        }
    }

    // ---------------- CHAT avec function calling ----------------

    private static final String CHAT_SYSTEM_PROMPT = """
        Tu es un assistant d'analyse de tickets pour une équipe technique.
        Tu n'as accès aux chiffres réels QUE via l'outil get_ticket_stats — tu ne dois
        JAMAIS inventer ou estimer un nombre toi-même. Si la question porte sur des
        statistiques de tickets, appelle systématiquement l'outil avant de répondre.
        Réponds en français, de façon claire et concise.
        """;

    private static final List<Map<String, Object>> TOOLS = List.of(
            Map.of(
                    "type", "function",
                    "function", Map.of(
                            "name", "get_ticket_stats",
                            "description", "Récupère les statistiques réelles de tickets (en cours, DI livrées, FC livrées, annulées, % livrées), globalement ou filtrées sur une entité précise.",
                            "parameters", Map.of(
                                    "type", "object",
                                    "properties", Map.of(
                                            "entite", Map.of(
                                                    "type", "string",
                                                    "description", "Nom exact de l'entité à filtrer. Omettre pour toutes les entités."
                                            )
                                    ),
                                    "required", List.of()
                            )
                    )
            )
    );

    public AgentChatResponse chat(AgentChatRequest req) {
        try {
            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", CHAT_SYSTEM_PROMPT));
            if (req.history() != null) {
                messages.addAll(req.history());
            }
            messages.add(Map.of("role", "user", "content", req.message()));

            JsonNode response = openRouterClient.chatRaw(messages, TOOLS);
            JsonNode message = response.path("choices").get(0).path("message");
            JsonNode toolCalls = message.path("tool_calls");

            if (toolCalls.isArray() && !toolCalls.isEmpty()) {
                messages.add(mapper.convertValue(message, Map.class));

                for (JsonNode toolCall : toolCalls) {
                    String toolCallId = toolCall.path("id").asText();
                    String functionName = toolCall.path("function").path("name").asText();
                    String argumentsJson = toolCall.path("function").path("arguments").asText("{}");

                    String result = executeTool(functionName, argumentsJson);

                    messages.add(Map.of(
                            "role", "tool",
                            "tool_call_id", toolCallId,
                            "content", result
                    ));
                }

                JsonNode finalResponse = openRouterClient.chatRaw(messages, null);
                String finalContent = finalResponse.path("choices").get(0).path("message").path("content").asText();
                return new AgentChatResponse(finalContent);
            }

            return new AgentChatResponse(message.path("content").asText());

        } catch (OpenRouterException e) {
            return new AgentChatResponse(e.getMessage());
        }
    }

    private String executeTool(String functionName, String argumentsJson) {
        if (!"get_ticket_stats".equals(functionName)) {
            return "{\"error\": \"outil inconnu\"}";
        }
        try {
            JsonNode args = mapper.readTree(argumentsJson);
            String entiteFilter = args.has("entite") ? args.get("entite").asText(null) : null;

            List<TicketStatsDto> stats = ticketStatsService.getStatsByEntite();
            if (entiteFilter != null && !entiteFilter.isBlank()) {
                stats = stats.stream()
                        .filter(s -> s.entite() != null && s.entite().equalsIgnoreCase(entiteFilter))
                        .collect(Collectors.toList());
            }
            return mapper.writeValueAsString(stats);
        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}