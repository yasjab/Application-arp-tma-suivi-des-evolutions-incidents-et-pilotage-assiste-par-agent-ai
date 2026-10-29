package com.atos.arp.models;

import java.util.List;
import java.util.Map;

public record AgentChatRequest(
        String message,
        List<Map<String, Object>> history // optionnel : historique envoyé par le frontend pour garder le contexte
) {}