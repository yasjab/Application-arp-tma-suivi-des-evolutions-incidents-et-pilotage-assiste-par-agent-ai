import { Component, signal } from '@angular/core';
import { RouterOutlet, provideRouter, Router, NavigationEnd } from '@angular/router';
import { routes } from './app.routes';
import { HeaderComponent } from './header/header';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.scss'],
})
export class App {
  protected readonly title = signal('arp-frontend');

  currentUrl = '';

  constructor(private router: Router) {
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
