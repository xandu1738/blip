import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartModule } from 'primeng/chart';

interface Stat {
  label: string;
  value: string;
  sub: string;
  icon: string;
  iconClass: string;
  trend?: string;
  trendUp?: boolean;
}

interface Trip {
  date: string;
  route: string;
  type: string;
  typeIcon: string;
  status: string;
  statusClass: string;
  amount: string;
}

interface ExpenseSummary {
  daily: string;
  weekly: string;
  monthly: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ChartModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  ngOnInit() {
    this.initChart();
  }

  /** Hero card info */
  companyName = 'BLIP Transport Control';
  partnerCode = 'BLP-KLA-2024';
  totalRevenue = 'UGX 12,450,000';

  /** Expense summary */
  expenseSummary: ExpenseSummary = {
    daily: 'UGX 450K',
    weekly: 'UGX 2.8M',
    monthly: 'UGX 11.2M',
  };

  /** Mini stat cards */
  stats: Stat[] = [
    {
      label: 'Total Bookings',
      value: '1,200',
      sub: '+8% this month',
      icon: 'pi-ticket',
      iconClass: 'icon-blue',
      trendUp: true,
    },
    {
      label: 'Parcels Sent',
      value: '850',
      sub: '+12% this month',
      icon: 'pi-box',
      iconClass: 'icon-green',
      trendUp: true,
    },
    {
      label: 'Active Consignments',
      value: '150',
      sub: '32 in transit',
      icon: 'pi-truck',
      iconClass: 'icon-orange',
    },
    {
      label: 'Fleet Utilization',
      value: '82%',
      sub: '18 of 22 vehicles',
      icon: 'pi-gauge',
      iconClass: 'icon-purple',
    },
  ];

  /** Recent trips table */
  recentTrips: Trip[] = [
    {
      date: 'Today',
      route: 'Kampala → Mbarara',
      type: 'Express Bus',
      typeIcon: 'pi-car',
      status: 'Confirmed',
      statusClass: 'status-green',
      amount: 'UGX 85,000',
    },
    {
      date: 'Today',
      route: 'Jinja → Kampala',
      type: 'Parcel',
      typeIcon: 'pi-box',
      status: 'In Transit',
      statusClass: 'status-amber',
      amount: 'UGX 32,000',
    },
    {
      date: '25 Feb',
      route: 'Kampala → Gulu',
      type: 'Express Bus',
      typeIcon: 'pi-car',
      status: 'Delivered',
      statusClass: 'status-blue',
      amount: 'UGX 150,000',
    },
    {
      date: '25 Feb',
      route: 'Entebbe → Kampala',
      type: 'Freight',
      typeIcon: 'pi-truck',
      status: 'Confirmed',
      statusClass: 'status-green',
      amount: 'UGX 220,000',
    },
    {
      date: '24 Feb',
      route: 'Kampala → Fort Portal',
      type: 'Express Bus',
      typeIcon: 'pi-car',
      status: 'Pending',
      statusClass: 'status-gray',
      amount: 'UGX 90,000',
    },
    {
      date: '24 Feb',
      route: 'Masaka → Kampala',
      type: 'Parcel',
      typeIcon: 'pi-box',
      status: 'Delivered',
      statusClass: 'status-blue',
      amount: 'UGX 28,000',
    },
  ];

  /** Donut chart */
  chartData: any;
  chartOptions: any;

  initChart() {
    this.chartData = {
      labels: ['Fuel', 'Maintenance', 'Salary', 'Insurance', 'Other'],
      datasets: [
        {
          data: [4500000, 2800000, 1500000, 800000, 600000],
          backgroundColor: [
            '#f97316', // fuel — orange
            '#3b82f6', // maintenance — blue
            '#16a34a', // salary — green
            '#8b5cf6', // insurance — purple
            '#94a3b8', // other — slate
          ],
          borderWidth: 0,
          hoverOffset: 6,
        },
      ],
    };

    this.chartOptions = {
      cutout: '70%',
      plugins: {
        legend: {
          display: false,
        },
        tooltip: {
          callbacks: {
            label: (ctx: any) => {
              const val: number = ctx.raw;
              return ` UGX ${val.toLocaleString()}`;
            },
          },
        },
      },
    };
  }

  expenseCategories = [
    { label: 'Fuel', color: '#f97316' },
    { label: 'Maintenance', color: '#3b82f6' },
    { label: 'Salary', color: '#16a34a' },
    { label: 'Insurance', color: '#8b5cf6' },
    { label: 'Other', color: '#94a3b8' },
  ];
}
