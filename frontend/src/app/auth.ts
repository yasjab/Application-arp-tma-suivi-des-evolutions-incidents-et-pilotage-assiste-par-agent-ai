import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of, switchMap, tap } from 'rxjs';
import { PostHogService } from './services/posthog.service';
import { ProfileService } from './services/profile.service';
import { UserDto } from './services/user.service';

export interface LoginResponse {
  login: string;
  role: string;
  token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly posthogService = inject(PostHogService);
  private readonly profileService = inject(ProfileService);

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
        switchMap((res) =>
          this.profileService.getProfile().pipe(
            tap((user) => {
              this.identifyUser(user);
              this.posthogService.posthog.capture('user_logged_in', {
                role: user.role,
              });
              this.posthogService.posthog.logger.info('authenticated_session_started', {
                role: user.role,
              });
            }),
            map(() => res),
            catchError(() => of(res)),
          ),
        ),
      );
  }

  restoreIdentity(): void {
    if (!this.getCurrentUser()) {
      return;
    }

    this.profileService.getProfile().subscribe({
      next: (user) => this.identifyUser(user),
      error: () => undefined,
    });
  }

  logout(): void {
    this.posthogService.posthog.capture('user_logged_out');
    this.posthogService.posthog.reset();
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

  private identifyUser(user: UserDto): void {
    this.posthogService.posthog.identify(String(user.id), {
      email: user.email,
      name: `${user.firstName} ${user.name}`.trim(),
      login: user.login,
      role: user.role,
    });
  }
}
