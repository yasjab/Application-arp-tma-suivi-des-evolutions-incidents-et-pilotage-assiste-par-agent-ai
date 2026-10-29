package com.atos.arp.controllers;
import org.springframework.jdbc.core.JdbcTemplate;
import com.atos.arp.services.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @PostMapping("/pv")
    public ResponseEntity<byte[]> exportPV(@RequestBody ExportRequest request) throws IOException {

        List<String> entites = request.getEntites();
        if (entites == null || entites.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        byte[] excelBytes = exportService.exportPV(entites);

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        String entitesPart = String.join("_", entites);
        String filename = entitesPart + "_" + date + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    public static class ExportRequest {
        private List<String> entites;
        public List<String> getEntites() { return entites; }
        public void setEntites(List<String> entites) { this.entites = entites; }
    }
    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT JIRA, CHIFFRAGE, DATE_DE_LIVRAISON, DATE_GO, " +
                            "DATE_PREVUE_DE_LIVRAISON, DATE_GO_ETUDE, DATE_PREVUE_ETUDE, " +
                            "DATE_PREMIERE_FSP FROM v_fc_livrees WHERE ENTITE = 'SIOGP' LIMIT 5"
            );

            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> row : rows) {
                sb.append("JIRA: ").append(row.get("JIRA")).append("\n");
                sb.append("  CHIFFRAGE: ").append(row.get("CHIFFRAGE"))
                        .append(" (type: ").append(row.get("CHIFFRAGE") != null ?
                                row.get("CHIFFRAGE").getClass().getSimpleName() : "null").append(")\n");
                sb.append("  DATE_LIVRAISON: ").append(row.get("DATE_DE_LIVRAISON"))
                        .append(" (type: ").append(row.get("DATE_DE_LIVRAISON") != null ?
                                row.get("DATE_DE_LIVRAISON").getClass().getSimpleName() : "null").append(")\n");
                sb.append("  DATE_GO: ").append(row.get("DATE_GO")).append("\n");
                sb.append("  DATE_PREVUE_LIVR: ").append(row.get("DATE_PREVUE_DE_LIVRAISON")).append("\n");
                sb.append("  DATE_GO_ETUDE: ").append(row.get("DATE_GO_ETUDE")).append("\n");
                sb.append("  DATE_PREVUE_ETUDE: ").append(row.get("DATE_PREVUE_ETUDE")).append("\n");
                sb.append("  DATE_PREMIERE_FSP: ").append(row.get("DATE_PREMIERE_FSP")).append("\n");
                sb.append("---\n");
            }
            return ResponseEntity.ok(sb.toString());
        } catch (Exception e) {
            return ResponseEntity.ok("ERREUR: " + e.getMessage());
        }
    }
}
