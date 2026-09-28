export type NotificationType = 'LEASE' | 'BILLING' | 'PAYMENT' | 'MAINTENANCE' | 'SYSTEM';

export interface Notification {
  id: string;
  title: string;
  message: string;
  type: NotificationType;
  read: boolean;
  createdAt: string;
}

export interface NotificationMarkReadRequest {
  read: boolean;
}
