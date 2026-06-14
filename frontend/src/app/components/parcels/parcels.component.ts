import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { BaseComponent } from '../../services/base-component';
import { ParcelService } from '../../services/parcel.service';
import { ConsignmentService } from '../../services/consignment.service';
import { LoaderService } from '../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../services/auth.service';
import { RemoteService } from '../../services/remoteService';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { AccordionModule } from 'primeng/accordion';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { TabsModule } from 'primeng/tabs';
import { SelectModule } from 'primeng/select';
import { Router } from '@angular/router';
import { AssignParcelDialog } from './assign-parcel-dialog/assign-parcel-dialog';

@Component({
  selector: 'app-parcels',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    AccordionModule,
    FormsModule,
    InputTextModule,
    TabsModule,
    DecimalPipe,
    SelectModule
  ],
  templateUrl: './parcels.html',
  styleUrl: './parcels.css'
})
export class ParcelsComponent extends BaseComponent implements OnInit {
  parcels: any[] = [];
  consignments: any[] = [];
  parcelStats: any = {};
  consignmentStats: any = {};

  loadingParcels: boolean = false;
  loadingConsignments: boolean = false;

  filterData: any = {
    parcelStatus: null,
    consignmentStatus: null,
    searchQuery: ''
  };

  statusOptions = [
    { label: 'Registered', value: 'registered' },
    { label: 'Assigned', value: 'assigned' },
    { label: 'Delivered', value: 'delivered' }
  ];

  consignmentStatusOptions = [
    { label: 'Created', value: 'created' },
    { label: 'In Transit', value: 'in_transit' },
    { label: 'Delivered', value: 'delivered' }
  ];

  constructor(
    private parcelService: ParcelService,
    private consignmentService: ConsignmentService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    authService: AuthService,
    helper: RemoteService,
    private router: Router
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit() {
    super.ngOnInit();
    this.loadAllData();
  }

  loadAllData() {
    this.loadParcels();
    this.loadConsignments();
    this.loadStats();
  }

  loadParcels() {
    this.loadingParcels = true;
    this.parcelService.getParcelsList().subscribe({
      next: (res) => {
        this.parcels = res.returnObject;
        this.loadingParcels = false;
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load parcels' });
        this.loadingParcels = false;
      }
    });
  }

  loadConsignments() {
    this.loadingConsignments = true;
    this.consignmentService.getConsignmentsList().subscribe({
      next: (res) => {
        this.consignments = res.returnObject;
        this.loadingConsignments = false;
      },
      error: () => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load consignments' });
        this.loadingConsignments = false;
      }
    });
  }

  loadStats() {
    this.parcelService.getStats().subscribe({
      next: (res) => this.parcelStats = res.returnObject
    });
    this.consignmentService.getStats().subscribe({
      next: (res) => this.consignmentStats = res.returnObject
    });
  }

  registerParcel() {
    this.router.navigate(['/parcels/register']);
  }

  createConsignment() {
    this.router.navigate(['/parcels/consignment/new']);
  }

  assignParcel(parcel: any) {
    const ref = this.dialogService.open(AssignParcelDialog, {
      header: 'Assign Parcel to Consignment',
      width: '500px',
      data: { parcel }
    });

    if (ref) {
      ref.onClose.subscribe((result) => {
        if (result) {
          this.loadAllData();
        }
      });
    }
  }

  viewConsignment(con: any) {
    this.router.navigate(['/parcels/consignment', con.id]);
  }

  filterDataAction() {
    this.loadAllData();
  }
}
