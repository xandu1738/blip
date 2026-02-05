import {Component, HostListener, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {NotificationService} from '../services/notification.service';
import {LoaderService} from '../services/loader.service';
import {TableModule} from 'primeng/table';
import {Dialog, DialogModule} from 'primeng/dialog';
import {Button} from 'primeng/button';
import {InputText} from 'primeng/inputtext';
import {RemoteService} from '../services/remoteService';
import {AccordionModule} from 'primeng/accordion';
import {FloatLabelModule} from 'primeng/floatlabel';
import {DatePickerModule} from 'primeng/datepicker';
import {AutoComplete} from 'primeng/autocomplete';
import {FileUpload} from 'primeng/fileupload';
import {BaseComponent} from '../services/base-component';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {ApiResponse} from '../models/user.models';
import {Tooltip} from 'primeng/tooltip';

interface Partner {
  // "partner_name", "account_number", "contact_person", "contact_phone", "account_id", "business_reference", "active", "package"
  partner_name: string;
  account_number: string;
  contact_person: string;
  contact_phone: string;
  account_id: number;
  business_reference: string;
  active: boolean;
  logo: string;
  bio: string;
  package: string;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    TableModule,
    Dialog,
    DialogModule,
    InputText,
    NgOptimizedImage,
    AccordionModule,
    FloatLabelModule,
    DatePickerModule,
    AutoComplete,
    Button,
    FileUpload,
    Tooltip
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent extends BaseComponent implements OnInit {
  constructor(
    helper: RemoteService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    protected authService: AuthService,
    protected remoteService: RemoteService,
    protected router: Router,
    protected notificationService: NotificationService
  ) {
    super(helper, loaderService, dialogService, confirmationService, messageService);
    this.filteredPackages = [...this.packages];
  }

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

  search: any = {
    pageNumber: 0,
    pageSize: 15,
  }

  packages = [
    {label: 'Transport', value: 'TRANSPORT'},
    {label: 'Logistics', value: 'LOGISTICS'},
    {label: 'Full Package', value: 'FULL'},
  ];

  partnerDetails: Partner = {
    account_id: 0,
    account_number: '',
    active: false,
    business_reference: '',
    contact_person: '',
    contact_phone: '',
    logo: '',
    bio: '',
    package: '',
    partner_name: ''
  }

  filteredPackages: any[];

  override ngOnInit(): void {
    // this.loadPartnersFromServer();
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
      .sendGetToServer(`${this.authService.apiUrl}/partners/list/${this.search.pageNumber}/${this.search.pageSize}`)
      .subscribe({
        next: (response) => {
          this.partners = response?.returnObject?.rows || [];

          this.search.totalRecords = response?.returnObject?.totalRecords || 20;
          this.filteredPartners = [...this.partners];
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

    this.userData = {...this.userData, [key]: value};

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

  registerPartner(): void {
    // Example: just closes the dialog
    this.sendGetOrPostRequestToServer(
      "partners/add-partner",
      {data: this.partnerDetails},
      true,
      (response: ApiResponse<any>) => {
        if (response.returnCode != 200) {
          this.showError(response.returnMessage);
          return;
        }
        this.showAddDialog = false;
        this.loadPartnersFromServer();
      },
      true,
    );
  }

  clearFilters(): void {
    //Clear input fields
    this.userData = {};
    this.filteredPartners = [...this.partners];
  }

  searchPackages(event: any) {
    this.filteredPackages = event.query ?
      this.packages.filter(p => p.label.toLowerCase().indexOf(event.query.toLowerCase()) == 0) :
      this.packages;
  }

  protected onUpload($event: any) {
    let file = $event.files[0];
    const fileReader = new FileReader();

    fileReader.onloadend = () => {
      this.partnerDetails.logo = fileReader.result as string;
      console.log(this.partnerDetails.logo);
    };

    fileReader.readAsDataURL(file);
  }

  protected loadLazy($event: any) {
    if ($event) {
      this.search.pageNumber = $event.first;
      this.search.pageSize = $event.rows;
      this.loadPartnersFromServer();
    }
  }
}
