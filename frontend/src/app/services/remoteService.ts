import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RemoteService {
  constructor(protected httpClient: HttpClient) {
  }

  sendPostToServer(url: string, data: string): Observable<any> {
    console.log("Will use url " + url)
    this.logDevMode("Sending to server " + data)
    let options = {
      headers: new HttpHeaders({
        'Content-Type': 'text/plain',
        'Request-Origin': 'BLIP-PORTAL'
      }),
    };
    return this.httpClient.post(url, data, options);
  }

  sendPutToServer(url: string, data: string): Observable<any> {
    console.log("Will use url " + url)
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
    console.log("Will use url " + url)
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
    console.log("Will use url " + url);
    let options = {
      headers: new HttpHeaders({
        'Content-Type': 'text/plain',
        'Request-Origin': 'BLIP-PORTAL'
      }),
    };
    return this.httpClient.get(url, options);
  }

  logDevMode(event: string) {
    // if (!environment.production) console.log(event);
    console.log(event);
  }
}
