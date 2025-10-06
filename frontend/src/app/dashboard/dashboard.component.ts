import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Stat {
  label: string;
  value: string;
  icon: string;
  color: string;
}

interface Activity {
  type: string;
  id: string;
  status: string;
  client: string;
  date: string;
  statusColor: string;
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
  type: string;
  unit_cost: number;
  quantity: number;
  total: number;
  code: string;
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
    { label: 'Total Bookings', value: '1,200', icon: '🚌', color: 'bg-blue-500' },
    { label: 'Parcels Sent', value: '850', icon: '📦', color: 'bg-green-500' },
    { label: 'Active Consignments', value: '150', icon: '📊', color: 'bg-purple-500' },
    { label: 'Revenue', value: 'UGX 12,500', icon: '💰', color: 'bg-amber-500' },
    { label: 'Revenue', value: 'UGX 12,500', icon: '💰', color: 'bg-amber-500' },
    { label: 'Revenue', value: 'UGX 12,500', icon: '💰', color: 'bg-amber-500' },
    { label: 'Revenue', value: 'UGX 12,500', icon: '💰', color: 'bg-amber-500' }

  ];

  recentActivity: Activity[] = [
    { type: 'Bus Booking', id: '#B-12345', status: 'Confirmed', client: 'John Doe', date: '2024-05-20', statusColor: 'bg-green-100 text-green-800' },
    { type: 'Parcel', id: '#P-67890', status: 'In Transit', client: 'Jane Smith', date: '2024-05-19', statusColor: 'bg-yellow-100 text-yellow-800' },
    { type: 'Consignment', id: '#C-54321', status: 'Delivered', client: 'Bob Johnson', date: '2024-05-18', statusColor: 'bg-blue-100 text-blue-800' },
    { type: 'Bus Booking', id: '#B-98765', status: 'Pending', client: 'Alice Brown', date: '2024-05-17', statusColor: 'bg-gray-100 text-gray-800' }
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
      message: 'Parcel #P-67890 may experience delays',
      time: '15 min ago',
      color: 'text-amber-500'
    },
    {
      id: 3,
      type: 'info',
      title: 'New Parcel Received',
      message: 'Parcel #P-11223 has arrived at warehouse',
      time: '1 hour ago',
      color: 'text-blue-500'
    },
    {
      id: 4,
      type: 'success',
      title: 'Payment Received',
      message: 'Payment of $450 received for booking #B-98765',
      time: '2 hours ago',
      color: 'text-green-500'
    },
    {
      id: 5,
      type: 'info',
      title: 'Route Update',
      message: 'Route KLA-MBS has been updated',
      time: '3 hours ago',
      color: 'text-purple-500'
    }
  ];

  expenses: Expense[] = [
    {
      id: 1,
      type: "appliance",
      unit_cost: 3000,
      quantity: 30,
      total: 30000,
      code: "code",
    }
  ]

}
