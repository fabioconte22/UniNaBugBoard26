import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { notBlank } from '../../../core/forms/validators';
import { describeHttpError } from '../../../core/http/describe-error';
import { IssueComment, STATUS_BADGE, STATUS_LABELS } from '../../../core/issue/issue.models';
import { IssueService } from '../../../core/issue/issue.service';
import { EmptyState } from '../../../core/ui/empty-state/empty-state';

const MAX_COMMENT_LENGTH = 2000; 

@Component({
    selector: 'app-issue-comments',
    imports: [DatePipe, ReactiveFormsModule, EmptyState],
    templateUrl: './issue-comments.html',
    styleUrl: './issue-comments.scss',
})
export class IssueComments {
    private readonly issueService = inject(IssueService);
    private readonly auth = inject(AuthService);
    private readonly fb = inject(FormBuilder);

    readonly issueId = input.required<string>(); 
    readonly refresh = input(0);

    protected readonly comments = signal<IssueComment[]>([]);
    protected readonly loading = signal(false);
    protected readonly loadError = signal<string | null>(null);
    protected readonly posting = signal(false);
    protected readonly postError = signal<string | null>(null);
    protected readonly attempted = signal(false); 

    protected readonly maxLength = MAX_COMMENT_LENGTH;
    protected readonly myEmail = computed(() => this.auth.user()?.email);
    protected readonly newestFirst = computed(() => [...this.comments()].reverse());
    protected readonly commentCount = computed(
        () => this.comments().filter((comment) => comment.kind !== 'STATUS_CHANGE').length,
    );

    protected readonly statusLabels = STATUS_LABELS;
    protected readonly statusBadge = STATUS_BADGE; 

    protected readonly shortcut = /Mac|iPhone|iPad/.test(navigator.userAgent)
        ? '⌘ + Invio'
        : 'Ctrl + Invio'; 

    protected readonly form = this.fb.nonNullable.group({
        content: ['', [notBlank, Validators.maxLength(MAX_COMMENT_LENGTH)]]
    });

    constructor() {
        effect(() => {
            this.refresh();
            this.load(this.issueId())
        }); 
    }

    protected retry(): void {
        this.load(this.issueId());
    }

    protected publishFromKeyboard(event: Event): void {
        event.preventDefault();
        this.publish();
    }

    protected publish(): void {
        if(this.form.invalid || this.posting()) {
            this.attempted.set(true);
            return; 
        }

        this.posting.set(true);
        this.postError.set(null);

        const content = this.form.getRawValue().content.trim();

        this.issueService.addComment(this.issueId(), content).subscribe({
            next: (comment) => {
                this.comments.update((list) => [...list, comment]);
                this.form.reset();
                this.attempted.set(false);
                this.posting.set(false);
            },
            error: (error: HttpErrorResponse) => {
                this.postError.set(describeHttpError(error));
                this.posting.set(false);
            },
        });
    }

    private load(issueId: string): void {
        this.loading.set(true);
        this.loadError.set(null);

        this.issueService.getComments(issueId).subscribe({
            next: (comments) => {
                this.comments.set(comments);
                this.loading.set(false);
            },
            error: (error: HttpErrorResponse) => {
                this.loadError.set(describeHttpError(error));
                this.loading.set(false);
            },
        });
    }
}