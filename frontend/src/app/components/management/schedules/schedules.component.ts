import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseComponent } from '../../services/base-component';
import { TripService } from '../../services/trip.service';
import { LoaderService } from '../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { RemoteService } from '../../services/remoteService';
import { AccordionModule } from 'primeng/accordion';
import { FormsModule } from '@angular/forms';
import { SelectModule } from 'primeng/select';

@Component({
    selector: 'app-schedules',
    standalone: true,
    imports: [
        CommonModule,
        TableModule,
        ButtonModule,
        AccordionModule,
        FormsModule,
        SelectModule
    ],
    templateUrl: './schedules.html',
    styleUrl: './schedules.css'
})
export class SchedulesComponent extends BaseComponent implements OnInit {
    trips: any[] = [];
    totalRecords: number = 0;
    loading: boolean = false;
    rows: number = 10;

    stats: any[] = [
        { label: 'Total Trips', value: 0, icon: 'pi pi-calendar', color: 'text-blue-500', bgColor: 'bg-blue-100' },
        { label: 'Pending', value: 0, icon: 'pi pi-clock', color: 'text-orange-500', bgColor: 'bg-orange-100' },
        { label: 'Completed', value: 0, icon: 'pi pi-check-circle', color: 'text-green-500', bgColor: 'bg-green-100' }
    ];

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
                    this.updateStats();
                }
                this.loading = false;
            },
            error: () => {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load trips' });
                this.loading = false;
            }
        });
    }

    updateStats() {
        this.stats[0].value = this.totalRecords;
        this.stats[1].value = this.trips.filter(t => t.status === 'SCHEDULED').length; // Mock stats for current page
        this.stats[2].value = this.trips.filter(t => t.status === 'COMPLETED').length;
    }

    addSchedule() {
        this.router.navigate(['/schedule-form']);
    }

    editSchedule(trip: any) {
        this.router.navigate(['/schedule-form', trip.id]);
    }

    deleteSchedule(trip: any) {
        this.confirmationService.confirm({
            message: 'Are you sure you want to delete this schedule?',
            header: 'Delete Confirmation',
            icon: 'pi pi-exclamation-triangle',
            accept: () => {
                this.tripService.removeTrip(trip.id).subscribe({
                    next: (res) => {
                        if (res.returnCode === 200) {
                            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Schedule removed successfully' });
                            this.loadTrips({ first: 0, rows: this.rows });
                        } else {
                            this.messageService.add({ severity: 'error', summary: 'Error', detail: res.returnMessage });
                        }
                    }
                });
            }
        });
    }
}
