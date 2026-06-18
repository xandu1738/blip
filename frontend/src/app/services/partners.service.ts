import { Injectable } from '@angular/core';
import { RemoteService } from './remoteService';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { PartnerModel } from '../components/models/partner.model';

@Injectable({
  providedIn: 'root'
})
export class PartnersService {

  constructor(private remoteService: RemoteService, private authService: AuthService) { }

  public fetchPartners(pageNumber: number, pageSize: number): Observable<any> {
    return this.remoteService.sendGetToServer(`${this.authService.apiUrl}/partners/list/${pageNumber}/${pageSize}`);
  }

  public fetchPartnerProfile(partnerCode: string): Observable<any> {
    return this.remoteService.sendGetToServer(`${this.authService.apiUrl}/partners/profile/${partnerCode}`);
  }

  public addPartner(partner: PartnerModel): Observable<any> {
    return this.remoteService.sendPostToServer(`${this.authService.apiUrl}/partners/add-partner`, { data: partner });
  }

  public editPartner(partner: PartnerModel): Observable<any> {
    return this.remoteService.sendPostToServer(`${this.authService.apiUrl}/partners/edit-partner-info`, { data: partner });
  }

  public archivePartner(id: number): Observable<any> {
    return this.remoteService.sendPostToServer(`${this.authService.apiUrl}/partners/archive-partner`, { data: { id } });
  }
}

