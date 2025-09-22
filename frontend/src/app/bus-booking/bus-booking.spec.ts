import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BusBooking } from './bus-booking';

describe('BusBooking', () => {
  let component: BusBooking;
  let fixture: ComponentFixture<BusBooking>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusBooking]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BusBooking);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
