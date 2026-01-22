import {Component, OnInit} from '@angular/core';
import {TableModule} from 'primeng/table';
import {OverlayBadge} from 'primeng/overlaybadge';
import {Avatar} from 'primeng/avatar';
import {Router, RouterOutlet} from '@angular/router';
import {MenuItem} from 'primeng/api';
import {CommonService} from './services/commonService';
import {Events} from './services/events';
import {AuthService} from './services/auth.service';
import {LoaderService} from './services/loader.service';
import {NgClass} from '@angular/common';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';
import {Tooltip} from 'primeng/tooltip';

@Component({
  selector: 'app-root',
  imports: [
    TableModule,
    OverlayBadge,
    Avatar,
    RouterOutlet,
    NgClass,
    Accordion,
    AccordionPanel,
    AccordionHeader,
    AccordionContent,
    Tooltip
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  items: MenuItem[] | undefined;

  constructor(
    protected commonService: CommonService,
    protected eventService: Events,
    protected router: Router,
    protected authService: AuthService,
    protected loaderService: LoaderService
  ) { // Inject Router and AuthService
  }

  ngOnInit() {
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
            value:0,
            command: () => {
              this.router.navigate(['/register'])
            }
          },
          {
            label: 'Modules & Subscriptions',
            icon: 'pi pi-verified',
            value:1,
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
            value:0,
            command: () => {
              this.router.navigate(['/users'])
            }
          },
          {
            label: 'Bus Booking',
            icon: 'pi pi-ticket',
            value:1,
            command: () => {
              this.router.navigate(['//dashboard'])
            }
          },
          {
            label: 'Drivers',
            icon: 'pi pi-user',
            value:2,
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
            label: 'Subscriptions & Licenses',
            icon: 'pi pi-check-circle',
            command: () => {
              this.router.navigate(['/manage-subscriptions'])
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
  }


}
