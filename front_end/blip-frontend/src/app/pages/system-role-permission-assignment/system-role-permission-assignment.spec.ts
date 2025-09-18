import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemRolePermissionAssignment } from './system-role-permission-assignment';

describe('SystemRolePermissionAssignment', () => {
  let component: SystemRolePermissionAssignment;
  let fixture: ComponentFixture<SystemRolePermissionAssignment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SystemRolePermissionAssignment]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SystemRolePermissionAssignment);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
