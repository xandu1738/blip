import {Component, OnInit} from '@angular/core';
import {TableModule} from 'primeng/table';
import {Avatar} from 'primeng/avatar';
import {Router, RouterOutlet} from '@angular/router';
import {MenuItem} from 'primeng/api';
import {CommonService} from './components/services/commonService';
import {Events} from './components/services/events';
import {AuthService} from './components/services/auth.service';
import {LoaderService} from './components/services/loader.service';
import {NgClass} from '@angular/common';
import {Button} from 'primeng/button';
import {SubscriptionsList} from './components/Subscriptions/subscriptions-list/subscriptions-list';
import {LandingComponent} from './components/common/landing/landing.component';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {Toast} from 'primeng/toast';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    TableModule,
    Avatar,
    RouterOutlet,
    NgClass,
    Button,
    SubscriptionsList,
    LandingComponent,
    Accordion,
    AccordionPanel,
    AccordionHeader,
    AccordionContent,
    ConfirmDialog,
    Toast
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  isLoggedIn: boolean = false; // Add login state
  isLicensed: boolean = false; // Add licensed state
  showLogin: boolean = false;
  showLoader: boolean = false;
  items: any[] | undefined;

  constructor(
    protected commonService: CommonService,
    protected eventService: Events,
    protected router: Router,
    protected authService: AuthService,
    protected loaderService: LoaderService
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
        value:'0',
        label: 'Dashboard',
        icon: 'pi pi-home',
        command: () => {
          this.router.navigate(['/dashboard'])
        }
      },
      {
        value:'1',
        label: 'Configuration',
        icon: 'pi pi-briefcase',
        items: [
          {
            label: 'Partners',
            icon: 'pi pi-ticket',
            value: 0,
            command: () => {
              this.router.navigate(['/register'])
            }
          },
          {
            label: 'Modules',
            icon: 'pi pi-verified',
            value: 1,
            command: () => {
              this.router.navigate(['/configuration'])
            }
          }
        ]
      },
      {
        value:'2',
        label: 'Management',
        icon: 'pi pi-box',
        items: [
          {
            label: 'Access Management',
            icon: 'pi pi-users',
            value: 0,
            command: () => {
              this.router.navigate(['/access'])
            }
          },
          {
            label: 'Bus Booking',
            icon: 'pi pi-ticket',
            value: 1,
            command: () => {
              this.router.navigate(['//dashboard'])
            }
          },
          {
            label: 'Drivers',
            icon: 'pi pi-user',
            value: 2,
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
          {
            label: 'Licenses',
            icon: 'pi pi-check-circle',
            command: () => {
              this.router.navigate(['/manage-subscriptions'])
            }
          },
        ]
      },
      {
        value:'3',
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
        value:'4',
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
        value:'5',
        label: 'Payments',
        icon: 'pi pi-wallet',
        command: () => {
          this.router.navigate(['/payments'])
        }
      },
      {
        value:'6',
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


}
