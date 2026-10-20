export type NotificationKind = 'ISSUE_RESOLVED'; 

export interface AppNotification {
    id: string; 
    kind: NotificationKind; 
    issueId: string; 
    issueTitolo: string; 
    actorEmail: string; 
    actorNome: string | null; 
    actorCognome: string | null; 
    createdAt: string; 
    readAt: string | null; 
}

