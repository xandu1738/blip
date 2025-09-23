import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {CommonService} from './services/commonService';
import {AuthService} from './services/auth.service';
import {MegaMenuModule} from 'primeng/megamenu';
import {MenuItem} from 'primeng/api';
import {Menubar} from 'primeng/menubar';
import {LoginComponent} from './login/login.component';
import {ButtonDirective} from 'primeng/button';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MegaMenuModule, Menubar, LoginComponent, ButtonDirective],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  isLoggedIn: boolean = false; // Add login state
  items: MenuItem[] | undefined;

  constructor(protected commonService: CommonService, private router: Router, private authService: AuthService) {
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
        command: () =>{this.router.navigate(['/dashboard'])}
      },
      {
        label: 'Configuration',
        icon: 'pi pi-spinner',
        items:[
          {
            label: 'Partners',
            icon: 'pi pi-ticket',
            command: () =>{this.router.navigate(['/bus-booking'])}
          },
          {
            label: 'Modules & Subscriptions',
            icon: 'pi pi-verified',
            command: () =>{this.router.navigate(['/bus-booking'])}
          }
        ]
      },
      {
        label: 'Management',
        icon: 'pi pi-box',
        items:[
          {
            label: 'Bus Booking',
            icon: 'pi pi-ticket',
            command: () =>{this.router.navigate(['/bus-booking'])}
          },
          {
            label: 'Drivers',
            icon: 'pi pi-user',
            command: () =>{this.router.navigate([''])}
          },
          {
            label:'Vehicles',
            icon: 'pi pi-car',
            command: () =>{this.router.navigate([''])}
          },
          {
            label:'Routes & Trips',
            icon: 'pi pi-map',
            command: () =>{this.router.navigate([''])}
          },
          {
            label:'Fares and Charges',
            icon: 'pi pi-megaphone',
            command: () =>{this.router.navigate([''])}
          },
        ]
      },
      {
        label: 'Logistics',
        icon: 'pi pi-box',
        items:[
          {
            label: 'Tracking',
            icon: 'pi pi-map-marker',
            command: () =>{this.router.navigate(['/tracking'])}
          },
          {
            label:'Parcels',
            icon: 'pi pi-gift',
            command: () =>{this.router.navigate(['/parcels'])}
          },
          {
            label:'Fleets & Consignments',
            icon: 'pi pi-warehouse',
            command: () =>{this.router.navigate(['/parcels'])}
          },
        ]
      },
      {
        label: 'Payments',
        icon: 'pi pi-wallet',
        command: () =>{this.router.navigate(['/payments'])}
      },
      {
        label: 'Analytics',
        icon: 'pi pi-chart-bar',
        items:[
          {
            label:'General Reports',
            icon: 'pi pi-receipt',
            command: () =>{this.router.navigate(['/reports'])}
          },
          {
            label:'Travel Reports',
            icon: 'pi pi-car',
            command: () =>{this.router.navigate(['/reports'])}
          },
          {
            label:'Logistics Reports',
            icon: 'pi pi-shopping-bag',
            command: () =>{this.router.navigate(['/reports'])}
          }
        ]
      },
      {
        label: 'Settings',
        icon: 'pi pi-cog',
        command: () =>{this.router.navigate(['/settings'])}
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
