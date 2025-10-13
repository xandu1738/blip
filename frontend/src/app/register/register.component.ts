import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { LoaderService } from '../services/loader.service';
import { CreateUserRequest } from '../models/user.models';
import {TableModule} from 'primeng/table';
import {Dialog, DialogModule} from 'primeng/dialog';
import {ButtonDirective} from 'primeng/button';
import {InputText} from 'primeng/inputtext';



interface Partner {
  partnerName: string;
  accountNumber:string;
  contactPerson:string;
  contactPhone:string;
  accountId:number;
  businessReference:string;
  active:boolean;
  logo:string;
  package:string;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink, TableModule, NgOptimizedImage, ButtonDirective, Dialog, DialogModule, InputText],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class RegisterComponent {
  firstName = '';
  lastName = '';
  email = '';
  password = '';
  confirmPassword = '';
  role = 'USER'; // Default role
  partner = '';
  isLoading = false;


  showAddDialog = false;
  partners: Partner[] = [
    {
      partnerName: "Swift Transit Ltd",
      accountNumber: "STL-001245",
      contactPerson: "James Okello",
      contactPhone: "+256772334455",
      accountId: 101,
      businessReference: "BR-UTR-001",
      active: true,
      logo: "https://cdn.example.com/logos/swift_transit.png",
      package: "FULL"
    },
    {
      partnerName: "Greenline Logistics",
      accountNumber: "GL-002189",
      contactPerson: "Sarah Namuli",
      contactPhone: "+256701223344",
      accountId: 102,
      businessReference: "BR-KLA-012",
      active: true,
      logo: "https://cdn.example.com/logos/greenline.png",
      package: "LOGISTICS"
    },
    {
      partnerName: "Metro Transport Co.",
      accountNumber: "MT-003451",
      contactPerson: "Dennis Mugisha",
      contactPhone: "+256782119988",
      accountId: 103,
      businessReference: "BR-MTR-017",
      active: false,
      logo: "https://cdn.example.com/logos/metro.png",
      package: "TRANSPORT"
    },
    {
      partnerName: "Blue Horizon Shuttles",
      accountNumber: "BHS-004200",
      contactPerson: "Linda Kabanda",
      contactPhone: "+256756890123",
      accountId: 104,
      businessReference: "BR-BHS-023",
      active: true,
      logo: "https://cdn.example.com/logos/blue_horizon.png",
      package: "TRANSPORT"
    },
    {
      partnerName: "CargoLink Africa",
      accountNumber: "CLA-005330",
      contactPerson: "Peter Owor",
      contactPhone: "+256709553312",
      accountId: 105,
      businessReference: "BR-CLA-009",
      active: true,
      logo: "https://cdn.example.com/logos/cargolink.png",
      package: "LOGISTICS"
    },
    {
      partnerName: "PrimeBus Services",
      accountNumber: "PBS-006789",
      contactPerson: "Rita Nakato",
      contactPhone: "+256773002210",
      accountId: 106,
      businessReference: "BR-PBS-041",
      active: false,
      logo: "https://cdn.example.com/logos/primebus.png",
      package: "TRANSPORT"
    },
    {
      partnerName: "Eastlink Freight",
      accountNumber: "EF-007512",
      contactPerson: "Andrew Katongole",
      contactPhone: "+256781145678",
      accountId: 107,
      businessReference: "BR-EF-056",
      active: true,
      logo: "https://cdn.example.com/logos/eastlink.png",
      package: "LOGISTICS"
    },
    {
      partnerName: "Kampala Shuttle Express",
      accountNumber: "KSE-008901",
      contactPerson: "Naomi Bwete",
      contactPhone: "+256755998877",
      accountId: 108,
      businessReference: "BR-KSE-077",
      active: true,
      logo: "https://cdn.example.com/logos/kampala_shuttle.png",
      package: "TRANSPORT"
    },
    {
      partnerName: "Lakeview Movers",
      accountNumber: "LM-009322",
      contactPerson: "Tom Waiswa",
      contactPhone: "+256704778899",
      accountId: 109,
      businessReference: "BR-LM-099",
      active: false,
      logo: "https://cdn.example.com/logos/lakeview.png",
      package: "LOGISTICS"
    },
    {
      partnerName: "Nation Ride Systems",
      accountNumber: "NRS-010654",
      contactPerson: "Brenda Kisakye",
      contactPhone: "+256776221144",
      accountId: 110,
      businessReference: "BR-NRS-100",
      active: true,
      logo: "https://cdn.example.com/logos/nation_ride.png",
      package: "FULL"
    }
  ];

  newModule = {
    name: '',
    description: '',
    version: '1.0.0',
    type: 'core',
    priority: 'medium',
    autoStart: false
  };

  modules = [
    {name:"transport",description:"transport module"},
    {name:"logistics",description:"logistics module"}
  ];

  roles = [
    { value: 'USER', label: 'User' },
    { value: 'ADMIN', label: 'Administrator' },
    { value: 'MANAGER', label: 'Manager' }
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private loaderService: LoaderService
  ) {}


  partnerFilter = '';
  filteredPartners: Partner[] = [...this.partners];

  ngOnInit() {
    this.filteredPartners = [...this.partners];
  }

  filterPartners() {
    const filter = this.partnerFilter.toLowerCase().trim();

    if (!filter) {
      this.filteredPartners = [...this.partners];
      return;
    }

    this.filteredPartners = this.partners.filter(partner =>
      partner.partnerName.toLowerCase().includes(filter) ||
      partner.accountNumber.toLowerCase().includes(filter) ||
      partner.contactPerson.toLowerCase().includes(filter)
    );
  }

  addPartner() {
    // Implement your add partner logic here
    this.notificationService.showInfo('Add Partner functionality to be implemented');

  }

  addModule() {
    if (!this.newModule.name.trim() || !this.newModule.description.trim()) {
      this.notificationService.showWarning('Please fill in all fields before saving');
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
    this.notificationService.showSuccess('New module added successfully');
    this.showAddDialog = false;
  }




  onRegister() {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;
    this.loaderService.display(true);

    const userData: CreateUserRequest = {
      data: {
        role: this.role,
        first_name: this.firstName,
        last_name: this.lastName,
        email: this.email,
        password: this.password,
        ...(this.partner && { partner: this.partner })
      }
    };

    this.authService.createUser(userData).subscribe({
      next: (response) => {
        this.notificationService.showSuccess(
          'Account created successfully! You can now log in.',
          'Registration Successful'
        );
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.notificationService.showError(
          error.message || 'Registration failed. Please try again.',
          'Registration Failed'
        );
        console.error('Registration error:', error);
      },
      complete: () => {
        this.isLoading = false;
        this.loaderService.display(false);
      }
    });
  }

  private validateForm(): boolean {
    if (!this.firstName.trim()) {
      this.notificationService.showWarning('Please enter your first name');
      return false;
    }

    if (!this.lastName.trim()) {
      this.notificationService.showWarning('Please enter your last name');
      return false;
    }

    if (!this.email.trim()) {
      this.notificationService.showWarning('Please enter your email');
      return false;
    }

    if (!this.isValidEmail(this.email)) {
      this.notificationService.showWarning('Please enter a valid email address');
      return false;
    }

    if (!this.password) {
      this.notificationService.showWarning('Please enter a password');
      return false;
    }

    if (this.password.length < 6) {
      this.notificationService.showWarning('Password must be at least 6 characters long');
      return false;
    }

    if (this.password !== this.confirmPassword) {
      this.notificationService.showWarning('Passwords do not match');
      return false;
    }

    return true;
  }

  private isValidEmail(email: string): boolean {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailPattern.test(email);
  }

}
