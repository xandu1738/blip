import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class CommonService {
  confirmDialogSettings = {
    header: 'Confirmation',
    icon: 'pi pi-exclamation-triangle'
  };
}
