import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';
import { LoaderService } from '../services/loader.service';

@Component({
  selector: 'app-configuration',
  standalone: true,
  imports: [
    TableModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    FormsModule,
    CommonModule
  ],
  templateUrl: './configuration.html',
  styleUrl: './configuration.css'
})
export class Configuration {
  modules = [
    {
      id: 1,
      name: "Transport Management",
      code: "TRANSPORT_MANAGEMENT",
      description: "Complete passenger transport system including bus booking, route management, vehicle tracking, and fare management",
      createdAt: new Date('2024-01-15'),
      createdBy: 1
    },
    {
      id: 2,
      name: "Logistics Management",
      code: "LOGISTICS_MANAGEMENT",
      description: "Comprehensive logistics solution for parcel delivery, consignment tracking, fleet management, and delivery proof",
      createdAt: new Date('2024-01-20'),
      createdBy: 1
    },
  ];

  // Mock subscriptions data - in real app would come from backend
  subscriptions = [
    { moduleCode: "TRANSPORT_MANAGEMENT", partnerCount: 15 },
    { moduleCode: "LOGISTICS_MANAGEMENT", partnerCount: 12 },
    { moduleCode: "ROUTE_OPTIMIZATION", partnerCount: 8 },
    { moduleCode: "PAYMENT_PROCESSING", partnerCount: 20 },
    { moduleCode: "FLEET_ANALYTICS", partnerCount: 6 }
  ];


  searchQuery = '';
  showAddDialog = false;

  newModule = {
    name: '',
    code: '',
    description: ''
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private loaderService: LoaderService
  ) {}

  get filteredModules() {
    return this.modules.filter(m =>
      m.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      m.description.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      m.code.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  getActiveSubscriptionsCount(): number {
    return this.subscriptions.reduce((total, sub) => total + sub.partnerCount, 0);
  }

  getModuleSubscriptions(moduleCode: string): number {
    const subscription = this.subscriptions.find(s => s.moduleCode === moduleCode);
    return subscription ? subscription.partnerCount : 0;
  }

  openAddDialog() {
    this.newModule = {
      name: '',
      code: '',
      description: ''
    };
    this.showAddDialog = true;
  }

  addModule() {
    if (!this.newModule.name.trim() || !this.newModule.description.trim()) {
      this.notificationService.showWarning('Please fill in all fields before saving');
      return;
    }

    // Auto-generate code from name if not provided
    const code = this.newModule.code.trim() ||
      this.newModule.name.toUpperCase().replace(/\s+/g, '_').replace(/[^A-Z0-9_]/g, '');

    const module = {
      id: this.modules.length + 1,
      name: this.newModule.name,
      code: code,
      description: this.newModule.description,
      createdAt: new Date(),
      createdBy: 1 // Current user ID
    };

    this.modules.push(module);
    this.notificationService.showSuccess(`Module '${module.name}' added successfully`);
    this.showAddDialog = false;
  }

  // New method implementations for transport/logistics modules

  refreshModules() {
    this.notificationService.showInfo('Refreshing modules list...');
    // In real app, would call ModulesService.modulesList()
    setTimeout(() => {
      this.notificationService.showSuccess('Modules list refreshed successfully');
    }, 1000);
  }

  viewModule(module: any) {
    this.notificationService.showInfo(`Viewing details for ${module.name}`);
    // In real app, would navigate to module details page or open detail modal
    console.log('View module:', module);
  }

  editModule(module: any) {
    this.notificationService.showInfo(`Editing ${module.name}`);
    // In real app, would open edit modal with module data
    console.log('Edit module:', module);
  }

  viewSubscriptions(module: any) {
    this.notificationService.showInfo(`Viewing subscriptions for ${module.name}`);
    // In real app, would show partners subscribed to this module
    console.log('View subscriptions for module:', module);
  }
}
