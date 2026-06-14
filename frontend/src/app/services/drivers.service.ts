import { Injectable } from '@angular/core';
import { RemoteService } from './remoteService';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { Driver } from '../components/models/driver.model';

@Injectable({
    providedIn: 'root'
})
export class DriversService {
    constructor(
        private remoteService: RemoteService,
        private authService: AuthService
    ) {
    }

    addNewDriver(driver: Driver): Observable<any> {
        return this.remoteService.sendPostToServer(
            `${this.authService.apiUrl}/drivers/add-driver`,
            { data: driver }
        );
    }

    editDriver(driver: Driver): Observable<any> {
        return this.remoteService.sendPostToServer(
            `${this.authService.apiUrl}/drivers/edit-driver-info`,
            { data: driver }
        );
    }

    getDriversList(pageNumber: number, pageSize: number): Observable<any> {
        return this.remoteService.sendGetToServer(
            `${this.authService.apiUrl}/drivers/list/${pageNumber}/${pageSize}`
        );
    }

    fetchDriverDetails(driverId: number): Observable<any> {
        return this.remoteService.sendGetToServer(
            `${this.authService.apiUrl}/drivers/${driverId}`
        );
    }

    deleteDriver(driverId: number): Observable<any> {
        return this.remoteService.sendDeleteToServer(
            `${this.authService.apiUrl}/drivers/${driverId}`
        );
    }
}
