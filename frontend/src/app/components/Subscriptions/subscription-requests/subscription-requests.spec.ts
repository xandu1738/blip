import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubscriptionRequests } from './subscription-requests';

describe('SubscriptionRequests', () => {
  let component: SubscriptionRequests;
  let fixture: ComponentFixture<SubscriptionRequests>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionRequests]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubscriptionRequests);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
