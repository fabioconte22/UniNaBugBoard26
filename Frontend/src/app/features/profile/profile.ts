import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth/auth.service';
import { EnterNext } from '../../core/forms/enter-next';
import { fieldsMatch } from '../../core/forms/validators';
import { describeHttpError } from '../../core/http/describe-error';
import { RevealPassword } from '../../core/forms/reveal-password/reveal-password';

@Component({
    selector: 'app-profile', 
    imports: [ReactiveFormsModule, EnterNext, RevealPassword],
    templateUrl: './profile.html',
})
export class Profile {
    private readonly fb = inject(FormBuilder); 
    protected readonly auth = inject(AuthService); 

    protected readonly saving = signal(false);
    protected readonly saved = signal(false);
    protected readonly errorMessage = signal<string | null>(null);

    protected readonly form = this.fb.nonNullable.group(
        {
            currentPassword: ['', [Validators.required]],
            newPassword: ['', [Validators.required, Validators.minLength(6)]],
            confirmPassword: ['', Validators.required],
        },
        { validators: [fieldsMatch('newPassword', 'confirmPassword')] },
    );

    constructor() {
        this.form.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(() => this.saved.set(false));
    }

    protected confirmInvalid(): boolean {
        const confirm = this.form.controls.confirmPassword;
        return confirm.touched && (confirm.invalid || this.form.hasError('fieldsMismatch'));
    }

    protected submit(): void {
        if (this.form.invalid || this.saving()) {
            this.form.markAllAsTouched();
            return; 
        }

        const { currentPassword, newPassword } = this.form.getRawValue(); 
        this.saving.set(true); 
        this.errorMessage.set(null); 

        this.auth.changePassword({ currentPassword, newPassword }).subscribe({
            next: () => {
                this.form.reset();
                this.saved.set(true); 
                this.saving.set(false);
            }, 
            error: (error: HttpErrorResponse) => {
                this.errorMessage.set(describeHttpError(error)); 
                this.saving.set(false); 
            }, 
        });
    }
}