import { Component, OnInit } from '@angular/core';
import { PartnersService } from '../../../../services/partners.service';
import { PartnerModel } from '../../../models/partner.model';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';

@Component({
  selector: 'app-partner-form',
  standalone: true,
  imports: [CommonModule, FormsModule, AutoCompleteModule, ButtonModule, InputTextModule, InputNumberModule],
  templateUrl: './partner-form.html',
  providers: [MessageService]
})
export class PartnerForm implements OnInit {

  partner: PartnerModel = {
    active: false,
    partnerName: '',
  };

  isEditMode = false;
  statuses = ['Active', 'Inactive'];
  filteredStatuses: string[] = [];

  constructor(
    private partnersService: PartnersService,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService
  ) {}

  showError(message: string) {
    this.messageService.add({severity: 'error', summary: 'Error', detail: message});
  }

  showSuccess(message: string) {
    this.messageService.add({severity: 'success', summary: 'Success', detail: message});
  }

  ngOnInit() {
    const code = this.route.snapshot.paramMap.get('id');
    if (code) {
      this.isEditMode = true;
      this.fetchPartnerDetails(code);
    }
  }

  get statusText(): string {
    return this.partner.active ? 'Active' : 'Inactive';
  }

  set statusText(v: string) {
    this.partner.active = (v === 'Active');
  }

  fetchPartnerDetails(code: string) {
    this.partnersService.fetchPartnerProfile(code).subscribe({
      next: (res: any) => {
        if (res.returnCode === 200) {
          this.partner = res.returnObject;
        } else {
          this.showError(res.returnMessage || 'Failed to load details');
        }
      },
      error: (err: any) => {
        this.showError('Failed to fetch partner details');
      }
    });
  }

  onLogoUpload(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.partner.logo = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  triggerFileInput() {
    document.getElementById('logoInput')?.click();
  }

  searchStatus(event: any) {
    this.filteredStatuses = this.statuses.filter(s => s.toLowerCase().startsWith(event.query.toLowerCase()));
  }

  cancel() {
    this.router.navigate(['/partners']);
  }

  savePartner() {
    const action = this.isEditMode ?
      this.partnersService.editPartner(this.partner) :
      this.partnersService.addPartner(this.partner);

    action.subscribe({
      next: (res: any) => {
        if (res.status === 200) {
          this.showSuccess(res.message || 'Saved successfully');
          this.router.navigate(['/partners']);
        } else {
          this.showError(res.message || 'Failed to save');
        }
      },
      error: (err: any) => {
        this.showError('Failed to save partner');
      }
    });
  }

  archivePartner() {
    if(this.partner.id) {
       this.partnersService.archivePartner(this.partner.id).subscribe({
         next: (res: any) => {
           if (res.status === 200) {
             this.showSuccess('Partner archived');
             this.router.navigate(['/partners']);
           } else {
             this.showError(res.message || 'Failed to archive');
           }
         },
         error: () => this.showError('Failed to archive partner')
       });
    }
  }
}
