import { Injectable } from '@angular/core';
import { RemoteService } from './remoteService';
import { Observable } from 'rxjs';
import { Amenity } from '../components/models/amenity.model';
import { VehicleCategory } from '../components/models/vehicle-category.model';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ManagementService {
    private baseUrl = environment.apiUrl + '/management';

    constructor(private remote: RemoteService) { }

    fetchAmenities(): Observable<any> {
        return this.remote.sendGetToServer(this.baseUrl + '/amenities');
    }

    addAmenity(amenity: Amenity): Observable<any> {
        const data = {
            data: amenity
        };
        return this.remote.sendPostToServer(this.baseUrl + '/add-amenity', data);
    }

    fetchVehicleCategories(): Observable<any> {
        return this.remote.sendGetToServer(this.baseUrl + '/vehicle-categories');
    }

    addVehicleCategory(category: VehicleCategory): Observable<any> {
        const data = {
            data: {
                category_name: category.name,
                description: category.description,
                amenities: category.amenities
            }
        };
        return this.remote.sendPostToServer(this.baseUrl + '/add-vehicle-category', data);
    }
}
