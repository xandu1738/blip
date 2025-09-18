import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripSeat } from './trip-seat';

describe('TripSeat', () => {
  let component: TripSeat;
  let fixture: ComponentFixture<TripSeat>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripSeat]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripSeat);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
