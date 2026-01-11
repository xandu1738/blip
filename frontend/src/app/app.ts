import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {CommonModule} from '@angular/common';
import {CommonService} from './services/commonService';
import {LoginComponent} from './login/login.component';
import {AuthService} from './services/auth.service';
import {LoaderService} from './services/loader.service';
import {MegaMenuModule} from 'primeng/megamenu';
import {Menubar} from 'primeng/menubar';
import {ToastModule} from 'primeng/toast';
import {MenuItem} from 'primeng/api';
import {TableModule} from 'primeng/table';
import {ButtonDirective} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {Events} from './services/events';
import {SubscriptionsList} from './Subscriptions/subscritions-list/subscriptions-list';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MegaMenuModule, ToastModule, Menubar, TableModule, CommonModule, LoginComponent, ButtonDirective, Ripple, SubscriptionsList],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  isLoggedIn: boolean = false; // Add login state
  isLicensed: boolean = false; // Add login state
  items: MenuItem[] | undefined;
  showLoader: boolean = false;

  constructor(
    protected commonService: CommonService,
    private eventService: Events,
    private router: Router,
    protected authService: AuthService,
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
      console.log('App component: Login state changed to:', loggedIn);
      this.isLoggedIn = loggedIn;
    });

    this.authService.licensed.subscribe((license: string) => {
      if (license !== 'ACTIVE') {
        this.isLicensed = true;
        return;
      }
      this.isLicensed = false;
    });

    this.items = [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        command: () => {
          this.router.navigate(['/dashboard'])
        }
      },
      {
        label: 'Configuration',
        icon: 'pi pi-briefcase',
        items: [
          {
            label: 'Partners',
            icon: 'pi pi-ticket',
            command: () => {
              this.router.navigate(['/register'])
            }
          },
          {
            label: 'Modules & Subscriptions',
            icon: 'pi pi-verified',
            command: () => {
              this.router.navigate(['/configuration'])
            }
          }
        ]
      },
      {
        label: 'Management',
        icon: 'pi pi-box',
        items: [
          {
            label: 'Access Users',
            icon: 'pi pi-users',
            command: () => {
              this.router.navigate(['/users'])
            }
          },
          {
            label: 'Bus Booking',
            icon: 'pi pi-ticket',
            command: () => {
              this.router.navigate(['//dashboard'])
            }
          },
          {
            label: 'Drivers',
            icon: 'pi pi-user',
            command: () => {
              this.router.navigate(['/dashboard'])
            }
          },
          {
            label: 'Vehicles',
            icon: 'pi pi-car',
            command: () => {
              this.router.navigate(['/dashboard'])
            }
          },
          {
            label: 'Routes & Trips',
            icon: 'pi pi-map',
            command: () => {
              this.router.navigate(['/dashboard'])
            }
          },
          {
            label: 'Fares and Charges',
            icon: 'pi pi-megaphone',
            command: () => {
              this.router.navigate(['/dashboard'])
            }
          },
        ]
      },
      {
        label: 'Logistics',
        icon: 'pi pi-box',
        items: [
          {
            label: 'Tracking',
            icon: 'pi pi-map-marker',
            command: () => {
              this.router.navigate(['/tracking'])
            }
          },
          {
            label: 'Parcels',
            icon: 'pi pi-gift',
            command: () => {
              this.router.navigate(['/parcels'])
            }
          },
          {
            label: 'Fleets & Consignments',
            icon: 'pi pi-warehouse',
            command: () => {
              this.router.navigate(['/parcels'])
            }
          },
        ]
      },
      {
        label: 'Payments',
        icon: 'pi pi-wallet',
        command: () => {
          this.router.navigate(['/payments'])
        }
      },
      {
        label: 'Analytics',
        icon: 'pi pi-chart-bar',
        items: [
          {
            label: 'General Reports',
            icon: 'pi pi-receipt',
            command: () => {
              this.router.navigate(['/reports'])
            }
          },
          {
            label: 'Travel Reports',
            icon: 'pi pi-car',
            command: () => {
              this.router.navigate(['/reports'])
            }
          },
          {
            label: 'Logistics Reports',
            icon: 'pi pi-shopping-bag',
            command: () => {
              this.router.navigate(['/reports'])
            }
          }
        ]
      },
      {
        label: 'Settings',
        icon: 'pi pi-cog',
        command: () => {
          this.router.navigate(['/settings'])
        }
      }
    ];

    this.eventService.connect();

    this.eventService.events$.subscribe((event) => {
      console.log("Received event from Redis:", event);
    });
  }

  protected currentTheme: WritableSignal<string> = signal('light');

  toggleDarkMode() {
    const element = document.querySelector('html')!;
    element.classList.toggle('dark-mode');
    this.currentTheme.set(element.classList.contains('dark-mode') ? 'dark' : 'light');
  }

  handleLogout() {
    this.authService.logout();
    this.isMobileMenuOpen = false;
    this.router.navigate(['/login']);
  }

  isMobileMenuOpen: boolean = false;

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }
}
