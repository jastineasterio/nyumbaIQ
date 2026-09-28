import apiClient from './client';
import type { User, UpdateUserRequest } from '../types/user';

export const usersApi = {
  getMe: async (): Promise<User> => {
    const response = await apiClient.get('/users/me');
    return response.data;
  },

  updateMe: async (data: UpdateUserRequest): Promise<User> => {
    const response = await apiClient.put('/users/me', data);
    return response.data;
  },
};
