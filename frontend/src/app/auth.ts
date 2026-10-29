import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface LoginResponse {
  login: string;
  role: string;
  token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(private http: HttpClient) {}

  login(loginUser: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>('http://localhost:8080/api/login', {
        login: loginUser,
        password,
      })
      .pipe(
        tap((res) => {
          localStorage.setItem('currentUser', JSON.stringify(res));
        }),
      );
  }

  logout(): void {
    localStorage.removeItem('currentUser');
  }

  getCurrentUser(): LoginResponse | null {
    const stored = localStorage.getItem('currentUser');
    return stored ? JSON.parse(stored) : null;
  }

  getToken(): string | null {
    const user = this.getCurrentUser();
    return user ? user.token : null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    return this.getCurrentUser()?.role === 'ADMIN';
  }
}
