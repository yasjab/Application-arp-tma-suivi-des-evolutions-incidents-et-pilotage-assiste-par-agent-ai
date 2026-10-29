package com.atos.arp.services;

import com.atos.arp.models.DiLivreeDto;
import com.atos.arp.models.FcLivreeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Alimente les pages écran de suivi de production (FC Livrées, DI Livrées, ...). */
@Service
public class TicketsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TicketMetricsService ticketMetricsService;

    public List<FcLivreeDto> getFcLivrees() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM v_fc_livrees");
        Map<String, TicketMetricsService.FcMetrics> metrics = ticketMetricsService.calculeFcMetrics(rows);

        List<FcLivreeDto> result = new ArrayList<>();
        Set<String> codesEvolutionTraites = new HashSet<>();

        for (Map<String, Object> row : rows) {
            Double chiffrage = toDouble(row.get("CHIFFRAGE"));
            String codeEvolRaw = rawTrim(row.get("CODE_EVOLUTION"));
            String jiraRaw = rawTrim(row.get("JIRA"));

            if (!ticketMetricsService.isTicketValide(codeEvolRaw, jiraRaw,
                    chiffrage != null ? chiffrage : 0, codesEvolutionTraites)) {
                continue;
            }

            String jira = str(row.get("JIRA"));

            FcLivreeDto dto = new FcLivreeDto();
            dto.setCodeEvolution(toLong(row.get("CODE_EVOLUTION")));
            dto.setJira(jira);
            dto.setSi(ticketMetricsService.deriveSi(jira));
            dto.setPalier(str(row.get("PALIER")));
            dto.setPalierEtude(str(row.get("PALIER_ETUDE")));
            dto.setLibelle(str(row.get("LIBELLER")));
            dto.setLotContractuel(str(row.get("LOT_CONTRACTUEL")));
            dto.setCategorie(str(row.get("CATEGORIE")));
            dto.setDateDeCreation(toLdt(row.get("DATE_DE_CREATION")));
            dto.setDateGo(toLdt(row.get("DATE_GO")));
            dto.setDatePrevueDeLivraison(toLdt(row.get("DATE_PREVUE_DE_LIVRAISON")));
            dto.setDateDeLivraison(toLdt(row.get("DATE_DE_LIVRAISON")));
            dto.setDateDeLivraisonQc(toLdt(row.get("DATE_DE_LIVRAISON_QC")));
            dto.setDateGoEtude(toLdt(row.get("DATE_GO_ETUDE")));
            dto.setDatePrevueEtude(toLdt(row.get("DATE_PREVUE_ETUDE")));
            dto.setDatePremiereFsp(toLdt(row.get("DATE_PREMIERE_FSP")));
            dto.setStatut(str(row.get("STATUT")));
            dto.setChiffrage(chiffrage);
            dto.setDelaiMT(toDouble(row.get("DELAI_MT")));
            dto.setDelaiEtudeMT(toDouble(row.get("DELAI_ETUDE_MT")));
            dto.setNombreAnomalies(toLong(row.get("NOMBRE_ANOMALIE")));
            dto.setEntite(str(row.get("ENTITE")));
            dto.setDirectionUtilisatrice(str(row.get("DIRECTION_UTILISATRISE")));
            dto.setChiffrageSim(str(row.get("CHIFFRAGE_SIM")));

            TicketMetricsService.FcMetrics m = metrics.get(jira);
            if (m != null && m.isCalculatedValide) {
                dto.setMoisFinanciere(m.moisFinancier);
                dto.setTar(round2(m.tar));
                dto.setTr(round2(m.tr));
                dto.setN(round2(m.n));
                dto.setK(round2(m.k));
                if (chiffrage != null) dto.setCharge(round2(chiffrage * m.k));
            }

            result.add(dto);
        }

        result.sort(Comparator.comparing(FcLivreeDto::getDateDeLivraison,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    public List<DiLivreeDto> getDiLivrees() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM v_di_livrees");

        List<DiLivreeDto> result = new ArrayList<>();
        Set<String> codesEvolutionTraites = new HashSet<>();

        for (Map<String, Object> row : rows) {
            Double chiffrage = toDouble(row.get("CHIFFRAGE"));
            String codeEvolRaw = rawTrim(row.get("CODE_EVOLUTION"));
            String jiraRaw = rawTrim(row.get("JIRA"));

            if (!ticketMetricsService.isTicketValide(codeEvolRaw, jiraRaw,
                    chiffrage != null ? chiffrage : 0, codesEvolutionTraites)) {
                continue;
            }

            String jira = str(row.get("JIRA"));

            DiLivreeDto dto = new DiLivreeDto();
            dto.setCodeEvolution(toLong(row.get("CODE_EVOLUTION")));
            dto.setJira(jira);
            dto.setSi(ticketMetricsService.deriveSi(jira));
            dto.setPalier(str(row.get("PALIER")));
            dto.setPalierEtude(str(row.get("PALIER_ETUDE")));
            dto.setLibelle(str(row.get("LIBELLER")));
            dto.setLotContractuel(str(row.get("LOT_CONTRACTUEL")));
            dto.setCategorie(str(row.get("CATEGORIE")));
            dto.setDateDeCreation(toLdt(row.get("DATE_DE_CREATION")));
            dto.setDateDeLivraison(toLdt(row.get("DATE_DE_LIVRAISON")));
            dto.setDateDeLivraisonHistorique(toLdt(row.get("DATE_DE_LIVRAISON_HP_QC")));
            dto.setDateDiClotureeHistorique(toLdt(row.get("DATE_DI_CLOTURER_HP_QC")));
            dto.setStatut(str(row.get("STATUT")));
            dto.setChiffrage(chiffrage);
            dto.setEntite(str(row.get("ENTITE")));
            dto.setDirectionUtilisatrice(str(row.get("DIRECTION_UTILISATRISE")));
            dto.setChiffrageSim(str(row.get("CHIFFRAGE_SIM")));

            result.add(dto);
        }

        result.sort(Comparator.comparing(DiLivreeDto::getDateDeLivraisonHistorique,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    private String rawTrim(Object v) {
        return v != null ? v.toString().trim() : "";
    }

    private String str(Object v) {
        if (v == null) return null;
        String s = v.toString().trim();
        return (s.isEmpty() || "null".equalsIgnoreCase(s)) ? null : s;
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString().trim()); }
        catch (Exception e) { return null; }
    }

    private Double toDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString().replace(",", ".").trim()); }
        catch (Exception e) { return null; }
    }

    private LocalDateTime toLdt(Object v) {
        if (v == null) return null;
        if (v instanceof LocalDateTime ldt) return ldt;
        if (v instanceof Timestamp ts) return ts.toLocalDateTime();
        if (v instanceof LocalDate ld) return ld.atStartOfDay();
        if (v instanceof java.util.Date d) return new Timestamp(d.getTime()).toLocalDateTime();
        return null;
    }

    private Double round2(double v) {
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
