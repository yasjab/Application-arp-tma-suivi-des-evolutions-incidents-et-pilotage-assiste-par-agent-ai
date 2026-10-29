package com.atos.arp.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "v_fc_livrees")
public class FcLivrer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CODE_EVOLUTION")
    private Long codeEvolution;

    @Column(name = "JIRA")
    private String jira;

    @Column(name = "LOGIN")
    private String login;

    @Column(name = "PALIER")
    private String palier;

    @Column(name = "PALIER_ETUDE")
    private String palierEtude;

    @Column(name = "LIBELLER")
    private String libelle;

    @Column(name = "DATE_DE_CREATION")
    private LocalDateTime dateDeCreation;

    @Column(name = "DATE_GO")
    private LocalDateTime dateGo;

    @Column(name = "DATE_PREVUE_DE_LIVRAISON")
    private LocalDateTime datePrevueDeLivraison;

    @Column(name = "DATE_DE_LIVRAISON")
    private LocalDateTime dateDeLivraison;

    @Column(name = "DATE_DE_LIVRAISON_QC")
    private LocalDateTime dateDeLivraisonQc;

    @Column(name = "DATE_GO_ETUDE")
    private LocalDateTime dateGoEtude;

    @Column(name = "DATE_PREVUE_ETUDE")
    private LocalDateTime datePrevueEtude;

    @Column(name = "DATE_PREMIERE_FSP")
    private LocalDateTime datePremiereFsp;

    @Column(name = "STATUT")
    private String statut;

    @Column(name = "CHIFFRAGE")
    private Double chiffrage;

    @Column(name = "CHIFFRAGE_MT")
    private String chiffrageMT;

    @Column(name = "CHIFFRAGE_SIM")
    private String chiffrageSim;

    @Column(name = "CHIFFRAGE_SIC")
    private String chiffrageSic;

    @Column(name = "CHIFFRAGE_IX")
    private String chiffrageIx;

    @Column(name = "DELAI_MT")
    private Double delaiMT;

    @Column(name = "DELAI_ETUDE_MT")
    private Double delaiEtudeMT;

    @Column(name = "NOMBRE_ANOMALIE")
    private Long nombreAnomalie;

    @Column(name = "LOT_CONTRACTUEL")
    private String lotContractuel;

    @Column(name = "PARAMETRE")
    private String parametre;

    @Column(name = "CATEGORIE")
    private String categorie;

    @Column(name = "ENTITE")
    private String entite;

    @Column(name = "DIRECTION_UTILISATRISE")
    private String directionUtilisatrise;

    @Column(name = "PROBLEME_CHIFFRAGE")
    private String problemeChiffrage;

    @Column(name = "CP_SI")
    private String cpSi;

    @Column(name = "CHAMPS_MODIFIER")
    private String champsModifier;

    @Column(name = "CONTRE_PROPOSITION_SIC")
    private String contrePropositionSic;

    @Column(name = "CONTRE_PROPOSITION_SIM")
    private String contrePropositionSim;

    @Column(name = "PROBLEME_CHIFFRAGE_SIC")
    private String problemeChiffrageSic;

    @Column(name = "PROBLEME_CHIFFRAGE_SIM")
    private String problemeChiffrageSim;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCodeEvolution() { return codeEvolution; }
    public void setCodeEvolution(Long codeEvolution) { this.codeEvolution = codeEvolution; }
    public String getJira() { return jira; }
    public void setJira(String jira) { this.jira = jira; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPalier() { return palier; }
    public void setPalier(String palier) { this.palier = palier; }
    public String getPalierEtude() { return palierEtude; }
    public void setPalierEtude(String palierEtude) { this.palierEtude = palierEtude; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
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
    public String getChiffrageMT() { return chiffrageMT; }
    public void setChiffrageMT(String chiffrageMT) { this.chiffrageMT = chiffrageMT; }
    public String getChiffrageSim() { return chiffrageSim; }
    public void setChiffrageSim(String chiffrageSim) { this.chiffrageSim = chiffrageSim; }
    public String getChiffrageSic() { return chiffrageSic; }
    public void setChiffrageSic(String chiffrageSic) { this.chiffrageSic = chiffrageSic; }
    public String getChiffrageIx() { return chiffrageIx; }
    public void setChiffrageIx(String chiffrageIx) { this.chiffrageIx = chiffrageIx; }
    public Double getDelaiMT() { return delaiMT; }
    public void setDelaiMT(Double delaiMT) { this.delaiMT = delaiMT; }
    public Double getDelaiEtudeMT() { return delaiEtudeMT; }
    public void setDelaiEtudeMT(Double delaiEtudeMT) { this.delaiEtudeMT = delaiEtudeMT; }
    public Long getNombreAnomalie() { return nombreAnomalie; }
    public void setNombreAnomalie(Long nombreAnomalie) { this.nombreAnomalie = nombreAnomalie; }
    public String getLotContractuel() { return lotContractuel; }
    public void setLotContractuel(String lotContractuel) { this.lotContractuel = lotContractuel; }
    public String getParametre() { return parametre; }
    public void setParametre(String parametre) { this.parametre = parametre; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getEntite() { return entite; }
    public void setEntite(String entite) { this.entite = entite; }
    public String getDirectionUtilisatrise() { return directionUtilisatrise; }
    public void setDirectionUtilisatrise(String d) { this.directionUtilisatrise = d; }
    public String getProblemeChiffrage() { return problemeChiffrage; }
    public void setProblemeChiffrage(String p) { this.problemeChiffrage = p; }
    public String getCpSi() { return cpSi; }
    public void setCpSi(String cpSi) { this.cpSi = cpSi; }
    public String getChampsModifier() { return champsModifier; }
    public void setChampsModifier(String c) { this.champsModifier = c; }
    public String getContrePropositionSic() { return contrePropositionSic; }
    public void setContrePropositionSic(String c) { this.contrePropositionSic = c; }
    public String getContrePropositionSim() { return contrePropositionSim; }
    public void setContrePropositionSim(String c) { this.contrePropositionSim = c; }
    public String getProblemeChiffrageSic() { return problemeChiffrageSic; }
    public void setProblemeChiffrageSic(String p) { this.problemeChiffrageSic = p; }
    public String getProblemeChiffrageSim() { return problemeChiffrageSim; }
    public void setProblemeChiffrageSim(String p) { this.problemeChiffrageSim = p; }
}