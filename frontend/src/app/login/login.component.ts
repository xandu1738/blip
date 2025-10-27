import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {NotificationService} from '../services/notification.service';
import {LoaderService} from '../services/loader.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  username = '';
  password = '';
  isLoading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private loaderService: LoaderService
  ) {
  }

  onLogin() {

    // Regular authentication
    if (!this.username || !this.password) {
      this.notificationService.showWarning('Please enter both username and password');
      return;
    }

    this.isLoading = true;

    this.loaderService.display(true);

    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        console.log("welcome back")
        this.notificationService.showSuccess(
          `Welcome back ${response?.user?.firstName || 'anthony'}!`,
          'Login Successful'
        );
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.log("error found")
        this.notificationService.showError(
          error.message || 'Login failed. Please check your credentials.',
          'Login Failed'
        );
        console.error('Login error:', error);
      },
      complete: () => {
        this.isLoading = false;
        this.loaderService.display(false);
      }
    });
  }
}
