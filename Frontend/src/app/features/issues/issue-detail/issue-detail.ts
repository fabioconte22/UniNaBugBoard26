import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { describeHttpError } from '../../../core/http/describe-error';
import {
    Issue,
    PRIORITY_BADGE,
    PRIORITY_LABELS,
    STATUS_BADGE,
    STATUS_LABELS,
    TYPE_LABELS,
} from '../../../core/issue/issue.models';
import { IssueService } from '../../../core/issue/issue.service';

@Component({
    selector: 'app-issue-detail',
    imports: [DatePipe, RouterLink],
    templateUrl: './issue-detail.html',
})
export class IssueDetail {
    private readonly route = inject(ActivatedRoute);
    private readonly issueService = inject(IssueService);

    protected readonly issue = signal<Issue | null>(null);
    protected readonly loading = signal(false);
    protected readonly errorMessage = signal<string | null>(null);

    protected readonly statusLabels = STATUS_LABELS;
    protected readonly priorityLabels = PRIORITY_LABELS;
    protected readonly typeLabels = TYPE_LABELS;
    protected readonly statusBadge = STATUS_BADGE;
    protected readonly priorityBadge = PRIORITY_BADGE;

    constructor() {
        this.load();
    }

    protected load(): void {
        const id = this.route.snapshot.paramMap.get('id');

        if(!id) {
            this.errorMessage.set('Issue non trovata'); 
            return;
        }

        this.loading.set(true); 
        this.errorMessage.set(null); 

        this.issueService.getIssueById(id).subscribe({
            next: (issue) => {
                this.issue.set(issue);
                this.loading.set(false); 
            },
            error: (error: HttpErrorResponse) => {
                this.errorMessage.set(describeHttpError(error));
                this.loading.set(false);
            }
        })
    }
}