import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsignmentParcel } from './consignment-parcel';

describe('ConsignmentParcel', () => {
  let component: ConsignmentParcel;
  let fixture: ComponentFixture<ConsignmentParcel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsignmentParcel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsignmentParcel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
