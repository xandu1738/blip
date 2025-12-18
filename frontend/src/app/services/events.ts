import { Injectable } from '@angular/core';
import {Subject} from 'rxjs';
import {Client, IMessage} from '@stomp/stompjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root',
})
export class Events {
  private stompClient!: Client;
  private eventStream = new Subject<string>();

  events$ = this.eventStream.asObservable();

  connect() {
    console.log("Attempting to connect to websocket...")
    // const socket = new SockJS('http://localhost:7071/ws');
    // this.stompClient = Stomp.Stomp.over(socket);
    this.stompClient = new Client({
      brokerURL: 'ws://localhost:7071/ws',   // native websocket
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str: string) => console.log(str)
    });

    // this.stompClient.connect({}, () => {
    //   this.stompClient.subscribe('/topic/events', (message: any) => {
    //     this.eventStream.next(message.body);
    //   });
    // });
    this.stompClient.onConnect = frame => {
      console.log('Connected:', frame);
      this.stompClient.subscribe('/topic/events', (msg: IMessage) => {
        this.eventStream.next(msg.body);
      });
    };
  }
}
