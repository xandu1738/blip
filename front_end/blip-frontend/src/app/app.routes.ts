import { Routes } from '@angular/router';
import { Main } from './components/main/main';
import { Login } from './authentication/login/login';
import { Signup } from './authentication/signup/signup';
import { AccessLog } from './pages/access-log/access-log';
import { Booking } from './pages/booking/booking';
import { ChangeRequest } from './pages/change-request/change-request';
import { Consignment } from './pages/consignment/consignment';
import { ConsignmentParcel } from './pages/consignment-parcel/consignment-parcel';
import { DeliveryProof } from './pages/delivery-proof/delivery-proof';
import { District } from './pages/district/district';
import { FileRepository } from './pages/file-repository/file-repository';
import { Module } from './pages/module/module';
import { Notification } from './pages/notification/notification';
import { Parcel } from './pages/parcel/parcel';
import { Partner } from './pages/partner/partner';
import { Payment } from './pages/payment/payment';
import { Refund } from './pages/refund/refund';
import { Route } from './pages/route/route';
import { Schedule } from './pages/schedule/schedule';
import { Seat } from './pages/seat/seat';
import { Stop } from './pages/stop/stop';
import { Subscription } from './pages/subscription/subscription';
import { SystemDomain } from './pages/system-domain/system-domain';
import { SystemPermission } from './pages/system-permission/system-permission';
import { SystemRole } from './pages/system-role/system-role';
import { SystemRolePermissionAssignment } from './pages/system-role-permission-assignment/system-role-permission-assignment';
import { SystemUser } from './pages/system-user/system-user';
import { Ticket } from './pages/ticket/ticket';
import { Trip } from './pages/trip/trip';
import { TripSeat } from './pages/trip-seat/trip-seat';
import { Vehicle } from './pages/vehicle/vehicle';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'signup', component: Signup },
  {
    path: 'main',
    component: Main,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'access-log', component: AccessLog },
      { path: 'booking', component: Booking },
      { path: 'change-request', component: ChangeRequest },
      { path: 'consignment', component: Consignment },
      { path: 'consignment-parcel', component: ConsignmentParcel },
      { path: 'delivery-proof', component: DeliveryProof },
      { path: 'district', component: District },
      { path: 'file-repository', component: FileRepository },
      { path: 'module', component: Module },
      { path: 'notification', component: Notification },
      { path: 'parcel', component: Parcel },
      { path: 'partner', component: Partner },
      { path: 'payment', component: Payment },
      { path: 'refund', component: Refund },
      { path: 'route', component: Route },
      { path: 'schedule', component: Schedule },
      { path: 'seat', component: Seat },
      { path: 'stop', component: Stop },
      { path: 'subscription', component: Subscription },
      { path: 'system-domain', component: SystemDomain },
      { path: 'system-permission', component: SystemPermission },
      { path: 'system-role', component: SystemRole },
      { path: 'system-role-permission-assignment', component: SystemRolePermissionAssignment },
      { path: 'system-user', component: SystemUser },
      { path: 'ticket', component: Ticket },
      { path: 'trip', component: Trip },
      { path: 'trip-seat', component: TripSeat },
      { path: 'vehicle', component: Vehicle },
    ],
  },
];