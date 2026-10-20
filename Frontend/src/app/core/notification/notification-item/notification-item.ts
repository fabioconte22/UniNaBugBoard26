import { DatePipe } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AppNotification } from '../notification.models';

@Component({
    selector: 'app-notification-item', 
    imports: [DatePipe, RouterLink], 
    templateUrl: './notification-item.html',
    host: { class: 'd-flex align-items-start gap-3' },
})
export class NotificationItem {
    readonly notification = input.required<AppNotification>(); 
    readonly markRead = output<void>(); 

    protected open(): void {
        if(!this.notification().readAt) {
            this.markRead.emit();
        }
    }
}