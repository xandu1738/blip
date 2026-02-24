import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseComponent } from '../../../services/base-component';
import { TripService } from '../../../services/trip.service';
import { LoaderService } from '../../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { RemoteService } from '../../../services/remoteService';
import { TooltipModule } from 'primeng/tooltip';

@Component({
    selector: 'app-trips-list',
    standalone: true,
    imports: [
        CommonModule,
        TableModule,
        ButtonModule,
        TooltipModule
    ],
    templateUrl: './trips-list.html'
})
export class TripsList extends BaseComponent implements OnInit {
    trips: any[] = [];
    totalRecords: number = 0;
    loading: boolean = false;
    rows: number = 10;

    constructor(
        private tripService: TripService,
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
        this.loadTrips({ first: 0, rows: this.rows });
    }

    loadTrips(event: any) {
        this.loading = true;
        const page = event.first / event.rows;

        this.tripService.getTripsList(page, event.rows).subscribe({
            next: (res) => {
                if (res.returnObject) {
                    this.trips = res.returnObject.content || [];
                    this.totalRecords = res.returnObject.totalElements || 0;
                }
                this.loading = false;
            },
            error: () => {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load trips' });
                this.loading = false;
            }
        });
    }

    editTrip(trip: any) {
        this.router.navigate(['/trip-form', trip.id]);
    }

    deleteTrip(trip: any) {
        this.confirmationService.confirm({
            message: 'Are you sure you want to delete this trip instance?',
            header: 'Delete Confirmation',
            accept: () => {
                this.tripService.removeTrip(trip.id).subscribe({
                    next: () => {
                        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Trip instance removed successfully' });
                        this.loadTrips({ first: 0, rows: this.rows });
                    }
                });
            }
        });
    }
}
