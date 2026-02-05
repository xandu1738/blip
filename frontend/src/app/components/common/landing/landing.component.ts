import {Component, OnInit} from '@angular/core';
import {RouterLink} from '@angular/router';
import {BaseComponent} from '../../services/base-component';

@Component({
  selector: 'app-landing',
  imports: [
    RouterLink
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css',
})
export class LandingComponent extends BaseComponent implements OnInit{
}
