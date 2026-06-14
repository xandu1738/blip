import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../../../services/base-component';
import {VehicleService} from '../../../../services/vehicle.service';
import {LoaderService} from '../../../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../../../services/auth.service';
import {Router} from '@angular/router';
import {TableModule} from 'primeng/table';
import {Button} from 'primeng/button';
import {DatePipe} from '@angular/common';
import {RemoteService} from '../../../../services/remoteService';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Amenities} from '../../amenities/amenities';
import {VehicleCategories} from '../../vehicle-categories/vehicle-categories';

// Add this import

@Component({
  selector: 'app-vehicles-list',
  standalone: true,
  imports: [
    TableModule,
    Button,
    DatePipe,
    Accordion,
    AccordionContent,
    AccordionHeader,
    AccordionPanel,
    FormsModule,
    InputText,
    Amenities,
    VehicleCategories
  ],
  templateUrl: './vehicles-list.html',
  styleUrl: './vehicles-list.css'
})
export class VehiclesList extends BaseComponent implements OnInit {
  vehicles: any[] = [];
  totalRecords: number = 0;
  loading: boolean = false;
  showVehicles: boolean = true;
  showAmenities: boolean = false;
  showCategories: boolean = false;
  first: number = 0;
  rows: number = 10;
  protected vehicleData: any = {};

  constructor(
    private vehicleService: VehicleService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    authService: AuthService,
    helper: RemoteService, // Inject RemoteService
    private router: Router
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit() {
    super.ngOnInit();
  }

  loadVehicles(event: any) {
    this.loading = true;
    this.first = event.first;
    this.rows = event.rows;
    const page = event.first / event.rows;

    this.vehicleService.getVehiclesList(page, event.rows).subscribe({
      next: (response) => {
        if (response.returnObject) {
          this.vehicles = response.returnObject.rows;
          this.totalRecords = response.returnObject.totalRecords;
        }
        this.loading = false;
      },
      error: (error) => {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Failed to load vehicles'});
        this.loading = false;
      }
    });
  }

  addVehicle() {
    this.router.navigate(['/vehicle-form']);
  }

  editVehicle(vehicle: any) {
    this.router.navigate(['/vehicle-form', vehicle.id]);
  }

  protected filterVehicles() {
    //Placeholder for filtering logic
  }

  protected toggleTab(code: string) {
    switch (code) {
      case 'AMENITIES':
        this.showAmenities = true;
        this.showCategories = false;
        this.showVehicles = false;
        break;
      case 'CATEGORIES':
        this.showAmenities = false;
        this.showCategories = true;
        this.showVehicles = false;
        break;
      case 'VEHICLES':
        this.showAmenities = false;
        this.showCategories = false;
        this.showVehicles = true;
        break;
      default:
        throw new Error("Invalid management code!");
    }
  }
}
