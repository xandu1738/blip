import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { BusBookingComponent } from './bus-booking/bus-booking.component';
import { ParcelsComponent } from './parcels/parcels.component';
import { TrackingComponent } from './tracking/tracking.component';
import { PaymentsComponent } from './payments/payments.component';
import { ReportsComponent } from './reports/reports.component';
import { SettingsComponent } from './settings/settings.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { authGuard } from './guards/auth.guard';
import {Configuration} from './configuration/configuration';

export const routes: Routes = [
  {
    path: 'dashboard',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'bus_booking',
    component: BusBookingComponent,
    canActivate: [authGuard]
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard]
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
    canActivate: [authGuard]
  },
  {
    path: 'modules',
    component: ParcelsComponent,
    canActivate: [authGuard]
  },
  {
    path: 'parcels',
    component: ParcelsComponent,
    canActivate: [authGuard]
  },
  {
    path: 'payments',
    component: PaymentsComponent,
    canActivate: [authGuard]
  },
  {
    path: 'reports',
    component: ReportsComponent,
    canActivate: [authGuard]
  },
  {
    path: 'tracking',
    component: TrackingComponent,
    canActivate: [authGuard]
  },
  {
    path: 'settings',
    component: SettingsComponent,
    canActivate: [authGuard]
  },
  {
    path: 'configuration',
    component: Configuration,
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
