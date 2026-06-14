import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../../services/base-component';
import {Button} from 'primeng/button';
import {Users} from '../../user-management/users/users';
import {Roles} from '../../user-management/roles/roles';
import {Domains} from '../../user-management/domains/domains';

@Component({
  selector: 'app-access-management',
  imports: [
    Button,
    Users,
    Roles,
    Domains
  ],
  templateUrl: './access-management.html',
  styleUrl: './access-management.css',
})
export class AccessManagement extends BaseComponent implements OnInit {
  override ngOnInit() {
    super.ngOnInit();
  }

  showUserManagement: boolean = true;
  showRoleManagement: boolean = false;
  showDomainManagement: boolean = false;

  protected toggleManagement(code: string) {
    switch (code) {
      case 'USERS':
        this.showUserManagement = true;
        this.showRoleManagement = false;
        this.showDomainManagement = false;
        break;
      case 'ROLES':
        this.showUserManagement = false;
        this.showRoleManagement = true;
        this.showDomainManagement = false;
        break;
      case 'DOMAINS':
        this.showUserManagement = false;
        this.showRoleManagement = false;
        this.showDomainManagement = true;
        break;
      default:
        throw new Error("Invalid management code!");
    }
  }
}
