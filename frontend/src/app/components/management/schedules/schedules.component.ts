import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BaseComponent} from '../../../services/base-component';
import {ScheduleService} from '../../../services/schedule.service';
import {LoaderService} from '../../../services/loader.service';
import {DialogService} from 'primeng/dynamicdialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../../services/auth.service';
import {Router} from '@angular/router';
import {TableModule} from 'primeng/table';
import {ButtonModule} from 'primeng/button';
import {RemoteService} from '../../../services/remoteService';
import {AccordionModule} from 'primeng/accordion';
import {FormsModule} from '@angular/forms';
import {SelectModule} from 'primeng/select';
import {TooltipModule} from 'primeng/tooltip';

@Component({
  selector: 'app-schedules',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    AccordionModule,
    FormsModule,
    SelectModule,
    TooltipModule
  ],
  templateUrl: './schedules.html',
  styleUrl: './schedules.css'
})
export class SchedulesComponent extends BaseComponent implements OnInit {
  schedules: any[] = [];
  search: any = {};

  stats: any[] = [
    {label: 'Active Schedules', value: 0, icon: 'pi pi-calendar', color: 'text-blue-500', bgColor: 'bg-blue-100'},
    {label: 'Weekly Runs', value: 0, icon: 'pi pi-sync', color: 'text-orange-500', bgColor: 'bg-orange-100'},
    {label: 'Routes Covered', value: 0, icon: 'pi pi-map', color: 'text-green-500', bgColor: 'bg-green-100'}
  ];

  constructor(
    private scheduleService: ScheduleService,
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
    this.loadSchedules();
  }

  loadSchedules() {
    this.scheduleService.getSchedules().subscribe((res) => {
      if (res.returnCode !== 200) {
        this.showError(res.returnMessage);
        return;
      }
      this.schedules = res?.returnObject;
      this.updateStats();
    });
  }

  updateStats() {
    this.stats[0].value = this.schedules?.length;
    // Count total weekly runs (sum of days selected across all schedules)
    this.stats[1].value = this.schedules?.reduce((acc, s) => acc + (s?.daysOfWeek ? s?.daysOfWeek?.length : 0), 0);
    // Count unique route IDs
    this.stats[2].value = new Set(this.schedules?.map(s => s?.routeId))?.size;
  }

  addSchedule() {
    this.router.navigate(['/schedule-form']);
  }

  editSchedule(schedule: any) {
    this.router.navigate(['/schedule-form', schedule.id]);
  }

  deleteSchedule(schedule: any) {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this schedule?',
      header: 'Delete Confirmation',
      accept: () => {
        this.scheduleService.removeSchedule(schedule.id).subscribe({
          next: () => {
            this.messageService.add({severity: 'success', summary: 'Success', detail: 'Schedule removed successfully'});
            this.loadSchedules();
          }
        });
      }
    });
  }

  recordTrip(schedule: any) {
    this.router.navigate(['/record-trip', schedule.id]);
  }
}
