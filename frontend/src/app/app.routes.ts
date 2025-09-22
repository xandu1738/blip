import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { BusBookingComponent } from './bus-booking/bus-booking.component';
import { ParcelsComponent } from './parcels/parcels.component';
import { TrackingComponent } from './tracking/tracking.component';
import { PaymentsComponent } from './payments/payments.component';
import { ReportsComponent } from './reports/reports.component';
import { SettingsComponent } from './settings/settings.component';
import { LoginComponent } from './login/login.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent
  },
  {
    path: 'bus_booking',
    component: BusBookingComponent
  },
  {
    path: 'parcels',
    component: ParcelsComponent
  },
  {
    path: 'tracking',
    component: TrackingComponent
  },
  {
    path: 'payments',
    component: PaymentsComponent
  },
  {
    path: 'reports',
    component: ReportsComponent
  },
  {
    path: 'settings',
    component: SettingsComponent
  }
];