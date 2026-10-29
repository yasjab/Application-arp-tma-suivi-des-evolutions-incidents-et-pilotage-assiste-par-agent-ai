import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-dashboard.html',
  styleUrls: ['./user-dashboard.scss'],
})
export class UserDashboard {
  constructor(
    private router: Router,
    private authService: AuthService,
  ) {}

  goToExport(): void   { this.router.navigate(['/export']); }
  goToProfile(): void  { this.router.navigate(['/profile']); }
  goToSuiviProduction(): void { this.router.navigate(['/suivi-production']); }
  logout(): void       { this.authService.logout(); this.router.navigate(['/']); }
}
