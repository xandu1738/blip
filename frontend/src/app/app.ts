import {Component, signal, WritableSignal} from '@angular/core';
import {Button} from 'primeng/button';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [Button, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected currentTheme:WritableSignal<string> = signal('light');

  toggleDarkMode() {
    const element = document.querySelector('html')!;
    element.classList.toggle('dark-mode');
    this.currentTheme.set(element.classList.contains('dark-mode') ? 'dark' : 'light');
  }
}
