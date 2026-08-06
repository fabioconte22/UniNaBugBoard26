import { HttpClient } from "@angular/common/http";
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, catchError, of, switchMap, tap } from "rxjs";
import { AuthResponse, LoginRequest, User } from './auth.models';

const TOKEN_KEY = 'bugboard26.token';

@Injectable({ providedIn: 'root'})
export class AuthService {
    private readonly http = inject(HttpClient); 
    private readonly currentUser = signal<User | null>(null);

    readonly user = this.currentUser.asReadonly(); 
    readonly isAuthenticated = computed(() => this.currentUser() !== null);
    readonly isAdmin = computed(() => this.currentUser()?.role === 'ADMIN');

    get token(): string | null {
        return localStorage.getItem(TOKEN_KEY);
    }

    login(credentials: LoginRequest): Observable<User> {
        return this.http.post<AuthResponse>('/api/auth/login', credentials).pipe(
            tap((response) => localStorage.setItem(TOKEN_KEY, response.token)),
            switchMap(() => this.loadCurrentUser()),
        )
    }

    loadCurrentUser(): Observable<User> {
        return this.http
            .get<User>('/api/auth/me')
            .pipe(tap((user) => this.currentUser.set(user)));
    }

    restoreSession(): Observable<User | null> {
        if(!this.token) {
            return of(null);
        }
        return this.loadCurrentUser().pipe(
            catchError(() => {
                this.clearSession();
                return of(null);
            }),
            
        );
    }

    logout(): void {
        this.clearSession(); 
    }

    clearSession(): void {
        localStorage.removeItem(TOKEN_KEY);
        this.currentUser.set(null);
    }


}