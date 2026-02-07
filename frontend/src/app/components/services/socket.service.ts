import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class SocketService {
    private client: Client;
    private connected$ = new BehaviorSubject<boolean>(false);
    private messages$ = new BehaviorSubject<any>(null);

    constructor(private authService: AuthService) {
        this.client = new Client({
            webSocketFactory: () => new SockJS(`${this.authService.apiUrl.replace('/api/v1', '')}/ws`),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        this.client.onConnect = (frame) => {
            console.log('Connected to WebSocket');
            this.connected$.next(true);

            this.client.subscribe('/topic/events', (message: Message) => {
                if (message.body) {
                    console.log('Received message from Redis:', message.body);
                    try {
                        this.messages$.next(JSON.parse(message.body));
                    } catch (e) {
                        this.messages$.next(message.body);
                    }
                }
            });
        };

        this.client.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };
    }

    public activate() {
        this.client.activate();
    }

    public deactivate() {
        this.client.deactivate();
    }

    public getMessages(): Observable<any> {
        return this.messages$.asObservable();
    }
}
