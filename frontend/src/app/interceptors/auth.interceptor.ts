import {HttpInterceptorFn, HttpRequest, HttpHandlerFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {catchError, EMPTY, switchMap, throwError} from 'rxjs';
import {AuthService} from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Skip interceptor for login and create-user endpoints
  if (req.url.includes('/login') || req.url.includes('/create-user')) {
    return next(req);
  }

  const token = authService.getAccessToken();

  // Add token to request if available
  let authReq = req;
  if (token) {
    // For demo tokens, just add basic headers without Authorization
    if (token.startsWith('demo-access-token')) {
      authReq = req.clone({
        setHeaders: {
          'Request-Origin': 'BLIP-PORTAL',
          'Content-Type': 'application/json'
        }
      });
    } else {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
          'Request-Origin': 'BLIP-PORTAL',
          'Content-Type': 'application/json'
        }
      });
    }
  }

  return next(authReq).pipe(
    catchError((error) => {
      // Handle 401 errors - token expired or invalid
      if (error.status === 401 && token && !req.headers.has('X-Skip-Auth')) {
        let refreshToken = authService.refreshToken();
        if (!refreshToken) {
          router.navigate(['/login']).catch(err => console.error(err));
          return EMPTY;
        }
        // Try to refresh the token
        return refreshToken.pipe(
          switchMap(() => {
            // Retry the original request with the new token
            const newToken = authService.getAccessToken();
            const retryReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`,
                'Request-Origin': 'BLIP-PORTAL',
                'Content-Type': 'application/json'
              }
            });
            return next(retryReq);
          }),
          catchError((refreshError) => {
            // Refresh failed, redirect to log in url
            authService.logout();
            router.navigate(['/login']).catch(err => console.error(err));
            return throwError(() => refreshError);
          })
        );
      }

      // For other errors or if no token, just pass through
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']).catch(err => console.error(err));
      }

      return throwError(() => error);
    })
  );
};
