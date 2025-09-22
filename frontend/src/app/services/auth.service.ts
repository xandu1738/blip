import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private _isLoggedIn = new BehaviorSubject<boolean>(false);
  isLoggedIn = this._isLoggedIn.asObservable();

  constructor() { }

  login() {
    // In a real app, you'd perform authentication here
    this._isLoggedIn.next(true);
  }

  logout() {
    this._isLoggedIn.next(false);
  }
}
