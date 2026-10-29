import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.scss'],
})
export class AdminDashboard {
  constructor(
    private router: Router,
    private authService: AuthService,
  ) {}

  goToExport(): void {
    this.router.navigate(['/export']);
  }
  goToEntites(): void {
    this.router.navigate(['/admin/entites']);
  }
  goToUsers(): void {
    this.router.navigate(['/admin/users']);
  }
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
  goToProfile(): void {
    this.router.navigate(['/profile']);
  }
  goToAgentIa(): void {
    this.router.navigate(['/admin/agent-ia']);
  }
  goToSuiviProduction(): void {
    this.router.navigate(['/suivi-production']);
  }
}
