import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet, provideRouter, Router, NavigationEnd } from '@angular/router';
import { routes } from './app.routes';
import { HeaderComponent } from './header/header';
import { filter } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { PostHogService } from './services/posthog.service';
import { AuthService } from './auth';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
})
export class App implements OnInit {
  protected readonly title = signal('arp-frontend');
  private readonly router = inject(Router);
  private readonly posthogService = inject(PostHogService);
  private readonly authService = inject(AuthService);

  currentUrl = '';

  ngOnInit(): void {
    this.posthogService.init(
      environment.posthogKey,
      {
        api_host: environment.posthogHost,
        defaults: '2026-08-30',
        capture_exceptions: true,
        logs: {
          serviceName: 'arp-frontend',
          environment: environment.production ? 'production' : 'development',
          serviceVersion: '0.0.0',
        },
      },
      environment.production,
    );
    this.authService.restoreIdentity();

    // On écoute les changements de route pour mettre à jour currentUrl
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentUrl = event.urlAfterRedirects;
      });
  }

  isLoginPage(): boolean {
    return this.currentUrl === '/' || this.currentUrl === '';
  }
}

export const appProviders = [provideRouter(routes)];
