import { useState, useEffect, useCallback } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { authApi } from '../api/auth';
import type { LoginRequest, LoginResponse, User } from '../types';

interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isLoading: boolean;
  isAuthenticated: boolean;
}

export const useAuth = () => {
  const [authState, setAuthState] = useState<AuthState>({
    user: null,
    accessToken: localStorage.getItem('accessToken'),
    refreshToken: localStorage.getItem('refreshToken'),
    isLoading: true,
    isAuthenticated: false,
  });

  const queryClient = useQueryClient();

  const validateAuth = useCallback(async () => {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    if (!accessToken) {
      setAuthState((prev) => ({ ...prev, isLoading: false, isAuthenticated: false }));
      return;
    }

    try {
      const user = await authApi.refresh(refreshToken!).then(() =>
        queryClient.fetchQuery({ queryKey: ['user'], queryFn: async () => {
          const { usersApi } = await import('../api/users');
          return usersApi.getMe();
        }})
      );
      setAuthState((prev) => ({
        ...prev,
        user,
        accessToken: localStorage.getItem('accessToken'),
        refreshToken: localStorage.getItem('refreshToken'),
        isLoading: false,
        isAuthenticated: true,
      }));
    } catch {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      setAuthState((prev) => ({
        ...prev,
        user: null,
        accessToken: null,
        refreshToken: null,
        isLoading: false,
        isAuthenticated: false,
      }));
    }
  }, [queryClient]);

  useEffect(() => {
    validateAuth();
  }, [validateAuth]);

  useEffect(() => {
    const handleLogout = () => {
      setAuthState({
        user: null,
        accessToken: null,
        refreshToken: null,
        isLoading: false,
        isAuthenticated: false,
      });
    };

    window.addEventListener('auth:logout', handleLogout);
    return () => window.removeEventListener('auth:logout', handleLogout);
  }, []);

  const loginMutation = useMutation({
    mutationFn: async (credentials: LoginRequest): Promise<LoginResponse> => {
      const response = await authApi.login(credentials);
      localStorage.setItem('accessToken', response.accessToken);
      localStorage.setItem('refreshToken', response.refreshToken);
      return response;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['user'] });
    },
  });

  const logoutMutation = useMutation({
    mutationFn: async () => {
      await authApi.logout();
    },
    onSettled: () => {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      queryClient.clear();
      window.dispatchEvent(new CustomEvent('auth:logout'));
    },
  });

  const changePasswordMutation = useMutation({
    mutationFn: async (data: { currentPassword: string; newPassword: string }) => {
      await authApi.changePassword(data);
    },
  });

  return {
    user: authState.user,
    accessToken: authState.accessToken,
    refreshToken: authState.refreshToken,
    isLoading: authState.isLoading,
    isAuthenticated: authState.isAuthenticated,
    login: loginMutation.mutate,
    logout: logoutMutation.mutate,
    changePassword: changePasswordMutation.mutate,
    isLoggingIn: loginMutation.isPending,
    isLoggingOut: logoutMutation.isPending,
    loginError: loginMutation.error,
  };
};
