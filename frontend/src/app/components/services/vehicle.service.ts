import {Injectable} from '@angular/core';
import {RemoteService} from './remoteService';
import {AuthService} from './auth.service';
import {Observable} from 'rxjs';
import {Vehicle} from '../models/vehicle.model';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {
  constructor(
    private remoteService: RemoteService,
    private authService: AuthService
  ) {
  }

  addNewVehicle(vehicle: Vehicle): Observable<any> {
    return this.remoteService.sendPostToServer(
      `${this.authService.apiUrl}/vehicles/add-vehicle`,
      {data: vehicle}
    );
  }

  editVehicleInformation(vehicle: Vehicle): Observable<any> {
    return this.remoteService.sendPostToServer(
      `${this.authService.apiUrl}/vehicles/edit-vehicle-info`,
      {data: vehicle}
    );
  }

  getVehiclesList(pageNumber: number, pageSize: number): Observable<any> {
    return this.remoteService.sendGetToServer(
      `${this.authService.apiUrl}/vehicles/list/${pageNumber}/${pageSize}`
    );
  }

  fetchVehicleDetails(vehicleId: number): Observable<any> {
    return this.remoteService.sendGetToServer(
      `${this.authService.apiUrl}/vehicles/${vehicleId}`
    );
  }

  assignVehicleToPartner(partnerCode: string, vehicleId: number): Observable<any> {
    return this.remoteService.sendGetToServer(
      `${this.authService.apiUrl}/vehicles/partner/${partnerCode}/${vehicleId}`
    );
  }
}
