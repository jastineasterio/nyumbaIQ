import apiClient from './client';
import type { Notification, NotificationMarkReadRequest, PaginatedResponse } from '../types';

export const notificationsApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Notification>> => {
    const response = await apiClient.get(`/notifications?page=${page}&size=${size}`);
    return response.data;
  },
  getUnreadCount: async (): Promise<{ count: number }> => {
    const response = await apiClient.get('/notifications/unread-count');
    return response.data;
  },
  markAsRead: async (id: string): Promise<Notification> => {
    const response = await apiClient.post(`/notifications/${id}/read`);
    return response.data;
  },
  markAllAsRead: async (): Promise<void> => {
    await apiClient.post('/notifications/mark-all-read');
  },
};
