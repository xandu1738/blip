import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Parcel } from './parcel';

describe('Parcel', () => {
  let component: Parcel;
  let fixture: ComponentFixture<Parcel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Parcel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Parcel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
