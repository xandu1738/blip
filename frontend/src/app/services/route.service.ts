import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {RemoteService} from './remoteService';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RouteService {

  constructor(protected http: HttpClient,
              protected helper: RemoteService,
              protected authService: AuthService) {
  }

  getRoutesList(partnerCode: string | null, page: number, size: number): Observable<any> {
    if (!partnerCode) {
      partnerCode = null
    }
    return this.http.get<any>(`${this.authService.apiUrl}/routes/list/${partnerCode}/${page}/${size}`);
  }

  getRouteDetails(routeId: number): Observable<any> {
    return this.http.get<any>(`${this.authService.apiUrl}/routes/${routeId}`);
  }

  createRoute(route: any): Observable<any> {
    return this.http.post<any>(`${this.authService.apiUrl}/routes/add-route`, {data: route});
  }

  editRoute(route: any): Observable<any> {
    return this.http.post<any>(`${this.authService.apiUrl}/routes/edit-route-details`, {data: route});
  }

  getDistricts(): Observable<any> {
    return this.http.get<any>(`${this.authService.apiUrl}/management/districts`);
  }
}
