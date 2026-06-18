import { Injectable } from '@angular/core';
import { RemoteService } from './remoteService';
import { Observable } from 'rxjs';
import { ApiResponse, Schedule } from '../components/models/user.models';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class ScheduleService {
    protected apiUrl: string;

    constructor(
        protected helper: RemoteService,
        protected authService: AuthService
    ) {
        this.apiUrl = `${this.authService.apiUrl}/schedules`;
    }

    addSchedule(schedule: Schedule): Observable<ApiResponse<Schedule>> {
        return this.helper.sendPostToServer(`${this.apiUrl}/add`, { data: schedule });
    }

    getSchedules(): Observable<ApiResponse<Schedule[]>> {
        return this.helper.sendGetToServer(`${this.apiUrl}/list`);
    }

    removeSchedule(id: number): Observable<ApiResponse<any>> {
        return this.helper.sendDeleteToServer(`${this.apiUrl}/remove/${id}`);
    }
}
