import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    loadComponent: () => import('./shared/components/shell/shell.component').then(m => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/employees/profile/profile.component').then(m => m.ProfileComponent)
      },
      {
        path: 'employees',
        loadComponent: () => import('./features/employees/list/employee-list.component').then(m => m.EmployeeListComponent),
        canActivate: [roleGuard('ADMIN', 'MANAGER')]
      },
      {
        path: 'leaves',
        loadComponent: () => import('./features/leaves/leaves.component').then(m => m.LeavesComponent)
      },
      {
        path: 'performance',
        loadComponent: () => import('./features/performance/performance.component').then(m => m.PerformanceComponent)
      },
      {
        path: 'goals',
        loadComponent: () => import('./features/goals/goals.component').then(m => m.GoalsComponent)
      },
      {
        path: 'admin',
        loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent),
        canActivate: [roleGuard('ADMIN')]
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
