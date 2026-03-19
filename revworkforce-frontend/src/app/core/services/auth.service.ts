import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { ApiResponse, AuthResponse, LoginRequest, Role } from '../models';

const API_URL = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.getStoredUser());
  currentUser$ = this.currentUserSubject.asObservable();

  private getStoredUser(): AuthResponse | null {
    try {
      const u = localStorage.getItem('rw_user');
      return u ? JSON.parse(u) : null;
    } catch { return null; }
  }

  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${API_URL}/auth/login`, request).pipe(
      tap(res => {
        if (res.success && res.data) {
          localStorage.setItem('rw_token', res.data.token);
          localStorage.setItem('rw_user', JSON.stringify(res.data));
          this.currentUserSubject.next(res.data);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('rw_token');
    localStorage.removeItem('rw_user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }
    //token is storing in local storage
  getToken(): string | null {
    return localStorage.getItem('rw_token');
  }

  get currentUser(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  get isLoggedIn(): boolean {
    return !!this.getToken();
  }

  get role(): Role | null {
    return this.currentUser?.role ?? null;
  }

  hasRole(...roles: Role[]): boolean {
    return this.role ? roles.includes(this.role) : false;
  }

  get isAdmin(): boolean { return this.role === 'ADMIN'; }
  get isManager(): boolean { return this.role === 'MANAGER'; }
  get isEmployee(): boolean { return this.role === 'EMPLOYEE'; }
  get isManagerOrAdmin(): boolean { return this.isAdmin || this.isManager; }
}
