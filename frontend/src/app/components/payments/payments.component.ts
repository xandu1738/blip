import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BaseComponent} from '../../services/base-component';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payments.html',
  styleUrl: './payments.css'
})
export class PaymentsComponent extends BaseComponent implements OnInit {
  override ngOnInit() {
    super.ngOnInit();
  }

  search: any = {};
  payments: any[] = [];

  loadPayments($event: any) {
    if ($event) {
      this.search.first = $event.first;
      this.search.rows = $event.rows;
      this.sendGetOrPostRequestToServer(
        "payments/0/15",
        {data: this.search},
        true,
        (response: any) => {
          if (response?.returnCode !== 200) return;
          this.payments = response?.returnObject?.rows;
          this.search.totalRecords = response?.returnObject?.totalRecords;
        },
        false
      )
    }
  }
}
