import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function notBlank(control: AbstractControl): ValidationErrors | null {
    const value = control.value; 
    return typeof value === 'string' && value.trim().length === 0 ? { required: true } : null; 
}

export function fieldsMatch(field: string, confirmField: string): ValidatorFn {
    return (group: AbstractControl): ValidationErrors | null => {
        const value = group.get(field)?.value; 
        const confirm = group.get(confirmField)?.value; 
        return confirm && value !== confirm ? { fieldsMismatch: true } : null; 
    }
}