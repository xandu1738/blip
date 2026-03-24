import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { BusBookingComponent } from './components/bus-booking/bus-booking.component';
import { ParcelsComponent } from './components/parcels/parcels.component';
import { TrackingComponent } from './components/tracking/tracking.component';
import { PaymentsComponent } from './components/payments/payments.component';
import { ReportsComponent } from './components/reports/reports.component';
import { SettingsComponent } from './components/settings/settings.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { authGuard } from './guards/auth.guard';
import { Configuration } from './components/configuration/configuration';
import { Users } from './components/user-management/users/users';
import { SubscriptionsList } from './components/subscriptions/subscriptions-list/subscriptions-list';
import { licenseGuard } from './guards/license-guard';
import { subscriptionsGuard } from './guards/subscriptions-guard';
import { Subscriptions } from './components/subscriptions/subscriptions/subscriptions';
import { AccessManagement } from './components/common/access-management/access-management';
import { UserForm } from './components/user-management/user-form/user-form';
import { VehiclesList } from './components/management/vehicles/vehicles-list/vehicles-list';
import { VehicleForm } from './components/management/vehicles/vehicle-form/vehicle-form';
import { DriversList } from './components/management/drivers/drivers-list/drivers-list';
import { DriverForm } from './components/management/drivers/driver-form/driver-form';
import { RoutesList } from './components/management/routes/routes-list/routes-list';
import { RouteForm } from './components/management/routes/route-form/route-form';
import { ParcelForm } from './components/parcels/parcel-form/parcel-form';
import { ConsignmentForm } from './components/parcels/consignment-form/consignment-form';
import { ConsignmentDetails } from './components/parcels/consignment-details/consignment-details';
import { SchedulesComponent } from './components/management/schedules/schedules.component';
import { ScheduleForm } from './components/management/schedule-form/schedule-form';
import { Amenities } from './components/management/amenities/amenities';
import { VehicleCategories } from './components/management/vehicle-categories/vehicle-categories';
import { TripsList } from './components/management/trips/trips-list/trips-list';
import { TripForm } from './components/management/trips/trip-form/trip-form';

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
    path: 'access',
    component: AccessManagement
  },
  {
    path: 'user-form',
    component: UserForm
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
    path: 'vehicles',
    component: VehiclesList,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'vehicle-form',
    component: VehicleForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'vehicle-form/:id',
    component: VehicleForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'drivers',
    component: DriversList,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'schedules',
    component: SchedulesComponent,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'schedule-form',
    component: ScheduleForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'schedule-form/:id',
    component: ScheduleForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'trips',
    component: TripsList,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'trip-form',
    component: TripForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'trip-form/:id',
    component: TripForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'record-trip/:scheduleId',
    component: TripForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'driver-form',
    component: DriverForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'driver-form/:id',
    component: DriverForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'routes',
    component: RoutesList,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'route-form',
    component: RouteForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'route-form/:id',
    component: RouteForm,
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
    path: 'parcels/register',
    component: ParcelForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'parcels/consignment/new',
    component: ConsignmentForm,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'parcels/consignment/:id',
    component: ConsignmentDetails,
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
    path: 'amenities',
    component: Amenities,
    canActivate: [authGuard, licenseGuard]
  },
  {
    path: 'vehicle-categories',
    component: VehicleCategories,
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
