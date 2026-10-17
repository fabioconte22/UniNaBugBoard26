import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, ElementRef, Injector, afterNextRender, computed, effect, inject, signal, viewChild } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { User } from '../../../core/auth/auth.models';
import { AuthService } from '../../../core/auth/auth.service';
import { describeHttpError } from '../../../core/http/describe-error';
import {
    Issue,
    IssueStatus,
    PRIORITY_BADGE,
    PRIORITY_LABELS,
    STATUS_BADGE,
    STATUS_LABELS,
    TYPE_LABELS,
} from '../../../core/issue/issue.models';
import { IssueService } from '../../../core/issue/issue.service';
import { UserService } from '../../../core/user/user.service';
import { IssueComments } from '../issue-comments/issue-comments';

const MAX_SUGGESTIONS = 8; 
const MAX_NOTE_LENGTH = 2000;

interface StatusAction {
    to: IssueStatus; 
    label: string; 
    style: string; 
    note: 'required' | 'optional' | 'none';
}

function normalize(text: string): string {
    return text.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
}

@Component({
    selector: 'app-issue-detail',
    imports: [DatePipe, RouterLink, IssueComments],
    templateUrl: './issue-detail.html',
    styleUrl: './issue-detail.scss',
})
export class IssueDetail {
    private readonly route = inject(ActivatedRoute);
    private readonly issueService = inject(IssueService);
    private readonly userService = inject(UserService);
    protected readonly auth = inject(AuthService);
    private readonly injector = inject(Injector);

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

    // -- avanzamento (workflow degli stati)
    protected readonly pendingAction = signal<StatusAction | null>(null); 
    protected readonly statusNote = signal(''); 
    protected readonly noteAttempted = signal(false);
    protected readonly statusSaving = signal(false);
    protected readonly statusError = signal<string | null>(null);
    protected readonly maxNoteLength = MAX_NOTE_LENGTH;

    protected readonly historyVersion = signal(0);

    private readonly statusNoteInput = viewChild<ElementRef<HTMLTextAreaElement>>('statusNoteInput');
    private readonly statusConfirm = viewChild<ElementRef<HTMLButtonElement>>('statusConfirm');
    private readonly statusActionButton = viewChild<ElementRef<HTMLButtonElement>>('statusActionButton');

    protected readonly statusActions = computed<StatusAction[]>(() => {
        const issue = this.issue();
        const me = this.auth.user()?.email; 
        if(!issue || !me) return [];

        const isAssignee = issue.assigneeEmail === me;

        switch (issue.status) {
            case 'TODO':
                if(isAssignee) {
                    return [{ to: 'IN_PROGRESS', label: 'Inizia a lavorarci', style: "btn-primary", note: 'none' }];
                }
                if (issue.type !== 'BUG' && !issue.assigneeEmail) {
                    return [{ to: 'IN_PROGRESS', label: 'Prendi in carico', style: 'btn-primary', note: 'none'}];
                }
                return[];
            case 'IN_PROGRESS':
                return isAssignee
                    ? [{ to: 'RESOLVED', label: 'Segna come risolta', style: 'btn-success', note: 'required' }]
                    :[];
            case 'RESOLVED':
                return this.auth.isAdmin()
                    ?[
                        {to: 'CLOSED', label: 'Chiudi', style: 'btn-dark', note: 'none'},
                        {to: 'IN_PROGRESS', label: 'Riapri', style: 'btn-outline-secondary', note: 'optional'}
                    ]
                    :[];
            default:
                return [];
        }
    });

    protected readonly statusHint = computed(() => {
        const issue = this.issue(); 
        if (!issue) return null; 

        const me = this.auth.user()?.email;
        const assignee = issue.assigneeEmail;

        switch (issue.status) {
            case 'TODO':
                if (issue.type === 'BUG') {
                    if (!assignee) return 'In attesa che un amministratore assegni il bug.';
                    return assignee === me
                        ? 'Ti è stato assegnato: avvialo quando inizi a lavorarci.'
                        : `Assegnato a ${assignee}, che potrà avviarlo.`;
                }
                return assignee
                    ? `Presa in carico da ${assignee}.`
                    : 'Nessuno se ne sta occupando: chi la prende in carico ne diventa responsabile.';
            case 'IN_PROGRESS':
                return assignee === me
                    ? 'Quando hai finito, segnala la issue come risolta con un breve report.'
                    : `In lavorazione: se ne occupa ${assignee}.`;
            case 'RESOLVED':
                return this.auth.isAdmin()
                    ? 'Leggi il report nella cronologia qui sotto, poi chiudi la issue o riaprila.'
                    : `Risolta da ${assignee}: in attesa della verifica di un amministratore.`;
            case 'CLOSED':
                return 'Chiusa: è lo stato finale.';
        }
    });


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

        effect(() => (this.statusNoteInput() ?? this.statusConfirm())?.nativeElement.focus());
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

    protected chooseAction(action: StatusAction): void {
        this.pendingAction.set(action); 
        this.statusNote.set('');
        this.noteAttempted.set(false); 
        this.statusError.set(null); 
    }

    protected cancelStatus(): void {
        this.pendingAction.set(null);
        afterNextRender(() => this.statusActionButton()?.nativeElement.focus(), {
            injector: this.injector,
        });
    }    

    protected confirmStatusFromKeyboard(event: Event): void {
        event.preventDefault(); 
        this.confirmStatus();
    }

    protected confirmStatus(): void {
        const issue = this.issue(); 
        const action = this.pendingAction();
        if (!issue || !action || this.statusSaving()) return; 

        const note = this.statusNote().trim();
        if (action.note === 'required' && !note) {
            this.noteAttempted.set(true);
            return; 
        }

        this.statusSaving.set(true);
        this.statusError.set(null);

        this.issueService.updateStatus(issue.id, action.to, note || undefined).subscribe({
            next: (updated) => {
                this.issue.set(updated);
                this.pendingAction.set(null);
                this.statusSaving.set(false); 
                this.historyVersion.update((version) => version + 1);
            },
            error: (error: HttpErrorResponse) => {
                this.statusError.set(describeHttpError(error));
                this.statusSaving.set(false);
            },
        });
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