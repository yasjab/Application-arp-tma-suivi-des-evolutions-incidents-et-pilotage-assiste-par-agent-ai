import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDto } from './user.service';

export interface InfosRequest {
  name: string;
  firstName: string;
  email: string;
  login: string;
}

export interface PasswordRequest {
  currentPassword: string;
  newPassword: string;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private url = 'http://localhost:8080/api/profile';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserDto> {
    return this.http.get<UserDto>(this.url);
  }

  updateInfos(req: InfosRequest): Observable<UserDto> {
    return this.http.put<UserDto>(`${this.url}/infos`, req);
  }

  updatePassword(req: PasswordRequest): Observable<void> {
    return this.http.put<void>(`${this.url}/password`, req);
  }
}
