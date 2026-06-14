import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseComponent } from '../../../services/base-component';
import { ScheduleService } from '../../../services/schedule.service';
import { RouteService } from '../../../services/route.service';
import { LoaderService } from '../../../services/loader.service';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AuthService } from '../../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RemoteService } from '../../../services/remoteService';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { DatePickerModule } from 'primeng/datepicker';
import { MultiSelectModule } from 'primeng/multiselect';

@Component({
  selector: 'app-schedule-form',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    FormsModule,
    InputTextModule,
    SelectModule,
    DatePickerModule,
    MultiSelectModule
  ],
  templateUrl: './schedule-form.html',
  styleUrl: './schedule-form.css'
})
export class ScheduleForm extends BaseComponent implements OnInit {
  schedule: any = {
    route_id: null,
    days_of_week: [],
    set_off_time: null,
    expected_arrival_time: null,
    status: 'ACTIVE'
  };

  routes: any[] = [];
  daysOptions = [
    { label: 'Monday', value: 'MON' },
    { label: 'Tuesday', value: 'TUE' },
    { label: 'Wednesday', value: 'WED' },
    { label: 'Thursday', value: 'THU' },
    { label: 'Friday', value: 'FRI' },
    { label: 'Saturday', value: 'SAT' },
    { label: 'Sunday', value: 'SUN' }
  ];
  isEditMode: boolean = false;

  constructor(
    protected scheduleService: ScheduleService,
    protected routeService: RouteService,
    loaderService: LoaderService,
    dialogService: DialogService,
    confirmationService: ConfirmationService,
    messageService: MessageService,
    authService: AuthService,
    helper: RemoteService,
    protected router: Router,
    protected route: ActivatedRoute
  ) {
    super(authService, helper, loaderService, dialogService, confirmationService, messageService);
  }

  override ngOnInit() {
    super.ngOnInit();
    this.loadInitialData();
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.loadSchedule(params['id']);
      }
    });
  }

  loadInitialData() {
    const partnerCode = this.user?.partnerCode || '';

    this.routeService.getRoutesList(partnerCode, 0, 100).subscribe({
      next: (res) => {
        this.routes = res?.returnObject?.map((r: any) => ({
          label: `${r.origin} -> ${r.destination}`,
          value: r.id
        })) || [];
      }
    });
  }

  loadSchedule(id: number) {
    // Note: Implementation for getScheduleById might be needed in ScheduleService if not there
    // For now assuming we have a way to get details.
    // If list already has it, we could pass it or fetch.
    this.scheduleService.getSchedules().subscribe({
      next: (res) => {
        const found = res.returnObject?.find((s: any) => s.id == id);
        if (found) {
          this.schedule = {
            id: found.id,
            route_id: found.routeId,
            days_of_week: found.daysOfWeek ? found.daysOfWeek.split(',') : [],
            set_off_time: found.setOffTime ? this.parseTime(found.setOffTime) : null,
            expected_arrival_time: found.expectedArrivalTime ? this.parseTime(found.expectedArrivalTime) : null,
            status: found.status
          };
        }
      }
    });
  }

  parseTime(timeStr: string): Date {
    const [hours, minutes, seconds] = timeStr.split(':').map(Number);
    const date = new Date();
    date.setHours(hours, minutes, seconds || 0, 0);
    return date;
  }

  saveSchedule() {
    const formatTime = (d: Date | null) => {
      if (!d) return null;
      const hours = String(d.getHours()).padStart(2, '0');
      const minutes = String(d.getMinutes()).padStart(2, '0');
      const seconds = String(d.getSeconds()).padStart(2, '0');
      return `${hours}:${minutes}:${seconds}`;
    };

    const payload = {
      ...this.schedule,
      route_id: this.schedule.route_id,
      days_of_week: this.schedule.days_of_week.join(','),
      set_off_time: formatTime(this.schedule.set_off_time),
      expected_arrival_time: formatTime(this.schedule.expected_arrival_time)
    };

    const action = this.scheduleService.addSchedule(payload);

    action.subscribe({
      next: (res) => {
        if (res.returnCode === 200) {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: `Schedule ${this.isEditMode ? 'updated' : 'created'} successfully`
          });
          this.router.navigate(['/schedules']);
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: res.returnMessage });
        }
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: `Failed to ${this.isEditMode ? 'update' : 'create'} schedule`
        });
      }
    });
  }

  cancel() {
    this.router.navigate(['/schedules']);
  }
}
