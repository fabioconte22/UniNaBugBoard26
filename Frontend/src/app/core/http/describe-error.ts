import { HttpErrorResponse } from '@angular/common/http';

export function describeHttpError(error: HttpErrorResponse): string {
    if(error.status === 0) return 'Server non raggiungibile.'; 
    if(error.status === 400) return error.error?.message ?? 'Richiesta non valida.';
    if(error.status === 401) return 'Sessione scaduta, accedi di nuovo.';
    if(error.status === 403) return 'Non hai i permessi per vedere questo contenuto.';
    if(error.status === 404) return 'Contenuto non trovato.';
    return 'Errore imprevisto, riprova più tardi'
}