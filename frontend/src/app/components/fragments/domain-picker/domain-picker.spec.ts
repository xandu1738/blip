import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DomainPicker } from './domain-picker';

describe('DomainPicker', () => {
  let component: DomainPicker;
  let fixture: ComponentFixture<DomainPicker>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DomainPicker]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DomainPicker);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
