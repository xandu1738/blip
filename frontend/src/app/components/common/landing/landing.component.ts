import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../services/base-component';
import {LoginComponent} from '../../login/login.component';

@Component({
  selector: 'app-landing',
  imports: [
    LoginComponent
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css',
})
export class LandingComponent extends BaseComponent implements OnInit {
  override ngOnInit() {
    super.ngOnInit();
  }

  loginVisible: boolean = false;

  protected gotoLogin() {
    this.loginVisible = true;
  }
  protected toLanding() {
    this.loginVisible = false;
  }
}
