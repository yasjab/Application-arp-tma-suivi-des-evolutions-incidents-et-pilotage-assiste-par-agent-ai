import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AgentChatResponse {
  message: string;
}

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

export interface TicketStatsDto {
  entite: string;
  enCours: number;
  diLivrees: number;
  fcLivrees: number;
  annulees: number;
  total: number;
  pourcentageLivrees: number;
}

@Injectable({ providedIn: 'root' })
export class AiAgentService {
  private readonly baseUrl = 'http://localhost:8080/api/agent';

  constructor(private http: HttpClient) {}

  getStats(): Observable<TicketStatsDto[]> {
    return this.http.get<TicketStatsDto[]>(`${this.baseUrl}/stats`);
  }

  getSummary(): Observable<AgentChatResponse> {
    return this.http.get<AgentChatResponse>(`${this.baseUrl}/summary`);
  }

  chat(message: string, history: ChatMessage[]): Observable<AgentChatResponse> {
    return this.http.post<AgentChatResponse>(`${this.baseUrl}/chat`, { message, history });
  }
}
