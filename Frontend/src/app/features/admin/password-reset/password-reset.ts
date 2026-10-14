import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, Injector, afterNextRender, effect, inject, input, output, signal, viewChild,} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { User } from '../../../core/auth/auth.models';
import { EnterNext } from '../../../core/forms/enter-next';
import { describeHttpError } from '../../../core/http/describe-error';
import { UserService } from '../../../core/user/user.service';
import { RevealPassword } from '../../../core/forms/reveal-password/reveal-password';

@Component({
    selector: 'app-password-reset',
    imports: [ReactiveFormsModule, EnterNext, RevealPassword],
    templateUrl: './password-reset.html',
})
export class PasswordReset {
    private readonly fb = inject(FormBuilder);
    private readonly userService = inject(UserService);
    private readonly injector = inject(Injector);

    readonly user = input.required<User>();
    readonly done = output<User>();
    readonly cancelled = output<void>();

    protected readonly confirming = signal(false);
    protected readonly saving = signal(false);
    protected readonly errorMessage = signal<string | null>(null);

    private readonly passwordInput = viewChild<ElementRef<HTMLInputElement>>('passwordInput');
    private readonly confirmButton = viewChild<ElementRef<HTMLButtonElement>>('confirmButton');

    protected readonly form = this.fb.nonNullable.group({
        newPassword: ['', [Validators.required, Validators.minLength(6)]],
    });

    constructor() {
        effect(() => {
            this.user();
            this.form.reset();
            this.confirming.set(false); 
            this.errorMessage.set(null);
            afterNextRender(() => this.passwordInput()?.nativeElement.focus(), {injector: this.injector });  
        }); 

        effect(() => this.confirmButton()?.nativeElement.focus()); 

        this.form.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(() => this.confirming.set(false)); 
    }

    protected submit(): void {
        if (this.form.invalid || this.saving()) {
            this.form.markAllAsTouched(); 
            return; 
        }
        this.errorMessage.set(null);
        this.confirming.set(true); 
    }

    protected backToEdit(): void {
        this.confirming.set(false);
        afterNextRender(() => this.passwordInput()?.nativeElement.focus(), { injector: this.injector });
    }

    protected onEscape(): void {
        if(this.confirming()) {
            this.backToEdit(); 
        } else {
            this.cancelled.emit();
        }
    }

    protected confirm(): void {
        if (this.saving()) return; 
        
        const user = this.user(); 
        this.saving.set(true); 

        this.userService.resetPassword(user.id, this.form.getRawValue().newPassword).subscribe({
            next: () => {
                this.saving.set(false); 
                this.done.emit(user); 
            },
            error: (error: HttpErrorResponse) => {
                this.errorMessage.set(describeHttpError(error)); 
                this.confirming.set(false);
                this.saving.set(false); 
            },
        });
    }
}