import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../../services/base-component';
import {RouteService} from '../../../services/route.service';
import {LoaderService} from '../../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {RemoteService} from '../../../services/remoteService';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Button} from 'primeng/button';
import {Select} from 'primeng/select';
import {InputNumber} from 'primeng/inputnumber';

@Component({
  selector: 'app-route-form',
  standalone: true,
  imports: [
    FormsModule,
    InputText,
    Button,
    Select,
    InputNumber
  ],
  templateUrl: './route-form.html',
  styleUrl: './route-form.css'
})
export class RouteForm extends BaseComponent implements OnInit {
  route: any = {
    origin: '',
    destination: '',
    estimatedDistance: 0,
    estimatedDuration: 0,
    status: 'ACTIVE',
    partnerCode: ''
  };
  isEdit: boolean = false;
  districts: any[] = [];

  constructor(
    private routeService: RouteService,
    private activatedRoute: ActivatedRoute,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    authService: AuthService,
    helper: RemoteService,
    private router: Router
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit() {
    super.ngOnInit();
    this.loadDistricts();
    const id = this.activatedRoute.snapshot.params['id'];
    if (id) {
      this.isEdit = true;
      this.loadRouteDetails(id);
    } else {
      this.route.partnerCode = this.user?.partnerCode || '';
    }
  }

  loadDistricts() {
    this.routeService.getDistricts().subscribe({
      next: (res) => {
        if (res.returnObject) {
          this.districts = res.returnObject.map((d: any) => ({label: d.name, value: d.name}));
        }
      }
    });
  }

  loadRouteDetails(id: number) {
    this.routeService.getRouteDetails(id).subscribe({
      next: (res) => {
        if (res.returnObject) {
          this.route = res.returnObject;
          // API returns estimatedDuration as Double, but we use it as hrs
        }
      }
    });
  }

  saveRoute() {
    this.showLoader = true;
    const action = this.isEdit ? this.routeService.editRoute(this.route) : this.routeService.createRoute(this.route);

    action.subscribe({
      next: (res) => {
        this.showLoader = false;
        if (res.returnCode !== 200) {
          this.showError(res?.returnMessage);
          return;
        }

        this.showSuccess(res?.returnMessage);
        this.router.navigate(['/routes']);
      },
      error: () => {
        this.showLoader = false;
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Operation failed'});
      }
    });
  }

  cancel() {
    this.router.navigate(['/routes']);
  }
}
