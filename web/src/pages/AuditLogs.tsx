import { useQuery } from '@tanstack/react-query';
import { auditApi } from '../api/audit';
import type { AuditLog } from '../types';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Skeleton, { TableSkeleton } from '../components/ui/Skeleton';
import { useAuth } from '../hooks/useAuth';

const AuditLogs = () => {
  const { user } = useAuth();
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['audit-logs'],
    queryFn: () => auditApi.getAll(0, 20),
  });

  const logs = data?.content || [];

  const getActionBadgeVariant = (action: string) => {
    switch (action) {
      case 'CREATE': return 'success';
      case 'UPDATE': return 'info';
      case 'DELETE': return 'danger';
      case 'LOGIN': return 'primary';
      case 'LOGOUT': return 'secondary';
      default: return 'secondary';
    }
  };

  if (error) {
    return (
      <div>
        <PageHeader title="Audit Logs" description="System activity logs" />
        <div className="text-center py-12">
          <p className="text-red-600 mb-4">Failed to load audit logs</p>
          <button onClick={() => refetch()} className="text-primary-600 hover:underline">Try again</button>
        </div>
      </div>
    );
  }

  return (
    <div>
      <PageHeader title="Audit Logs" description="System activity logs" />

      {isLoading ? (
        <TableSkeleton rows={10} cols={6} />
      ) : logs.length === 0 ? (
        <Card>
          <div className="text-center py-12">
            <p className="text-slate-500">No audit logs found</p>
          </div>
        </Card>
      ) : (
        <Card>
          <table className="w-full">
            <thead>
              <tr className="border-b border-slate-200">
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Timestamp</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">User</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Action</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Entity</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Entity Name</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">IP Address</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log: AuditLog) => (
                <tr key={log.id} className="border-b border-slate-100 hover:bg-slate-50">
                  <td className="px-4 py-4 text-sm text-slate-700">
                    {new Date(log.timestamp).toLocaleString()}
                  </td>
                  <td className="px-4 py-4 text-sm font-medium text-slate-900">{log.userName}</td>
                  <td className="px-4 py-4">
                    <Badge variant={getActionBadgeVariant(log.action) as any}>{log.action}</Badge>
                  </td>
                  <td className="px-4 py-4 text-sm text-slate-700">{log.entityType}</td>
                  <td className="px-4 py-4 text-sm text-slate-700">{log.entityName || log.entityId}</td>
                  <td className="px-4 py-4 text-sm text-slate-700">{log.ipAddress || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  );
};

export default AuditLogs;


