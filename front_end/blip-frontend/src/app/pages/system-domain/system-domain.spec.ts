import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemDomain } from './system-domain';

describe('SystemDomain', () => {
  let component: SystemDomain;
  let fixture: ComponentFixture<SystemDomain>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SystemDomain]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SystemDomain);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
