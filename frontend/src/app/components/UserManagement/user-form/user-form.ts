import { Component, OnInit } from '@angular/core';
import { BaseComponent } from '../../services/base-component';
import { Button } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { InputText } from 'primeng/inputtext';
import { IconField } from 'primeng/iconfield';
import { InputIcon } from 'primeng/inputicon';
import { PartnerPicker } from '../../fragments/partner-picker/partner-picker';
import { RolePicker } from '../../fragments/role-picker/role-picker';
import { SocketService } from '../../services/socket.service';
import { AuthService } from '../../services/auth.service';
import { OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { RemoteService } from '../../services/remoteService';
import { LoaderService } from '../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';

@Component({
  selector: 'app-user-form',
  imports: [
    Button,
    FormsModule,
    InputText,
    PartnerPicker,
    RolePicker,
    IconField,
    InputIcon
  ],
  templateUrl: './user-form.html',
  styleUrl: './user-form.css',
})
export class UserForm extends BaseComponent implements OnInit, OnDestroy {
  protected userDetails: any = {};
  private socketSubscription: Subscription | undefined;

  constructor(
    helper: RemoteService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    private socketService: SocketService,
    private authService: AuthService
  ) {
    super(helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit() {
    super.ngOnInit();

    this.socketService.activate();
    this.socketSubscription = this.socketService.getMessages().subscribe(msg => {
      if (msg && msg.type === 'USER_REGISTRATION') {
        this.messageService.add({
          severity: 'info',
          summary: 'New User Registered',
          detail: `${msg.firstName} ${msg.lastName} has just joined!`
        });
      }
    });
  }

  ngOnDestroy() {
    if (this.socketSubscription) {
      this.socketSubscription.unsubscribe();
    }
    this.socketService.deactivate();
  }

  protected registerUser() {
    this.authService.createUser({ data: this.userDetails }).subscribe(res => {
      if (res) {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User Created Successfully' });
        // reset form or close dialog
        this.userDetails = {};
      }
    })
  }

  protected setPartner($event: any) {
    console.log("Partner selected", $event);
    this.userDetails.partner = $event;
  }

  protected selectRole($event: any) {
    this.userDetails.role = $event;
    console.log("Role selected", $event);
  }
}
