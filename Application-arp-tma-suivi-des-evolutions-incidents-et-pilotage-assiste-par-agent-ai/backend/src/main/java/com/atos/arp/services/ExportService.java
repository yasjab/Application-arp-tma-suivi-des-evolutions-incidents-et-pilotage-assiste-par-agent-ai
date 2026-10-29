package com.atos.arp.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TicketMetricsService ticketMetricsService;

    private static final List<String> DSV_ENTITES = List.of("SSVSIC", "SSVSIGR", "DSV");
    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] exportPV(List<String> entitesSelectionnees) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle stdStyle    = createStandardStyle(wb);
            CellStyle numStyle    = createNumberStyle(wb);

            // ── Onglet FC Livrée ──
            buildSheet(wb, "FC Livrée",
                    buildFcLivreesQuery(entitesSelectionnees),
                    buildGenericParams(entitesSelectionnees),
                    new String[]{
                            "Code évolution", "Jira", "Mois financière", "SI", "Palier", "Catégorie",
                            "Lot Contractuel", "Libellé", "Date de création", "Date Go",
                            "Date prévue de livraison", "Date de livraison", "Date GO Etude",
                            "Date Prévue Etude", "Date Première FSP", "Statut", "Chiffrage Dev/TU",
                            "Délai MT", "Délai Etude MT", "Nombre anomalies", "TAR", "TR", "N", "K/K'",
                            "Chiffrage Total", "Entité", "Direction Utilisatrice", "Chiffrage SIM",
                            "Chiffrage SIC", "Contre proposition SIC", "Contre proposition SIM",
                            "Pb chiffrage SIC", "Pb chiffrage SIM", "Chiffrage MT", "CP SI", "CP SIM",
                            "CP SSV", "Auteur", "Chiffrage intégrateur SIM", "catégorie", "Chiffrage SIM",
                            "Charge SIC", "Charge SIM", "Dif entre champs chiffrage et chiffrage intégrateur", "Taux",
                            "Nouvelle valeur contre proposition SIC (valeur sur QC)",
                            "Nouvelle valeur Pb chiffrage SIC (valeur sur QC)",
                            "Chiffrage validé/Chiffrage non validé", "Chiffrage SIC corrigée",
                            "Commentaire SIC", "Nouvelle valeur contre proposition SIM",
                            "Nouvelle valeur Pb chiffrage SIM", "Chiffrage SIM corrigée",
                            "Commentaire SIM", "Retour Prestataire", "OK/Not OK", "Commentaire Prestataire"
                    },
                    new String[]{
                            "CODE_EVOLUTION", "JIRA", "~MOIS~", "~SI~", "PALIER", "CATEGORIE",
                            "LOT_CONTRACTUEL", "LIBELLER", "~DATE~DATE_DE_CREATION", "~DATE~DATE_GO",
                            "~DATE~DATE_PREVUE_DE_LIVRAISON", "~DATE~DATE_DE_LIVRAISON", "~DATE~DATE_GO_ETUDE",
                            "~DATE~DATE_PREVUE_ETUDE", "~DATE~DATE_PREMIERE_FSP", "STATUT", "CHIFFRAGE",
                            "DELAI_MT", "DELAI_ETUDE_MT", "NOMBRE_ANOMALIE", "~TAR~", "~TR~", "~N~", "~K~",
                            "~CT~", "ENTITE", "DIRECTION_UTILISATRISE", "CHIFFRAGE_SIM", "CHIFFRAGE_SIC",
                            "CONTRE_PROPOSITION_SIC", "CONTRE_PROPOSITION_SIM", "PROBLEME_CHIFFRAGE_SIC",
                            "PROBLEME_CHIFFRAGE_SIM", "CHIFFRAGE_MT", "CP_SI", null, null, "LOGIN",
                            "CHIFFRAGE_IX", "CATEGORIE", "CHIFFRAGE_SIM", "~CHARGE_SIC~", "~CHARGE_SIM~",
                            "~DIF_CHIFFRAGE~", "~TAUX~", null, null, null, null, null, null, null, null,
                            null, null, null, null
                    },
                    headerStyle, stdStyle, numStyle);

            // ── Onglet DI Livrée ──
            buildSheet(wb, "DI Livrée",
                    buildDiLivreesQuery(entitesSelectionnees),
                    buildGenericParams(entitesSelectionnees),
                    new String[]{
                            "Code évolution", "Jira", "SI", "Palier", "Catégorie",
                            "Lot Contractuel", "Libellé", "Date de création",
                            "Date de livraison", "Date de livraison (champ QC)",
                            "Date de Passage DI Clôturée (historique)", "Statut",
                            "Chiffrage Dev/TU", "Entité", "Direction Utilisatrice",
                            "Chiffrage SIM", "Chiffrage SIC",
                            "Contre proposition SIC", "Contre proposition SIM",
                            "Pb chiffrage SIC", "Pb chiffrage SIM",
                            "Chiffrage MT", "CP SI", "CP SIM", "CP SSV",
                            "Auteur", "Chiffrage intégrateur SIM", "catégorie",
                            "Chiffrage SIC", "Charge SIC", "Charge SIM", "Dif entre champs chiffrage et chiffrage intégrateur"
                    },
                    new String[]{
                            "CODE_EVOLUTION", "JIRA", "~SI~", "PALIER", "CATEGORIE",
                            "LOT_CONTRACTUEL", "LIBELLER", "~DATE~DATE_DE_CREATION",
                            "~DATE~DATE_DE_LIVRAISON", "~DATE~DATE_DE_LIVRAISON_HP_QC",
                            "~DATE~DATE_DI_CLOTURER_HP_QC", "STATUT",
                            "CHIFFRAGE", "ENTITE", "DIRECTION_UTILISATRISE",
                            "CHIFFRAGE_SIM", "CHIFFRAGE_SIC",
                            "CONTRE_PROPOSITION_SIC", "CONTRE_PROPOSITION_SIM",
                            "PROBLEME_CHIFFRAGE_SIC", "PROBLEME_CHIFFRAGE_SIM",
                            "CHIFFRAGE_MT", "CP_SI", null, null,
                            "LOGIN", "CHIFFRAGE_IX", "CATEGORIE",
                            "CHIFFRAGE_SIC", "~DI_CHARGE_SIC~", "~DI_CHARGE_SIM~", "~DI_DIF_CHIFFRAGE~"
                    },
                    headerStyle, stdStyle, numStyle);

            // ── Onglet FSP Annulée ──
            buildSheet(wb, "FSP Annulée",
                    buildFspAnnuleesQuery(entitesSelectionnees),
                    buildGenericParams(entitesSelectionnees),
                    new String[]{
                            "Code évolution", "Jira", "SI", "Palier", "Catégorie",
                            "Lot Contractuel", "Libellé", "Date de création",
                            "Date d'annulation", "Statut", "Chiffrage Dev/TU",
                            "Entité", "Direction Utilisatrice",
                            "Chiffrage SIC", "Contre proposition SIC",
                            "Contre proposition SIM", "Pb chiffrage SIC",
                            "Pb chiffrage SIM", "Chiffrage MT", "Charge", "CP SI",
                            "CP SIM", "CP SSV", "Auteur",
                            "Chiffrage intégrateur SIM", "catégorie", "Chiffrage SIM",
                            "Charge SIC", "Charge SIM", "Dif entre champs chiffrage et chiffrage intégrateur", "Taux"
                    },
                    new String[]{
                            "CODE_EVOLUTION", "JIRA", "~SI~", "PALIER", "CATEGORIE",
                            "LOT_CONTRACTUEL", "LIBELLER", "~DATE~DATE_DE_CREATION",
                            "~DATE~DATE_DE_ANNULATION", "STATUT", "CHIFFRAGE",
                            "ENTITE", "DIRECTION_UTILISATRISE",
                            "CHIFFRAGE_SIC", "CONTRE_PROPOSITION_SIC",
                            "CONTRE_PROPOSITION_SIM", "PROBLEME_CHIFFRAGE_SIC",
                            "PROBLEME_CHIFFRAGE_SIM", "CHIFFRAGE_MT", "~FSP_CHARGE_CALC~", "CP_SI",
                            null, null, "LOGIN",
                            "CHIFFRAGE_IX", "CATEGORIE", "CHIFFRAGE_SIM",
                            "~FSP_CHARGE_SIC~", "~FSP_CHARGE_SIM~", "~FSP_DIF_CHIFFRAGE~", "~FSP_TAUX~"
                    },
                    headerStyle, stdStyle, numStyle);

            // ── Onglet Encours Engagés ──
            buildSheet(wb, "Encours Engagés",
                    buildEncoursQuery(entitesSelectionnees),
                    buildGenericParams(entitesSelectionnees),
                    new String[]{
                            "Code évolution", "Jira", "SI", "Palier", "Catégorie",
                            "Lot Contractuel", "Libellé", "Date de création",
                            "Statut", "Chiffrage Dev/TU", "Charge Evol",
                            "Entité", "Direction Utilisatrice",
                            "Chiffrage SIM", "Chiffrage SIC",
                            "Contre proposition SIC", "Contre proposition SIM",
                            "Pb chiffrage SIC", "Pb chiffrage SIM",
                            "Chiffrage MT", "CP SI", "CP SIM", "CP SSV",
                            "Auteur", "Chiffrage intégrateur SIM", "catégorie",
                            "Chiffrage SIM",
                            "Charge SIC",
                            "Charge SIM",
                            "Dif entre champs chiffrage et chiffrage intégrateur",
                            "Taux"
                    },
                    new String[]{
                            "CODE_EVOLUTION", "JIRA", "~SI~", "PALIER", "CATEGORIE",
                            "LOT_CONTRACTUEL", "LIBELLER", "~DATE~DATE_DE_CREATION",
                            "STATUT", "CHIFFRAGE", "~ENCOURS_CHARGE_EVOL~",
                            "ENTITE", "DIRECTION_UTILISATRISE",
                            "CHIFFRAGE_SIM", "CHIFFRAGE_SIC",
                            "CONTRE_PROPOSITION_SIC", "CONTRE_PROPOSITION_SIM",
                            "PROBLEME_CHIFFRAGE_SIC", "PROBLEME_CHIFFRAGE_SIM",
                            "CHIFFRAGE_MT", "CP_SI", null, null,
                            "LOGIN", "~ENCOURS_CHIFFRAGE_IX~", "CATEGORIE",
                            "CHIFFRAGE_SIM",
                            "~ENCOURS_CHARGE_SIC~",
                            "~ENCOURS_CHARGE_SIM~",
                            "~ENCOURS_DIF_CHIFFRAGE~",
                            "~ENCOURS_TAUX~"
                    },
                    headerStyle, stdStyle, numStyle);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    private void buildSheet(XSSFWorkbook wb, String sheetName,
                            String sql, Object[] params,
                            String[] headers, String[] columns,
                            CellStyle headerStyle, CellStyle stdStyle, CellStyle numStyle) {

        Sheet sheet = wb.createSheet(sheetName);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        List<Map<String, Object>> rows = params.length == 0
                ? jdbcTemplate.queryForList(sql)
                : jdbcTemplate.queryForList(sql, params);

        Map<String, TicketMetricsService.FcMetrics> fcMetricsMap = "FC Livrée".equals(sheetName)
                ? ticketMetricsService.calculeFcMetrics(rows)
                : new HashMap<>();

        if ("FC Livrée".equals(sheetName)) {
            rows.sort((a, b) -> {
                Object jA = a.get("JIRA");
                Object jB = b.get("JIRA");
                String jiraA = (jA != null) ? jA.toString().trim() : "";
                String jiraB = (jB != null) ? jB.toString().trim() : "";

                TicketMetricsService.FcMetrics mA = fcMetricsMap.get(jiraA);
                TicketMetricsService.FcMetrics mB = fcMetricsMap.get(jiraB);

                int valA = (mA != null && !mA.moisFinancier.isEmpty()) ? mA.iMoisFinancier : 0;
                int valB = (mB != null && !mB.moisFinancier.isEmpty()) ? mB.iMoisFinancier : 0;

                if (valA > 0 && valB == 0) return -1;
                if (valA == 0 && valB > 0) return 1;
                if (valA == 0 && valB == 0) return jiraA.compareTo(jiraB);
                if (valB != valA) return Integer.compare(valB, valA);
                return jiraA.compareTo(jiraB);
            });
        }

        int rowIdx = 1;
        Set<String> codesEvolutionTraites = new HashSet<>();

        for (Map<String, Object> rowData : rows) {
            Object codeEvolVal = rowData.get("CODE_EVOLUTION");
            String codeEvol = (codeEvolVal != null) ? codeEvolVal.toString().trim() : "";

            if ("Encours Engagés".equals(sheetName)) {
                Object catVal = rowData.get("CATEGORIE");
                String categorie = (catVal != null) ? catVal.toString().trim() : "";
                if (!"EB".equalsIgnoreCase(categorie)) {
                    continue;
                }
            }
            Object jiraVal = rowData.get("JIRA");
            String jira = (jiraVal != null) ? jiraVal.toString().trim() : "";
            double chiffrageDevTu = getDouble(rowData.get("CHIFFRAGE"));

            if (!ticketMetricsService.isTicketValide(codeEvol, jira, chiffrageDevTu, codesEvolutionTraites)) {
                continue;
            }

            TicketMetricsService.FcMetrics m = fcMetricsMap.getOrDefault(jira, new TicketMetricsService.FcMetrics());
            Row row = sheet.createRow(rowIdx++);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = row.createCell(i);
                String col = columns[i];

                if (col == null) {
                    cell.setCellValue("");
                    cell.setCellStyle(stdStyle);
                } else if ("~SI~".equals(col)) {
                    cell.setCellValue(jira.startsWith("BM-") ? "MOBILE" : "FIXE");
                    cell.setCellStyle(stdStyle);
                } else if ("~MOIS~".equals(col)) {
                    cell.setCellValue(m.isCalculatedValide ? m.moisFinancier : "");
                    cell.setCellStyle(stdStyle);
                } else if ("~TAR~".equals(col)) {
                    cell.setCellValue(round2(m.tar * 100) + "%");
                    cell.setCellStyle(stdStyle);
                } else if ("~TR~".equals(col)) {
                    cell.setCellValue(round2(m.tr * 100) + "%");
                    cell.setCellStyle(stdStyle);
                } else if ("~N~".equals(col)) {
                    cell.setCellValue(round2(m.n * 100) + "%");
                    cell.setCellStyle(stdStyle);
                } else if ("~K~".equals(col)) {
                    String statut = rowData.get("STATUT") != null
                            ? rowData.get("STATUT").toString() : "";

                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        double valeur = ticketMetricsService.tauxPourStatut(statut);
                        cell.setCellValue(round2(valeur));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~TAUX~".equals(col) || "~FSP_TAUX~".equals(col)) {
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        String statut = rowData.get("STATUT") != null ? rowData.get("STATUT").toString() : "";
                        double taux = ticketMetricsService.tauxPourStatut(statut);
                        cell.setCellValue(round2(taux));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~CT~".equals(col)) {
                    String statut = rowData.get("STATUT") != null
                            ? rowData.get("STATUT").toString() : "";
                    double k = ticketMetricsService.tauxPourStatut(statut);
                    cell.setCellValue(round2(chiffrageDevTu * k));
                    cell.setCellStyle(numStyle);
                } else if ("~CHARGE_SIM~".equals(col)) {
                    double sim = getDouble(rowData.get("CHIFFRAGE_SIM"));
                    double ix  = getDouble(rowData.get("CHIFFRAGE_IX"));
                    cell.setCellValue(round2(sim * ix));
                    cell.setCellStyle(numStyle);
                } else if ("~CHARGE_SIC~".equals(col)) {
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        String statut = rowData.get("STATUT") != null ? rowData.get("STATUT").toString() : "";
                        double kStatut = ticketMetricsService.tauxPourStatut(statut);
                        double chiffrageTotalLigne = chiffrageDevTu * kStatut;

                        double kFacteur = (m != null && m.k != 1.0) ? m.k : kStatut;

                        Object ixObj = rowData.get("CHIFFRAGE_IX");
                        double amValue = 0.0;
                        if (ixObj != null && !ixObj.toString().isBlank()) {
                            amValue = getDouble(ixObj);
                        }

                        double chargeSic = chiffrageTotalLigne - (kFacteur * amValue);

                        cell.setCellValue(round2(chargeSic));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~DI_CHARGE_SIM~".equals(col)) {
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        Object ixObj = rowData.get("CHIFFRAGE_IX");
                        double chargeSim = 0.0;
                        if (ixObj != null && !ixObj.toString().isBlank()) {
                            chargeSim = getDouble(ixObj);
                        }
                        cell.setCellValue(round2(chargeSim));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~DI_CHARGE_SIC~".equals(col)) {
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        Object ixObj = rowData.get("CHIFFRAGE_IX");
                        double chargeSim = 0.0;
                        if (ixObj != null && !ixObj.toString().isBlank()) {
                            chargeSim = getDouble(ixObj);
                        }
                        double chargeSic = chiffrageDevTu - chargeSim;
                        cell.setCellValue(round2(chargeSic));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~DI_DIF_CHIFFRAGE~".equals(col)) {
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        double chiffrageMt = getDouble(rowData.get("CHIFFRAGE_MT"));
                        double dif = chiffrageMt - chiffrageDevTu;
                        cell.setCellValue(round2(dif));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~FSP_CHARGE_SIM~".equals(col)) {
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        double chiffrageSim = getDouble(rowData.get("CHIFFRAGE_SIM"));
                        double chargeSim = chiffrageSim * 0.24;
                        cell.setCellValue(round2(chargeSim));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~FSP_CHARGE_CALC~".equals(col)) {
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        String statut = rowData.get("STATUT") != null ? rowData.get("STATUT").toString() : "";
                        double taux = ticketMetricsService.tauxPourStatut(statut);
                        double chargeCalc = chiffrageDevTu * taux;
                        cell.setCellValue(round2(chargeCalc));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~FSP_CHARGE_SIC~".equals(col)) {
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        String statut = rowData.get("STATUT") != null ? rowData.get("STATUT").toString() : "";
                        double taux = ticketMetricsService.tauxPourStatut(statut);
                        double chargeCalc = chiffrageDevTu * taux;

                        double chiffrageSim = getDouble(rowData.get("CHIFFRAGE_SIM"));
                        double chargeSim = chiffrageSim * 0.24;

                        double chargeSic = chargeCalc - chargeSim;
                        cell.setCellValue(round2(chargeSic));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~FSP_DIF_CHIFFRAGE~".equals(col)) {
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        double chiffrageMt = getDouble(rowData.get("CHIFFRAGE_MT"));
                        double dif = chiffrageMt - chiffrageDevTu;
                        cell.setCellValue(round2(dif));
                        cell.setCellStyle(numStyle);
                    }
                } else if ("~DIF_CHIFFRAGE~".equals(col)) {
                    double chiffrageMt = getDouble(rowData.get("CHIFFRAGE_MT"));
                    cell.setCellValue(round2(chiffrageMt - chiffrageDevTu));
                    cell.setCellStyle(numStyle);

                    // ── Encours Engagés : colonnes calculées ──

                } else if ("~ENCOURS_CHIFFRAGE_IX~".equals(col)) {
                    // Chiffrage intégrateur SIM : toujours afficher la valeur, 0 si vide/null
                    double ix = getDouble(rowData.get("CHIFFRAGE_IX"));
                    cell.setCellValue(round2(ix));
                    cell.setCellStyle(numStyle);

                } else if ("~ENCOURS_TAUX~".equals(col)) {
                    // =SI(A2="";"";RECHERCHEV(statut; TAUX; 2; FAUX))
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        String statut = rowData.get("STATUT") != null ? rowData.get("STATUT").toString() : "";
                        double taux = ticketMetricsService.tauxPourStatut(statut);
                        cell.setCellValue(round2(taux));
                        cell.setCellStyle(numStyle);
                    }

                } else if ("~ENCOURS_CHARGE_EVOL~".equals(col)) {
                    // =SI(A2="";"";J2*AE2)  →  chiffrageDevTu * taux
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        String statut = rowData.get("STATUT") != null ? rowData.get("STATUT").toString() : "";
                        double taux = ticketMetricsService.tauxPourStatut(statut);
                        cell.setCellValue(round2(chiffrageDevTu * taux));
                        cell.setCellStyle(numStyle);
                    }

                } else if ("~ENCOURS_CHARGE_SIM~".equals(col)) {
                    // =SI(A2<>"";SI(J2<>"";SI(AA2<>"";AA2;0)*K2/J2;0);"")
                    // K = chargeEvol, AA = CHIFFRAGE_SIM, J = chiffrageDevTu
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        String statut = rowData.get("STATUT") != null ? rowData.get("STATUT").toString() : "";
                        double taux = ticketMetricsService.tauxPourStatut(statut);
                        double chargeEvol = chiffrageDevTu * taux;
                        double chiffrageSim = getDouble(rowData.get("CHIFFRAGE_SIM"));
                        double chargeSim = (chiffrageDevTu != 0)
                                ? chiffrageSim * chargeEvol / chiffrageDevTu
                                : 0.0;
                        cell.setCellValue(round2(chargeSim));
                        cell.setCellStyle(numStyle);
                    }

                } else if ("~ENCOURS_CHARGE_SIC~".equals(col)) {
                    // =SI(A2<>"";SI(K2<>"";K2;0)-AC2;"")
                    // K = chargeEvol, AC = chargeSIM
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        String statut = rowData.get("STATUT") != null ? rowData.get("STATUT").toString() : "";
                        double taux = ticketMetricsService.tauxPourStatut(statut);
                        double chargeEvol = chiffrageDevTu * taux;
                        double chiffrageSim = getDouble(rowData.get("CHIFFRAGE_SIM"));
                        double chargeSim = (chiffrageDevTu != 0)
                                ? chiffrageSim * chargeEvol / chiffrageDevTu
                                : 0.0;
                        double chargeSic = chargeEvol - chargeSim;
                        cell.setCellValue(round2(chargeSic));
                        cell.setCellStyle(numStyle);
                    }

                } else if ("~ENCOURS_DIF_CHIFFRAGE~".equals(col)) {
                    // =SI(A2<>"";T2-J2;"")  →  chiffrageMT - chiffrageDevTu
                    if (codeEvol.isBlank()) {
                        cell.setCellValue("");
                        cell.setCellStyle(stdStyle);
                    } else {
                        double chiffrageMt = getDouble(rowData.get("CHIFFRAGE_MT"));
                        cell.setCellValue(round2(chiffrageMt - chiffrageDevTu));
                        cell.setCellStyle(numStyle);
                    }

                } else if (col.startsWith("~DATE~")) {
                    String dateKey = col.substring(6);
                    if ("FC Livrée".equals(sheetName) && "DATE_PREMIERE_FSP".equals(dateKey) && !m.isCalculatedValide) {
                        cell.setCellValue("");
                    } else {
                        if ("Encours Engagés".equals(sheetName)
                                && "DATE_DE_CREATION".equals(dateKey)) {
                            cell.setCellValue(formatDateTime(rowData.get(dateKey)));
                        } else {
                            cell.setCellValue(formatDate(rowData.get(dateKey)));
                        }
                    }
                    cell.setCellStyle(stdStyle);
                } else if ("CHIFFRAGE_IX".equals(col)) {
                    // Chiffrage intégrateur SIM : ne jamais laisser vide, mettre 0 si null
                    Object val = rowData.get(col);
                    double v = (val != null && !val.toString().isBlank()) ? getDouble(val) : 0.0;
                    cell.setCellValue(round2(v));
                    cell.setCellStyle(numStyle);
                } else {
                    writeCell(cell, rowData.get(col), stdStyle, numStyle);
                }
            }
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < 2000) sheet.setColumnWidth(i, 2000);
        }
    }

    private String buildFcLivreesQuery(List<String> e)   { return buildGenericQuery("v_fc_livrees",  e); }
    private String buildDiLivreesQuery(List<String> e)   { return buildGenericQuery("v_di_livrees",  e); }
    private String buildFspAnnuleesQuery(List<String> e) { return buildGenericQuery("v_fsp_annulees",e); }
    private String buildEncoursQuery(List<String> e)     { return buildGenericQuery("v_encours",     e); }

    private String buildGenericQuery(String table, List<String> entites) {
        if (entites.contains("*")) return "SELECT * FROM " + table;
        List<String> conds  = new ArrayList<>();
        List<String> inList = new ArrayList<>();
        for (String e : entites) {
            if      ("DSI".equals(e)) conds.add("(ENTITE = 'DSI' OR ENTITE IS NULL OR ENTITE = '')");
            else if ("DSV".equals(e)) inList.addAll(DSV_ENTITES);
            else                      inList.add(e);
        }
        if (!inList.isEmpty())
            conds.add("ENTITE IN (" + String.join(",", inList.stream().map(x -> "?").toList()) + ")");
        return "SELECT * FROM " + table
                + (conds.isEmpty() ? " WHERE 1=0" : " WHERE " + String.join(" OR ", conds));
    }

    private Object[] buildGenericParams(List<String> entites) {
        if (entites.contains("*")) return new Object[]{};
        List<Object> p = new ArrayList<>();
        for (String e : entites) {
            if ("DSV".equals(e)) p.addAll(DSV_ENTITES);
            else if (!"DSI".equals(e)) p.add(e);
        }
        return p.toArray();
    }

    private String formatDate(Object val) {
        if (val == null) return "";

        try {
            if (val instanceof LocalDateTime ldt)
                return ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            if (val instanceof Timestamp ts)
                return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            if (val instanceof Date d)
                return new SimpleDateFormat("dd/MM/yyyy").format(d);

            return val.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private double getDouble(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString().replace(",", ".").trim()); }
        catch (Exception e) { return 0; }
    }

    private int getInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString().trim()); }
        catch (Exception e) { return 0; }
    }

    private double round2(double v) {
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private void writeCell(Cell cell, Object val, CellStyle s, CellStyle n) {
        if (val instanceof Number num) {
            cell.setCellValue(num.doubleValue());
            cell.setCellStyle(n);
        } else {
            String str = (val == null) ? "" : val.toString();
            cell.setCellValue("null".equals(str) ? "" : str);
            cell.setCellStyle(s);
        }
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.RED.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        setBorders(s);
        return s;
    }

    private CellStyle createNumberStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        s.setAlignment(HorizontalAlignment.RIGHT);
        setBorders(s);
        return s;
    }

    private CellStyle createStandardStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        setBorders(s);
        return s;
    }

    private void setBorders(CellStyle s) {
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }

    private String formatDateTime(Object val) {
        if (val == null) return "";

        try {
            if (val instanceof LocalDateTime ldt)
                return ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            if (val instanceof Timestamp ts)
                return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            if (val instanceof Date d)
                return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(d);

            return val.toString();
        } catch (Exception e) {
            return "";
        }
    }
}