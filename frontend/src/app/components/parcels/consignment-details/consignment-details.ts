import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { BaseComponent } from '../../../services/base-component';
import { ConsignmentService } from '../../../services/consignment.service';
import { LoaderService } from '../../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RemoteService } from '../../../services/remoteService';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';

@Component({
    selector: 'app-consignment-details',
    standalone: true,
    imports: [CommonModule, TableModule, ButtonModule, DecimalPipe],
    templateUrl: './consignment-details.html',
    styleUrl: './consignment-details.css'
})
export class ConsignmentDetails extends BaseComponent implements OnInit {
    consignment: any = null;
    parcels: any[] = [];
    loading: boolean = false;

    constructor(
        private consignmentService: ConsignmentService,
        loaderService: LoaderService,
        dialogService: DialogService,
        confirmationService: ConfirmationService,
        messageService: MessageService,
        authService: AuthService,
        helper: RemoteService,
        private route: ActivatedRoute,
        private router: Router
    ) {
        super(authService, helper, loaderService, dialogService, confirmationService, messageService);
    }

    override ngOnInit() {
        super.ngOnInit();
        this.route.params.subscribe(params => {
            if (params['id']) {
                this.loadDetails(params['id']);
            }
        });
    }

    loadDetails(id: number) {
        this.loading = true;
        this.consignmentService.getConsignmentDetails(id).subscribe({
            next: (res) => {
                this.consignment = res.returnObject.consignment;
                this.parcels = res.returnObject.parcels;
                this.loading = false;
            },
            error: () => {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load consignment details' });
                this.loading = false;
            }
        });
    }

    back() {
        this.router.navigate(['/parcels']);
    }
}
