import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../services/base-component';

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
  imports: [],
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

  plans: Plan[] = [
    {
      id: 'starter',
      name: 'Starter',
      icon: 'pi pi-cog',
      price: { monthly: 9, annual: 90 },
      description: 'Perfect for individuals getting started',
      features: [
        '5 Projects',
        '10GB Storage'
      ],
      color: 'blue',
      popular: false
    },
    {
      id: 'pro',
      name: 'Professional',
      icon: 'pi pi-cog',
      price: { monthly: 29, annual: 290 },
      description: 'For professionals and small teams',
      features: [
        'Unlimited Projects',
        '100GB Storage'
      ],
      color: 'purple',
      popular: true
    },
    {
      id: 'enterprise',
      name: 'Enterprise',
      icon: 'pi pi-cog',
      price: { monthly: 99, annual: 990 },
      description: 'Advanced features for large organizations',
      features: [
        'Unlimited Everything',
        '1TB Storage'
      ],
      color: 'orange',
      popular: false
    }
  ];

  getPrice(plan: Plan): number {
    return this.billingCycle === 'monthly' ? plan.price.monthly : plan.price.annual;
  }

  getSavings(plan: Plan): number {
    return Math.round(((plan.price.monthly * 12 - plan.price.annual) / (plan.price.monthly * 12)) * 100);
  }

  fetchSubPlans(){
    this.sendGetOrPostRequestToServer(
      "subscriptions/list/0/15",
      null,
      true,
      (response:any)=>{
        if(response?.returnCode !== 200) return;
        this.plans = response?.returnObject;
      },
      false
    )
  }
}
