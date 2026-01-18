import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../services/base-component';
import {PrimeTemplate} from 'primeng/api';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TableModule} from 'primeng/table';
import {Tooltip} from 'primeng/tooltip';
import {Button} from 'primeng/button';
import {SubscriptionRequestPayment} from '../subscription-request-payment/subscription-request-payment';

@Component({
  selector: 'app-subscriptions',
  imports: [
    PrimeTemplate,
    ReactiveFormsModule,
    TableModule,
    FormsModule,
    Tooltip,
    Button
  ],
  templateUrl: './subscriptions.html',
  styleUrl: './subscriptions.css',
})
export class Subscriptions extends BaseComponent implements OnInit {
  protected userData: any = {};
  protected filteredPartners: any[] = [];
  protected search: any = {};

  override ngOnInit() {
    super.ngOnInit();
  }

  protected onChangeSearch($event: Event) {

  }

  protected filterRequests() {
    this.loadLazy(null);
  }

  protected loadLazy($event: any) {
    this.sendGetOrPostRequestToServer(
      "subscriptions/requests/0/15",
      null,
      true,
      (response: any) => {
        if (response?.returnCode !== 200) return;
        this.filteredPartners = response?.returnObject;
      },
      false
    )
  }

  protected confirmPayment(req: any) {
    if (!req) {
      this.showError("Oops! Couldn't find request details.")
      return;
    }
    let dialogRequest: any = {
      component: SubscriptionRequestPayment,
      config: {
        data: {
          details: req,
          onComplete: (response: any) => {
            this.filterRequests();
          },
        },
        header: req?.partnerCode + ' Subscription Request',
        width: '50%'
      }
    }
    this.openDialog(dialogRequest);
  }
}
