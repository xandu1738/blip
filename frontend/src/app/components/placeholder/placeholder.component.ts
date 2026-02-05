import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-placeholder',
  standalone: true,
  imports: [CommonModule],
  template: '<p>This is a placeholder page for {{ pageName }}</p>',
  styles: ['']
})
export class PlaceholderComponent {
  pageName: string = '';

  constructor() {
    // You can set pageName dynamically based on route data if needed
    // For now, it will be set by the route configuration
  }
}
