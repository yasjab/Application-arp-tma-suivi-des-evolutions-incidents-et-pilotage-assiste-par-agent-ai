import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EntiteService } from '../services/entite.service';
import { EntiteDto } from '../services/export.service';

@Component({
  selector: 'app-entites-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './entites-admin.html',
  styleUrls: ['./entites-admin.scss'],
})
export class EntitesAdminComponent implements OnInit {
  entites: EntiteDto[] = [];
  formNom = '';
  editingId: number | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(
    private entiteService: EntiteService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,  // ← ajouté
  ) {}

  ngOnInit(): void {
    this.entites = [...this.route.snapshot.data['entites']];
  }

  saveEntite(): void {
    this.errorMessage = '';
    this.successMessage = '';
    const nom = this.formNom.trim();
    if (!nom) { this.errorMessage = 'Le nom est obligatoire.'; return; }

    if (this.editingId) {
      this.entiteService.update(this.editingId, { nom }).subscribe({
        next: (updated) => {
          const index = this.entites.findIndex((e) => e.id === this.editingId);
          if (index !== -1) {
            this.entites[index] = { ...updated };
            this.entites = [...this.entites];  // force nouvelle référence
          }
          this.successMessage = `"${nom}" modifiée avec succès.`;
          this.resetForm();
          this.cdr.detectChanges();  // ← force la détection
        },
        error: () => { this.errorMessage = 'Erreur lors de la modification.'; },
      });
    } else {
      this.entiteService.create({ nom }).subscribe({
        next: (created) => {
          this.entites = [...this.entites, { ...created }];
          this.successMessage = `"${nom}" ajoutée avec succès.`;
          this.resetForm();
          this.cdr.detectChanges();  // ← force la détection
        },
        error: () => { this.errorMessage = 'Entité déjà existante ou erreur serveur.'; },
      });
    }
  }

  deleteEntite(id: number, nom: string): void {
    if (!confirm(`Supprimer l'entité "${nom}" ?`)) return;
    this.entiteService.delete(id).subscribe({
      next: () => {
        this.entites = this.entites.filter((e) => e.id !== id);
        this.successMessage = `"${nom}" supprimée.`;
        if (this.editingId === id) this.resetForm();
        this.cdr.detectChanges();  // ← force la détection
      },
      error: () => { this.errorMessage = 'Erreur lors de la suppression.'; },
    });
  }

  editEntite(e: EntiteDto): void {
    this.editingId = e.id;
    this.formNom = e.nom;
    this.errorMessage = '';
    this.successMessage = '';
  }

  cancelEdit(): void { this.resetForm(); }

  goBack(): void { this.router.navigate(['/admin-dashboard']); }

  private resetForm(): void {
    this.formNom = '';
    this.editingId = null;
  }
}
