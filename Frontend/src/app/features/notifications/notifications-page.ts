import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { describeHttpError } from '../../core/http/describe-error';
import { NotificationItem } from '../../core/notification/notification-item/notification-item';
import { AppNotification } from '../../core/notification/notification.models';
import { NotificationService } from '../../core/notification/notification.service';
import { EmptyState } from '../../core/ui/empty-state/empty-state';

@Component({
    selector: 'app-notifications-page', 
    imports: [NotificationItem, EmptyState], 
    templateUrl: './notifications-page.html', 
})
export class NotificationsPage {
    private readonly notificationService = inject(NotificationService);

    protected readonly items = signal<AppNotification[]>([]);
    protected readonly loading = signal(false);
    protected readonly error = signal<string | null>(null);
    protected readonly unreadCount = computed(() => this.items().filter((item) => !item.readAt).length);

    constructor() {
        this.load(); 
    }

    protected load(): void {
        this.loading.set(true); 
        this.error.set(null); 

        this.notificationService.history().subscribe({
            next: (items) => {
                this.items.set(items); 
                this.loading.set(false); 
            }, 
            error: (error: HttpErrorResponse) => {
                this.error.set(describeHttpError(error)); 
                this.loading.set(false); 
            },
        });
    }

    protected markRead(id: string): void {
        this.items.update((list) => 
            list.map((item) => (item.id === id ? {...item, readAt: new Date().toISOString() } : item)),
        );

        this.notificationService.markAsRead(id); 
    }

}