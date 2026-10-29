package com.atos.arp.models;

public record TicketStatsDto(
        String entite,
        long enCours,
        long diLivrees,
        long fcLivrees,
        long annulees,
        long total,
        double pourcentageLivrees   // (diLivrees + fcLivrees) / total * 100
) {}
