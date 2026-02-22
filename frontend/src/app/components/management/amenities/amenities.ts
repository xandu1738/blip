import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../services/base-component';
import {ManagementService} from '../../services/management.service';
import {LoaderService} from '../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../services/auth.service';
import {RemoteService} from '../../services/remoteService';
import {TableModule} from 'primeng/table';
import {ButtonModule} from 'primeng/button';
import {DialogModule} from 'primeng/dialog';
import {InputTextModule} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Amenity} from '../../models/amenity.model';

@Component({
  selector: 'app-amenities',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    FormsModule
  ],
  templateUrl: './amenities.html'
})
export class Amenities extends BaseComponent implements OnInit {
  amenities: Amenity[] = [];
  showAddDialog: boolean = false;
  newAmenity: Amenity = {name: '', description: '', code: ''};

  constructor(
    protected managementService: ManagementService,
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
    this.loadAmenities();
  }

  loadAmenities() {
    this.managementService.fetchAmenities().subscribe({
      next: (res) => {
        if (res.returnObject) {
          this.amenities = res.returnObject;
        }
      }
    });
  }

  saveAmenity() {
    if (!this.newAmenity.name || !this.newAmenity.description) {
      this.messageService.add({severity: 'warn', summary: 'Validation', detail: 'Please fill all fields'});
      return;
    }

    this.managementService.addAmenity(this.newAmenity).subscribe({
      next: (res) => {
        if (res.returnCode === 200) {
          this.messageService.add({severity: 'success', summary: 'Success', detail: 'Amenity added'});
          this.showAddDialog = false;
          this.newAmenity = {name: '', description: '', code: ''};
          this.loadAmenities();
        } else {
          this.messageService.add({severity: 'error', summary: 'Error', detail: res.returnMessage});
        }
      }
    });
  }
}
