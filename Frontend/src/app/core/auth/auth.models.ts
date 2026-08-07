export type Role = 'ADMIN' | 'USER'; 

export interface User {
    id: string; 
    nome: string; 
    cognome: string; 
    email: string; 
    role: Role;
    
}

export interface LoginRequest {
    email: string; 
    password: string;
}

export interface AuthResponse {
    token: string; 
}

export interface ApiError {
    timestrap: string; 
    status: number; 
    error: string; 
    message: string; 
    details: Record<string, string>;
}