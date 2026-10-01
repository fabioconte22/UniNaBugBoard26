import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'app-navbar', 
    imports: [RouterLink, RouterLinkActive],
    templateUrl: './navbar.html',
})
export class Navbar {
    private readonly router = inject(Router);
    protected readonly auth = inject(AuthService); 
    protected readonly menuOpen = signal(false); 

    protected toggleMenu(): void {
        this.menuOpen.update((open) => !open); 
    }

    protected closeMenu(): void {
        this.menuOpen.set(false); 
    }

    protected logout(): void {
        this.closeMenu();
        this.auth.logout(); 
        this.router.navigate(['/']); 
    }
}