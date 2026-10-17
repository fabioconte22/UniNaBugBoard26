export type IssueStatus = 'TODO' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
export type IssuePriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type IssueType = 'QUESTION' | 'BUG' | 'DOCUMENTATION' | 'FEATURE';
export type CommentKind = 'COMMENT' | 'STATUS_CHANGE';

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
    TODO: 'app-status app-status-todo',
    IN_PROGRESS: 'app-status app-status-in-progress',
    RESOLVED: 'app-status app-status-resolved',
    CLOSED: 'app-status app-status-closed',
}

export const PRIORITY_BADGE: Record<IssuePriority, string> = {
    LOW: 'app-priority bg-body-tertiary text-body-secondary',
    MEDIUM: 'app-priority bg-info-subtle text-info-emphasis',
    HIGH: 'app-priority bg-warning-subtle text-warning-emphasis',
    CRITICAL: 'app-priority bg-danger-subtle text-danger-emphasis',
}

export interface CreateIssueRequest {
    titolo: string; 
    descrizione: string; 
    type: IssueType;
    priority: IssuePriority; 
}

export interface IssueComment {
    id: string;
    content: string;
    authorEmail: string; 
    authorNome: string | null;
    authorCognome: string | null;
    createdAt: string;
    kind: CommentKind;
    fromStatus: IssueStatus | null;
    toStatus: IssueStatus | null;
}

export interface WeekTrend {
    weekStart: string; 
    created: number; 
    closed: number;
}

export interface IssueStats {
    byStatus: Record<IssueStatus, number>;
    byTypeAndStatus: Record<IssueType, Record<IssueStatus, number>>; 
    openByPriority: Record<IssuePriority, number>; 
    lastWeeks: WeekTrend[]; 
}