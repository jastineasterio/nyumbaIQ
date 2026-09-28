export type AuditAction = 'CREATE' | 'UPDATE' | 'DELETE' | 'LOGIN' | 'LOGOUT' | 'ASSIGN' | 'REVIEW' | 'OTHER';
export type AuditEntityType = 'USER' | 'PROPERTY' | 'BUILDING' | 'FLOOR' | 'UNIT' | 'TENANT' | 'MANAGER' | 'DOCUMENT' | 'AUTH';

export interface AuditLog {
  id: string;
  userId: string;
  userName: string;
  userRole: string;
  action: AuditAction;
  entityType: AuditEntityType;
  entityId: string;
  entityName?: string;
  changes?: Record<string, unknown>;
  ipAddress?: string;
  userAgent?: string;
  timestamp: string;
}
