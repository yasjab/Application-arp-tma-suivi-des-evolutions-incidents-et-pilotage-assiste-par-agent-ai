import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FcLivree {
  codeEvolution: number;
  jira: string;
  si: string;
  palier: string | null;
  palierEtude: string | null;
  libelle: string | null;
  lotContractuel: string | null;
  categorie: string | null;
  moisFinanciere: string | null;
  dateDeCreation: string | null;
  dateGo: string | null;
  datePrevueDeLivraison: string | null;
  dateDeLivraison: string | null;
  dateDeLivraisonQc: string | null;
  dateGoEtude: string | null;
  datePrevueEtude: string | null;
  datePremiereFsp: string | null;
  statut: string | null;
  chiffrage: number | null;
  delaiMT: number | null;
  delaiEtudeMT: number | null;
  nombreAnomalies: number | null;
  tar: number | null;
  tr: number | null;
  n: number | null;
  k: number | null;
  charge: number | null;
  entite: string | null;
  directionUtilisatrice: string | null;
  chiffrageSim: string | null;
}

export interface DiLivree {
  codeEvolution: number;
  jira: string;
  si: string;
  palier: string | null;
  palierEtude: string | null;
  libelle: string | null;
  lotContractuel: string | null;
  categorie: string | null;
  dateDeCreation: string | null;
  dateDeLivraison: string | null;
  dateDeLivraisonHistorique: string | null;
  dateDiClotureeHistorique: string | null;
  statut: string | null;
  chiffrage: number | null;
  entite: string | null;
  directionUtilisatrice: string | null;
  chiffrageSim: string | null;
}

@Injectable({ providedIn: 'root' })
export class TicketsService {
  private base = 'http://localhost:8080/api/tickets';

  constructor(private http: HttpClient) {}

  getFcLivrees(): Observable<FcLivree[]> {
    return this.http.get<FcLivree[]>(`${this.base}/fc-livrees`);
  }

  getDiLivrees(): Observable<DiLivree[]> {
    return this.http.get<DiLivree[]>(`${this.base}/di-livrees`);
  }
}
