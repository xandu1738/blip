import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ConsignmentService {
    private apiUrl = `${environment.apiUrl}/consignments`;

    constructor(private http: HttpClient) { }

    createConsignment(data: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/create`, { data });
    }

    assignParcel(data: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/assign`, { data });
    }

    getConsignmentsList(): Observable<any> {
        return this.http.get(`${this.apiUrl}/list`);
    }

    getStats(): Observable<any> {
        return this.http.get(`${this.apiUrl}/stats`);
    }
}
