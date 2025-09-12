import {Injectable} from '@angular/core';
import {BehaviorSubject, interval, Observable, Subscription, throttle} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class IdleService {

  constructor() {
    this.resetTimer();
    this.startWatching();
  }

  private idleSubject = new BehaviorSubject<boolean>(false);
  private timeout = 180;
  private lastActivity?: Date;
  private idleCheckInterval = 10;
  private idleSubscription?: Subscription;

  get idleState(): Observable<boolean> {
    return this.idleSubject.asObservable();
  }

  private startWatching(){
    this.idleSubscription = interval(this.idleCheckInterval * 1000)
      .pipe(throttle(() => interval(1000)))
      .subscribe(()=>{
        const now = new Date();

        if (now.getTime() - this.lastActivity?.getTime()! > this.timeout * 1000){
          this.idleSubject.next(true);
        }
      })
  }

  resetTimer(){
    this.lastActivity = new Date();
    this.idleSubject.next(false);
  }

  stopWatching(){
    if(this.idleSubscription){
      this.idleSubscription.unsubscribe();
    }
  }
}
