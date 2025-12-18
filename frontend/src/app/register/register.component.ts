import {Component, HostListener, OnInit} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { LoaderService } from '../services/loader.service';
import { CreateUserRequest } from '../models/user.models';
import { TableModule } from 'primeng/table';
import { Dialog, DialogModule } from 'primeng/dialog';
import { ButtonDirective } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { RemoteService } from '../services/remoteService';
import { AccordionModule } from 'primeng/accordion';
import {FloatLabelModule} from 'primeng/floatlabel';
import { DatePickerModule } from 'primeng/datepicker';

interface Partner {
  partnerName: string;
  accountNumber: string;
  contactPerson: string;
  contactPhone: string;
  accountId: number;
  businessReference: string;
  active: boolean;
  logo: string;
  packageField: string;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    TableModule,
    ButtonDirective,
    Dialog,
    DialogModule,
    InputText,
    NgOptimizedImage,
    AccordionModule,
    FloatLabelModule,
    DatePickerModule
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent implements OnInit {
  userData: any = {};
  partners: Partner[] = [];
  filteredPartners: Partner[] = [];
  isLoading = false;
  showAddDialog = false;

  // Module form example
  newModule = {
    name: '',
    description: '',
    version: '1.0.0',
    type: 'core'
  };

  constructor(
    private authService: AuthService,
    private remoteService: RemoteService,
    private router: Router,
    private notificationService: NotificationService,
    private loaderService: LoaderService
  ) {}

  ngOnInit(): void {
    this.loadPartnersFromServer();
  }


  @HostListener('window:scroll', [])
  onScroll() {
    const tableHeader = document.querySelector('.p-datatable-thead');
    if (window.scrollY > 300) {
      tableHeader?.classList.add('sticky-active');
    } else {
      tableHeader?.classList.remove('sticky-active');
    }
  }


  loadPartnersFromServer(): void {
    this.isLoading = true;
    this.loaderService.display(true);

    this.remoteService
      .sendGetToServer(`${this.authService.apiUrl}/partners/list/0/20`)
      .subscribe({
        next: (response) => {
          this.partners = response.returnObject || [];
          this.filteredPartners = [...this.partners];
          this.notificationService.showSuccess('', 'Partners loaded successfully');
        },
        error: (error) => {
          console.error(error);
          this.notificationService.showError(error.message || 'Failed to load partners', 'Request Failed');
        },
        complete: () => {
          this.isLoading = false;
          this.loaderService.display(false);
        }
      });
  }

  // Live filter as user types
  onChangeSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    const key = input.id;
    const value = input.value.toLowerCase();

    this.userData = { ...this.userData, [key]: value };

    this.filteredPartners = this.partners.filter(partner => {
      return Object.keys(this.userData).every(k => {
        const searchValue = (this.userData[k] || '').toLowerCase();
        if (!searchValue) return true;
        return (partner[k as keyof Partner] || '').toString().toLowerCase().includes(searchValue);
      });
    });
  }

  // Optional: Refresh button to reload from backend
  filterPartners(): void {
    this.loadPartnersFromServer();
  }

  addPartner(): void {
    this.showAddDialog = true;
  }

  addModule(): void {
    // Example: just closes the dialog
    this.notificationService.showSuccess('New partner added successfully');
    this.showAddDialog = false;
  }

  clearFilters():void{

  }
}
