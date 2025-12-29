import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {CanActivateFn} from '@angular/router';
import {map, take} from 'rxjs/operators';
import {AuthService} from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isLoggedIn.pipe(
    take(1),
    map(isLoggedIn => {
      console.log('Auth guard checking login state:', isLoggedIn);
      if (isLoggedIn) {
        // router.navigate(['dashboard']);
        return true;
      }
      console.log('Not logged in, redirecting to login');
      router.navigate(['login'], {queryParams: {returnUrl: state.url}})
        .catch(err => console.log("Error redirecting:", err.message));
      return false;

    })
  );
};
