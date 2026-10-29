import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketsService, FcLivree, DiLivree } from '../services/tickets.service';

type Tab = 'fc' | 'di';
type SortDir = 'asc' | 'desc' | null;

interface FcFilters {
  code: string;
  si: string;
  palier: string;
  lot: string;
  statut: string;
}

const ROWS_PER_PAGE_OPTIONS = [10, 15, 20, 25, 30, 35, 40, 45, 50];

@Component({
  selector: 'app-suivi-production',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './suivi-production.html',
  styleUrls: ['./suivi-production.scss'],
})
export class SuiviProductionComponent implements OnInit {
  activeTab: Tab = 'fc';

  fcRows: FcLivree[] = [];
  diRows: DiLivree[] = [];
  fcLoaded = false;
  diLoaded = false;
  loading = false;
  errorMessage = '';

  filters: FcFilters = { code: '', si: '', palier: '', lot: '', statut: '' };

  sortField = '';
  sortDir: SortDir = null;

  page = 1;
  rowsPerPage = 10;
  rowsPerPageOptions = ROWS_PER_PAGE_OPTIONS;

  selectedFc: FcLivree | null = null;
  selectedDi: DiLivree | null = null;

  constructor(
    private ticketsService: TicketsService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadFc();
  }

  goBack(): void {
    this.router.navigate(['/admin-dashboard']);
  }

  switchTab(tab: Tab): void {
    if (this.activeTab === tab) return;
    this.activeTab = tab;
    this.resetTableState();
    if (tab === 'di' && !this.diLoaded) this.loadDi();
    if (tab === 'fc' && !this.fcLoaded) this.loadFc();
  }

  private resetTableState(): void {
    this.filters = { code: '', si: '', palier: '', lot: '', statut: '' };
    this.sortField = '';
    this.sortDir = null;
    this.page = 1;
  }

  private loadFc(): void {
    this.loading = true;
    this.errorMessage = '';
    this.ticketsService.getFcLivrees().subscribe({
      next: (rows) => {
        this.fcRows = rows;
        this.fcLoaded = true;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Erreur lors du chargement des FC Livrées.';
      },
    });
  }

  private loadDi(): void {
    this.loading = true;
    this.errorMessage = '';
    this.ticketsService.getDiLivrees().subscribe({
      next: (rows) => {
        this.diRows = rows;
        this.diLoaded = true;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Erreur lors du chargement des DI Livrées.';
      },
    });
  }

  // ── Options de filtre dérivées des données chargées ──

  get siOptions(): string[] {
    return this.distinctValues((r: any) => r.si);
  }

  get lotOptions(): string[] {
    return this.distinctValues((r: any) => r.lotContractuel);
  }

  get statutOptions(): string[] {
    return this.distinctValues((r: any) => r.statut);
  }

  private distinctValues(pick: (row: any) => string | null): string[] {
    const source: any[] = this.activeTab === 'fc' ? this.fcRows : this.diRows;
    const set = new Set<string>();
    for (const row of source) {
      const v = pick(row);
      if (v) set.add(v);
    }
    return [...set].sort();
  }

  // ── Filtrage + tri + pagination ──

  get filteredRows(): any[] {
    const source: any[] = this.activeTab === 'fc' ? this.fcRows : this.diRows;
    const codeQuery = this.filters.code.trim().toLowerCase();
    const palierQuery = this.filters.palier.trim().toLowerCase();

    let rows = source.filter((r) => {
      if (codeQuery) {
        const combo = `${r.codeEvolution ?? ''}/${r.jira ?? ''}`.toLowerCase();
        if (!combo.includes(codeQuery)) return false;
      }
      if (this.filters.si && r.si !== this.filters.si) return false;
      if (palierQuery && !(r.palier ?? '').toLowerCase().includes(palierQuery)) return false;
      if (this.filters.lot && r.lotContractuel !== this.filters.lot) return false;
      if (this.filters.statut && r.statut !== this.filters.statut) return false;
      return true;
    });

    if (this.sortField && this.sortDir) {
      const field = this.sortField;
      const dir = this.sortDir === 'asc' ? 1 : -1;
      rows = [...rows].sort((a, b) => {
        const va = a[field];
        const vb = b[field];
        if (va == null && vb == null) return 0;
        if (va == null) return 1;
        if (vb == null) return -1;
        if (typeof va === 'number' && typeof vb === 'number') return (va - vb) * dir;
        return String(va).localeCompare(String(vb)) * dir;
      });
    }

    return rows;
  }

  get pagedRows(): any[] {
    const start = (this.page - 1) * this.rowsPerPage;
    return this.filteredRows.slice(start, start + this.rowsPerPage);
  }

  get totalRows(): number {
    return this.filteredRows.length;
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.totalRows / this.rowsPerPage));
  }

  get rangeStart(): number {
    return this.totalRows === 0 ? 0 : (this.page - 1) * this.rowsPerPage + 1;
  }

  get rangeEnd(): number {
    return Math.min(this.page * this.rowsPerPage, this.totalRows);
  }

  onFilterChange(): void {
    this.page = 1;
  }

  changeRowsPerPage(value: string): void {
    this.rowsPerPage = Number(value);
    this.page = 1;
  }

  goToPage(p: number): void {
    if (p < 1 || p > this.totalPages) return;
    this.page = p;
  }

  toggleSort(field: string): void {
    if (this.sortField !== field) {
      this.sortField = field;
      this.sortDir = 'asc';
    } else if (this.sortDir === 'asc') {
      this.sortDir = 'desc';
    } else if (this.sortDir === 'desc') {
      this.sortField = '';
      this.sortDir = null;
    } else {
      this.sortDir = 'asc';
    }
    this.page = 1;
  }

  sortIcon(field: string): string {
    if (this.sortField !== field) return 'none';
    return this.sortDir ?? 'none';
  }

  // ── Détail ──

  openFcDetail(row: FcLivree): void {
    this.selectedFc = row;
  }

  openDiDetail(row: DiLivree): void {
    this.selectedDi = row;
  }

  closeDetail(): void {
    this.selectedFc = null;
    this.selectedDi = null;
  }

  // ── Formatage ──

  formatDate(iso: string | null, withTime = false): string {
    if (!iso) return '—';
    const d = new Date(iso);
    if (isNaN(d.getTime())) return '—';
    const pad = (n: number) => String(n).padStart(2, '0');
    const base = `${pad(d.getDate())}-${pad(d.getMonth() + 1)}-${d.getFullYear()}`;
    if (!withTime) return base;
    return `${base} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  formatPercent(v: number | null): string {
    if (v == null) return '—';
    return `${(v * 100).toFixed(2)}%`;
  }

  formatNumber(v: number | null): string {
    return v == null ? '—' : String(v);
  }

  statutClass(statut: string | null): string {
    if (!statut) return 'neutral';
    const s = statut.toLowerCase();
    if (s.includes('livrée') || s.includes('production') || s.includes('go pour mep')) return 'success';
    if (s.includes('annul')) return 'danger';
    if (s.includes('recette') || s.includes('tig') || s.includes('dev')) return 'info';
    return 'neutral';
  }
}
