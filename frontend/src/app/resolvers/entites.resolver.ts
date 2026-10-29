import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { ExportService, EntiteDto } from '../services/export.service';

export const entitesResolver: ResolveFn<EntiteDto[]> = () => {
  return inject(ExportService).getEntites();
};
