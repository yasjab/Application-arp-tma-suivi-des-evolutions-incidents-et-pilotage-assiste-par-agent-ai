import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EntiteDto {
  id: number;
  nom: string;
}

@Injectable({ providedIn: 'root' })
export class ExportService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getEntites(): Observable<EntiteDto[]> {
    return this.http.get<EntiteDto[]>(`${this.base}/entites`);
  }

  exportPV(entites: string[]): Observable<Blob> {
    return this.http.post(
      `${this.base}/export/pv`,
      { entites },
      { responseType: 'blob' }
    );
  }
}
