import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

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

  constructor(private authService: AuthService, private router: Router) { }

  onLogin() {
    console.log('Login attempt with:', this.username, this.password);
    // For now, just log and simulate success
    // In a real app, you'd call an authentication service here
    alert('Login successful! (Prototype functionality)');
    this.authService.login();
    this.router.navigate(['/dashboard']);
  }
}
