import {Component, OnInit, WritableSignal} from '@angular/core';
import {BaseComponent} from '../../services/base-component';
import {Button} from 'primeng/button';
import {Dialog} from 'primeng/dialog';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {CurrencyPipe} from '@angular/common';

interface Plan {
  id: string;
  name: string;
  icon: string;
  price: { monthly: number; annual: number };
  description: string;
  features: string[];
  color: string;
  popular: boolean;
}

@Component({
  selector: 'app-subscriptions-list',
  imports: [
    Button,
    Dialog,
    FormsModule,
    InputText,
    CurrencyPipe
  ],
  templateUrl: './subscriptions-list.html',
  styleUrl: './subscriptions-list.css',
})
export class SubscriptionsList extends BaseComponent implements OnInit {
  override ngOnInit() {
    super.ngOnInit();
    this.fetchSubPlans();
  }

  billingCycle: 'monthly' | 'annual' = 'monthly';
  hoveredCard: string | null = null;
  productKeyForm: boolean = false;

  plans: Plan[] = [];
  protected productKey: any;
  protected showSubSuccess: boolean = false;

  getPrice(plan: Plan): number {
    return this.billingCycle === 'monthly' ? plan?.price?.monthly : plan?.price?.annual;
  }

  getSavings(plan: Plan): number {
    return Math.round(((plan.price.monthly * 12 - plan.price.annual) / (plan.price.monthly * 12)) * 100);
  }

  fetchSubPlans() {
    this.sendGetOrPostRequestToServer(
      "subscriptions/plans/0/15",
      null,
      true,
      (response: any) => {
        if (response?.returnCode !== 200) return;
        this.plans = response?.returnObject;
      },
      false
    )
  }

  protected enterProductKey() {
    this.productKeyForm = true;
  }

  protected completeSubRequest(plan: Plan) {
    console.log(plan);
    let dialogRequest = {
      heading: 'Send Subscription Request',
      message: 'You are about to subscribe to ' + plan?.name +
        '. \n<b>Click "Yes" to confirm this action.</b>\n' +
        'You will receive a reference via email.\n' +
        'Please use this as \'COMMENT\' or \'REASON\' when transferring the subscription charge.\n' +
        'Then notify admin to activate your subscription.' +
        'Thanks for supporting us!',
      onConfirm: () => {
        this.sendGetOrPostRequestToServer(
          "subscriptions/request",
          {data: {subscription_id: plan?.id}},
          true,
          (response: any) => {
            if (response?.returnCode !== 200) {
              this.updateSubscriptionRequest(plan, response?.returnMessage);
              return;
            }
            this.showSubSuccess = true;
          },
          false
        );
      }
    };
    this.confirmDialog(dialogRequest);
  }

  updateSubscriptionRequest(plan: any, message: string) {
    let req = {
      heading: 'Send Subscription Request',
      message: message,
      onConfirm: () => {
        this.sendGetOrPostRequestToServer(
          "subscriptions/request",
          {data: {subscription_id: plan?.id, update_subscription: true}},
          true,
          (response: any) => {
            if (response?.returnCode !== 200) {
              this.showError(response?.returnMessage);
              return;
            }
            this.showSubSuccess = true;
          },
          false
        );
      }
    };
    this.confirmDialog(req);
  }
}
