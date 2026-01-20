import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OldHome } from './old-home';

describe('OldHome', () => {
  let component: OldHome;
  let fixture: ComponentFixture<OldHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OldHome]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OldHome);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
