import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'app-dashboard', 
    templateUrl: './dashboard.html', 
})
export class Dashboard {
    protected readonly auth = inject(AuthService); 
}