import {Component} from '@angular/core';
import {TableModule} from 'primeng/table';
import {DialogModule} from 'primeng/dialog';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {NotificationService} from '../../services/notification.service';
import {LoaderService} from '../../services/loader.service';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';
import {FloatLabel} from 'primeng/floatlabel';
import {BaseComponent} from '../../services/base-component';
import {ModulesService} from '../../services/modules.service';
import {ModuleModel} from '../models/module.model';
import {ApiResponse} from '../models/user.models';
import {RemoteService} from '../../services/remoteService';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {Tooltip} from 'primeng/tooltip';

@Component({
  selector: 'app-modules',
  standalone: true,
  imports: [
    TableModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    FormsModule,
    CommonModule,
    Accordion,
    AccordionContent,
    AccordionHeader,
    AccordionPanel,
    FloatLabel,
    Tooltip
  ],
  templateUrl: './modules.component.html',
  styleUrl: './modules.component.css'
})
export class Modules extends BaseComponent {
  modules: ModuleModel[] = [];
  isLoading = false;
  showAddDialog = false;
  showDetailDialog = false;
  isEditMode = false;

  selectedModule: ModuleModel = {};
  moduleStats: any = {};

  newModule: ModuleModel = {
    name: '',
    code: '',
    description: ''
  };

  search: any = {
    pageNumber: 0,
    pageSize: 15,
    totalRecords: 0,
    module_name: '',
    category: '',
    status: ''
  };

  constructor(
    helper: RemoteService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    authService: AuthService,
    protected remoteService: RemoteService,
    protected router: Router,
    protected notificationService: NotificationService,
    private modulesService: ModulesService
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit(): void {
  }

  loadModules() {
    this.isLoading = true;
    this.loaderService.display(true);
    this.modulesService.fetchModules(this.search.pageNumber, this.search.pageSize).subscribe({
      next: (response: ApiResponse<any>) => {
        if (response.returnCode === 200) {
          this.modules = response.returnObject.content;
          this.search.totalRecords = response.returnObject.totalElements;
        } else {
          this.showError(response.returnMessage);
        }
      },
      error: (err) => this.showError('Failed to load modules'),
      complete: () => {
        this.isLoading = false;
        this.loaderService.display(false);
      }
    });
  }

  loadLazy(event: any) {
    this.search.pageNumber = event.first / event.rows;
    this.search.pageSize = event.rows;
    this.loadModules();
  }

  // get filteredModules() {
  //   return this.modules.filter(m =>
  //     m?.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
  //     m?.description.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
  //     m?.code.toLowerCase().includes(this.searchQuery.toLowerCase())
  //   );
  // }

  // getActiveSubscriptionsCount(): number {
  //   return this.subscriptions.reduce((total, sub) => total + sub.partnerCount, 0);
  // }
  //
  // getModuleSubscriptions(moduleCode: string): number {
  //   const subscription = this.subscriptions.find(s => s.moduleCode === moduleCode);
  //   return subscription ? subscription.partnerCount : 0;
  // }

  openAddDialog() {
    this.isEditMode = false;
    this.newModule = {
      name: '',
      code: '',
      description: ''
    };
    this.showAddDialog = true;
  }

  openEditDialog(module: ModuleModel) {
    this.isEditMode = true;
    this.newModule = { ...module };
    this.showAddDialog = true;
  }

  saveModule() {
    if (!this.newModule.name?.trim() || !this.newModule.description?.trim()) {
      this.showWarning('Please fill in all required fields');
      return;
    }

    const action = this.isEditMode ?
      this.modulesService.editModule(this.newModule) :
      this.modulesService.addModule(this.newModule);

    this.loaderService.display(true);
    action.subscribe({
      next: (response: ApiResponse<any>) => {
        if (response.returnCode === 200) {
          this.showSuccess(response.returnMessage);
          this.showAddDialog = false;
          this.loadModules();
        } else {
          this.showError(response.returnMessage);
        }
      },
      error: (err) => this.showError('Operation failed'),
      complete: () => this.loaderService.display(false)
    });
  }

  refreshModules() {
    this.loadModules();
  }

  viewModule(module: ModuleModel) {
    this.loaderService.display(true);
    this.modulesService.fetchModuleDetail(module.id!).subscribe({
      next: (response: ApiResponse<any>) => {
        if (response.returnCode === 200) {
          this.selectedModule = response.returnObject.module;
          this.moduleStats = response.returnObject.stats;
          this.showDetailDialog = true;
        } else {
          this.showError(response.returnMessage);
        }
      },
      error: (err) => this.showError('Failed to fetch module details'),
      complete: () => this.loaderService.display(false)
    });
  }

  archiveModule(module: ModuleModel) {
    this.confirmDialog({
      message: `Are you sure you want to archive the module '${module.name}'?`,
      header: 'Confirm Archiving',
      icon: 'pi pi-exclamation-triangle',
      onConfirm: () => {
        this.loaderService.display(true);
        this.modulesService.archiveModule(module.id!).subscribe({
          next: (response: ApiResponse<any>) => {
            if (response.returnCode === 200) {
              this.showSuccess(response.returnMessage);
              this.loadModules();
            } else {
              this.showError(response.returnMessage);
            }
          },
          error: (err) => this.showError('Failed to archive module'),
          complete: () => this.loaderService.display(false)
        });
      }
    });
  }

  viewSubscriptions(module: ModuleModel) {
    this.router.navigate(['/subscriptions'], { queryParams: { moduleCode: module.code } });
  }

  filterModules() {
    this.loadModules();
  }
}
