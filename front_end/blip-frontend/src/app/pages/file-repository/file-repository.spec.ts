import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileRepository } from './file-repository';

describe('FileRepository', () => {
  let component: FileRepository;
  let fixture: ComponentFixture<FileRepository>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FileRepository]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FileRepository);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
