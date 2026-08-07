import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'app-home', 
    template: `
        <div class="container py-5">
            <h1 class="h3">Ciao {{ auth.user()?.nome }}</h1>
            <p class="text-body-secondary">
                Ruolo: <span class="badge text-bg-secondary">{{ auth.user()?.role }}</span>
            </p>
            <button class="btn btn-outline-secondary" (click)="logout()">Esci</button>
        </div>
    `,
})
export class Home {
    private readonly router = inject(Router);
    protected readonly auth = inject(AuthService); 

    protected logout(): void {
        this.auth.logout();
        this.router.navigate(['/login']);
    }
}