package com.atos.arp.models;

import java.time.LocalDateTime;

/** DTO écran pour la page DI Livrées — champs bruts de v_di_livrees + SI dérivé. */
public class DiLivreeDto {

    private Long codeEvolution;
    private String jira;
    private String si;
    private String palier;
    private String palierEtude;
    private String libelle;
    private String lotContractuel;
    private String categorie;

    private LocalDateTime dateDeCreation;
    private LocalDateTime dateDeLivraison;
    private LocalDateTime dateDeLivraisonHistorique;
    private LocalDateTime dateDiClotureeHistorique;

    private String statut;
    private Double chiffrage;

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
    public LocalDateTime getDateDeCreation() { return dateDeCreation; }
    public void setDateDeCreation(LocalDateTime dateDeCreation) { this.dateDeCreation = dateDeCreation; }
    public LocalDateTime getDateDeLivraison() { return dateDeLivraison; }
    public void setDateDeLivraison(LocalDateTime d) { this.dateDeLivraison = d; }
    public LocalDateTime getDateDeLivraisonHistorique() { return dateDeLivraisonHistorique; }
    public void setDateDeLivraisonHistorique(LocalDateTime d) { this.dateDeLivraisonHistorique = d; }
    public LocalDateTime getDateDiClotureeHistorique() { return dateDiClotureeHistorique; }
    public void setDateDiClotureeHistorique(LocalDateTime d) { this.dateDiClotureeHistorique = d; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Double getChiffrage() { return chiffrage; }
    public void setChiffrage(Double chiffrage) { this.chiffrage = chiffrage; }
    public String getEntite() { return entite; }
    public void setEntite(String entite) { this.entite = entite; }
    public String getDirectionUtilisatrice() { return directionUtilisatrice; }
    public void setDirectionUtilisatrice(String d) { this.directionUtilisatrice = d; }
    public String getChiffrageSim() { return chiffrageSim; }
    public void setChiffrageSim(String chiffrageSim) { this.chiffrageSim = chiffrageSim; }
}
