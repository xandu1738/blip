import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {map, take} from 'rxjs/operators';

export const licenseGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.licensed.pipe(
    take(1),
    map(license => {
      let licensed = license === 'ACTIVE';
      console.log('License guard checking subscription state:', licensed);
      if (licensed) {
        return true;
      }

      console.log('Not Licensed in, redirecting to subscriptions');
      router.navigate(['subscriptions'])
        .catch(err => console.log("Error redirecting:", err.message));
      return false;

    })
  );
};
