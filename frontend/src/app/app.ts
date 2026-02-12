import { Component, OnInit } from '@angular/core';
import { TableModule } from 'primeng/table';
import { Avatar } from 'primeng/avatar';
import { Router, RouterOutlet } from '@angular/router';
import { Events } from './components/services/events';
import { AuthService } from './components/services/auth.service';
import { LoaderService } from './components/services/loader.service';
import { Button } from 'primeng/button';
import { SubscriptionsList } from './components/Subscriptions/subscriptions-list/subscriptions-list';
import { LandingComponent } from './components/common/landing/landing.component';
import { Accordion, AccordionContent, AccordionHeader, AccordionPanel } from 'primeng/accordion';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { Toast } from 'primeng/toast';
import { BaseComponent } from './components/services/base-component';
import { RemoteService } from './components/services/remoteService';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { SocketService } from './components/services/socket.service';
import { TruncatePipe } from './pipes/truncate-pipe';

interface MenuItem {
  value: string | number;
  label: string;
  icon?: string;
  items?: MenuItem[];
  command?: () => void;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    TableModule,
    Avatar,
    RouterOutlet,
    Button,
    SubscriptionsList,
    LandingComponent,
    Accordion,
    AccordionPanel,
    AccordionHeader,
    AccordionContent,
    ConfirmDialog,
    Toast,
    TruncatePipe
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App extends BaseComponent implements OnInit {
  isLoggedIn: boolean = false; // Add login state
  isLicensed: boolean = false; // Add license state
  items: MenuItem[] = [];

  // constructor(
  //   protected commonService: CommonService,
  //   protected eventService: Events,
  //   protected router: Router,
  //   protected authService: AuthService,
  //   protected loaderService: LoaderService
  // ) { // Inject Router and AuthService
  // }
  constructor(
    protected eventService: Events,
    protected router: Router,
    helper: RemoteService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    private socketService: SocketService,
    authService: AuthService
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit() {
    super.ngOnInit();
    // Subscribe to loader service
    this.loaderService.status.subscribe(
      (isLoading: boolean) => {
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
    this.configurePanelMenu();

    this.eventService.connect();

    this.eventService.events$.subscribe((event) => {
      console.log("Received event from Redis:", event);
    });

    console.log(this.user?.permissions);
    console.log(this.user?.permissions?.includes('MANAGE_USERS'));
  }

  configurePanelMenu() {
    this.items?.push({
      value: '0',
      label: 'Dashboard',
      icon: 'pi pi-home',
      command: () => {
        this.router.navigate(['/dashboard'])
      }
    }
    );

    let configMenu = this.getConfigMenu();
    if (!configMenu.items) {
      configMenu.items = [];
    }
    if (configMenu.items?.length > 0) {
      this.items?.push(configMenu);
    }

    if (this.user?.permissions?.includes('MANAGE_MODULES')) {
      this.items?.push({
        label: 'Modules',
        icon: 'pi pi-verified',
        value: 1,
        command: () => {
          this.router.navigate(['/configuration'])
        }
      });
    }

    let mgt = this.getManagementMenu();
    if (!mgt.items) {
      mgt.items = [];
    }
    if (mgt.items?.length > 0) {
      this.items.push(mgt);
    }

    let logistics = this.getLogisticsMenu();
    if (!logistics.items) {
      logistics.items = [];
    }
    if (logistics.items?.length > 0) {
      this.items.push(logistics);
    }

    let analytics = this.getAnalyticsMenu();
    if (!analytics.items) {
      analytics.items = [];
    }

    let payments = {
      value: '5',
      label: 'Payments',
      icon: 'pi pi-wallet',
      command: () => {
        this.router.navigate(['/payments'])
      }
    };
    if (this.user?.permissions?.includes('MANAGE_PAYMENTS')) {
      this.items.push(payments);
    }

    let settings = {
      value: '6',
      label: 'Settings',
      icon: 'pi pi-cog',
      command: () => {
        this.router.navigate(['/settings'])
      }
    }
    this.items.push(settings);
  }

  getConfigMenu() {
    let config: MenuItem = {
      value: '1',
      label: 'Configuration',
      icon: 'pi pi-briefcase',
      items: []
    };

    if (!config.items) {
      config.items = [];
    }

    if (this.user?.permissions?.includes('MANAGE_PARTNERS')) {
      config.items.push({
        label: 'Partners',
        icon: 'pi pi-ticket',
        value: 0,
        command: () => {
          this.router.navigate(['/register'])
        }
      });
    }
    return config;
  }
  getManagementMenu() {
    let management: MenuItem = {
      value: '2',
      label: 'Management',
      icon: 'pi pi-box',
      items: []
    }

    if (!management.items) {
      management.items = [];
    }

    if (this.user?.permissions?.includes('MANAGE_USERS')) {
      management.items.push({
        label: 'Access Management',
        icon: 'pi pi-users',
        value: 0,
        command: () => {
          this.router.navigate(['/access'])
        }
      });
    }


    if (this.user?.permissions?.includes('MANAGE_TICKETS')) {
      management.items.push({
        label: 'Bus Booking',
        icon: 'pi pi-ticket',
        value: 1,
        command: () => {
          this.router.navigate(['//dashboard'])
        }
      });
    }
    if (this.user?.permissions?.includes('MANAGE_DRIVERS')) {
      management.items.push({
        label: 'Drivers',
        icon: 'pi pi-user',
        value: 2,
        command: () => {
          this.router.navigate(['/drivers'])
        }
      });
    }
    if (this.user?.permissions?.includes('MANAGE_VEHICLES')) {
      management.items.push({
        value: 3,
        label: 'Vehicles',
        icon: 'pi pi-car',
        command: () => {
          this.router.navigate(['/vehicles'])
        }
      }
      )
    }
    if (this.user?.permissions?.includes('MANAGE_ROUTES')) {
      management.items.push({
        value: 4,
        label: 'Routes & Trips',
        icon: 'pi pi-map',
        command: () => {
          this.router.navigate(['/routes'])
        }
      })
    }

    if (this.user?.permissions?.includes('MANAGE_FARES')) {
      management.items.push({
        value: 5,
        label: 'Fares and Charges',
        icon: 'pi pi-megaphone',
        command: () => {
          this.router.navigate(['/dashboard'])
        }
      }
      )
    }
    if (this.user?.permissions?.includes('MANAGE_LICENSES')) {
      management.items.push({
        value: 6,
        label: 'Licenses',
        icon: 'pi pi-check-circle',
        command: () => {
          this.router.navigate(['/manage-subscriptions'])
        }
      })
    }
    return management;
  }

  getLogisticsMenu() {
    let logistics: MenuItem = {
      value: '3',
      label: 'Logistics',
      icon: 'pi pi-box',
      items: []
    };

    if (!logistics.items) {
      logistics.items = [];
    }

    if (this.user?.permissions?.includes('MANAGE_TRACKING')) {
      logistics.items.push({
        value: 0,
        label: 'Tracking',
        icon: 'pi pi-map-marker',
        command: () => {
          this.router.navigate(['/tracking'])
        }
      }
      );
    }

    if (this.user?.permissions?.includes('MANAGE_CONSIGNMENTS')) {
      logistics.items.push({
        value: 2,
        label: 'Fleets & Consignments',
        icon: 'pi pi-warehouse',
        command: () => {
          this.router.navigate(['/parcels'])
        }
      });
    }

    if (this.user?.permissions?.includes('MANAGE_PARCELS')) {
      logistics.items.push({
        value: 1,
        label: 'Parcels',
        icon: 'pi pi-box',
        command: () => {
          this.router.navigate(['/parcels'])
        }
      }
      );
    }
    return logistics;
  }

  getAnalyticsMenu() {
    let analytics: MenuItem = {
      value: '4',
      label: 'Analytics',
      icon: 'pi pi-chart-bar',
      items: []
    }

    if (!analytics.items) {
      analytics.items = [];
    }

    if (this.user?.permissions?.includes('MANAGE_REPORTS')) {
      analytics.items.push({
        value: 0,
        label: 'General Reports',
        icon: 'pi pi-receipt',
        command: () => {
          this.router.navigate(['/reports'])
        }
      })
    }

    if (this.user?.permissions?.includes('MANAGE_TRAVEL')) {
      analytics.items.push({
        value: 1,
        label: 'Travel Reports',
        icon: 'pi pi-car',
        command: () => {
          this.router.navigate(['/reports'])
        }
      })
    }

    if (this.user?.permissions?.includes('MANAGE_LOGISTICS')) {
      analytics.items.push({
        value: 2,
        label: 'Logistics Reports',
        icon: 'pi pi-shopping-bag',
        command: () => {
          this.router.navigate(['/reports'])
        }
      });
    }

    return analytics;
  }

}
