package com.atos.arp.controllers;

import com.atos.arp.models.AgentChatRequest;
import com.atos.arp.models.AgentChatResponse;
import com.atos.arp.models.TicketStatsDto;
import com.atos.arp.services.AiAgentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class AiAgentController {

    private final AiAgentService aiAgentService;

    public AiAgentController(AiAgentService aiAgentService) {
        this.aiAgentService = aiAgentService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public AgentChatResponse getSummary() {
        return aiAgentService.getSummary();
    }

    @PostMapping("/chat")
    @PreAuthorize("hasRole('ADMIN')")
    public AgentChatResponse chat(@RequestBody AgentChatRequest req) {
        return aiAgentService.chat(req);
    }

    @GetMapping("/stats")
    public List<TicketStatsDto> getStats() {
        return aiAgentService.getRawStats();
    }
}