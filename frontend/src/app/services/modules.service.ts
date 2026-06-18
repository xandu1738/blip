import { Injectable } from '@angular/core';
import { RemoteService } from './remoteService';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { ModuleModel } from '../components/models/module.model';

@Injectable({
  providedIn: 'root'
})
export class ModulesService {

  constructor(private remoteService: RemoteService, private authService: AuthService) { }

  public fetchModules(pageNumber: number, pageSize: number): Observable<any> {
    return this.remoteService.sendGetToServer(`${this.authService.apiUrl}/modules/list/${pageNumber}/${pageSize}`);
  }

  public fetchModuleDetail(id: number): Observable<any> {
    return this.remoteService.sendGetToServer(`${this.authService.apiUrl}/modules/${id}`);
  }

  public addModule(module: ModuleModel): Observable<any> {
    return this.remoteService.sendPostToServer(`${this.authService.apiUrl}/modules/add-module`, { data: module });
  }

  public editModule(module: ModuleModel): Observable<any> {
    return this.remoteService.sendPostToServer(`${this.authService.apiUrl}/modules/edit-module`, { data: module });
  }

  public archiveModule(id: number): Observable<any> {
    return this.remoteService.sendDeleteToServer(`${this.authService.apiUrl}/modules/${id}`);
  }

  public subscribeToModule(partnerCode: string, moduleCode: string, startDate: string, endDate: string): Observable<any> {
    return this.remoteService.sendPostToServer(`${this.authService.apiUrl}/modules/subscribe`, {
      data: {
        partner_code: partnerCode,
        module_code: moduleCode,
        start_date: startDate,
        end_date: endDate
      }
    });
  }
}
