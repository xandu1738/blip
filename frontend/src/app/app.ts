import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import {Button} from 'primeng/button';
import {RouterOutlet} from '@angular/router';
import {CommonService} from './services/commonService';

@Component({
  selector: 'app-root',
  imports: [Button, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  constructor(protected commonService: CommonService) {
  }

  ngOnInit() {
    this.showLoader = this.commonService.showLoader;
  }

  protected currentTheme: WritableSignal<string> = signal('light');
  showLoader: any;

  toggleDarkMode() {
    const element = document.querySelector('html')!;
    element.classList.toggle('dark-mode');
    this.currentTheme.set(element.classList.contains('dark-mode') ? 'dark' : 'light');
  }
}
