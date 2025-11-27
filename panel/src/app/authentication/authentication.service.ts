import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

import { LoginRequest, LoginResponse, User } from './login.models';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  readonly currentUser = signal<User | null>(null);
  readonly isAuthenticated = computed(() => !!this.getToken());

  constructor() { 
    // this.loadUserFromStorage();
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.getPath('auth/login'), credentials)
      .pipe(
        tap(response => {
          this.setToken(response.token);
          // TODO: Create user model from response if available
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  // private setUserDataFromResponse(loginResponse: LoginResponse): void {
    

  // }


  private getPath(path: string): string {
    return `http://localhost:8080/${path}`;
  }
}
