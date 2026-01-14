import {CanActivateFn} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '../services/auth.service';

export const subscriptionsGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  let currentUser = authService.getCurrentUser();
  if (currentUser == null){
    console.error("User is not logged in!");
    return false;
  }
  //Only partner users are granted access to the subscription Page
  return currentUser?.partnerCode != null;
};
