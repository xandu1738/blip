import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RemoteService {
  constructor(protected httpClient: HttpClient) {
  }

  sendPostToServer(url: string, data: any, authenticated: boolean = true): Observable<any> {
    this.logDevMode("Will use url " + url)
    this.logDevMode("Sending to server " + data)
    let requestHeaders: any = {
      'Content-Type': 'application/json',
      'Request-Origin': 'BLIP-PORTAL'
    };

    if (authenticated) {
      const accessToken = localStorage.getItem("access_token");
      requestHeaders['Authorization'] = `Bearer ${accessToken}`;
    }

    let options = {
      headers: new HttpHeaders(requestHeaders),
    };
    return this.httpClient.post(url, data, options);
  }

  sendPutToServer(url: string, data: string): Observable<any> {
    this.logDevMode("Will use url " + url)
    this.logDevMode("Sending to server " + data)
    let options = {
      headers: new HttpHeaders({
        'Content-Type': 'text/plain',
        'Request-Origin': 'BLIP-PORTAL'
      }),
    };
    return this.httpClient.put(url, data, options);
  }

  sendDeleteToServer(url: string, data: string): Observable<any> {
    this.logDevMode("Will use url " + url)
    this.logDevMode("Sending to server " + data)
    let options = {
      headers: new HttpHeaders({
        'Content-Type': 'text/plain',
        'Request-Origin': 'BLIP-PORTAL'
      }),
    };
    return this.httpClient.delete(url, options);
  }

  sendGetToServer(url: string): Observable<any> {
    this.logDevMode("Will use url " + url);
    const accessToken = localStorage.getItem("blip_access_token");
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Request-Origin': 'BLIP-PORTAL',
      'Authorization': `Bearer ${accessToken}`
    });

    return this.httpClient.get(url, {headers});
  }


  logDevMode(event: string) {
    if (!environment.production) console.log(event);
  }
}
