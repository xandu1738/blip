import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { SocketService } from './socket.service';
import { AuthService } from './auth.service';
import { MessageService } from 'primeng/api';

export interface Notification {
  id: string;
  title: string;
  message: string;
  type: string;
  timestamp: string;
  read: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications$ = new BehaviorSubject<Notification[]>([]);
  private unreadCount$ = new BehaviorSubject<number>(0);

  constructor(
    private socketService: SocketService,
    private authService: AuthService,
    private messageService: MessageService
  ) {
    this.init();
  }

  private init() {
    this.authService.currentUser.subscribe(user => {
      if (user && user.partnerCode && this.authService.hasPermission('RECEIVE_ADMIN_NOTIFICATION')) {
        // The socket service now handles this automatically on user login
      }
    });

    this.socketService.getMessages().subscribe(msg => {
      if (msg && msg.title) {
        if (this.authService.hasPermission('RECEIVE_ADMIN_NOTIFICATION')) {
          this.addNotification(msg);
        }
      }
    });
  }

  private addNotification(data: any) {
    const newNotification: Notification = {
      id: Math.random().toString(36).substring(2, 9),
      title: data.title,
      message: data.message,
      type: data.type,
      timestamp: data.timestamp,
      read: false
    };

    const current = this.notifications$.value;
    this.notifications$.next([newNotification, ...current]);
    this.unreadCount$.next(this.unreadCount$.value + 1);

    this.messageService.add({
      severity: 'info',
      summary: newNotification.title,
      detail: newNotification.message,
      life: 5000
    });
  }

  getNotifications(): Observable<Notification[]> {
    return this.notifications$.asObservable();
  }

  getUnreadCount(): Observable<number> {
    return this.unreadCount$.asObservable();
  }

  markAsRead(id: string) {
    const current = this.notifications$.value.map(n =>
      n.id === id ? { ...n, read: true } : n
    );
    this.notifications$.next(current);
    this.updateUnreadCount();
  }

  markAllAsRead() {
    const current = this.notifications$.value.map(n => ({ ...n, read: true }));
    this.notifications$.next(current);
    this.unreadCount$.next(0);
  }

  private updateUnreadCount() {
    const count = this.notifications$.value.filter(n => !n.read).length;
    this.unreadCount$.next(count);
  }
}
