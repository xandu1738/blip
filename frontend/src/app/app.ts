import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router'; // Import Router
import {CommonService} from './services/commonService';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './login/login.component'; // Import LoginComponent
import { AuthService } from './services/auth.service'; // Import AuthService
import { MegaMenuModule } from 'primeng/megamenu'; // Import MegaMenuModule
import { MegaMenuItem } from 'primeng/api'; // Import MegaMenuItem

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MegaMenuModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  isLoggedIn: boolean = false; // Add login state
  items: MegaMenuItem[] | undefined;

  constructor(protected commonService: CommonService, private router: Router, private authService: AuthService) { // Inject Router and AuthService
  }

  ngOnInit() {
    this.showLoader = this.commonService.showLoader;
    this.authService.isLoggedIn.subscribe((loggedIn: boolean) => {
      this.isLoggedIn = loggedIn;
    });

    this.items = [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        routerLink: ['/dashboard']
      },
      {
        label: 'Bus Booking',
        icon: 'pi pi-ticket',
        routerLink: ['/bus_booking']
      },
      {
        label: 'Parcels',
        icon: 'pi pi-box',
        routerLink: ['/parcels']
      },
      {
        label: 'Tracking',
        icon: 'pi pi-map-marker',
        routerLink: ['/tracking']
      },
      {
        label: 'Payments',
        icon: 'pi pi-wallet',
        routerLink: ['/payments']
      },
      {
        label: 'Reports',
        icon: 'pi pi-chart-bar',
        routerLink: ['/reports']
      },
      {
        label: 'Settings',
        icon: 'pi pi-cog',
        routerLink: ['/settings']
      }
    ];
  }

  protected currentTheme: WritableSignal<string> = signal('light');
  showLoader: any;

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
