export type IssueStatus = 'TODO' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type IssuePriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type IssueType = 'QUESTION' | 'BUG' | 'DOCUMENTATION' | 'FEATURE';

export interface Issue {
    id: string;
    titolo: string;
    descrizione: string;
    imageUrl: string | null;
    type: IssueType; 
    status: IssueStatus; 
    priority: IssuePriority; 
    creatorEmail: string; 
    assigneeEmail: string | null; 
    createdAt: string;  

}

export interface Page<T> {
    content: T[];
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
}

export interface IssueFilters {
    status: IssueStatus | ''; 
    type: IssueType | ''; 
    priority: IssuePriority | ''; 
}

export const STATUS_LABELS: Record<IssueStatus, string> = {
    TODO: 'Da fare', 
    IN_PROGRESS: 'In corso',
    RESOLVED: 'Risolta',
    CLOSED: 'Chiusa',
};

export const PRIORITY_LABELS: Record<IssuePriority, string> = {
    LOW: 'Bassa',
    MEDIUM: 'Media',
    HIGH: 'Alta',
    CRITICAL: 'Critica',
};

export const TYPE_LABELS: Record<IssueType, string> = {
    QUESTION: 'Domanda',
    BUG: 'Bug',
    DOCUMENTATION: 'Documentazione',
    FEATURE: 'Funzionalità',
};

export const STATUS_BADGE: Record<IssueStatus, string> = {
    TODO: 'text-bg-secondary',
    IN_PROGRESS: 'text-bg-primary',
    RESOLVED: 'text-bg-success',
    CLOSED: 'text-bg-dark',
}

export const PRIORITY_BADGE: Record<IssuePriority, string> = {
    LOW: 'text-bg-light border',
    MEDIUM: 'text-bg-info',
    HIGH: 'text-bg-warning',
    CRITICAL: 'text-bg-danger',
}