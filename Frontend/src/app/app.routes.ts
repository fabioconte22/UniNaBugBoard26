import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/auth/auth.guard';

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
        path: 'issues/:id',
        canActivate: [authGuard],
        loadComponent: () => import('./features/issues/issue-detail/issue-detail').then((m) => m.IssueDetail),
    },
    { path: '**', redirectTo: ''},
];
