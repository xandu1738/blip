import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { LoaderService } from '../services/loader.service';
import { CreateUserRequest } from '../models/user.models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  firstName = '';
  lastName = '';
  email = '';
  password = '';
  confirmPassword = '';
  role = 'USER'; // Default role
  partner = '';
  isLoading = false;

  // Available roles - you might want to fetch these from a service
  roles = [
    { value: 'USER', label: 'User' },
    { value: 'ADMIN', label: 'Administrator' },
    { value: 'MANAGER', label: 'Manager' }
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private loaderService: LoaderService
  ) {}

  onRegister() {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;
    this.loaderService.display(true);

    const userData: CreateUserRequest = {
      data: {
        role: this.role,
        first_name: this.firstName,
        last_name: this.lastName,
        email: this.email,
        password: this.password,
        ...(this.partner && { partner: this.partner })
      }
    };

    this.authService.createUser(userData).subscribe({
      next: (response) => {
        this.notificationService.showSuccess(
          'Account created successfully! You can now log in.',
          'Registration Successful'
        );
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.notificationService.showError(
          error.message || 'Registration failed. Please try again.',
          'Registration Failed'
        );
        console.error('Registration error:', error);
      },
      complete: () => {
        this.isLoading = false;
        this.loaderService.display(false);
      }
    });
  }

  private validateForm(): boolean {
    if (!this.firstName.trim()) {
      this.notificationService.showWarning('Please enter your first name');
      return false;
    }

    if (!this.lastName.trim()) {
      this.notificationService.showWarning('Please enter your last name');
      return false;
    }

    if (!this.email.trim()) {
      this.notificationService.showWarning('Please enter your email');
      return false;
    }

    if (!this.isValidEmail(this.email)) {
      this.notificationService.showWarning('Please enter a valid email address');
      return false;
    }

    if (!this.password) {
      this.notificationService.showWarning('Please enter a password');
      return false;
    }

    if (this.password.length < 6) {
      this.notificationService.showWarning('Password must be at least 6 characters long');
      return false;
    }

    if (this.password !== this.confirmPassword) {
      this.notificationService.showWarning('Passwords do not match');
      return false;
    }

    return true;
  }

  private isValidEmail(email: string): boolean {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
  }
}
