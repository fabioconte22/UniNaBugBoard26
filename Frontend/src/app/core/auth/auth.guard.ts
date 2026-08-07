import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (_route, state) => {
    const auth = inject(AuthService);
    const router = inject(Router);

    return(
        auth.isAuthenticated() || 
        router.createUrlTree(['/login'], { queryParams: { redirectdTo: state.url } })
    );
};

export const adminGuard: CanActivateFn = () => {
    const auth = inject(AuthService); 
    const router = inject(Router); 

    if(auth.isAdmin()) return true; 
    return router.createUrlTree([auth.isAuthenticated() ? '/home' : '/login']); 
};

export const guestGuard: CanActivateFn = () => {
    const auth = inject(AuthService);
    const router = inject(Router); 

    return auth.isAuthenticated() ? router.createUrlTree(['/home']) : true; 
};

