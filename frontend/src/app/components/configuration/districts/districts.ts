import {Component, OnInit} from '@angular/core';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';
import {AutoComplete} from 'primeng/autocomplete';
import {Button} from 'primeng/button';
import {Dialog} from 'primeng/dialog';
import {FileUpload} from 'primeng/fileupload';
import {FloatLabel} from 'primeng/floatlabel';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {NgOptimizedImage} from '@angular/common';
import {ConfirmationService, MessageService, PrimeTemplate} from 'primeng/api';
import {TableModule} from 'primeng/table';
import {Tooltip} from 'primeng/tooltip';
import {ButtonModule} from 'primeng/button';
import {DialogModule} from 'primeng/dialog';
import {Amenity} from '../../models/amenity.model';
import {BaseComponent} from '../../services/base-component';
import {ManagementService} from '../../services/management.service';
import {LoaderService} from '../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {AuthService} from '../../services/auth.service';
import {RemoteService} from '../../services/remoteService';

@Component({
  selector: 'app-districts',
  imports: [
    Accordion,
    AccordionContent,
    AccordionHeader,
    AccordionPanel,
    AutoComplete,
    Button,
    Dialog,
    FileUpload,
    FloatLabel,
    FormsModule,
    InputText,
    NgOptimizedImage,
    PrimeTemplate,
    TableModule,
    DialogModule,
    ButtonModule,
    Tooltip
  ],
  templateUrl: './districts.html',
  styleUrl: './districts.css',
})
export class Districts extends BaseComponent implements OnInit{
  userData: any = {};
  filteredPartners: any[] = [];
  filteredPackages: any[] = [];
  amenities: Amenity[] = [];
  protected partnerDetails: any = {
    district_name: '',
    active: false,
  };

  search: any = { pageNumber: 0, pageSize: 15, totalRecords: 0 };


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

  loadLazy(event: any): void {
    if (event) {
      this.search.pageNumber = event.first;
      this.search.pageSize = event.rows;
      this.filterPartners();
    }
  }

  showAddDialog: boolean = false;

  districts: any[] = [];

  packages = [
    {label: 'Transport', value: 'TRANSPORT'},
    {label: 'Logistics', value: 'LOGISTICS'},
    {label: 'Full Package', value: 'FULL'},
  ];

  addPartner(): void {
    this.showAddDialog = true;
  }


  loadAmenities() {
    this.managementService.fetchDistricts().subscribe({
      next: (res) => {
        if (res.returnObject) {
          this.districts = res.returnObject;
        }
      }
    });
  }




  filterPartners(): void {
    this.filteredPartners = this.districts.filter(district => {
      return Object.keys(this.userData).every(k => {
        const searchValue = (this.userData[k] || '').toLowerCase();
        if (!searchValue) return true;
        return (district[k] || '').toString().toLowerCase().includes(searchValue);
      });
    });
  }

  searchPackages(event: any): void {
    this.filteredPackages = event.query
      ? this.packages.filter(p => p.label.toLowerCase().startsWith(event.query.toLowerCase()))
      : [...this.packages];
  }

  onUpload(event: any): void {
    const file = event.files[0];
    const fileReader = new FileReader();
    fileReader.onloadend = () => {
      this.partnerDetails.logo = fileReader.result as string;
    };
    fileReader.readAsDataURL(file);
  }

  registerPartner(): void {
    console.log('Registering district:', this.partnerDetails);
    this.showAddDialog = false;
  }
}
