import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemPermission } from './system-permission';

describe('SystemPermission', () => {
  let component: SystemPermission;
  let fixture: ComponentFixture<SystemPermission>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SystemPermission]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SystemPermission);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
