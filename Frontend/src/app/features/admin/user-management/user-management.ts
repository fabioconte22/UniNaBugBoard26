import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Role, User } from '../../../core/auth/auth.models';
import { describeHttpError } from '../../../core/http/describe-error';
import { UserService } from '../../../core/user/user.service';
import { notBlank } from '../../../core/forms/validators';

@Component({
    selector: 'app-user-management',
    imports: [ReactiveFormsModule],
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

        this.loading.set(true); 
        this.errorMessage.set(null); 
        this.createdUser.set(null); 

        this.userService.createUser(this.form.getRawValue()).subscribe({
            next: (user) => {
                this.createdUser.set(user);
                this.form.reset();
                this.loadUsers();
                this.loading.set(false);
            },
            error: (error: HttpErrorResponse) => {
                this.errorMessage.set(describeHttpError(error));
                this.loading.set(false);
            },
        });
    }

    constructor() {
        this.loadUsers();
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