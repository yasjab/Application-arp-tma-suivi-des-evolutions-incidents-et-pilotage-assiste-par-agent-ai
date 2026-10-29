import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfileService } from '../services/profile.service';
import { UserDto } from '../services/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss'],
})
export class ProfileComponent implements OnInit {
  user!: UserDto;

  // Infos
  formName = '';
  formFirstName = '';
  formEmail = '';
  formLogin = '';
  infosSuccess = '';
  infosError = '';
  savingInfos = false;
  showReconnectBtn = false;

  // Mot de passe
  formCurrentPassword = '';
  formNewPassword = '';
  formConfirmPassword = '';
  passwordSuccess = '';
  passwordError = '';
  savingPassword = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private profileService: ProfileService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.user          = this.route.snapshot.data['profile'];
    this.formName      = this.user.name;
    this.formFirstName = this.user.firstName;
    this.formEmail     = this.user.email;
    this.formLogin     = this.user.login;
  }

  saveInfos(): void {
    this.infosError      = '';
    this.infosSuccess    = '';
    this.showReconnectBtn = false;

    if (!this.formName.trim() || !this.formFirstName.trim() ||
      !this.formEmail.trim() || !this.formLogin.trim()) {
      this.infosError = 'Tous les champs sont obligatoires.';
      return;
    }

    this.savingInfos = true;

    this.profileService.updateInfos({
      name:      this.formName.trim(),
      firstName: this.formFirstName.trim(),
      email:     this.formEmail.trim(),
      login:     this.formLogin.trim(),
    }).subscribe({
      next: (updated) => {
        const loginChanged = updated.login !== this.user.login;
        this.user = { ...updated };

        const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
        currentUser.login = updated.login;
        localStorage.setItem('currentUser', JSON.stringify(currentUser));

        this.savingInfos = false;

        if (loginChanged) {
          this.infosSuccess     = 'Login modifié avec succès. Veuillez vous reconnecter pour appliquer les changements.';
          this.showReconnectBtn = true;
        } else {
          this.infosSuccess = 'Informations mises à jour avec succès.';
        }

        this.cdr.detectChanges();
      },
      error: (err) => {
        this.infosError  = err.status === 409
          ? 'Ce login est déjà utilisé par un autre compte.'
          : 'Erreur lors de la mise à jour.';
        this.savingInfos = false;
      },
    });
  }

  savePassword(): void {
    this.passwordError   = '';
    this.passwordSuccess = '';

    if (!this.formCurrentPassword.trim()) {
      this.passwordError = 'Le mot de passe actuel est obligatoire.';
      return;
    }

    if (this.formNewPassword.length < 6) {
      this.passwordError = 'Le nouveau mot de passe doit contenir au moins 6 caractères.';
      return;
    }

    if (this.formNewPassword !== this.formConfirmPassword) {
      this.passwordError = 'Les mots de passe ne correspondent pas.';
      return;
    }

    this.savingPassword = true;

    this.profileService.updatePassword({
      currentPassword: this.formCurrentPassword,
      newPassword:     this.formNewPassword,
    }).subscribe({
      next: () => {
        this.passwordSuccess     = 'Mot de passe modifié avec succès.';
        this.formCurrentPassword = '';
        this.formNewPassword     = '';
        this.formConfirmPassword = '';
        this.savingPassword      = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.passwordError  = 'Mot de passe actuel incorrect ou erreur serveur.';
        this.savingPassword = false;
      },
    });
  }

  reconnect(): void {
    localStorage.removeItem('currentUser');
    this.router.navigate(['/']);
  }

  goBack(): void {
    this.router.navigate([this.user.role === 'ADMIN' ? '/admin-dashboard' : '/user-dashboard']);
  }

  get initials(): string {
    return (this.user.firstName?.charAt(0) ?? '') + (this.user.name?.charAt(0) ?? '');
  }
}
