import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
  User,
  LoginRequest,
  LoginResponse,
  CreateUserRequest,
  ApiResponse,
  RefreshTokenRequest
} from '../models/user.models';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public _isLoggedIn = new BehaviorSubject<boolean>(false);
  public _license = new BehaviorSubject<string>('ACTIVE');
  public _currentUser = new BehaviorSubject<User | null>(null);

  isLoggedIn = this._isLoggedIn.asObservable();
  licensed = this._license.asObservable();
  currentUser = this._currentUser.asObservable();

  readonly apiUrl = environment.apiUrl;
  private readonly TOKEN_KEY = 'blip_access_token';
  private readonly REFRESH_TOKEN_KEY = 'blip_refresh_token';
  private readonly USER_KEY = 'blip_user';

  constructor(protected http: HttpClient, protected messageService: MessageService) {
    // Initialize with a demo user for testing
    // this.initializeDemoMode();
    this._isLoggedIn.next(this.hasValidToken());
    this._currentUser.next(this.getUserFromStorage());
  }

  login(email: string, password: string): Observable<LoginResponse> {
    const loginData: LoginRequest = {
      data: { email, password }
    };

    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/user-management/login`, loginData)
      .pipe(
        map(response => {
          this.validateLoginResponse(response);
          return response?.returnObject;
        }),
        tap(returnObject => {
          if (!returnObject) return;

          this.setTokens(returnObject?.accessToken, returnObject?.refreshToken);

          const user = returnObject?.user;
          if (user) {
            user.permissions = returnObject?.permissions;
            this.setUser(user);
          }

          this._license.next(returnObject?.license);
          this._isLoggedIn.next(true);
        }),
        catchError(this.handleError)
      );
  }
  createUser(userData: CreateUserRequest): Observable<any> {
    const accessToken = localStorage.getItem("blip_access_token");
    let requestHeaders: any = {
      'Content-Type': 'application/json',
      'Request-Origin': 'BLIP-PORTAL',
      'Authorization': `Bearer ${accessToken}`
    }
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/user-management/create-user`, userData, { headers: requestHeaders })
      .pipe(
        map(response => response?.returnObject),
        catchError(this.handleError)
      );
  }

  refreshToken(): Observable<string> | undefined {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      console.log('No refresh token available');
      this.logout();
      return;
    }

    const refreshData: RefreshTokenRequest = {
      data: { refreshToken }
    };

    return this.http.post<ApiResponse<string>>(
      `${this.apiUrl}/user-management/refresh-token`,
      refreshData,
      { headers: { 'Request-Origin': 'BLIP-PORTAL', 'X-Skip-Auth': 'true' } })
      .pipe(
        map(response => response?.returnObject),
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

  public setTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(this.TOKEN_KEY, accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  public setUser(user: User): void {
    console.log('AuthService: Setting user:', user);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));

    this._currentUser.next(user);
  }

  private getUserFromStorage(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  public assignCurrentUser() {
    const user = this.getUserFromStorage();
    if (user) {
      this._currentUser.next(user);
    }
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
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (error.status === 0) {
        errorMessage = 'Unable to connect to server. Please check your connection.';
      } else {
        errorMessage = `Error ${error.status}: ${error.message}`;
      }
    }

    return throwError(() => new Error(errorMessage));
  };

  private validateLoginResponse(response: ApiResponse<any>) {
    if (response?.returnCode != 200) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: response?.returnMessage || 'Invalid Credentials'
      });
    }
  }

  hasPermission(permission: string): boolean {
    const user = this._currentUser.value;
    return !!(user?.permissions?.includes(permission));
  }
}
