import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NotificationItem } from '../../../core/notification/notification-item/notification-item';
import { NotificationService } from '../../../core/notification/notification.service';

@Component({
    selector: 'app-dashboard-notifications',
    imports: [RouterLink, NotificationItem], 
    templateUrl: './notifications.html',
})
export class DashboardNotifications {
    protected readonly notifications = inject(NotificationService); 

    constructor() {
        this.notifications.load(); 
    }
}