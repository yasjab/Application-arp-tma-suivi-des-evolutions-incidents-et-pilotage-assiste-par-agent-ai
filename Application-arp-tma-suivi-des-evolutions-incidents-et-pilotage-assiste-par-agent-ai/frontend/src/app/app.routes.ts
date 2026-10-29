import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { AdminDashboard } from './admin-dashboard/admin-dashboard';
import { UserDashboard } from './user-dashboard/user-dashboard';
import { ExportComponent } from './export/export';
import { EntitesAdminComponent } from './entites-admin/entites-admin';
import { UsersAdminComponent } from './users-admin/users-admin';
import { ProfileComponent } from './profile/profile';
import { AgentIaComponent } from './agent-ia/agent-ia';
import { SuiviProductionComponent } from './suivi-production/suivi-production';
import { entitesResolver } from './resolvers/entites.resolver';
import { usersResolver } from './resolvers/users.resolver';
import { profileResolver } from './resolvers/profile.resolver';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'admin-dashboard', component: AdminDashboard },
  { path: 'user-dashboard', component: UserDashboard },
  {path: 'admin/entites', component: EntitesAdminComponent, resolve: { entites: entitesResolver },},
  { path: 'admin/users', component: UsersAdminComponent, resolve: { users: usersResolver } },
  { path: 'admin/agent-ia', component: AgentIaComponent },
  { path: 'suivi-production', component: SuiviProductionComponent },
  { path: 'profile', component: ProfileComponent, resolve: { profile: profileResolver } },
  { path: 'export', component: ExportComponent, resolve: { entites: entitesResolver } },
  { path: '**', redirectTo: '' },
];
