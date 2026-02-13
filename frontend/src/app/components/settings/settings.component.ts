import {Component, OnInit} from '@angular/core';
import { TableModule } from 'primeng/table';
import {Dialog, DialogModule} from 'primeng/dialog';
import {ButtonDirective, ButtonModule} from 'primeng/button';
import {InputText, InputTextModule} from 'primeng/inputtext';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { NotificationService } from '../services/notification.service';
import { LoaderService } from '../services/loader.service';
import {BaseComponent} from '../services/base-component';
import {Events} from '../services/events';
import {RemoteService} from '../services/remoteService';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {SocketService} from '../services/socket.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    TableModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    FormsModule,
    CommonModule
  ],
  templateUrl: './settings.html',
  styleUrl: './settings.css'
})
export class SettingsComponent extends BaseComponent implements OnInit{
  override ngOnInit(): void {
    super.ngOnInit();
  }
  modules = [
    {
      name: "Transport",
      description: "Handles passenger transport and vehicle management",
      version: "2.1.0",
      status: "active",
      type: "core"
    },
    {
      name: "Logistics",
      description: "Tracks consignments, parcels, and fleet movement",
      version: "2.1.0",
      status: "active",
      type: "core"
    },
    {
      name: "Payment Gateway",
      description: "Integrated payment processing and billing",
      version: "1.8.2",
      status: "active",
      type: "integration"
    },
    {
      name: "Analytics",
      description: "Business intelligence and reporting tools",
      version: "1.5.0",
      status: "inactive",
      type: "plugin"
    }
  ];

  integrations = [
    {
      name: "Stripe",
      type: "Payment Gateway",
      description: "Online payment processing",
      status: "active",
      icon: "💳",
      lastSync: "2 hours ago"
    },
    {
      name: "Google Maps",
      type: "Location Service",
      description: "Route optimization and tracking",
      status: "active",
      icon: "🗺️",
      lastSync: "15 minutes ago"
    },
    {
      name: "SMS Gateway",
      type: "Communication",
      description: "Customer notifications via SMS",
      status: "error",
      icon: "📱",
      lastSync: "1 day ago"
    },
    {
      name: "Email Service",
      type: "Communication",
      description: "Automated email notifications",
      status: "active",
      icon: "📧",
      lastSync: "5 minutes ago"
    },
    {
      name: "Backup Service",
      type: "Data Management",
      description: "Automated database backups",
      status: "inactive",
      icon: "💾",
      lastSync: "12 hours ago"
    },
    {
      name: "Monitoring",
      type: "System Health",
      description: "Application performance monitoring",
      status: "active",
      icon: "📊",
      lastSync: "1 minute ago"
    }
  ];

  systemStatus = [
    { service: "Database", status: "healthy", uptime: "99.9%" },
    { service: "API Gateway", status: "healthy", uptime: "99.8%" },
    { service: "Message Queue", status: "healthy", uptime: "99.5%" },
    { service: "Cache Layer", status: "warning", uptime: "98.2%" },
    { service: "File Storage", status: "healthy", uptime: "99.9%" },
    { service: "Search Engine", status: "healthy", uptime: "99.7%" }
  ];

  recentChanges = [
    {
      id: 1,
      type: "modified",
      description: "Updated Transport module to v2.1.0",
      user: "Admin",
      time: "2 hours ago"
    },
    {
      id: 2,
      type: "added",
      description: "Added new SMS Gateway integration",
      user: "System",
      time: "4 hours ago"
    },
    {
      id: 3,
      type: "modified",
      description: "Modified session timeout settings",
      user: "Admin",
      time: "1 day ago"
    },
    {
      id: 4,
      type: "removed",
      description: "Removed deprecated analytics module",
      user: "System",
      time: "2 days ago"
    },
    {
      id: 5,
      type: "modified",
      description: "Updated security configurations",
      user: "Security Team",
      time: "3 days ago"
    }
  ];

  settings = {
    debugMode: false,
    sessionTimeout: 30,
    twoFactorAuth: true,
    emailNotifications: true,
    pushNotifications: false,
    environment: 'production'
  };

  searchQuery = '';
  showAddDialog = false;

  newModule = {
    name: '',
    description: '',
    version: '1.0.0',
    type: 'core',
    priority: 'medium',
    autoStart: false
  };


  constructor(
    protected router: Router,
    helper: RemoteService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    protected notificationService: NotificationService,
    authService: AuthService
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  get filteredModules() {
    return this.modules.filter(m =>
      m.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      m.description.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }

  getActiveModulesCount(): number {
    return this.modules.filter(m => m.status === 'active').length;
  }

  getPendingActionsCount(): number {
    // Count modules with issues, failed integrations, etc.
    const failedIntegrations = this.integrations.filter(i => i.status === 'error').length;
    const inactiveModules = this.modules.filter(m => m.status === 'inactive').length;
    return failedIntegrations + inactiveModules;
  }

  openAddDialog() {
    this.newModule = {
      name: '',
      description: '',
      version: '1.0.0',
      type: 'core',
      priority: 'medium',
      autoStart: false
    };
    this.showAddDialog = true;
  }

  addModule() {
    if (!this.newModule.name.trim() || !this.newModule.description.trim()) {
      this.showWarning('Please fill in all fields before saving');
      return;
    }

    const module = {
      name: this.newModule.name,
      description: this.newModule.description,
      version: this.newModule.version,
      status: 'active',
      type: this.newModule.type
    };

    this.modules.push(module);
    this.showSuccess('New module added successfully');
    this.showAddDialog = false;
  }

  // New method implementations
  exportConfiguration() {
    // Mock export functionality
    this.showInfo('Configuration export started. Download will begin shortly.');
    console.log('Exporting configuration...');
  }

  refreshModules() {
    this.showInfo('Refreshing modules list...');
    // Mock refresh - in real app, would call API
    setTimeout(() => {
      this.showSuccess('Modules list refreshed successfully');
    }, 1000);
  }

  configureModule(module: any) {
    this.showInfo(`Opening configuration for ${module.name}`);
    console.log('Configure module:', module);
  }

  showModuleOptions(module: any) {
    this.showInfo(`Showing options for ${module.name}`);
    console.log('Module options:', module);
  }

  saveSettings() {
    this.showSuccess('Settings saved successfully');
    console.log('Saving settings:', this.settings);
  }

  addIntegration() {
    this.showInfo('Opening integration wizard...');
    console.log('Adding new integration...');
  }

  configureIntegration(integration: any) {
    this.showInfo(`Configuring ${integration.name}`);
    console.log('Configure integration:', integration);
  }

  testIntegration(integration: any) {
    this.showInfo(`Testing ${integration.name} connection...`);
    console.log('Testing integration:', integration);

    // Mock test result
    setTimeout(() => {
      if (integration.status === 'error') {
        this.showError(`${integration.name} test failed. Please check configuration.`);
      } else {
        this.showSuccess(`${integration.name} test successful!`);
      }
    }, 2000);
  }
}
