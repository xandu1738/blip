import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BaseComponent} from '../../services/base-component';
import {TripService} from '../../services/trip.service';
import {RouteService} from '../../services/route.service';
import {VehicleService} from '../../services/vehicle.service';
import {DriversService} from '../../services/drivers.service';
import {LoaderService} from '../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {RemoteService} from '../../services/remoteService';
import {ButtonModule} from 'primeng/button';
import {FormsModule} from '@angular/forms';
import {InputTextModule} from 'primeng/inputtext';
import {SelectModule} from 'primeng/select';
import {DatePickerModule} from 'primeng/datepicker';

@Component({
  selector: 'app-schedule-form',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    FormsModule,
    InputTextModule,
    SelectModule,
    DatePickerModule
  ],
  templateUrl: './schedule-form.html',
  styleUrl: './schedule-form.css'
})
export class ScheduleForm extends BaseComponent implements OnInit {
  trip: any = {
    route_id: null,
    bus_id: null,
    driver_id: null,
    trip_date: null,
    status: 'SCHEDULED'
  };

  routes: any[] = [];
  vehicles: any[] = [];
  drivers: any[] = [];
  isEditMode: boolean = false;

  constructor(
    protected tripService: TripService,
    protected routeService: RouteService,
    protected vehicleService: VehicleService,
    protected driversService: DriversService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    authService: AuthService,
    helper: RemoteService,
    protected router: Router,
    protected route: ActivatedRoute
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit() {
    super.ngOnInit();
    this.loadInitialData();
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.loadTrip(params['id']);
      }
    });
  }

  loadInitialData() {
    const partnerCode = this.user?.partnerCode || '';

    this.routeService.getRoutesList(partnerCode, 0, 100).subscribe({
      next: (res) => {
        this.routes = res?.returnObject?.map((r: any) => ({
          label: `${r.origin} -> ${r.destination}`,
          value: r.id
        })) || [];
      }
    });

    this.vehicleService.getVehiclesList(0, 100).subscribe({
      next: (res) => {
        this.vehicles = res?.returnObject?.rows.map((v: any) => ({
          label: `${v.registrationNumber} (${v.model || v.type})`,
          value: v.id
        })) || [];
      }
    });

    this.driversService.getDriversList(0, 10).subscribe({
      next: (res: any) => {
        this.drivers = res?.returnObject?.rows?.map((d: any) => ({
          label: d.name,
          value: d.id
        })) || [];
      }
    });
  }

  loadTrip(id: number) {
    this.tripService.getTripDetails(id).subscribe({
      next: (res) => {
        if (res.returnObject) {
          const data = res.returnObject;
          this.trip = {
            id: data.id,
            route_id: data.routeId,
            bus_id: data.busId,
            driver_id: data.driverId,
            trip_date: new Date(data.tripDate),
            status: data.status
          };
        }
      }
    });
  }

  saveTrip() {
    // Format date for backend YYYY-MM-DD
    const date = new Date(this.trip.trip_date);
    const formattedDate = date.toISOString().split('T')[0];

    const payload = {
      ...this.trip,
      trip_date: formattedDate
    };

    const action = this.isEditMode ?
      this.tripService.editTrip(payload) :
      this.tripService.addTrip(payload);

    action.subscribe({
      next: (res) => {
        if (res.returnCode === 200) {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: `Trip ${this.isEditMode ? 'updated' : 'added'} successfully`
          });
          this.router.navigate(['/schedules']);
        } else {
          this.messageService.add({severity: 'error', summary: 'Error', detail: res.returnMessage});
        }
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: `Failed to ${this.isEditMode ? 'update' : 'add'} trip`
        });
      }
    });
  }

  cancel() {
    this.router.navigate(['/schedules']);
  }
}
