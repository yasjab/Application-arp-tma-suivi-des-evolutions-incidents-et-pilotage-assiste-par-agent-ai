package com.atos.arp.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "v_encours")
public class Encours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CODE_EVOLUTION") private Long codeEvolution;
    @Column(name = "JIRA")           private String jira;
    @Column(name = "LOGIN")          private String login;
    @Column(name = "PALIER")         private String palier;
    @Column(name = "PALIER_ETUDE")   private String palierEtude;
    @Column(name = "LIBELLER")       private String libelle;

    @Column(name = "DATE_DE_CREATION")  private LocalDateTime dateDeCreation;
    @Column(name = "DATE_LAST_MODIF")   private LocalDateTime dateLastModif;

    @Column(name = "STATUT")                  private String statut;
    @Column(name = "CHIFFRAGE")               private Double chiffrage;
    @Column(name = "CHIFFRAGE_MT")            private String chiffrageMT;
    @Column(name = "LOT_CONTRACTUEL")         private String lotContractuel;
    @Column(name = "AVANCEMENT")              private Integer avancement;
    @Column(name = "CATEGORIE")               private String categorie;
    @Column(name = "ENTITE")                  private String entite;
    @Column(name = "DIRECTION_UTILISATRISE")  private String directionUtilisatrise;
    @Column(name = "PROBLEME_CHIFFRAGE")      private String problemeChiffrage;
    @Column(name = "CP_SI")                   private String cpSi;
    @Column(name = "CHIFFRAGE_SIC")           private String chiffrageSic;
    @Column(name = "CONTRE_PROPOSITION_SIC")  private String contrePropositionSic;
    @Column(name = "CONTRE_PROPOSITION_SIM")  private String contrePropositionSim;
    @Column(name = "PROBLEME_CHIFFRAGE_SIC")  private String problemeChiffrageSic;
    @Column(name = "PROBLEME_CHIFFRAGE_SIM")  private String problemeChiffrageSim;

    // Getters & Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getCodeEvolution() { return codeEvolution; } public void setCodeEvolution(Long v) { this.codeEvolution = v; }
    public String getJira() { return jira; } public void setJira(String v) { this.jira = v; }
    public String getLogin() { return login; } public void setLogin(String v) { this.login = v; }
    public String getPalier() { return palier; } public void setPalier(String v) { this.palier = v; }
    public String getPalierEtude() { return palierEtude; } public void setPalierEtude(String v) { this.palierEtude = v; }
    public String getLibelle() { return libelle; } public void setLibelle(String v) { this.libelle = v; }
    public LocalDateTime getDateDeCreation() { return dateDeCreation; } public void setDateDeCreation(LocalDateTime v) { this.dateDeCreation = v; }
    public LocalDateTime getDateLastModif() { return dateLastModif; } public void setDateLastModif(LocalDateTime v) { this.dateLastModif = v; }
    public String getStatut() { return statut; } public void setStatut(String v) { this.statut = v; }
    public Double getChiffrage() { return chiffrage; } public void setChiffrage(Double v) { this.chiffrage = v; }
    public String getChiffrageMT() { return chiffrageMT; } public void setChiffrageMT(String v) { this.chiffrageMT = v; }
    public String getLotContractuel() { return lotContractuel; } public void setLotContractuel(String v) { this.lotContractuel = v; }
    public Integer getAvancement() { return avancement; } public void setAvancement(Integer v) { this.avancement = v; }
    public String getCategorie() { return categorie; } public void setCategorie(String v) { this.categorie = v; }
    public String getEntite() { return entite; } public void setEntite(String v) { this.entite = v; }
    public String getDirectionUtilisatrise() { return directionUtilisatrise; } public void setDirectionUtilisatrise(String v) { this.directionUtilisatrise = v; }
    public String getProblemeChiffrage() { return problemeChiffrage; } public void setProblemeChiffrage(String v) { this.problemeChiffrage = v; }
    public String getCpSi() { return cpSi; } public void setCpSi(String v) { this.cpSi = v; }
    public String getChiffrageSic() { return chiffrageSic; } public void setChiffrageSic(String v) { this.chiffrageSic = v; }
    public String getContrePropositionSic() { return contrePropositionSic; } public void setContrePropositionSic(String v) { this.contrePropositionSic = v; }
    public String getContrePropositionSim() { return contrePropositionSim; } public void setContrePropositionSim(String v) { this.contrePropositionSim = v; }
    public String getProblemeChiffrageSic() { return problemeChiffrageSic; } public void setProblemeChiffrageSic(String v) { this.problemeChiffrageSic = v; }
    public String getProblemeChiffrageSim() { return problemeChiffrageSim; } public void setProblemeChiffrageSim(String v) { this.problemeChiffrageSim = v; }
}