import { Component, input, signal } from '@angular/core';

@Component({
    selector: 'button[appRevealPassword]',
    templateUrl: './reveal-password.html',
    styleUrl: './reveal-password.scss',
    host: {
        type: 'button',
        'aria-label': 'Tieni premuto per mostrare la password',
        '(pointerdown)': 'show($event)',
        '(pointerup)': 'hide()',
        '(pointerleave)': 'hide()',
        '(pointercancel)': 'hide()',
        '(keydown.space)': 'show($event)',
        '(keyup.space)': 'hide()',
        '(blur)': 'hide()',
        '(contextmenu)': '$event.preventDefault()',
    }, 
})
export class RevealPassword {
    readonly target = input.required<HTMLInputElement>({ alias: 'appRevealPassword' });
    protected readonly visible = signal(false);

    protected show(event: Event): void {
        event.preventDefault(); 
        this.target().type = 'text'; 
        this.visible.set(true); 
    }

    protected hide(): void {
        this.target().type = "password"; 
        this.visible.set(false); 
    }
}