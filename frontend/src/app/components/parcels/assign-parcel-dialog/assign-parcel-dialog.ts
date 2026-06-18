import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ConsignmentService } from '../../../services/consignment.service';
import { MessageService } from 'primeng/api';
import { TextareaModule } from 'primeng/textarea';

@Component({
    selector: 'app-assign-parcel-dialog',
    standalone: true,
    imports: [CommonModule, ButtonModule, SelectModule, FormsModule, TextareaModule],
    templateUrl: './assign-parcel-dialog.html'
})
export class AssignParcelDialog implements OnInit {
    parcel: any;
    consignments: any[] = [];
    selectedConsignmentId: number | null = null;
    remarks: string = '';
    loading: boolean = false;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private consignmentService: ConsignmentService,
        private messageService: MessageService
    ) {
        this.parcel = this.config.data.parcel;
    }

    ngOnInit() {
        this.loadConsignments();
    }

    loadConsignments() {
        this.consignmentService.getConsignmentsList().subscribe({
            next: (res) => {
                this.consignments = res.returnObject
                    .filter((c: any) => c.status === 'created') // Only created consignments
                    .map((c: any) => ({
                        label: `${c.consignmentNumber} (${c.origin} -> ${c.destination})`,
                        value: c.id
                    }));
            }
        });
    }

    assign() {
        if (!this.selectedConsignmentId) {
            this.messageService.add({ severity: 'warn', summary: 'Warning', detail: 'Please select a consignment' });
            return;
        }

        this.loading = true;
        const payload = {
            consignmentId: this.selectedConsignmentId,
            parcelId: this.parcel.id,
            remarks: this.remarks
        };

        this.consignmentService.assignParcel(payload).subscribe({
            next: (res) => {
                this.loading = false;
                if (res.returnCode === 200) {
                    this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Parcel assigned successfully' });
                    this.ref.close(true);
                } else {
                    this.messageService.add({ severity: 'error', summary: 'Error', detail: res.returnMessage });
                }
            },
            error: () => {
                this.loading = false;
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to assign parcel' });
            }
        });
    }

    cancel() {
        this.ref.close();
    }
}
