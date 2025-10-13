// dashboard.component.html template

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Stat {
  label: string;
  value: string;
  icon: string;
  color: string;
  bgColor: string;
}

interface Activity {
  type: string;
  id: string;
  status: string;
  client: string;
  date: string;
  statusColor: string;
  icon: string;
}

interface Notification {
  id: number;
  type: string;
  title: string;
  message: string;
  time: string;
  color: string;
}

interface Expense {
  id: number;
  category: string;
  description: string;
  amount: number;
  date: string;
  vehicle?: string;
  type: 'fuel' | 'maintenance' | 'salary' | 'insurance' | 'other';
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  stats: Stat[] = [
    {
      label: 'Total Bookings',
      value: '1,200',
      icon: 'bus',
      color: 'text-blue-600',
      bgColor: 'bg-blue-50'
    },
    {
      label: 'Parcels Sent',
      value: '850',
      icon: 'package',
      color: 'text-green-600',
      bgColor: 'bg-green-50'
    },
    {
      label: 'Active Consignments',
      value: '150',
      icon: 'truck',
      color: 'text-purple-600',
      bgColor: 'bg-purple-50'
    },
    {
      label: 'Revenue',
      value: 'UGX 12M',
      icon: 'dollar-sign',
      color: 'text-amber-600',
      bgColor: 'bg-amber-50'
    },
    {
      label: 'Fleet Utilization',
      value: '82%',
      icon: 'activity',
      color: 'text-indigo-600',
      bgColor: 'bg-indigo-50'
    },
    {
      label: 'Active Clients',
      value: '430',
      icon: 'users',
      color: 'text-pink-600',
      bgColor: 'bg-pink-50'
    }
  ];

  recentActivity: Activity[] = [
    {
      type: 'Bus Booking',
      id: '#B-12345',
      status: 'Confirmed',
      client: 'John Doe',
      date: '2024-05-20',
      statusColor: 'bg-green-100 text-green-800',
      icon: 'bus'
    },
    {
      type: 'Parcel Delivery',
      id: '#P-67890',
      status: 'In Transit',
      client: 'Jane Smith',
      date: '2024-05-19',
      statusColor: 'bg-yellow-100 text-yellow-800',
      icon: 'package'
    },
    {
      type: 'Freight Consignment',
      id: '#C-54321',
      status: 'Delivered',
      client: 'Bob Johnson',
      date: '2024-05-18',
      statusColor: 'bg-blue-100 text-blue-800',
      icon: 'truck'
    },
    {
      type: 'Bus Booking',
      id: '#B-98765',
      status: 'Pending',
      client: 'Alice Brown',
      date: '2024-05-17',
      statusColor: 'bg-gray-100 text-gray-800',
      icon: 'bus'
    }
  ];

  notifications: Notification[] = [
    {
      id: 1,
      type: 'success',
      title: 'Booking Confirmed',
      message: 'Bus booking #B-12345 has been confirmed',
      time: '5 min ago',
      color: 'text-green-500'
    },
    {
      id: 2,
      type: 'warning',
      title: 'Delivery Delay',
      message: 'Parcel #P-67890 may experience delays due to weather',
      time: '15 min ago',
      color: 'text-amber-500'
    },
    {
      id: 3,
      type: 'info',
      title: 'New Parcel Received',
      message: 'Parcel #P-11223 has arrived at Kampala warehouse',
      time: '1 hour ago',
      color: 'text-blue-500'
    },
    {
      id: 4,
      type: 'success',
      title: 'Payment Received',
      message: 'Payment of UGX 450,000 received for booking #B-98765',
      time: '2 hours ago',
      color: 'text-green-500'
    },
    {
      id: 5,
      type: 'info',
      title: 'Route Update',
      message: 'Route Kampala-Mbarara has been updated with new stops',
      time: '3 hours ago',
      color: 'text-purple-500'
    }
  ];

  expenses: Expense[] = [
    {
      id: 1,
      category: "Fuel",
      description: "Diesel for UAH 234X - Kampala route",
      amount: 450000,
      date: "2024-10-13",
      vehicle: "UAH 234X",
      type: 'fuel'
    },
    {
      id: 2,
      category: "Maintenance",
      description: "Oil change and brake service",
      amount: 280000,
      date: "2024-10-12",
      vehicle: "UAG 567Y",
      type: 'maintenance'
    },
    {
      id: 3,
      category: "Driver Salary",
      description: "Weekly payment - 5 drivers",
      amount: 1500000,
      date: "2024-10-11",
      type: 'salary'
    },
    {
      id: 4,
      category: "Insurance",
      description: "Monthly premium - Fleet coverage",
      amount: 800000,
      date: "2024-10-10",
      type: 'insurance'
    },
    {
      id: 5,
      category: "Fuel",
      description: "Petrol for UAB 890Z - Jinja route",
      amount: 320000,
      date: "2024-10-13",
      vehicle: "UAB 890Z",
      type: 'fuel'
    }
  ];

  getExpenseIcon(type: string): string {
    const icons: {[key: string]: string} = {
      'fuel': 'fuel',
      'maintenance': 'wrench',
      'salary': 'user',
      'insurance': 'shield',
      'other': 'receipt'
    };
    return icons[type] || 'receipt';
  }

  getExpenseColor(type: string): string {
    const colors: {[key: string]: string} = {
      'fuel': 'text-orange-500',
      'maintenance': 'text-blue-500',
      'salary': 'text-green-500',
      'insurance': 'text-purple-500',
      'other': 'text-gray-500'
    };
    return colors[type] || 'text-gray-500';
  }

  formatCurrency(amount: number): string {
    return `UGX ${amount.toLocaleString()}`;
  }
}
