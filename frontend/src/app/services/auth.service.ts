import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import {
  User,
  LoginRequest,
  LoginResponse,
  CreateUserRequest,
  ApiResponse,
  RefreshTokenRequest
} from '../models/user.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private _isLoggedIn = new BehaviorSubject<boolean>(this.hasValidToken());
  private _currentUser = new BehaviorSubject<User | null>(this.getUserFromStorage());
  
  isLoggedIn = this._isLoggedIn.asObservable();
  currentUser = this._currentUser.asObservable();

  private readonly apiUrl = environment.apiUrl;
  private readonly TOKEN_KEY = 'blip_access_token';
  private readonly REFRESH_TOKEN_KEY = 'blip_refresh_token';
  private readonly USER_KEY = 'blip_user';

  constructor(private http: HttpClient) {
    // Initialize with demo user for testing
    this.initializeDemoMode();
  }
  
  private initializeDemoMode(): void {
    // Check if we should initialize demo mode
    const existingToken = this.getAccessToken();
    if (!existingToken) {
      console.log('AuthService: No existing token, initializing demo mode');
      const demoUser = {
        id: 1,
        firstName: 'Demo',
        lastName: 'User',
        email: 'demo@blip.com',
        roleCode: 'USER',
        domain: 'DEMO',
        createdAt: new Date().toISOString(),
        lastLoggedInAt: new Date().toISOString(),
        isActive: true,
        username: 'demo@blip.com'
      };
      this.simulateLogin(demoUser);
    }
  }

  login(email: string, password: string): Observable<LoginResponse> {
    const loginData: LoginRequest = {
      data: { email, password }
    };

    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/user-management/login`, loginData)
      .pipe(
        map(response => response.data),
        tap(loginResponse => {
          this.setTokens(loginResponse.accessToken, loginResponse.refreshToken);
          this.setUser(loginResponse.user);
          this._isLoggedIn.next(true);
          this._currentUser.next(loginResponse.user);
        }),
        catchError(this.handleError)
      );
  }

  createUser(userData: CreateUserRequest): Observable<any> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/user-management/create-user`, userData)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  refreshToken(): Observable<string> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    const refreshData: RefreshTokenRequest = {
      data: { refreshToken }
    };

    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/user-management/refresh-token`, refreshData)
      .pipe(
        map(response => response.data),
        tap(newToken => {
          localStorage.setItem(this.TOKEN_KEY, newToken);
        }),
        catchError((error) => {
          this.logout();
          return throwError(() => error);
        })
      );
  }

  simulateLogin(user: User): void {
    console.log('AuthService: simulateLogin called with user:', user); // Debug log
    
    // Generate mock tokens for demo mode
    const mockAccessToken = 'demo-access-token-' + Date.now();
    const mockRefreshToken = 'demo-refresh-token-' + Date.now();
    
    console.log('AuthService: Generated tokens:', { mockAccessToken, mockRefreshToken }); // Debug log
    
    this.setTokens(mockAccessToken, mockRefreshToken);
    this.setUser(user);
    
    console.log('AuthService: Setting login state to true'); // Debug log
    this._isLoggedIn.next(true);
    this._currentUser.next(user);
    
    console.log('AuthService: Current login state:', this._isLoggedIn.value); // Debug log
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this._isLoggedIn.next(false);
    this._currentUser.next(null);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  getCurrentUser(): User | null {
    return this._currentUser.value;
  }

  private setTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(this.TOKEN_KEY, accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  private setUser(user: User): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  private getUserFromStorage(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  private hasValidToken(): boolean {
    const token = this.getAccessToken();
    if (!token) return false;
    
    // Handle demo tokens
    if (token.startsWith('demo-access-token')) {
      return true;
    }
    
    try {
      // Basic JWT expiry check (you might want to use a JWT library for this)
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp > Date.now() / 1000;
    } catch {
      return false;
    }
  }

  private handleError = (error: HttpErrorResponse) => {
    let errorMessage = 'An unexpected error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check your connection.';
      } else {
        errorMessage = `Error ${error.status}: ${error.message}`;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  };
}
