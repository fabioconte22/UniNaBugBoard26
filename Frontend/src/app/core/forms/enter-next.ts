import { Directive, ElementRef, inject } from '@angular/core';

const FIELDS = 'input:not([type="file"]):not([type="hidden"]), select, textarea';

@Directive({
    selector: 'form[appEnterNext]',
    host: {
        '(keydown.enter)': 'onEnter($event)',
        '(focusin)': 'onFocusIn($event)',
    },
})
export class EnterNext {
    private readonly form = inject<ElementRef<HTMLFormElement>>(ElementRef).nativeElement;

    protected onEnter(event: Event): void {
        const key = event as KeyboardEvent; 
        const target = key.target as HTMLElement; 

        if (key.isComposing || key.shiftKey || key.ctrlKey || key.metaKey || key.altKey) return; 
        if (target instanceof HTMLTextAreaElement || target instanceof HTMLButtonElement) return; 

        const fields = this.fields(); 
        const index = fields.indexOf(target); 
        if (index === -1) return; 

        key.preventDefault(); 

        const next = fields[index + 1]; 
        if (next) {
            next.focus();
        } else {
            this.form.requestSubmit(); 
        }
    }

    protected onFocusIn(event: Event): void {
        const target = event.target as HTMLElement; 
        if (target instanceof HTMLTextAreaElement) return; 

        const fields = this.fields();
        const index = fields.indexOf(target); 
        if (index === -1) return; 
        
        target.setAttribute('enterkeyhint', index === fields.length -1 ? 'go' : 'next'); 
    }

    private fields(): HTMLElement[] {
        return Array.from(this.form.querySelectorAll<HTMLElement>(FIELDS)).filter(
            (el) => !(el as HTMLInputElement).disabled && el.getClientRects().length > 0,
        );
    }
}