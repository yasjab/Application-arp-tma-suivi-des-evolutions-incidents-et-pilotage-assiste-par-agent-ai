import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService, UserDto, UserRequest } from '../services/user.service';

@Component({
  selector: 'app-users-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users-admin.html',
  styleUrls: ['./users-admin.scss'],
})
export class UsersAdminComponent implements OnInit {
  users: UserDto[] = [];
  roles = ['ADMIN', 'USER'];

  // Formulaire
  formName = '';
  formFirstName = '';
  formLogin = '';
  formPassword = '';
  formEmail = '';
  formRole = 'USER';

  editingId: number | null = null;
  errorMessage = '';
  successMessage = '';
  showForm = false;

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.users = [...this.route.snapshot.data['users']];
  }

  openCreate(): void {
    this.resetForm();
    this.showForm = true;
    this.editingId = null;
  }

  openEdit(u: UserDto): void {
    this.editingId      = u.id;
    this.formName       = u.name;
    this.formFirstName  = u.firstName;
    this.formLogin      = u.login;
    this.formPassword   = '';
    this.formEmail      = u.email;
    this.formRole       = u.role;
    this.showForm       = true;
    this.errorMessage   = '';
    this.successMessage = '';
  }

  saveUser(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.formName.trim() || !this.formFirstName.trim() ||
      !this.formLogin.trim() || !this.formEmail.trim()) {
      this.errorMessage = 'Tous les champs obligatoires doivent être remplis.';
      return;
    }

    if (!this.editingId && !this.formPassword.trim()) {
      this.errorMessage = 'Le mot de passe est obligatoire pour un nouveau compte.';
      return;
    }

    const req: UserRequest = {
      name:      this.formName.trim(),
      firstName: this.formFirstName.trim(),
      login:     this.formLogin.trim(),
      email:     this.formEmail.trim(),
      role:      this.formRole,
      // mot de passe seulement à la création
      ...(this.editingId ? {} : { password: this.formPassword.trim() }),
    };

    if (this.editingId) {
      this.userService.update(this.editingId, req).subscribe({
        next: (updated) => {
          const index = this.users.findIndex((u) => u.id === this.editingId);
          if (index !== -1) this.users[index] = { ...updated };
          this.users = [...this.users];
          this.successMessage = `Compte "${updated.login}" modifié avec succès.`;
          this.showForm = false;
          this.resetForm();
          this.cdr.detectChanges();
        },
        error: () => { this.errorMessage = 'Erreur lors de la modification.'; },
      });
    } else {
      this.userService.create(req).subscribe({
        next: (created) => {
          this.users = [...this.users, { ...created }];
          this.successMessage = `Compte "${created.login}" créé avec succès.`;
          this.showForm = false;
          this.resetForm();
          this.cdr.detectChanges();
        },
        error: () => { this.errorMessage = 'Login déjà existant ou erreur serveur.'; },
      });
    }
  }

  deleteUser(id: number, login: string): void {
    if (!confirm(`Supprimer le compte "${login}" ?`)) return;
    this.userService.delete(id).subscribe({
      next: () => {
        this.users = this.users.filter((u) => u.id !== id);
        this.successMessage = `Compte "${login}" supprimé.`;
        if (this.editingId === id) { this.showForm = false; this.resetForm(); }
        this.cdr.detectChanges();
      },
      error: () => { this.errorMessage = 'Erreur lors de la suppression.'; },
    });
  }

  cancelForm(): void {
    this.showForm = false;
    this.resetForm();
  }

  goBack(): void {
    this.router.navigate(['/admin-dashboard']);
  }

  formatDate(d: string): string {
    if (!d) return '-';
    return new Date(d).toLocaleDateString('fr-FR');
  }

  private resetForm(): void {
    this.formName      = '';
    this.formFirstName = '';
    this.formLogin     = '';
    this.formPassword  = '';
    this.formEmail     = '';
    this.formRole      = 'USER';
    this.editingId     = null;
    this.errorMessage  = '';
    this.successMessage = '';
  }
}
