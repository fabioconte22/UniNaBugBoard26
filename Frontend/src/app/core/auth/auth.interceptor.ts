import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

const LOGIN_URL = '/api/auth/login';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const token = auth.token; 

    const authorized = 
        token && req.url.startsWith('/api') && !req.url.startsWith(LOGIN_URL)
        ? req.clone({setHeaders: {Authorization: `Bearer ${token}`} })
        : req;

    return next(authorized).pipe(
        catchError((error: HttpErrorResponse) => {
            if(error.status === 401 && !req.url.startsWith(LOGIN_URL)) {
                auth.clearSession();
                router.navigate(['/login']);
            }
            return throwError(() => error);
        }),
    );
};