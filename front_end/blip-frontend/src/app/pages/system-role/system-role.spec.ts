import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemRole } from './system-role';

describe('SystemRole', () => {
  let component: SystemRole;
  let fixture: ComponentFixture<SystemRole>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SystemRole]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SystemRole);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
