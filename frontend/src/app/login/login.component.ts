import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { LoaderService } from '../services/loader.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  username = '';
  password = '';
  isLoading = false;
  demoMode = true; // Default to true for easier testing

  constructor(
    private authService: AuthService, 
    private router: Router,
    private notificationService: NotificationService,
    private loaderService: LoaderService
  ) { }

  onLogin() {
    console.log('Login clicked, demo mode:', this.demoMode); // Debug log
    
    // Handle demo mode first
    if (this.demoMode) {
      console.log('Running demo mode login'); // Debug log
      this.simulateLogin();
      return;
    }

    console.log('Running regular login'); // Debug log
    
    // Regular authentication
    if (!this.username || !this.password) {
      this.notificationService.showWarning('Please enter both username and password');
      return;
    }

    this.isLoading = true;
    this.loaderService.display(true);
    
    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        this.notificationService.showSuccess(
          `Welcome back ${response.user.firstName}!`,
          'Login Successful'
        );
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
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

  private simulateLogin() {
    console.log('Starting demo login simulation'); // Debug log
    this.isLoading = true;
    this.loaderService.display(true);
    
    // Simulate network delay
    setTimeout(() => {
      console.log('Demo login timeout reached'); // Debug log
      
      // Create a mock user for demo mode
      const mockUser = {
        id: 1,
        firstName: 'Demo',
        lastName: 'User',
        email: this.username || 'demo@blip.com',
        roleCode: 'USER',
        domain: 'DEMO',
        createdAt: new Date().toISOString(),
        lastLoggedInAt: new Date().toISOString(),
        isActive: true,
        username: this.username || 'demo@blip.com'
      };
      
      console.log('Simulating login for user:', mockUser); // Debug log
      
      // Simulate successful authentication
      this.authService.simulateLogin(mockUser);
      
      this.notificationService.showSuccess(
        `Welcome ${mockUser.firstName}! You're in demo mode.`,
        'Demo Login Successful'
      );
      
      this.isLoading = false;
      this.loaderService.display(false);
      
      console.log('Navigating to dashboard'); // Debug log
      this.router.navigate(['/dashboard']);
    }, 1000); // Reduced timeout to 1 second
  }
}
