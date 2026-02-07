import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AutoComplete} from 'primeng/autocomplete';
import {FormsModule} from '@angular/forms';
import {BaseComponent} from '../../services/base-component';
import {RemoteService} from '../../services/remoteService';
import {LoaderService} from '../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {NotificationService} from '../../services/notification.service';

@Component({
  selector: 'app-domain-picker',
  imports: [
    AutoComplete,
    FormsModule
  ],
  templateUrl: './domain-picker.html',
  styleUrl: './domain-picker.css',
})
export class DomainPicker extends BaseComponent implements OnInit {
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
  selectedDomainCode: string | undefined;
  domains: any[] = [];
  filteredDomains: any[] = [];

  selectedDomain: any;

  protected search($event: any) {
    this.filteredDomains = $event.query ?
      this.domains.filter(
        (domain: any) =>
          domain.optionLabel.toLowerCase().indexOf($event.query.toLowerCase()) === 0) :
      [...this.domains];
  }

  loadDomains() {
    this.remoteService.sendGetToServer(`${this.authService.apiUrl}/management`)
      .subscribe(list => {
        this.domains = list?.returnObject?.domains;

        this.filteredDomains = [...this.domains];
      })
  }

  protected onSelectedDomain($event: any) {
    if (this.onSelected) this.onSelected.emit($event);
  }
}
