import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {NotificationService} from '../services/notification.service';
import {LoaderService} from '../services/loader.service';
import {BaseComponent} from '../services/base-component';
import {RemoteService} from '../services/remoteService';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent extends BaseComponent implements OnInit {
  username = '';
  password = '';
  isLoading = false;

  override ngOnInit(): void {
    super.ngOnInit();
  }

  constructor(
    protected notificationService: NotificationService,
    protected router: Router,
    helper: RemoteService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    authService: AuthService
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  onLogin() {
    // Regular authentication
    if (!this.username || !this.password) {
      this.showWarning('Please enter both username and password');
      return;
    }

    this.isLoading = true;

    this.loaderService.display(true);

    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        console.log(response);
        if (!response) return;
        if (response?.user?.partnerCode && response?.license !== 'ACTIVE') {
          this.router.navigate(['/subscriptions'])
            .catch(err => console.error('Login redirect error:', err));
          return;
        }
        this.router.navigate(['/dashboard'])
          .catch(err => console.error('Login redirect error:', err));
      },
      error: (error) => {
        console.log("error found")
        this.showError(error.message || 'Login failed. Please check your credentials.');
        console.error('Login error:', error);
      },
      complete: () => {
        this.isLoading = false;
        this.loaderService.display(false);
      }
    });
  }
}
