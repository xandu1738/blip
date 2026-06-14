import {Component, OnInit} from '@angular/core';
import {BaseComponent} from '../../../services/base-component';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-domains',
  imports: [

  ],
  templateUrl: './domains.html',
  styleUrl: './domains.css',
})
export class Domains extends BaseComponent implements OnInit{
  override ngOnInit() {
    super.ngOnInit();
    this.loadLazy(null);
  }

  domains: any[] = [];

  loadLazy(event: any) {
    this.sendGetOrPostRequestToServer(
      "management",
      null,
      true,
      (response: any) => {
        if (response?.returnCode !== 200) return;
        this.domains = response?.returnObject?.domains;
      },
      true
    );
  }

  getDynamicGradient(): string {
    const gradientMap: Record<string, string> = {
      blue: 'bg-gradient-to-br from-blue-400 via-blue-500 to-blue-600',
      green: 'bg-gradient-to-br from-green-400 via-green-500 to-green-600',
      purple: 'bg-gradient-to-br from-purple-400 via-purple-500 to-purple-600',
      orange: 'bg-gradient-to-br from-orange-400 via-orange-500 to-orange-600',
      pink: 'bg-gradient-to-br from-pink-400 via-pink-500 to-pink-600',
      red: 'bg-gradient-to-br from-red-400 via-red-500 to-red-600',
    };
    const gradients = Object.values(gradientMap);
    return gradients[Math.floor(Math.random() * gradients.length)];
  }

}
