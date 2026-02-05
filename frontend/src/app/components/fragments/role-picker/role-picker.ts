import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BaseComponent} from '../../services/base-component';
import {RemoteService} from '../../services/remoteService';
import {LoaderService} from '../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {NotificationService} from '../../services/notification.service';
import {AutoComplete} from 'primeng/autocomplete';
import {FloatLabel} from 'primeng/floatlabel';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-role-picker',
  imports: [
    AutoComplete,
    FloatLabel,
    FormsModule
  ],
  templateUrl: './role-picker.html',
  styleUrl: './role-picker.css',
})
export class RolePicker extends BaseComponent implements OnInit {
  override ngOnInit() {
    super.ngOnInit();
    this.loadRoles();
  }

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
  }

  @Output()
  public onSelected: EventEmitter<any> = new EventEmitter();

  @Input()
  selectedRoleCode: string | undefined;
  roles: any[] = [];
  filteredRoles: any[] = [];

  selectedRole: any;

  protected search($event: any) {
    this.filteredRoles = $event.query ?
      this.roles.filter(
        (role: any) =>
          role?.roleName.toLowerCase().indexOf($event.query.toLowerCase()) === 0) :
      [...this.roles];
  }

  loadRoles() {
    this.remoteService.sendGetToServer(`${this.authService.apiUrl}/management/our-roles`)
      .subscribe(list => {
        this.roles = list?.returnObject;

        this.filteredRoles = [...this.roles];
      })
  }

  protected onSelectedDomain($event: any) {
    if (this.onSelected) this.onSelected.emit($event);
  }

}
