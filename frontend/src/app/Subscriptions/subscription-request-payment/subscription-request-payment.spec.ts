import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubscriptionRequestPayment } from './subscription-request-payment';

describe('SubscriptionRequestPayment', () => {
  let component: SubscriptionRequestPayment;
  let fixture: ComponentFixture<SubscriptionRequestPayment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionRequestPayment]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubscriptionRequestPayment);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
