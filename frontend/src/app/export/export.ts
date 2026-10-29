import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ExportService, EntiteDto } from '../services/export.service';

interface EntiteOption extends EntiteDto {
  checked: boolean;
}

@Component({
  selector: 'app-export',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './export.html',
  styleUrls: ['./export.scss'],
})
export class ExportComponent implements OnInit {
  loading = false;
  errorMessage = '';
  successMessage = '';
  entites: EntiteOption[] = [];

  constructor(
    private route: ActivatedRoute,
    private exportService: ExportService,
  ) {}

  ngOnInit(): void {
    // Les entités sont déjà chargées — pas d'attente
    const data: EntiteDto[] = this.route.snapshot.data['entites'];
    this.entites = data.map((e) => ({ ...e, checked: false }));
  }

  get allChecked(): boolean {
    return this.entites.length > 0 && this.entites.every((e) => e.checked);
  }

  get someChecked(): boolean {
    return this.entites.some((e) => e.checked);
  }

  get selectedCount(): number {
    return this.entites.filter((e) => e.checked).length;
  }

  get selectedLabels(): string {
    return this.entites
      .filter((e) => e.checked)
      .map((e) => e.nom)
      .join(', ');
  }

  toggleAll(checked: boolean): void {
    this.entites.forEach((e) => (e.checked = checked));
  }

  export(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const selected = this.entites.filter((e) => e.checked).map((e) => e.nom);

    if (!selected.length) {
      this.errorMessage = 'Veuillez sélectionner au moins une entité.';
      return;
    }

    this.loading = true;

    this.exportService.exportPV(selected).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const date = new Date().toLocaleDateString('fr-FR').replace(/\//g, '-');
        a.download = `ALL_${selected.join('_')}_${date}.xlsx`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.loading = false;
        this.successMessage = 'Export téléchargé avec succès.';
      },
      error: () => {
        this.loading = false;
        this.errorMessage = "Erreur lors de l'export. Veuillez réessayer.";
      },
    });
  }
}
