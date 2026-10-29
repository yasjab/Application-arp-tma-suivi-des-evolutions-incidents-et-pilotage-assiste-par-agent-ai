package com.atos.arp.controllers;

import com.atos.arp.models.DiLivreeDto;
import com.atos.arp.models.FcLivreeDto;
import com.atos.arp.services.TicketsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketsController {

    @Autowired
    private TicketsService ticketsService;

    @GetMapping("/fc-livrees")
    public List<FcLivreeDto> fcLivrees() {
        return ticketsService.getFcLivrees();
    }

    @GetMapping("/di-livrees")
    public List<DiLivreeDto> diLivrees() {
        return ticketsService.getDiLivrees();
    }
}
