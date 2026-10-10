import { AbstractControl, ValidationErrors } from '@angular/forms';

export function notBlank(control: AbstractControl): ValidationErrors | null {
    const value = control.value; 
    return typeof value === 'string' && value.trim().length === 0 ? { required: true } : null; 
}