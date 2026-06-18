import { Component, OnInit } from '@angular/core';
import { BaseComponent } from '../../../../services/base-component';
import { RouteService } from '../../../../services/route.service';
import { LoaderService } from '../../../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { Button } from 'primeng/button';
import { DatePipe, DecimalPipe } from '@angular/common';
import { RemoteService } from '../../../../services/remoteService';
import { Accordion, AccordionContent, AccordionHeader, AccordionPanel } from 'primeng/accordion';
import { FormsModule } from '@angular/forms';
import { Select } from 'primeng/select';

@Component({
    selector: 'app-routes-list',
    standalone: true,
    imports: [
        TableModule,
        Button,
        DatePipe,
        DecimalPipe,
        Accordion,
        AccordionContent,
        AccordionHeader,
        AccordionPanel,
        FormsModule,
        Select
    ],
    templateUrl: './routes-list.html',
    styleUrl: './routes-list.css'
})
export class RoutesList extends BaseComponent implements OnInit {
    routes: any[] = [];
    totalRecords: number = 10;
    loading: boolean = false;
    rows: number = 10;
    districts: any[] = [];
    filterData: any = {
        origin: '',
        destination: '',
        status: ''
    };

    stats: any[] = [
        { label: 'Total Routes', value: 0, icon: 'pi pi-map', color: 'text-blue-500', bgColor: 'bg-blue-100' },
        { label: 'Active Routes', value: 0, icon: 'pi pi-check-circle', color: 'text-green-500', bgColor: 'bg-green-100' },
        { label: 'Inactive Routes', value: 0, icon: 'pi pi-times-circle', color: 'text-red-500', bgColor: 'bg-red-100' }
    ];

    constructor(
        private routeService: RouteService,
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
        this.loadDistricts();
    }

    loadDistricts() {
        this.routeService.getDistricts().subscribe({
            next: (res) => {
                if (res.returnObject) {
                    this.districts = res.returnObject.map((d: any) => ({ label: d.name, value: d.name }));
                }
            }
        });
    }

    loadRoutes(event: any) {
        this.loading = true;
        const page = event.first / event.rows;
        const partnerCode = this.user?.partnerCode || '';

        this.routeService.getRoutesList(partnerCode, page, event.rows).subscribe({
            next: (response) => {
                if (response.returnObject) {
                    this.routes = response?.returnObject || response.returnObject.rows || [];
                    this.totalRecords = response.returnObject.totalRecords || 0;
                    this.updateStats();
                }
                this.loading = false;
            },
            error: () => {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load routes' });
                this.loading = false;
            }
        });
    }

    updateStats() {
        const total = this.totalRecords;
        const active = this.routes.filter(r => r.status === 'ACTIVE').length; // This is a bit fake since it's only current page, but for simple stats it might suffice or we need a specific stats endpoint
        this.stats[0].value = total;
        this.stats[1].value = active;
        this.stats[2].value = total - active;
    }

    addRoute() {
        this.router.navigate(['/route-form']);
    }

    editRoute(route: any) {
        this.router.navigate(['/route-form', route.id]);
    }

    filterRoutes() {
        // Basic local filtering for now or re-load with params if backend supports it
        this.loadRoutes({ first: 0, rows: this.rows });
    }
}
