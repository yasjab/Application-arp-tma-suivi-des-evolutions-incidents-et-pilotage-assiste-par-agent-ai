package com.atos.arp.models;

import java.time.LocalDateTime;

/** DTO écran pour la page FC Livrées — combine les champs bruts de v_fc_livrees
 *  avec les indicateurs calculés (SI, mois financier, TAR/TR/N/K, charge). */
public class FcLivreeDto {

    private Long codeEvolution;
    private String jira;
    private String si;
    private String palier;
    private String palierEtude;
    private String libelle;
    private String lotContractuel;
    private String categorie;

    private String moisFinanciere;

    private LocalDateTime dateDeCreation;
    private LocalDateTime dateGo;
    private LocalDateTime datePrevueDeLivraison;
    private LocalDateTime dateDeLivraison;
    private LocalDateTime dateDeLivraisonQc;
    private LocalDateTime dateGoEtude;
    private LocalDateTime datePrevueEtude;
    private LocalDateTime datePremiereFsp;

    private String statut;
    private Double chiffrage;
    private Double delaiMT;
    private Double delaiEtudeMT;
    private Long nombreAnomalies;

    private Double tar;
    private Double tr;
    private Double n;
    private Double k;
    private Double charge;

    private String entite;
    private String directionUtilisatrice;
    private String chiffrageSim;

    public Long getCodeEvolution() { return codeEvolution; }
    public void setCodeEvolution(Long codeEvolution) { this.codeEvolution = codeEvolution; }
    public String getJira() { return jira; }
    public void setJira(String jira) { this.jira = jira; }
    public String getSi() { return si; }
    public void setSi(String si) { this.si = si; }
    public String getPalier() { return palier; }
    public void setPalier(String palier) { this.palier = palier; }
    public String getPalierEtude() { return palierEtude; }
    public void setPalierEtude(String palierEtude) { this.palierEtude = palierEtude; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getLotContractuel() { return lotContractuel; }
    public void setLotContractuel(String lotContractuel) { this.lotContractuel = lotContractuel; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getMoisFinanciere() { return moisFinanciere; }
    public void setMoisFinanciere(String moisFinanciere) { this.moisFinanciere = moisFinanciere; }
    public LocalDateTime getDateDeCreation() { return dateDeCreation; }
    public void setDateDeCreation(LocalDateTime dateDeCreation) { this.dateDeCreation = dateDeCreation; }
    public LocalDateTime getDateGo() { return dateGo; }
    public void setDateGo(LocalDateTime dateGo) { this.dateGo = dateGo; }
    public LocalDateTime getDatePrevueDeLivraison() { return datePrevueDeLivraison; }
    public void setDatePrevueDeLivraison(LocalDateTime d) { this.datePrevueDeLivraison = d; }
    public LocalDateTime getDateDeLivraison() { return dateDeLivraison; }
    public void setDateDeLivraison(LocalDateTime d) { this.dateDeLivraison = d; }
    public LocalDateTime getDateDeLivraisonQc() { return dateDeLivraisonQc; }
    public void setDateDeLivraisonQc(LocalDateTime d) { this.dateDeLivraisonQc = d; }
    public LocalDateTime getDateGoEtude() { return dateGoEtude; }
    public void setDateGoEtude(LocalDateTime d) { this.dateGoEtude = d; }
    public LocalDateTime getDatePrevueEtude() { return datePrevueEtude; }
    public void setDatePrevueEtude(LocalDateTime d) { this.datePrevueEtude = d; }
    public LocalDateTime getDatePremiereFsp() { return datePremiereFsp; }
    public void setDatePremiereFsp(LocalDateTime d) { this.datePremiereFsp = d; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Double getChiffrage() { return chiffrage; }
    public void setChiffrage(Double chiffrage) { this.chiffrage = chiffrage; }
    public Double getDelaiMT() { return delaiMT; }
    public void setDelaiMT(Double delaiMT) { this.delaiMT = delaiMT; }
    public Double getDelaiEtudeMT() { return delaiEtudeMT; }
    public void setDelaiEtudeMT(Double delaiEtudeMT) { this.delaiEtudeMT = delaiEtudeMT; }
    public Long getNombreAnomalies() { return nombreAnomalies; }
    public void setNombreAnomalies(Long nombreAnomalies) { this.nombreAnomalies = nombreAnomalies; }
    public Double getTar() { return tar; }
    public void setTar(Double tar) { this.tar = tar; }
    public Double getTr() { return tr; }
    public void setTr(Double tr) { this.tr = tr; }
    public Double getN() { return n; }
    public void setN(Double n) { this.n = n; }
    public Double getK() { return k; }
    public void setK(Double k) { this.k = k; }
    public Double getCharge() { return charge; }
    public void setCharge(Double charge) { this.charge = charge; }
    public String getEntite() { return entite; }
    public void setEntite(String entite) { this.entite = entite; }
    public String getDirectionUtilisatrice() { return directionUtilisatrice; }
    public void setDirectionUtilisatrice(String d) { this.directionUtilisatrice = d; }
    public String getChiffrageSim() { return chiffrageSim; }
    public void setChiffrageSim(String chiffrageSim) { this.chiffrageSim = chiffrageSim; }
}
