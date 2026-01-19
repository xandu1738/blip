import {Routes} from '@angular/router';
import {DashboardComponent} from './dashboard/dashboard.component';
import {BusBookingComponent} from './bus-booking/bus-booking.component';
import {ParcelsComponent} from './parcels/parcels.component';
import {TrackingComponent} from './tracking/tracking.component';
import {PaymentsComponent} from './payments/payments.component';
import {ReportsComponent} from './reports/reports.component';
import {SettingsComponent} from './settings/settings.component';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {authGuard} from './guards/auth.guard';
import {Configuration} from './configuration/configuration';
import {Users} from './UserManagement/users/users';
import {SubscriptionsList} from './Subscriptions/subscriptions-list/subscriptions-list';
import {licenseGuard} from './guards/license-guard';
import {subscriptionsGuard} from './guards/subscriptions-guard';
import {Subscriptions} from './Subscriptions/subscriptions/subscriptions';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'bus_booking',
    component: BusBookingComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'partners',
    component: ParcelsComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'users',
    component: Users,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'modules',
    component: ParcelsComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'parcels',
    component: ParcelsComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'payments',
    component: PaymentsComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'reports',
    component: ReportsComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'tracking',
    component: TrackingComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'settings',
    component: SettingsComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'configuration',
    component: Configuration,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'subscriptions',
    canActivate: [authGuard, subscriptionsGuard],
    component: SubscriptionsList
  },
  {
    path: 'manage-subscriptions',
    canActivate: [authGuard],
    component: Subscriptions
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
