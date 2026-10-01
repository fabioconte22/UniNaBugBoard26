import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { describeHttpError } from '../../../core/http/describe-error';
import {
    Issue,
    IssueFilters,
    IssuePriority,
    IssueStatus,
    IssueType,
    PRIORITY_BADGE,
    PRIORITY_LABELS,
    STATUS_BADGE,
    STATUS_LABELS,
    TYPE_LABELS,
} from '../../../core/issue/issue.models';
import { IssueService } from '../../../core/issue/issue.service';

@Component({
    selector: 'app-issue-list',
    imports: [DatePipe, RouterLink],
    templateUrl: './issue-list.html',
})
export class IssueList {
    private readonly issueService = inject(IssueService); 

    protected readonly issues = signal<Issue[]>([]); 
    protected readonly loading = signal(false); 
    protected readonly errorMessage = signal<string | null>(null); 

    protected readonly page = signal(0); 
    protected readonly totalPages = signal(0); 
    protected readonly totalElements = signal(0); 

    protected readonly filters = signal<IssueFilters>({ status: '', type: '', priority: '' });

    protected readonly statusLabels = STATUS_LABELS;
    protected readonly priorityLabels = PRIORITY_LABELS;
    protected readonly typeLabels = TYPE_LABELS;
    protected readonly statusBadge = STATUS_BADGE;
    protected readonly priorityBadge = PRIORITY_BADGE;

    protected readonly statusOptions: IssueStatus[] = ['TODO', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];
    protected readonly typeOptions: IssueType[] = ['QUESTION', 'BUG', 'DOCUMENTATION', 'FEATURE'];
    protected readonly priorityOptions: IssuePriority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

    constructor() {
        this.load();
    }
 
    protected onFilterChange(key: keyof IssueFilters, value: string): void {
        this.filters.update((current) => ({ ...current, [key]:value }));
        this.page.set(0); 
        this.load();
    }

    protected goToPage(page: number): void {
        if(page < 0 || page >= this.totalPages()) return; 
        this.page.set(page);
        this.load(); 
    }

    protected load(): void {
        this.loading.set(true); 
        this.errorMessage.set(null); 

        this.issueService.getIssues(this.page(), this.filters()).subscribe({
            next: (result) => {
                this.issues.set(result.content); 
                this.totalPages.set(result.totalPages);
                this.totalElements.set(result.totalElements); 
                this.loading.set(false); 
            },
            error: (error: HttpErrorResponse) => {
                this.errorMessage.set(describeHttpError(error)); 
                this.loading.set(false); 
            },
        });
    }
    
}