import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router'; // Import Router
import {CommonModule} from '@angular/common';
import {CommonService} from './services/commonService';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component'; // Import LoginComponent
import { AuthService } from './services/auth.service'; // Import AuthService
import { LoaderService } from './services/loader.service';
import { MegaMenuModule } from 'primeng/megamenu'; // Import MegaMenuModule
import {Menubar} from 'primeng/menubar';
import { ToastModule } from 'primeng/toast'; // Import ToastModule
import { MegaMenuItem } from 'primeng/api'; // Import MegaMenuItem

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MegaMenuModule, ToastModule,Menubar, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  isLoggedIn: boolean = false; // Add login state
  items: MegaMenuItem[] | undefined;
  showLoader: boolean = false;

  constructor(
    protected commonService: CommonService,
    private router: Router,
    private authService: AuthService,
    private loaderService: LoaderService
  ) { // Inject Router and AuthService
  }

  ngOnInit() {
    // Subscribe to loader service
    this.loaderService.status.subscribe((isLoading: boolean) => {
      this.showLoader = isLoading;
    });

    // Subscribe to authentication state
    this.authService.isLoggedIn.subscribe((loggedIn: boolean) => {
      console.log('App component: Login state changed to:', loggedIn); // Debug log
      this.isLoggedIn = loggedIn;
    });

    this.items = [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        routerLink: ['/dashboard'],
        styleClass: 'custom-menu-item'
      },
      {
        label: 'Bus Booking',
        icon: 'pi pi-ticket',
        routerLink: ['/bus_booking'],
        styleClass: 'custom-menu-item'
      },
      {
        label: 'Parcels',
        icon: 'pi pi-box',
        routerLink: ['/parcels'],
        styleClass: 'custom-menu-item'
      },
      {
        label: 'Tracking',
        icon: 'pi pi-map-marker',
        routerLink: ['/tracking'],
        styleClass: 'custom-menu-item'
      },
      {
        label: 'Payments',
        icon: 'pi pi-wallet',
        routerLink: ['/payments'],
        styleClass: 'custom-menu-item'
      },
      {
        label: 'Reports',
        icon: 'pi pi-chart-bar',
        routerLink: ['/reports'],
        styleClass: 'custom-menu-item'
      },
      {
        label: 'Settings',
        icon: 'pi pi-cog',
        routerLink: ['/settings'],
        styleClass: 'custom-menu-item'
      }
    ];
  }

  protected currentTheme: WritableSignal<string> = signal('light');

  toggleDarkMode() {
    const element = document.querySelector('html')!;
    element.classList.toggle('dark-mode');
    this.currentTheme.set(element.classList.contains('dark-mode') ? 'dark' : 'light');
  }

  handleLogout() {
    this.authService.logout(); // Call authService.logout()
    this.isMobileMenuOpen = false; // Close mobile menu on logout
    this.router.navigate(['/login']); // Navigate to login page
  }

  isMobileMenuOpen: boolean = false;

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }
}
