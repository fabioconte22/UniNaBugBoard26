import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ThemeToggle } from '../../core/theme/theme-toggle/theme-toggle';
import { NotificationService } from '../../core/notification/notification.service';

@Component({
    selector: 'app-navbar', 
    imports: [RouterLink, RouterLinkActive, ThemeToggle],
    templateUrl: './navbar.html',
})
export class Navbar {
    private readonly router = inject(Router);
    protected readonly auth = inject(AuthService); 
    protected readonly notifications = inject(NotificationService); 
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