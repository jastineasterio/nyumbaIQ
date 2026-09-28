import type { UserRole } from '../types';

export const useRole = (userRole: UserRole | undefined) => {
  const role = userRole || 'TENANT';

  const canAccess = (allowedRoles: UserRole[]): boolean => {
    return allowedRoles.includes(role);
  };

  const isOwner = role === 'OWNER';
  const isManager = role === 'MANAGER';
  const isTenant = role === 'TENANT';

  return {
    role,
    canAccess,
    isOwner,
    isManager,
    isTenant,
  };
};
