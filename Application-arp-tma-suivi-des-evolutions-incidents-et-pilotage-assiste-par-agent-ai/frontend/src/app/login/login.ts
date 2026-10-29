import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AuthService } from '../auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
})
export class LoginComponent {
  loginUser = '';
  password = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  login() {
    console.log('login clicked');
    this.authService.login(this.loginUser, this.password).subscribe({
      next: (res) => {
        const role = res.role;
        if (role === 'ADMIN')
          this.router.navigate(['/admin-dashboard']); //export
        else this.router.navigate(['/user-dashboard']);
      },
      error: () => {
        this.errorMessage = 'Identifiants invalides';
      },
    });
  }
}
