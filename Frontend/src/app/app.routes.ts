import { Routes } from '@angular/router';
import { adminGuard, authGuard, guestGuard } from './core/auth/auth.guard';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        loadComponent: () => import('./features/landing/landing').then((m) => m.Landing),
    },
    {
        path: 'login',
        canActivate: [guestGuard],
        loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
    },
    {
        path: 'dashboard',
        canActivate: [authGuard],
        loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard), 
    },
    {
        path: 'issues',
        canActivate: [authGuard],
        loadComponent: () => import('./features/issues/issue-list/issue-list').then((m) => m.IssueList),
    },
    {
        path: 'issues/new', 
        canActivate: [authGuard],
        loadComponent: () => import('./features/issues/issue-create/issue-create').then((m) => m.IssueCreate),
    },
    {
        path: 'issues/:id',
        canActivate: [authGuard],
        loadComponent: () => import('./features/issues/issue-detail/issue-detail').then((m) => m.IssueDetail),
    },
    {
        path: 'admin/users',
        canActivate: [authGuard, adminGuard],
        loadComponent: () => import('./features/admin/user-management/user-management').then((m) => m.UserManagement),
    },
    { path: '**', redirectTo: ''},
];
