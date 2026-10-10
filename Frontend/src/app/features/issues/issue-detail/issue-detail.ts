import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { User } from '../../../core/auth/auth.models';
import { AuthService } from '../../../core/auth/auth.service';
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
import { UserService } from '../../../core/user/user.service';

const MAX_SUGGESTIONS = 8; 

function normalize(text: string): string {
    return text.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
}

@Component({
    selector: 'app-issue-detail',
    imports: [DatePipe, RouterLink],
    templateUrl: './issue-detail.html',
})
export class IssueDetail {
    private readonly route = inject(ActivatedRoute);
    private readonly issueService = inject(IssueService);
    private readonly userService = inject(UserService);
    protected readonly auth = inject(AuthService);

    // --- stato delle issue
    protected readonly issue = signal<Issue | null>(null);
    protected readonly loading = signal(false);
    protected readonly errorMessage = signal<string | null>(null);
    protected readonly imageNotUploaded = this.route.snapshot.queryParamMap.get('immagine') === 'non-caricata';

    // --- etichette e colori per il template
    protected readonly statusLabels = STATUS_LABELS;
    protected readonly priorityLabels = PRIORITY_LABELS;
    protected readonly typeLabels = TYPE_LABELS;
    protected readonly statusBadge = STATUS_BADGE;
    protected readonly priorityBadge = PRIORITY_BADGE;

    // --- assegnazione
    protected readonly assignees = signal<User[]>([]);
    protected readonly selectedAssignee = signal('');
    protected readonly assigneeQuery = signal('');
    protected readonly assigning = signal(false);
    protected readonly assignError = signal<string | null>(null);
    protected readonly assignSuccess = signal(false);


    protected readonly canAssign = computed( () => this.auth.isAdmin() && this.issue()?.type === 'BUG', );

    private readonly assigneeMatches = computed(() => {
        const words = normalize(this.assigneeQuery()).split(/[\s·]+/).filter(Boolean);
        if (words.length === 0) return[];

        return this.assignees().filter((user) => {
            const text = normalize(`${user.nome} ${user.cognome} ${user.email}`);
            return words.every((word) => text.includes(word));
        });
    });

    protected readonly suggestions = computed(() => 
        this.assigneeMatches().slice(0, MAX_SUGGESTIONS),
    );

    protected readonly hiddenMatches = computed(() => 
        Math.max(0, this.assigneeMatches().length - MAX_SUGGESTIONS),
    );

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
                this.selectedAssignee.set(issue.assigneeEmail ?? '');
                this.loading.set(false);
                
                if(this.canAssign()) {
                    this.loadAssignees();
                }
            },
            error: (error: HttpErrorResponse) => {
                this.errorMessage.set(describeHttpError(error));
                this.loading.set(false);
            }
        });
    }

    private loadAssignees(): void {
        this.userService.getUsers().subscribe({
            next: (users) => this.assignees.set(users),
            error: (error: HttpErrorResponse) => this.assignError.set(describeHttpError(error)),
        });
    }

    protected onAssigneeQuery(text: string): void {
        this.assigneeQuery.set(text);
        this.selectedAssignee.set('');
        this.assignSuccess.set(false);
        this.assignError.set(null);
    }

    protected pickAssignee(user: User): void {
        this.selectedAssignee.set(user.email);
        this.assigneeQuery.set(`${user.cognome} ${user.nome} · ${user.email}`)
    }
    protected onAssigneeEnter(event: Event): void {
        event.preventDefault();

        const first = this.suggestions()[0];
        if(this.assigneeQuery() && !this.selectedAssignee() && first) {
            this.pickAssignee(first);
            return;
        }

        if (this.selectedAssignee() && this.selectedAssignee() !== this.issue()?.assigneeEmail) {
            this.assign(); 
        }
    }

    protected assign(): void {
        const current = this.issue();
        const email = this.selectedAssignee(); 
        
        if(!current || !email || this.assigning()) return;

        this.assigning.set(true);
        this.assignError.set(null);
        this.assignSuccess.set(false);

        this.issueService.assignIssue(current.id, email).subscribe({
            next: (updated) => {
                this.issue.set(updated);
                this.assigning.set(false);
                this.assignSuccess.set(true);
            },
            error: (error: HttpErrorResponse) => {
                this.assignError.set(describeHttpError(error));
                this.assigning.set(false);
            }
        })
    }

    
}