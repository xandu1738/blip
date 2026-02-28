import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Districts } from './districts';

describe('Districts', () => {
  let component: Districts;
  let fixture: ComponentFixture<Districts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Districts]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Districts);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
