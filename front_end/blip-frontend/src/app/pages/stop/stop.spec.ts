import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Stop } from './stop';

describe('Stop', () => {
  let component: Stop;
  let fixture: ComponentFixture<Stop>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Stop]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Stop);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
