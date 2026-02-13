import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RemoteService } from './remoteService';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class TripService {
    protected apiUrl: string;

    constructor(
        protected helper: RemoteService,
        protected authService: AuthService
    ) {
        this.apiUrl = `${this.authService.apiUrl}/trips`;
    }

    addTrip(trip: any): Observable<any> {
        return this.helper.sendPostToServer(`${this.apiUrl}/add-trip`, { data: trip });
    }

    editTrip(trip: any): Observable<any> {
        return this.helper.sendPostToServer(`${this.apiUrl}/edit-trip`, { data: trip });
    }

    getTripsList(page: number, size: number): Observable<any> {
        return this.helper.sendGetToServer(`${this.apiUrl}/list/${page}/${size}`);
    }

    getTripDetails(id: number): Observable<any> {
        return this.helper.sendGetToServer(`${this.apiUrl}/detail/${id}`);
    }

    removeTrip(id: number): Observable<any> {
        return this.helper.sendDeleteToServer(`${this.apiUrl}/remove/${id}`,"");
    }
}
