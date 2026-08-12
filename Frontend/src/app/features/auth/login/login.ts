import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
    selector: 'app-login',
    imports: [ReactiveFormsModule],
    templateUrl: './login.html',
})

export class Login{
    private readonly fb = inject(FormBuilder); 
    private readonly auth = inject(AuthService); 
    private readonly router = inject(Router); 
    private readonly route = inject(ActivatedRoute);
    
    protected readonly loading = signal(false); 
    protected readonly errorMessage = signal<string | null>(null); 

    protected readonly form = this.fb.nonNullable.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required]],
    });

    protected submit(): void {
        if(this.form.invalid || this.loading()) {
            this.form.markAllAsTouched();
            return;
        }

        this.loading.set(true); 
        this.errorMessage.set(null);

        this.auth.login(this.form.getRawValue()).subscribe({
            next: () => {
                const redirectTo = this.route.snapshot.queryParamMap.get('redirectTo');
                this.router.navigateByUrl(redirectTo ?? '/dashboard');
            },
            error: (error: HttpErrorResponse) => {
                this.loading.set(false);
                this.errorMessage.set(this.describe(error));
            },
        });
    }

    private describe(error: HttpErrorResponse): string {
        if(error.status === 0) return 'Server non raggiungibile.';
        if(error.status === 401) return 'Email o password non corretti.'; 
        if(error.status === 400) return error.error?.message ?? 'Dati inviati non validi.'; 
        return 'Errore imprevisto. Riprova più tardi. '; 
    }
    
}
