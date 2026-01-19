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

@Component({
  selector: 'app-users',
  imports: [
    TableModule
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

  usersList:any[] = [];
  search:any = {
    first: 0,
    rows: 15,
  }

  loadLazy($event:any){
    if($event){
      this.search['first'] = $event.first;
      this.search['rows'] = $event.rows;
    }
    this.remoteService.sendGetToServer(`${this.authService.apiUrl}/user-management/users-list/${this.search.first}/${this.search.rows}`)
      .subscribe(list => {
        this.usersList = list?.returnObject?.rows;
        this.search.totalRecords = list?.returnObject?.totalRecords;
      })
  }
}
