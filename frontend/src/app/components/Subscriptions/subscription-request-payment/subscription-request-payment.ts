import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../services/base-component';
import {RemoteService} from '../../services/remoteService';
import {LoaderService} from '../../services/loader.service';
import {DialogService, DynamicDialogConfig, DynamicDialogRef} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {Message} from 'primeng/message';
import {InputText} from 'primeng/inputtext';
import {FloatLabel} from 'primeng/floatlabel';
import {Button} from 'primeng/button';
import {Checkbox} from 'primeng/checkbox';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-subscription-request-payment',
  imports: [
    Message,
    InputText,
    FloatLabel,
    Button,
    Checkbox,
    FormsModule
  ],
  templateUrl: './subscription-request-payment.html',
  styleUrl: './subscription-request-payment.css',
})
export class SubscriptionRequestPayment extends BaseComponent implements OnInit {
  override ngOnInit() {
    super.ngOnInit();
  }

  details: any = {}
  onSubmit: any;

  confirmationRequest: any = {}

  constructor(
    helper: RemoteService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    protected authService: AuthService,
    protected router: Router,
    public ref: DynamicDialogRef,
    public config: DynamicDialogConfig
  ) {
    super(helper, loaderService, dialogService, confirmationService, messageService);
    this.details = this.config?.data?.details;
    this.onSubmit = this.config?.data?.onComplete;

    this.confirmationRequest.reference = this.details?.subscriptionReference;
    this.confirmationRequest.partner_code = this.details?.partnerCode;
  }

  protected confirmPayment() {
    console.log(this.confirmationRequest);
    this.sendGetOrPostRequestToServer(
      "subscriptions/request/confirm-payment",
      {data: this.confirmationRequest},
      true,
      (response: any) => {
        if (response?.returnCode !== 200) {
          this.showError(response?.returnMessage);
          return;
        }
        this.showSuccess(response?.returnMessage);
        this.onSubmit();

        this.ref.close();
      }
    )
  }
}
