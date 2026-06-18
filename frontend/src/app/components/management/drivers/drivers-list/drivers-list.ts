import { Component, OnInit } from '@angular/core';
import { BaseComponent } from '../../../../services/base-component';
import { DriversService } from '../../../../services/drivers.service';
import { LoaderService } from '../../../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { Button } from 'primeng/button';
import { DatePipe } from '@angular/common';
import { RemoteService } from '../../../../services/remoteService';
import { Accordion, AccordionContent, AccordionHeader, AccordionPanel } from 'primeng/accordion';
import { FormsModule } from '@angular/forms';
import { InputText } from 'primeng/inputtext';

@Component({
    selector: 'app-drivers-list',
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
        InputText
    ],
    templateUrl: './drivers-list.html',
    styleUrl: './drivers-list.css'
})
export class DriversList extends BaseComponent implements OnInit {
    drivers: any[] = [];
    totalRecords: number = 0;
    loading: boolean = false;
    first: number = 0;
    rows: number = 10;
    protected driverData: any = {};

    constructor(
        private driversService: DriversService,
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
    }

    loadDrivers(event: any) {
        this.loading = true;
        this.first = event.first;
        this.rows = event.rows;
        const page = event.first / event.rows;

        this.driversService.getDriversList(page, event.rows).subscribe({
            next: (response) => {
                if (response.returnObject) {
                    this.drivers = response.returnObject.rows;
                    this.totalRecords = response.returnObject.totalRecords;
                }
                this.loading = false;
            },
            error: (error) => {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load drivers' });
                this.loading = false;
            }
        });
    }

    addDriver() {
        this.router.navigate(['/driver-form']);
    }

    editDriver(driver: any) {
        this.router.navigate(['/driver-form', driver.id]);
    }

    deleteDriver(driver: any) {
        this.confirmationService.confirm({
            message: 'Are you sure you want to delete this driver?',
            accept: () => {
                this.driversService.deleteDriver(driver.id).subscribe({
                    next: (res) => {
                        if (res.returnCode === 200) {
                            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Driver deleted successfully' });
                            this.loadDrivers({ first: 0, rows: this.rows });
                        } else {
                            this.messageService.add({ severity: 'error', summary: 'Error', detail: res.returnMessage });
                        }
                    },
                    error: (err) => {
                        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete driver' });
                    }
                });
            }
        });
    }

    protected filterDrivers() {
        //Placeholder for filtering logic
    }
}
