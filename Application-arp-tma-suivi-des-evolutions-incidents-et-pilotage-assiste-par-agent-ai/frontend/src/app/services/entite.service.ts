// src/app/services/entite.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EntiteDto } from './export.service';

@Injectable({ providedIn: 'root' })
export class EntiteService {
  private url = 'http://localhost:8080/api/entites';

  constructor(private http: HttpClient) {}

  getAll(): Observable<EntiteDto[]> {
    return this.http.get<EntiteDto[]>(this.url);
  }
  create(e: { nom: string }): Observable<EntiteDto> {
    return this.http.post<EntiteDto>(this.url, e);
  }
  update(id: number, e: { nom: string }): Observable<EntiteDto> {
    return this.http.put<EntiteDto>(`${this.url}/${id}`, e);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
