import { Component, OnInit } from '@angular/core';
import { BaseComponent } from '../../services/base-component';
import { ManagementService } from '../../services/management.service';
import { LoaderService } from '../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../services/auth.service';
import { RemoteService } from '../../services/remoteService';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MultiSelectModule } from 'primeng/multiselect';
import { Amenity } from '../../models/amenity.model';
import { VehicleCategory } from '../../models/vehicle-category.model';

@Component({
    selector: 'app-vehicle-categories',
    standalone: true,
    imports: [
        CommonModule,
        TableModule,
        ButtonModule,
        DialogModule,
        InputTextModule,
        FormsModule,
        MultiSelectModule
    ],
    templateUrl: './vehicle-categories.html'
})
export class VehicleCategories extends BaseComponent implements OnInit {
    categories: any[] = [];
    availableAmenities: Amenity[] = [];
    showAddDialog: boolean = false;
    newCategory: any = { name: '', description: '', amenities: [] };

    constructor(
        private managementService: ManagementService,
        loaderService: LoaderService,
        dialogService: DialogService,
        confirmationService: ConfirmationService,
        messageService: MessageService,
        authService: AuthService,
        helper: RemoteService
    ) {
        super(authService, helper, loaderService, dialogService, confirmationService, messageService);
    }

    override ngOnInit() {
        super.ngOnInit();
        this.loadCategories();
        this.loadAmenities();
    }

    loadCategories() {
        this.managementService.fetchVehicleCategories().subscribe({
            next: (res) => {
                if (res.returnObject) {
                    this.categories = res.returnObject;
                }
            }
        });
    }

    loadAmenities() {
        this.managementService.fetchAmenities().subscribe({
            next: (res) => {
                if (res.returnObject) {
                    this.availableAmenities = res.returnObject;
                }
            }
        });
    }

    openAddDialog() {
        this.newCategory = { name: '', description: '', amenities: [] };
        this.showAddDialog = true;
    }

    saveCategory() {
        if (!this.newCategory.name || !this.newCategory.description) {
            this.messageService.add({ severity: 'warn', summary: 'Validation', detail: 'Please fill all fields' });
            return;
        }

        this.managementService.addVehicleCategory(this.newCategory).subscribe({
            next: (res) => {
                if (res.returnCode === 200) {
                    this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Category added' });
                    this.showAddDialog = false;
                    this.loadCategories();
                } else {
                    this.messageService.add({ severity: 'error', summary: 'Error', detail: res.returnMessage });
                }
            }
        });
    }
}
