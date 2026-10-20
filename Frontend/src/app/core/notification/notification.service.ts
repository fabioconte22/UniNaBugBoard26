import { HttpClient } from '@angular/common/http';
import { Injectable, InjectionToken, computed, effect, inject, signal, untracked } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { AppNotification } from './notification.models';
import { observableToBeFn } from 'rxjs/internal/testing/TestScheduler';

@Injectable({ providedIn: 'root' })
export class NotificationService {
    private readonly http = inject(HttpClient);
    private readonly auth = inject(AuthService);
    private readonly baseUrl = '/api/notifications'; 

    private readonly items = signal<AppNotification[]>([]); 
    readonly unread = this.items.asReadonly(); 
    readonly count = computed(() => this.items().length); 

    constructor() {
        effect(() => {
            const user = this.auth.user(); 
            untracked(() => (user ? this.load() : this.items.set([]))); 
        });
    }
  
    load(): void {
        this.http.get<AppNotification[]>(this.baseUrl).subscribe({
            next: (notification) => this.items.set(notification),
            error: () => this.items.set([]),
        });
    }

    history(): Observable<AppNotification[]> {
        return this.http.get<AppNotification[]>(`${this.baseUrl}/history`);
    }

    markAsRead(id: string): void {
        this.items.update((list) => list.filter((notification) => notification.id !== id));
        this.http.patch<void>(`${this.baseUrl}/${id}/read`, {}).subscribe({
            error: () => this.load(),
        });
    }
}