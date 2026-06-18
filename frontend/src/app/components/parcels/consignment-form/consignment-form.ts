import { Component, OnInit } from '@angular/core';
import { BaseComponent } from '../../../services/base-component';
import { ConsignmentService } from '../../../services/consignment.service';
import { VehicleService } from '../../../services/vehicle.service';
import { RouteService } from '../../../services/route.service';
import { LoaderService } from '../../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RemoteService } from '../../../services/remoteService';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-consignment-form',
    standalone: true,
    imports: [
        CommonModule,
        ButtonModule,
        FormsModule,
        InputTextModule,
        SelectModule
    ],
    templateUrl: './consignment-form.html',
    styleUrl: './consignment-form.css'
})
export class ConsignmentForm extends BaseComponent implements OnInit {
    consignment: any = {
        scheduleId: null,
        vehicleId: null,
        origin: '',
        destination: ''
    };

    vehicles: any[] = [];
    schedules: any[] = [];
    districts: any[] = [];

    constructor(
        private consignmentService: ConsignmentService,
        private vehicleService: VehicleService,
        private routeService: RouteService,
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
        this.loadVehicles();
        this.loadDistricts();
        // Assuming schedules come from another service or are just IDs for now
    }

    loadVehicles() {
        this.vehicleService.getVehiclesList(0, 100).subscribe({
            next: (res: any) => {
                this.vehicles = res?.returnObject?.rows.map((v: any) => ({
                    label: `${v.registrationNumber} (${v.model})`,
                    value: v.id
                }));
            }
        });
    }

    loadDistricts() {
        this.routeService.getDistricts().subscribe({
            next: (res: any) => {
                this.districts = res.returnObject.map((d: any) => ({
                    label: d.name,
                    value: d.name
                }));
            }
        });
    }

    saveConsignment() {
        this.consignmentService.createConsignment(this.consignment).subscribe({
            next: (res) => {
                if (res.returnCode === 200) {
                    this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Consignment created successfully' });
                    this.router.navigate(['/parcels']);
                } else {
                    this.messageService.add({ severity: 'error', summary: 'Error', detail: res.returnMessage });
                }
            },
            error: () => {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to create consignment' });
            }
        });
    }

    cancel() {
        this.router.navigate(['/parcels']);
    }
}
