import { Injectable, signal } from '@angular/core';

export type Theme = 'light' | 'dark';

const STORAGE_KEY = 'bugboard26.theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
    private readonly root = document.documentElement; 
    private readonly current = signal<Theme>(this.root.getAttribute('data-bs-theme') === 'dark' ? 'dark' : 'light'); 

    readonly theme = this.current.asReadonly(); 

    toggle(): void {
        const next: Theme = this.current() === 'dark' ? 'light' : 'dark'; 

        this.root.setAttribute('data-bs-theme', next); 
        this.current.set(next); 

        try {
            localStorage.setItem(STORAGE_KEY, next); 
        } catch {
            
        }
    }
}

