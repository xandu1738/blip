import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PartnerPicker } from './partner-picker';

describe('PartnerPicker', () => {
  let component: PartnerPicker;
  let fixture: ComponentFixture<PartnerPicker>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PartnerPicker]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PartnerPicker);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
