import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { describeHttpError } from '../../../core/http/describe-error';
import {
    CreateIssueRequest,
    IssuePriority,
    IssueType,
    PRIORITY_LABELS,
    TYPE_LABELS,
} from '../../../core/issue/issue.models';
import { IssueService } from '../../../core/issue/issue.service';
import { notBlank } from '../../../core/forms/validators';

const ALLOWED_IMAGE_TYPES = ['image/png', 'image/jpeg', 'image/gif', 'image/webp'];

const MAX_IMAGE_BYTES = 1024 * 1024;

@Component({
    selector: 'app-issue-create', 
    imports: [ReactiveFormsModule, RouterLink], 
    templateUrl: './issue-create.html', 
})
export class IssueCreate {
    private readonly fb = inject(FormBuilder); 
    private readonly issueService = inject(IssueService);
    private readonly router = inject(Router); 

    protected readonly loading = signal(false); 
    protected readonly errorMessage = signal<string | null>(null);
    protected readonly image = signal<File | null>(null);
    protected readonly imageError = signal<string | null>(null);

    protected readonly typeLabels = TYPE_LABELS; 
    protected readonly priorityLabels = PRIORITY_LABELS;
    protected readonly typeOptions: IssueType[] = ['BUG', 'FEATURE', 'QUESTION', 'DOCUMENTATION'];
    protected readonly priorityOptions: IssuePriority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

    protected readonly form = this.fb.nonNullable.group({
        titolo: ['', [notBlank, Validators.maxLength(255)]],
        descrizione:['', [notBlank, Validators.maxLength(2000)]],
        type: ['' as IssueType | '', [Validators.required]],
        priority: ['LOW' as IssuePriority, [Validators.required]], 
    });

    protected onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement; 
        const file = input.files?.[0] ?? null; 
        this.imageError.set(null); 
        this.image.set(null); 

        if(!file) return; 

        if(!ALLOWED_IMAGE_TYPES.includes(file.type)) {
            this.imageError.set("Formato non supportato: usa PNG, JPEG, GIF o WebP.");
            input.value = ''; 
            return; 
        }

        if(file.size > MAX_IMAGE_BYTES) {
            this.imageError.set("L'immagine supera il limite di 1MB."); 
            input.value = ''; 
            return; 
        }

        this.image.set(file); 
    }

    protected removeImage(input: HTMLInputElement): void {
        input.value = '';
        this.image.set(null);
        this.imageError.set(null);
    }

    protected submit(): void {
        if(this.form.invalid || this.loading()) {
            this.form.markAllAsTouched(); 
            return;
        }
        this.loading.set(true); 
        this.errorMessage.set(null); 

        const value = this.form.getRawValue(); 
        const request: CreateIssueRequest = {
            titolo: value.titolo,
            descrizione: value.descrizione,
            type: value.type as IssueType,
            priority: value.priority,
        }; 

        this.issueService.createIssue(request).subscribe({
            next: (issue) => this.attachImageAndOpen(issue.id),
            error: (error: HttpErrorResponse) => {
                this.errorMessage.set(describeHttpError(error)); 
                this.loading.set(false); 
            }
        });
    }

    private attachImageAndOpen(id: string): void {
    const file = this.image(); 

        if(!file) {
            this.router.navigate(['/issues', id])
            return; 
        }

        this.issueService.uploadImage(id,file).subscribe({
            next: () => this.router.navigate(['/issues', id]),
            error: () => this.router.navigate(['issues', id], {
                queryParams: { immagine: 'non-caricata' },
            }),
        });
    }

}