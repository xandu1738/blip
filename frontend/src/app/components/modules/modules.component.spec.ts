import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { Modules } from './modules.component';
import { ModulesService } from '../../services/modules.service';
import { ConfirmationService, MessageService } from 'primeng/api';

describe('ModulesComponent', () => {
  let component: Modules;
  let fixture: ComponentFixture<Modules>;
  let modulesService: jasmine.SpyObj<ModulesService>;

  beforeEach(async () => {
    const modulesSpy = jasmine.createSpyObj('ModulesService', ['fetchModules', 'fetchModuleDetail', 'addModule', 'editModule', 'archiveModule']);

    await TestBed.configureTestingModule({
      imports: [Modules, HttpClientTestingModule],
      providers: [
        { provide: ModulesService, useValue: modulesSpy },
        ConfirmationService,
        MessageService
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Modules);
    component = fixture.componentInstance;
    modulesService = TestBed.inject(ModulesService) as jasmine.SpyObj<ModulesService>;

    modulesService.fetchModules.and.returnValue(of({ returnCode: 200, returnObject: { content: [], totalElements: 0 } }));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load modules on init', () => {
    expect(modulesService.fetchModules).toHaveBeenCalled();
  });

  it('should open add dialog', () => {
    component.openAddDialog();
    expect(component.showAddDialog).toBeTrue();
    expect(component.isEditMode).toBeFalse();
  });
});
