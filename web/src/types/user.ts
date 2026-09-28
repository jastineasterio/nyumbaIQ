export type UserRole = 'OWNER' | 'MANAGER' | 'TENANT';
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

export interface User {
  id: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  email: string;
  phone: string;
  username: string;
  role: UserRole;
  status: UserStatus;
  profilePhotoUrl?: string;
  lastLogin?: string;
  createdAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    role: UserRole;
    status: UserStatus;
  };
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface UpdateUserRequest {
  firstName?: string;
  middleName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
}
