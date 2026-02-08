import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../../services/base-component';
import {VehicleService} from '../../../services/vehicle.service';
import {LoaderService} from '../../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {RemoteService} from '../../../services/remoteService';
import {Button} from 'primeng/button';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Vehicle, VehicleType} from '../../../models/vehicle.model';
import {InputNumberModule} from 'primeng/inputnumber';
import {AutoComplete} from 'primeng/autocomplete';

@Component({
  selector: 'app-vehicle-form',
  standalone: true,
  imports: [
    Button,
    FormsModule,
    InputText,
    InputNumberModule,
    AutoComplete
  ],
  templateUrl: './vehicle-form.html',
  styleUrl: './vehicle-form.css'
})
export class VehicleForm extends BaseComponent implements OnInit {
  vehicle: Vehicle = {
    registration_number: '',
    capacity: 0,
    type: VehicleType.BUS,
    status: 'ACTIVE'
  };

  isEditMode: boolean = false;
  vehicleTypes: any[] = [];
  filteredStatuses: any[] = [];
  filteredTypes: any[] = [];
  vehicleStatuses: any[] = [
    {label: 'Active', value: 'ACTIVE'},
    {label: 'Inactive', value: 'INACTIVE'},
    {label: 'Maintenance', value: 'MAINTENANCE'}
  ];

  constructor(
    private vehicleService: VehicleService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    authService: AuthService,
    helper: RemoteService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit() {
    super.ngOnInit();
    this.initializeVehicleTypes();

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.loadVehicle(params['id']);
      }
    });
  }

  initializeVehicleTypes() {
    this.vehicleTypes = Object.keys(VehicleType).map(key => ({
      label: key,
      value: VehicleType[key as keyof typeof VehicleType]
    }));
  }

  loadVehicle(id: number) {
    this.vehicleService.fetchVehicleDetails(id).subscribe({
      next: (res) => {
        if (res.returnObject) {
          this.vehicle = res.returnObject;
        }
      },
      error: (err) => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Failed to load vehicle details'});
      }
    });
  }

  saveVehicle() {
    if (this.isEditMode) {
      this.vehicleService.editVehicleInformation(this.vehicle).subscribe({
        next: (res) => {
          if (res.returnCode === 200) {
            this.messageService.add({severity: 'success', summary: 'Success', detail: 'Vehicle updated successfully'});
            this.router.navigate(['/vehicles']);
          } else {
            this.messageService.add({severity: 'error', summary: 'Error', detail: res.returnMessage});
          }
        },
        error: (err) => {
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Failed to update vehicle'});
        }
      });
      return;
    }

    this.vehicleService.addNewVehicle(this.vehicle).subscribe({
      next: (res) => {
        if (res.returnCode === 200) {
          this.messageService.add({severity: 'success', summary: 'Success', detail: 'Vehicle added successfully'});
          this.router.navigate(['/vehicles']);
        } else {
          this.messageService.add({severity: 'error', summary: 'Error', detail: res.returnMessage});
        }
      },
      error: (err) => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Failed to add vehicle'});
      }
    });

  }

  search($event: any) {
    this.filteredStatuses = $event.query ?
      this.vehicleStatuses.filter(
        (domain: any) =>
          domain.label.toLowerCase().indexOf($event.query.toLowerCase()) === 0) :
      [...this.vehicleStatuses];
  }

  searchVehicleTypes($event: any) {
    this.filteredTypes = $event.query ?
      this.vehicleTypes.filter(
        (domain: any) =>
          domain.label.toLowerCase().indexOf($event.query.toLowerCase()) === 0) :
      [...this.vehicleTypes];
  }

  cancel() {
    this.router.navigate(['/vehicles']);
  }
}
