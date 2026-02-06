import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../services/base-component';
import {RemoteService} from '../../services/remoteService';
import {LoaderService} from '../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {NotificationService} from '../../services/notification.service';
import {TableModule} from 'primeng/table';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';
import {Button} from 'primeng/button';
import {FloatLabel} from 'primeng/floatlabel';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {DomainPicker} from '../../fragments/domain-picker/domain-picker';
import {RolePicker} from '../../fragments/role-picker/role-picker';
import {AutoComplete} from 'primeng/autocomplete';
import {Dialog} from 'primeng/dialog';
import {FileUpload} from 'primeng/fileupload';
import {PartnerPicker} from '../../fragments/partner-picker/partner-picker';

@Component({
  selector: 'app-users',
  imports: [
    TableModule,
    Accordion,
    AccordionContent,
    AccordionHeader,
    AccordionPanel,
    Button,
    FloatLabel,
    FormsModule,
    InputText,
    DomainPicker,
    RolePicker,
    AutoComplete,
    Dialog,
    FileUpload,
    PartnerPicker
  ],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users extends BaseComponent implements OnInit {
  override ngOnInit() {
    super.ngOnInit();
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

  usersList: any[] = [];
  showAddDialog: boolean = false;
  search: any = {
    first: 0,
    rows: 15,
  }
  protected userData: any = {};
  protected userDetails: any = {};

  loadLazy($event: any) {
    if ($event) {
      this.search['first'] = $event.first;
      this.search['rows'] = $event.rows;
    }
    this.remoteService.sendGetToServer(`${this.authService.apiUrl}/user-management/users-list/${this.search.first}/${this.search.rows}`)
      .subscribe(list => {
        this.usersList = list?.returnObject?.rows;
        this.search.totalRecords = list?.returnObject?.totalRecords;
      })
  }

  protected addUser() {
    // this.showAddDialog = true;
    this.router.navigate(['/user-form']).catch(err => console.log(err));
  }

  protected filterUsers() {
    console.log("Filtering users...")
  }

  protected onSelectDomain($event: any) {
    console.log("Change Alert", $event);
  }

  protected onSelectRole($event: any) {
    console.log("Selection alert", $event);
  }

  protected registerUser() {
    console.log("Registering user...")
  }
}
