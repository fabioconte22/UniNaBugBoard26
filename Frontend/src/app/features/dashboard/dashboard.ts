import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'app-dashboard', 
    template: `
        <div class="container py-5">
            <h1 class="h3">Ciao {{ auth.user()?.nome }}</h1>
            <p class="text-body-secondary mb-0">
                Qui compariranno le tue segnalazioni 
            </p>
        </div>
    `,
})
export class Dashboard {
    protected readonly auth = inject(AuthService); 
}