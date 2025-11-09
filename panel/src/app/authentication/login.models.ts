export interface LoginRequest {
    username: string;
    password: string;
}

export interface LoginResponse {
    token: string;
    refreshToken?: string;
}

export interface User {
    id: string;
    username: string;
}