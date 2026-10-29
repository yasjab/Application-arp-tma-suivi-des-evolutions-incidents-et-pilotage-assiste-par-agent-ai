package com.atos.arp.services;

import com.atos.arp.models.TicketStatsDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Statistiques de tickets par entité pour l'Agent IA — ne compte que les tickets
 * valides, selon la même règle que l'export Excel et la page Suivi Production
 * (voir TicketMetricsService#isTicketValide).
 */
@Service
public class TicketStatsService {

    private final JdbcTemplate jdbcTemplate;
    private final TicketMetricsService ticketMetricsService;

    public TicketStatsService(JdbcTemplate jdbcTemplate, TicketMetricsService ticketMetricsService) {
        this.jdbcTemplate = jdbcTemplate;
        this.ticketMetricsService = ticketMetricsService;
    }

    public List<TicketStatsDto> getStatsByEntite() {
        List<String> entites = jdbcTemplate.queryForList("SELECT nom FROM entite ORDER BY nom", String.class);

        Map<String, Long> enCoursParEntite  = countValidByEntite("v_encours", true);
        Map<String, Long> diParEntite       = countValidByEntite("v_di_livrees", false);
        Map<String, Long> fcParEntite       = countValidByEntite("v_fc_livrees", false);
        Map<String, Long> annuleesParEntite = countValidByEntite("v_fsp_annulees", false);

        List<TicketStatsDto> result = new ArrayList<>();
        for (String entite : entites) {
            long enCours   = enCoursParEntite.getOrDefault(entite, 0L);
            long diLivrees = diParEntite.getOrDefault(entite, 0L);
            long fcLivrees = fcParEntite.getOrDefault(entite, 0L);
            long annulees  = annuleesParEntite.getOrDefault(entite, 0L);
            long total     = enCours + diLivrees + fcLivrees + annulees; // total réel, affichage

            long baseActive = enCours + diLivrees + fcLivrees; // dénominateur du %, annulés exclus

            double pourcentage = baseActive == 0
                    ? 0.0
                    : (double) (diLivrees + fcLivrees) / baseActive * 100;

            result.add(new TicketStatsDto(
                    entite, enCours, diLivrees, fcLivrees, annulees, total,
                    Math.round(pourcentage * 100.0) / 100.0
            ));
        }
        return result;
    }

    /**
     * Compte, par entité, les tickets valides d'une vue (v_encours, v_di_livrees, ...).
     * @param filtreCategorieEB true pour n'écarter que les EB (cas "Encours Engagés" de l'export Excel).
     */
    private Map<String, Long> countValidByEntite(String view, boolean filtreCategorieEB) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + view);
        Set<String> codesEvolutionTraites = new HashSet<>();
        Map<String, Long> counts = new HashMap<>();

        for (Map<String, Object> row : rows) {
            if (filtreCategorieEB) {
                String categorie = rawTrim(row.get("CATEGORIE"));
                if (!"EB".equalsIgnoreCase(categorie)) continue;
            }

            String codeEvol = rawTrim(row.get("CODE_EVOLUTION"));
            String jira = rawTrim(row.get("JIRA"));
            double chiffrage = toDouble(row.get("CHIFFRAGE"));

            if (!ticketMetricsService.isTicketValide(codeEvol, jira, chiffrage, codesEvolutionTraites)) {
                continue;
            }

            String entite = rawTrim(row.get("ENTITE"));
            if (entite.isEmpty()) continue; // même comportement que le LEFT JOIN SQL d'origine

            counts.merge(entite, 1L, Long::sum);
        }
        return counts;
    }

    private String rawTrim(Object v) {
        return v != null ? v.toString().trim() : "";
    }

    private double toDouble(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString().replace(",", ".").trim()); }
        catch (Exception e) { return 0; }
    }
}
