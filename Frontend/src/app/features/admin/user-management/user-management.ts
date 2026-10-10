import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, effect, inject, signal, viewChild, Injector, afterNextRender } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Role, User } from '../../../core/auth/auth.models';
import { describeHttpError } from '../../../core/http/describe-error';
import { CreateUserRequest } from '../../../core/user/user.models';
import { UserService } from '../../../core/user/user.service';
import { notBlank } from '../../../core/forms/validators';
import { EnterNext } from '../../../core/forms/enter-next';

@Component({
    selector: 'app-user-management',
    imports: [ReactiveFormsModule, EnterNext],
    templateUrl: './user-management.html' 
})
export class UserManagement {
    private readonly fb = inject(FormBuilder); 
    private readonly userService = inject(UserService);

    protected readonly loading = signal(false);
    protected readonly errorMessage = signal<string | null>(null);
    protected readonly createdUser = signal<User | null>(null); 
    protected readonly users = signal<User[]>([]);
    protected readonly usersLoading = signal(false);
    protected readonly usersError = signal<string | null>(null);
    protected readonly pending = signal<CreateUserRequest | null>(null);
    private readonly confirmButton = viewChild<ElementRef<HTMLButtonElement>>('confirmButton')
    private readonly injector = inject(Injector);
    private readonly submitButton = viewChild<ElementRef<HTMLButtonElement>>('submitButton'); 


    protected readonly form = this.fb.nonNullable.group({
        nome: ['', [notBlank]],
        cognome: ['', [notBlank]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]], 
        role: ['USER' as Role, [Validators.required]]
    })

    protected submit(): void {
        if(this.form.invalid || this.loading()) {
            this.form.markAllAsTouched();
            return; 
        }
        this.errorMessage.set(null); 
        this.createdUser.set(null); 
        this.pending.set(this.form.getRawValue());
    }

    protected cancel(): void {
        this.pending.set(null);
        afterNextRender(() => this.submitButton()?.nativeElement.focus(), {
            injector: this.injector,
        });
    }

    protected confirm(): void {
        const request = this.pending();
        if (!request || this.loading()) return; 

        this.loading.set(true);

        this.userService.createUser(request).subscribe({
            next: (user) => {
                this.pending.set(null);
                this.createdUser.set(user);
                this.form.reset();
                this.loadUsers(); 
                this.loading.set(false);
            },
            error: (error: HttpErrorResponse) => {
                this.pending.set(null);
                this.errorMessage.set(describeHttpError(error));
                this.loading.set(false);
                
            }
        });
    }

    constructor() {
        this.loadUsers();

        this.form.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(() => this.pending.set(null));
        
        effect(() => this.confirmButton()?.nativeElement.focus());
    }

    protected loadUsers(): void {
        this.usersLoading.set(true);
        this.usersError.set(null);

        this.userService.getUsers().subscribe({
            next: (users) => {
                this.users.set(users);
                this.usersLoading.set(false);
            },
            error: (error: HttpErrorResponse) => {
                this.usersError.set(describeHttpError(error));
                this.usersLoading.set(false);
            },
         });
    }

}