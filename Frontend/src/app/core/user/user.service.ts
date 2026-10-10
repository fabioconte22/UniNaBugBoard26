import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../auth/auth.models';
import { CreateUserRequest } from './user.models';

@Injectable({ providedIn: 'root' })
export class UserService {
    private readonly http = inject(HttpClient); 
    private readonly baseUrl = '/api/admin/users'

    createUser(request: CreateUserRequest): Observable<User> {
        return this.http.post<User>(this.baseUrl, request);
    }

    getUsers(): Observable<User[]> {
        return this.http.get<User[]>(this.baseUrl);
    }
}