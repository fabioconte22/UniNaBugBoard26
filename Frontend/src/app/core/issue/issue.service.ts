import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Issue, IssueFilters, Page, CreateIssueRequest } from './issue.models';


export const PAGE_SIZE = 10; 

@Injectable({ providedIn: 'root'})
export class IssueService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = '/api/issue'

    getIssues(page: number, filters: IssueFilters): Observable<Page<Issue>> {
        let params = new HttpParams().set('page', page).set('size', PAGE_SIZE);

        if(filters.status) params = params.set('status', filters.status);
        if(filters.type) params = params.set('type', filters.type);
        if(filters.priority) params = params.set('priority', filters.priority);
        
        return this.http.get<Page<Issue>>(this.baseUrl, { params }); 

    }

    getIssueById(id: string): Observable<Issue> {
        return this.http.get<Issue>(`${this.baseUrl}/${id}`);
    }

    createIssue(request: CreateIssueRequest): Observable<Issue> {
        return this.http.post<Issue>(this.baseUrl, request);
    }

    uploadImage(id: string, file: File): Observable<Issue> {
        const body = new FormData(); 
        body.append('file', file);
        return this.http.post<Issue>(`${this.baseUrl}/${id}/image`, body) 
    }

    assignIssue(id: string, assigneeEmail: string): Observable<Issue> {
        return this.http.patch<Issue>(`${this.baseUrl}/${id}/assign`, { assigneeEmail });
    }
} 