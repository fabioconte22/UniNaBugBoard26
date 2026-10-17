import { Component, input } from '@angular/core';

@Component({
    selector: 'app-empty-state',
    templateUrl: './empty-state.html',
    styleUrl: './empty-state.scss', 
})
export class EmptyState {
    readonly icon = input.required<string>(); 
    readonly title = input.required<string>(); 
    readonly text = input<string>(); 
}