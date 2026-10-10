import { Role } from "../auth/auth.models";

export interface CreateUserRequest {
    nome: string; 
    cognome: string; 
    email: string; 
    password: string; 
    role: Role;
}

