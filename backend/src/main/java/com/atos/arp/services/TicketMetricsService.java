package com.atos.arp.services;

import com.atos.arp.models.Coefficient;
import com.atos.arp.models.HoraireJourneeTravail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Moteur de calcul des indicateurs par ticket (TAR/TR/N/K, mois financier, chiffrage total).
 * Extrait d'ExportService pour être réutilisable à la fois par l'export Excel
 * et par les pages d'affichage écran (FC Livrées, ...).
 */
@Service
public class TicketMetricsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Map<String, Double> TAUX_PAR_STATUT = new LinkedHashMap<>();
    static {
        TAUX_PAR_STATUT.put("en production",                 2.0);
        TAUX_PAR_STATUT.put("livrée",                        1.76046933);
        TAUX_PAR_STATUT.put("livrée/annulée",                1.760469333);
        TAUX_PAR_STATUT.put("go pour mep",                   1.88380267);
        TAUX_PAR_STATUT.put("en recette",                    1.76046933);
        TAUX_PAR_STATUT.put("ef à valider par moa",          0.27333333);
        TAUX_PAR_STATUT.put("dispo pour pré recette",        1.408576);
        TAUX_PAR_STATUT.put("en tig",                        1.374288);
        TAUX_PAR_STATUT.put("fsp à valider par dsi",         0.27333333);
        TAUX_PAR_STATUT.put("en dev",                        0.34095467);
        TAUX_PAR_STATUT.put("fsp annulée",                   0.27333333);
        TAUX_PAR_STATUT.put("dispo pour mep",                1.88380267);
        TAUX_PAR_STATUT.put("attente chiffrage fournisseur", 0.27333333);
        TAUX_PAR_STATUT.put("ef validée par moa",            0.27333333);
        TAUX_PAR_STATUT.put("a valider par dsc",             0.27333333);
        TAUX_PAR_STATUT.put("attente presicions métier",     0.27333333);
        TAUX_PAR_STATUT.put("fsp validée",                   0.27333333);
        TAUX_PAR_STATUT.put("retour opportunité reçu",       0.27333333);
        TAUX_PAR_STATUT.put("attente précisions moa",        0.27333333);
        TAUX_PAR_STATUT.put("validée au comité chiffrage",   0.27333333);
        TAUX_PAR_STATUT.put("pré recette ok",                1.408576);
        TAUX_PAR_STATUT.put("eb moa validée",                0.27333333);
        TAUX_PAR_STATUT.put("attente precisions dsi",        0.27333333);
        TAUX_PAR_STATUT.put("en recette-dispo pour mep",     1.88380267);
        TAUX_PAR_STATUT.put("en tig-livrée/annulée",         1.76046933);
        TAUX_PAR_STATUT.put("annulation avec consommation",  1.0);
    }

    public double tauxPourStatut(String statut) {
        if (statut == null) return 0.0;
        return TAUX_PAR_STATUT.getOrDefault(statut.toLowerCase().trim(), 0.0);
    }

    /** SI dérivé du préfixe JIRA : BM- => MOBILE, sinon FIXE. */
    public String deriveSi(String jira) {
        return (jira != null && jira.startsWith("BM-")) ? "MOBILE" : "FIXE";
    }

    /**
     * Règle de validité d'un ticket, identique à celle utilisée pour l'export Excel
     * (ExportService#buildSheet) : on écarte les doublons de code évolution, les JIRA
     * vides/"null" et les chiffrages non renseignés ou nuls/négatifs.
     * {@code codesEvolutionTraites} est mis à jour (ticket marqué comme traité) quand le ticket est valide.
     */
    public boolean isTicketValide(String codeEvolution, String jira, double chiffrage,
                                   Set<String> codesEvolutionTraites) {
        String codeEvol = (codeEvolution != null) ? codeEvolution.trim() : "";
        String jiraTrim = (jira != null) ? jira.trim() : "";

        if (!codeEvol.isEmpty() && codesEvolutionTraites.contains(codeEvol)) return false;
        if (jiraTrim.isEmpty() || "null".equalsIgnoreCase(jiraTrim)) return false;
        if (chiffrage == -999999 || chiffrage <= 0) return false;

        if (!codeEvol.isEmpty()) codesEvolutionTraites.add(codeEvol);
        return true;
    }

    public static class FcMetrics {
        public String  moisFinancier      = "";
        public int     iMoisFinancier     = 0;
        public boolean isCalculatedValide = false;
        public double  tar            = 0.0;
        public double  tr             = 0.0;
        public double  n              = 0.0;
        public double  k              = 1.0;
        public double  chiffrageTotal = 0.0;
    }

    public Map<String, FcMetrics> calculeFcMetrics(List<Map<String, Object>> rows) {
        List<Coefficient> coeffs = jdbcTemplate.query(
                "SELECT * FROM coefficient_k ORDER BY ID",
                (rs, rowNum) -> new Coefficient(
                        rs.getInt("ID"),
                        rs.getDouble("COEF_K"),
                        rs.getDouble("VALEUR_MIN"),
                        rs.getString("OPERATION_MIN"),
                        rs.getDouble("VALEUR_MAX"),
                        rs.getString("OPERATION_MAX")
                )
        );
        HoraireJourneeTravail horaire = loadHoraire();

        List<Date> joursFeries = jdbcTemplate.queryForList(
                "SELECT JOUR FROM jours_feries", Date.class
        );

        Set<String> statutsFcLivrees = new HashSet<>();
        Set<String> statutsDiLivrees = new HashSet<>();

        List<Map<String, Object>> cats = jdbcTemplate.queryForList("SELECT * FROM categorie");
        for (Map<String, Object> cat : cats) {
            String catName    = cat.get("CATEGORIE_NAME") != null ? cat.get("CATEGORIE_NAME").toString() : "";
            String statutName = cat.get("STATUT_NAME")    != null ? cat.get("STATUT_NAME").toString()    : "";
            if ("FC Livrées (FIXE)".equals(catName) || "FC Livrées (MOBILE)".equals(catName)) {
                statutsFcLivrees.add(statutName.toLowerCase());
            }
            if ("DI Livrées (FIXE)".equals(catName) || "DI Livrées (MOBILE)".equals(catName)) {
                statutsDiLivrees.add(statutName.toLowerCase());
            }
        }

        GlobalMethodeManager timeManager = new GlobalMethodeManager();

        Map<String, FcMetrics> result      = new LinkedHashMap<>();
        Map<String, Integer>   sommeAnom   = new HashMap<>();
        Map<String, Double>    sommeCharge = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Object jiraVal   = row.get("JIRA");
            String jira      = (jiraVal != null) ? jiraVal.toString().trim() : "";
            double chiffrage = getDouble(row.get("CHIFFRAGE"));
            String categorie = row.get("CATEGORIE") != null ? row.get("CATEGORIE").toString().trim() : "";
            String statut    = row.get("STATUT")    != null ? row.get("STATUT").toString().trim()    : "";

            if (jira.isEmpty() || "null".equalsIgnoreCase(jira)) continue;
            if (chiffrage == -999999 || chiffrage <= 0) continue;
            if (categorie.isEmpty() || "null".equalsIgnoreCase(categorie)) continue;
            if (timeManager.isDI(categorie)) continue;
            if (!timeManager.isNotFC(categorie)) {
                if (!statutsFcLivrees.contains(statut.toLowerCase()) && !"FSP Annulée".equals(statut)) {
                    continue;
                }
            }

            Date dLiv  = toDate(row.get("DATE_DE_LIVRAISON"));
            Date dGo   = toDate(row.get("DATE_GO"));
            Date dPLiv = toDate(row.get("DATE_PREVUE_DE_LIVRAISON"));
            Date dGoE  = toDate(row.get("DATE_GO_ETUDE"));
            Date dPEt  = toDate(row.get("DATE_PREVUE_ETUDE"));
            Date dFsp  = toDate(row.get("DATE_PREMIERE_FSP"));

            FcMetrics m = new FcMetrics();

            if (dLiv != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dLiv);
                int mois  = cal.get(Calendar.MONTH) + 1;
                int annee = cal.get(Calendar.YEAR);
                m.moisFinancier  = String.format("%02d/%d", mois, annee);
                m.iMoisFinancier = mois + (annee * 12);
            }

            boolean isValide = chiffrage > 0
                    && dLiv != null && dGo   != null && dPLiv != null
                    && dGoE != null && dPEt  != null && dFsp  != null;

            m.isCalculatedValide = isValide;

            if (isValide && chiffrage < 10 && !m.moisFinancier.isEmpty()) {
                int anom = getInt(row.get("NOMBRE_ANOMALIE"));
                sommeAnom.merge(m.moisFinancier,   anom,     Integer::sum);
                sommeCharge.merge(m.moisFinancier, chiffrage, Double::sum);
            }

            result.put(jira, m);
        }

        Date dateSeuil = java.sql.Date.valueOf("2013-07-01");

        for (Map<String, Object> row : rows) {
            Object jiraVal = row.get("JIRA");
            String jira    = (jiraVal != null) ? jiraVal.toString().trim() : "";
            FcMetrics m    = result.get(jira);
            if (m == null || !m.isCalculatedValide) continue;

            double chiffrage = getDouble(row.get("CHIFFRAGE"));
            int    anom      = getInt(row.get("NOMBRE_ANOMALIE"));
            double delMt     = getDouble(row.get("DELAI_MT"));
            double delEtMt   = getDouble(row.get("DELAI_ETUDE_MT"));

            Date dLiv  = toDate(row.get("DATE_DE_LIVRAISON"));
            Date dPLiv = toDate(row.get("DATE_PREVUE_DE_LIVRAISON"));
            Date dGo   = toDate(row.get("DATE_GO"));
            Date dFsp  = toDate(row.get("DATE_PREMIERE_FSP"));
            Date dPEt  = toDate(row.get("DATE_PREVUE_ETUDE"));
            Date dGoE  = toDate(row.get("DATE_GO_ETUDE"));

            if (chiffrage >= 10) {
                m.tar = (chiffrage > 0) ? (double) anom / chiffrage : 0;
            } else {
                Double sc  = sommeCharge.get(m.moisFinancier);
                Integer sa = sommeAnom.get(m.moisFinancier);
                m.tar = (sc != null && sc > 0 && sa != null) ? (double) sa / sc : 0;
            }

            double retL = countDays(dPLiv, dLiv, joursFeries, horaire);
            double retE = (dFsp != null && dPEt != null)
                    ? countDays(dPEt, dFsp, joursFeries, horaire) : 0;
            double delL = countDays(dGo, dPLiv, joursFeries, horaire);
            double delE = (dPEt != null && dGoE != null)
                    ? countDays(dGoE, dPEt, joursFeries, horaire) : 0;
            if (delE < 0) delE = 0;

            if (dGoE.before(dateSeuil)) {
                m.tr = (delL != 0) ? (retL - delMt) / delL : 0;
            } else {
                double denom = delL + delE;
                m.tr = (denom != 0) ? (retL + retE - (delMt + delEtMt)) / denom : 0;
            }

            if (m.tr < 0) m.tr = 0;

            m.n              = (m.tar + m.tr) / 2;
            m.k              = getCoefK(m.n, coeffs);
            m.chiffrageTotal = round2(chiffrage * m.k);
        }

        return result;
    }

    private double getCoefK(double n, List<Coefficient> coeffs) {
        for (Coefficient c : coeffs) {
            boolean minOk;
            if ("non défini".equalsIgnoreCase(c.getOperationMin())) minOk = true;
            else if ("<".equals(c.getOperationMin())) minOk = n > c.getValeurMin();
            else if ("<=".equals(c.getOperationMin())) minOk = n >= c.getValeurMin();
            else minOk = false;

            boolean maxOk;
            if ("non défini".equalsIgnoreCase(c.getOperationMax())) maxOk = true;
            else if ("<".equals(c.getOperationMax())) maxOk = n < c.getValeurMax();
            else if ("<=".equals(c.getOperationMax())) maxOk = n <= c.getValeurMax();
            else maxOk = false;

            if (minOk && maxOk) return c.getCoef();
        }
        return 1.568;
    }

    private HoraireJourneeTravail loadHoraire() {
        return jdbcTemplate.queryForObject(
                // Prendre la ligne du lundi (ID=2), valide pour tous les jours ouvrés
                "SELECT * FROM horaire_travail WHERE ID = 2",
                (rs, rowNum) -> new HoraireJourneeTravail(
                        rs.getString("HEURE_DEBUT_MATIN"),
                        rs.getString("MINUTE_DEBUT_MATIN"),
                        rs.getString("SECONDE_DEBUT_MATIN"),
                        rs.getString("HEURE_FIN_MATIN"),
                        rs.getString("MINUTE_FIN_MATIN"),
                        rs.getString("SECONDE_FIN_MATIN"),
                        rs.getString("HEURE_DEBUT_SOIR"),
                        rs.getString("MINUTE_DEBUT_SOIR"),
                        rs.getString("SECONDE_DEBUT_SOIR"),
                        rs.getString("HEURE_FIN_SOIR"),
                        rs.getString("MINUTE_FIN_SOIR"),
                        rs.getString("SECONDE_FIN_SOIR")
                )
        );
    }

    private Date toDate(Object val) {
        if (val == null) return null;

        Date raw = null;

        if (val instanceof Date d)          raw = d;
        else if (val instanceof Timestamp ts) raw = new Date(ts.getTime());
        else if (val instanceof LocalDateTime ldt) raw = Timestamp.valueOf(ldt);
        else if (val instanceof String s && s.length() >= 10) {
            try {
                String clean = s.substring(0, 10).replace("/", "-");
                String[] p = clean.split("-");
                Calendar cal = Calendar.getInstance();
                if (p[0].length() == 4)
                    cal.set(Integer.parseInt(p[0]), Integer.parseInt(p[1])-1, Integer.parseInt(p[2]));
                else
                    cal.set(Integer.parseInt(p[2]), Integer.parseInt(p[1])-1, Integer.parseInt(p[0]));
                cal.set(Calendar.HOUR_OF_DAY, 18);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            } catch (Exception e) { return null; }
        }

        if (raw == null) return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(raw);

        int hour   = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        boolean isAtMidnight = (hour == 0 && minute == 0 && second == 0);

        if (isAtMidnight) {
            cal.set(Calendar.HOUR_OF_DAY, 18);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        return cal.getTime();
    }

    private long diffInDay(Date deb, Date fin,
                           HoraireJourneeTravail h,
                           SimpleDateFormat sdfDate,
                           SimpleDateFormat sdfDateTime) throws ParseException {

        String day = sdfDate.format(deb);
        Date p1Deb = sdfDateTime.parse(day + " " + h.get(1) + ":" + h.get(2));
        Date p1Fin = sdfDateTime.parse(day + " " + h.get(3) + ":" + h.get(4));
        Date p2Deb = sdfDateTime.parse(day + " " + h.get(5) + ":" + h.get(6));
        Date p2Fin = sdfDateTime.parse(day + " " + h.get(7) + ":" + h.get(8));

        long count = 0;

        Date s1 = deb.before(p1Deb) ? p1Deb : deb;
        Date e1 = fin.before(p1Fin) ? fin : p1Fin;
        if (s1.before(e1)) count += e1.getTime() - s1.getTime();

        Date s2 = deb.before(p2Deb) ? p2Deb : deb;
        Date e2 = fin.before(p2Fin) ? fin : p2Fin;
        if (s2.before(e2)) count += e2.getTime() - s2.getTime();

        return count;
    }

    private long differenceBetweenDateInMillis(Date dateDebut, Date dateFin,
                                               List<Date> joursFeries,
                                               HoraireJourneeTravail horaire) {
        if (dateDebut == null || dateFin == null) return 0;

        int l = 0;
        if (dateFin.before(dateDebut)) {
            Date tmp = dateFin; dateFin = dateDebut; dateDebut = tmp;
            l = 1;
        }

        SimpleDateFormat sdfDate     = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Set<String> feriesSet = new HashSet<>();
        for (Date j : joursFeries) {
            if (j != null) feriesSet.add(sdfDate.format(j));
        }

        List<Date> dates = new ArrayList<>();
        long count = 0;

        try {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(sdfDate.parse(sdfDate.format(dateDebut)));

            Date dateFinNorm = sdfDate.parse(sdfDate.format(dateFin));

            while (calendar.getTime().before(dateFinNorm)) {
                String d = sdfDate.format(calendar.getTime());
                int dow = calendar.get(Calendar.DAY_OF_WEEK);

                if (dow != Calendar.SUNDAY
                        && dow != Calendar.SATURDAY
                        && !feriesSet.contains(d)) {
                    dates.add(sdfDate.parse(d));
                }
                calendar.add(Calendar.DATE, 1);
            }

            String lastDay = sdfDate.format(calendar.getTime());
            int dow = calendar.get(Calendar.DAY_OF_WEEK);

            if (dow != Calendar.SUNDAY
                    && dow != Calendar.SATURDAY
                    && !feriesSet.contains(lastDay)) {
                dates.add(sdfDate.parse(lastDay));
            }

            if (dates.size() == 0) {
                return l == 1 ? -count : count;
            } else if (dates.size() == 1) {
                count += diffInDay(dateDebut, dateFin, horaire, sdfDate, sdfDateTime);
            } else if (dates.size() == 2) {
                count += diffInDay(dateDebut,
                        sdfDateTime.parse(sdfDate.format(dateDebut) + " 23:59:59"),
                        horaire, sdfDate, sdfDateTime);
                count += diffInDay(
                        sdfDateTime.parse(sdfDate.format(dateFin) + " 00:00:00"),
                        dateFin, horaire, sdfDate, sdfDateTime);
            } else {
                count += diffInDay(dateDebut,
                        sdfDateTime.parse(sdfDate.format(dateDebut) + " 23:59:59"),
                        horaire, sdfDate, sdfDateTime);
                for (int i = 1; i < dates.size() - 1; i++) {
                    String d = sdfDate.format(dates.get(i));
                    count += diffInDay(
                            sdfDateTime.parse(d + " 00:00:00"),
                            sdfDateTime.parse(d + " 23:59:59"),
                            horaire, sdfDate, sdfDateTime);
                }
                count += diffInDay(
                        sdfDateTime.parse(sdfDate.format(dateFin) + " 00:00:00"),
                        dateFin, horaire, sdfDate, sdfDateTime);
            }

        } catch (ParseException e) {
            // log error
        }

        return l == 1 ? -count : count;
    }

    private double countDays(Date d1, Date d2,
                             List<Date> joursFeries,
                             HoraireJourneeTravail horaire) {
        if (d1 == null || d2 == null) return 0;
        long ms = differenceBetweenDateInMillis(d1, d2, joursFeries, horaire);
        return ms / (double)(10 * 60 * 60 * 1000);
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
}
