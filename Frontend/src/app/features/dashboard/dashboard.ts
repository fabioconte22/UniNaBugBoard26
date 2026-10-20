import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { describeHttpError } from '../../core/http/describe-error';
import {
    Issue,
    IssuePriority,
    IssueStatus,
    PRIORITY_BADGE,
    PRIORITY_LABELS,
    STATUS_BADGE,
    STATUS_LABELS,
    TYPE_LABELS,
} from '../../core/issue/issue.models';
import { IssueService } from '../../core/issue/issue.service';
import { DashboardOverview } from './overview/overview';
import { EmptyState } from '../../core/ui/empty-state/empty-state';
import { DashboardNotifications } from './notifications/notifications';

const PRIORITY_ORDER: Record<IssuePriority, number> = {CRITICAL: 0, HIGH: 1, MEDIUM: 2, LOW: 3 };
const OPEN_STATUSES: IssueStatus[] = ['TODO', 'IN_PROGRESS'];

@Component({
    selector: 'app-dashboard',
    imports: [DatePipe, RouterLink, DashboardNotifications, DashboardOverview, EmptyState],
    templateUrl: './dashboard.html',  
})
export class Dashboard {
    private readonly issueService = inject(IssueService);
    protected readonly auth = inject(AuthService);

    protected readonly statusLabels = STATUS_LABELS;
    protected readonly priorityLabels = PRIORITY_LABELS;
    protected readonly typeLabels = TYPE_LABELS;
    protected readonly statusBadge = STATUS_BADGE;
    protected readonly priorityBadge = PRIORITY_BADGE;

    // --- assegnate a me
    protected readonly assigned = signal<Issue[]>([]);
    protected readonly assignedLoading = signal(false);
    protected readonly assignedError = signal<string | null>(null);

    protected readonly assignedSorted = computed(() => 
        [...this.assigned()].sort(
            (a, b) => 
                Number(this.isOpen(b)) - Number(this.isOpen(a)) ||
                PRIORITY_ORDER[a.priority] - PRIORITY_ORDER[b.priority] ||
                b.createdAt.localeCompare(a.createdAt)
        ),
    );

    protected readonly openAssignedCount = computed(
        () => this.assigned().filter((issue) => this.isOpen(issue)).length,
    );

    // -- le mie segnalazioni 
    protected readonly mine = signal<Issue[]>([]);
    protected readonly mineLoading = signal(false);
    protected readonly mineError = signal<string | null>(null);

    protected readonly mineSorted = computed(() => 
        [...this.mine()].sort((a, b) => b.createdAt.localeCompare(a.createdAt)),
    );

    constructor() {
        this.loadAssigned();
        this.loadMine();
    }

    protected isOpen(issue: Issue): boolean {
        return OPEN_STATUSES.includes(issue.status);
    }

    protected loadAssigned(): void {
        this.assignedLoading.set(true);
        this.assignedError.set(null);  
        
        this.issueService.getAssignedIssues().subscribe({
            next: (issuees) => {
                this.assigned.set(issuees);
                this.assignedLoading.set(false);
            },
            error: (error: HttpErrorResponse) => {
                this.assignedError.set(describeHttpError(error));
                this.assignedLoading.set(false);
            },
        });
    }

    protected loadMine(): void {
        this.mineLoading.set(true);
        this.mineError.set(null); 

        this.issueService.getMyIssues().subscribe({
            next: (issues) => {
                this.mine.set(issues);
                this.mineLoading.set(false); 
            },
            error: (error: HttpErrorResponse) => {
                this.mineError.set(describeHttpError(error));
                this.mineLoading.set(false);
            },
        });
    }
}