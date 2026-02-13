import { Component, OnInit } from '@angular/core';
import { BaseComponent } from '../../services/base-component';
import { ParcelService } from '../../services/parcel.service';
import { LoaderService } from '../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RemoteService } from '../../services/remoteService';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { SelectModule } from 'primeng/select';
import { InputNumberModule } from 'primeng/inputnumber';
import { CommonModule } from '@angular/common';
import { Parcel } from '../../models/parcel.model';

@Component({
    selector: 'app-parcel-form',
    standalone: true,
    imports: [
        CommonModule,
        ButtonModule,
        FormsModule,
        InputTextModule,
        TextareaModule,
        SelectModule,
        InputNumberModule
    ],
    templateUrl: './parcel-form.html',
    styleUrl: './parcel-form.css'
})
export class ParcelForm extends BaseComponent implements OnInit {
    parcel: any = {
        receiverName: '',
        receiverPhone: '',
        pickupLocation: '',
        dropOffLocation: '',
        weight: 0,
        dimensions: '',
        type: 'Standard',
        cost: 0
    };

    typeOptions = [
        { label: 'Standard', value: 'Standard' },
        { label: 'Fragile', value: 'Fragile' },
        { label: 'Express', value: 'Express' },
        { label: 'Heavy', value: 'Heavy' }
    ];

    isEditMode: boolean = false;

    constructor(
        private parcelService: ParcelService,
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
                this.loadParcel(params['id']);
            }
        });
    }

    loadParcel(id: number) {
        // Logic to load parcel if needed for edit
        this.messageService.add({ severity: 'info', summary: 'Info', detail: 'Edit mode not fully implemented yet' });
    }

    saveParcel() {
        this.parcelService.registerParcel(this.parcel).subscribe({
            next: (res) => {
                if (res.returnCode === 200) {
                    this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Parcel registered successfully' });
                    this.router.navigate(['/parcels']);
                } else {
                    this.messageService.add({ severity: 'error', summary: 'Error', detail: res.returnMessage });
                }
            },
            error: () => {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to register parcel' });
            }
        });
    }

    cancel() {
        this.router.navigate(['/parcels']);
    }
}
