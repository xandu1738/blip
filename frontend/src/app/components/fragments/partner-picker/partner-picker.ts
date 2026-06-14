import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BaseComponent} from '../../../services/base-component';
import {RemoteService} from '../../../services/remoteService';
import {LoaderService} from '../../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../../services/auth.service';
import {Router} from '@angular/router';
import {NotificationService} from '../../../services/notification.service';
import {AutoComplete} from 'primeng/autocomplete';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-partner-picker',
  imports: [
    AutoComplete,
    FormsModule
  ],
  templateUrl: './partner-picker.html',
  styleUrl: './partner-picker.css',
})
export class PartnerPicker extends BaseComponent implements OnInit{
  constructor(
    helper: RemoteService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    authService: AuthService,
    protected remoteService: RemoteService,
    protected router: Router,
    protected notificationService: NotificationService
  ) {
    super(authService,helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit() {
    super.ngOnInit();

    this.loadDomains();
  }

  @Output()
  public onSelected: EventEmitter<any> = new EventEmitter();

  @Input()
  selectedPartnerCode: string | undefined;
  partners: any[] = [];
  filteredPartners: any[] = [];

  selectedPartner: any;

  protected search($event: any) {
    this.filteredPartners = $event.query ?
      this.partners.filter(
        (domain: any) =>
          domain.partnerName.toLowerCase().indexOf($event.query.toLowerCase()) === 0) :
      [...this.partners];
  }

  loadDomains() {
    this.remoteService.sendGetToServer(`${this.authService.apiUrl}/partners/list/0/10`)
      .subscribe(list => {
        this.partners = list?.returnObject?.rows;

        this.filteredPartners = [...this.partners];
      })
  }

  protected onSelectedPartner($event: any) {
    if (this.onSelected) this.onSelected.emit($event);
  }
}
