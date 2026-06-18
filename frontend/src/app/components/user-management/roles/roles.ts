import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../../services/base-component';
import {Accordion, AccordionContent, AccordionHeader, AccordionPanel} from 'primeng/accordion';
import {Button} from 'primeng/button';
import {DomainPicker} from '../../fragments/domain-picker/domain-picker';
import {InputText} from 'primeng/inputtext';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TableModule} from 'primeng/table';

@Component({
  selector: 'app-roles',
  imports: [
    Accordion,
    AccordionContent,
    AccordionHeader,
    AccordionPanel,
    Button,
    DomainPicker,
    InputText,
    ReactiveFormsModule,
    TableModule,
    FormsModule
  ],
  templateUrl: './roles.html',
  styleUrl: './roles.css',
})
export class Roles extends BaseComponent implements OnInit {
  roleData: any = {};
  override ngOnInit() {
    super.ngOnInit();
    this.loadLazy(null);
  }

  roles: any[] = [];
  protected search: any = {
    first: 0,
    rows: 15,
    totalRecords: 10
  };

  loadLazy(event: any) {
    this.sendGetOrPostRequestToServer(
      "management/our-roles",
      null,
      true,
      (response: any) => {
        if (response?.returnCode !== 200) return;
        this.roles = response?.returnObject;
      },
      true
    );
  }

  protected filterRoles() {
    //Filter logic
  }

  protected onSelectDomain($event: any) {
    this.roleData.domain = $event;
  }

  protected addRole() {
    //Create Role
  }
}
