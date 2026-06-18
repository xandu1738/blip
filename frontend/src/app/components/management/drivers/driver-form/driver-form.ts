import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../../../services/base-component';
import {DriversService} from '../../../../services/drivers.service';
import {LoaderService} from '../../../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService, PrimeTemplate} from 'primeng/api';
import {AuthService} from '../../../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {RemoteService} from '../../../../services/remoteService';
import {Button} from 'primeng/button';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Driver} from '../../../models/driver.model';
import {AutoComplete} from 'primeng/autocomplete';

@Component({
  selector: 'app-driver-form',
  standalone: true,
  imports: [
    Button,
    FormsModule,
    InputText,
    AutoComplete,
    PrimeTemplate
  ],
  templateUrl: './driver-form.html',
  styleUrl: './driver-form.css'
})
export class DriverForm extends BaseComponent implements OnInit {
  driver: Driver = {
    name: '',
    license_number: '',
    contact_number: '',
    status: 'ACTIVE'
  };

  isEditMode: boolean = false;
  filteredStatuses: any[] = [];
  driverStatuses: any[] = [
    {label: 'Active', value: 'ACTIVE'},
    {label: 'Inactive', value: 'INACTIVE'},
    {label: 'Deleted', value: 'DELETED'}
  ];

  constructor(
    private driversService: DriversService,
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

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.loadDriver(params['id']);
      }
    });
  }

  loadDriver(id: number) {
    this.driversService.fetchDriverDetails(id).subscribe({
      next: (res) => {
        if (res.returnObject) {
          // Map backend response fields to frontend model if necessary
          // Backend DriverModel: name, licenseNumber, contactNumber, status
          // Frontend Driver: name, license_number, contact_number, status
          // If backend returns camelCase, we might need mapping or update frontend model to match backend strictly.
          // Usually Spring Boot returns generic JSON. Let's assume it returns camelCase.
          // But my Driver model uses snake_case for some fields.
          // Wait, VehicleModel had snake_case in TS but camelCase in Java?
          // VehicleModel.java: registrationNumber (camel)
          // VehicleModel.ts: registration_number (snake)
          // VehicleService.ts: uses data.get(REGISTRATION_NUMBER) where REGISTRATION_NUMBER="registration_number"
          // So backend EXPECTS snake_case in JSON input.
          // And returns what? "savedVehicle" which is the Entity.
          // Jackson usually serializes camelCase fields as camelCase unless configured otherwise.
          // Let's check VehicleService.addNewVehicle returns "savedVehicle".
          // If Jackson is default, it returns "registrationNumber".
          // But VehicleModel.ts has "registration_number".
          // This implies the frontend might be using different field names than what backend returns, OR backend is configured to snake_case.

          // Let's check DriverService.addNewDriver.
          // It reads "name", "license_number", "contact_number".
          // So it expects snake_case inputs.

          // It returns savedDriver (Entity).
          // Entity has "name", "licenseNumber", "contactNumber".
          // So response will likely have camelCase.

          // If I map res.returnObject (camelCase) to this.driverWithSnakeCase, it won't work automatically if names mismatch.
          // I should probably manually map or ensure my frontend model matches backend response for display,
          // AND matches backend expectation for input.

          // Quick fix: Use `any` or generic object for `driver` to avoid strict type issues,
          // or manually map.
          // But wait, `VehicleForm` used `this.vehicle = res.returnObject`.
          // If `Vehicle` interface has snake_case, and `res.returnObject` has camelCase,
          // `this.vehicle.registration_number` would be undefined if `res` has `registrationNumber`.

          // Looking at `VehicleModel.java`: @Column(name = "registration_number") private String registrationNumber;
          // Jackson uses getter names by default (getRegistrationNumber -> registrationNumber).

          // Looking at `VehicleService.java`:
          // private static final String REGISTRATION_NUMBER = "registration_number";
          // data.get(REGISTRATION_NUMBER).asText();

          // So INPUT must be snake_case.

          // I should verify if the project uses a global Jackson config or `@JsonProperty`.
          // I don't see `@JsonProperty` in `VehicleModel.java`.
          // Maybe `PropertyNamingStrategy` is set in headers or config.

          // Anyway, for `DriverForm`, I will prepare the object for saving.
          // And for loading, I will assume the response matches.
          // If the previous code worked, there must be a mechanism (or `vehicle.model.ts` is ignored/loose).

          let data = res.returnObject;
          this.driver = {
            id: data.id,
            name: data.name,
            license_number: data.licenseNumber || data.license_number,
            contact_number: data.contactNumber || data.contact_number,
            status: data.status,
            partner_code: data.partnerCode || data.partner_code,
            created_at: data.createdAt || data.created_at,
            created_by: data.createdBy || data.created_by
          };
        }
      },
      error: (err) => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Failed to load driver details'});
      }
    });
  }

  saveDriver() {
    // Prepare payload with snake_case for backend
    // My Driver model ID `license_number` matches what backend expects in `DriverService`.

    const statusValue = typeof this.driver.status === 'object' ? (this.driver.status as any).value : this.driver.status;

    const payload: Driver = {
      ...this.driver,
      status: statusValue
      // If `this.driver` has camelCase values from load, we need to ensure they are mapped to snake_case for sending?
      // Wait, `this.driver` is bound to inputs.
      // `ngModel` will map to `driver.name`, `driver.license_number`, etc.
      // So `this.driver` will have the correct structure for sending.
    };

    if (this.isEditMode) {
      // Payload needs `driver_id` for edit.
      // DriverService expects `data.get(DRIVER_ID)` -> "driver_id"
      // My payload has `id`. I should map it or add `driver_id`.
      const editPayload = {
        ...payload,
        driver_id: this.driver.id
      };

      this.driversService.editDriver(editPayload).subscribe({
        next: (res) => {
          if (res.returnCode === 200) {
            this.messageService.add({severity: 'success', summary: 'Success', detail: 'Driver updated successfully'});
            this.router.navigate(['/drivers']);
          } else {
            this.messageService.add({severity: 'error', summary: 'Error', detail: res.returnMessage});
          }
        },
        error: (err) => {
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Failed to update driver'});
        }
      });
      return;
    }

    this.driversService.addNewDriver(payload).subscribe({
      next: (res) => {
        if (res.returnCode === 200) {
          this.messageService.add({severity: 'success', summary: 'Success', detail: 'Driver added successfully'});
          this.router.navigate(['/drivers']);
        } else {
          this.messageService.add({severity: 'error', summary: 'Error', detail: res.returnMessage});
        }
      },
      error: (err) => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Failed to add driver'});
      }
    });
  }

  search($event: any) {
    this.filteredStatuses = $event.query ?
      this.driverStatuses.filter(
        (status: any) =>
          status.label.toLowerCase().indexOf($event.query.toLowerCase()) === 0) :
      [...this.driverStatuses];
  }

  cancel() {
    this.router.navigate(['/drivers']);
  }
}
