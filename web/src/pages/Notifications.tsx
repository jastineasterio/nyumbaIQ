import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { notificationsApi } from '../api/notifications';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import ErrorState from '../components/ui/ErrorState';
import { TableSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';
import type { Notification } from '../types';

const Notifications = () => {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['notifications'], queryFn: () => notificationsApi.getAll(0, 100), staleTime: 30000 });
  const markReadMutation = useMutation({ mutationFn: notificationsApi.markAsRead, onSuccess: () => queryClient.invalidateQueries({ queryKey: ['notifications'] }) });
  const markAllMutation = useMutation({ mutationFn: notificationsApi.markAllAsRead, onSuccess: () => queryClient.invalidateQueries({ queryKey: ['notifications'] }) });

  if (error) return <div><PageHeader title="Notifications" /><ErrorState onRetry={() => refetch()} /></div>;

  const notifications = data?.content ?? [];

  return (
    <div>
      <PageHeader title="Notifications" description="Your recent notifications" action={<Button onClick={() => markAllMutation.mutate()} isLoading={markAllMutation.isPending}>Mark All Read</Button>} />
      {isLoading ? <TableSkeleton rows={5} cols={3} /> : notifications.length === 0 ? (
        <EmptyState title="No notifications" description="You are all caught up!" />
      ) : (
        <Card padding="none">
          <Table data={notifications} keyExtractor={(n) => n.id} columns={[
            { key: 'title', header: 'Title', render: (n) => <div className={n.read ? '' : 'font-semibold text-slate-900'}>{n.title}</div> },
            { key: 'message', header: 'Message' },
            { key: 'type', header: 'Type', render: (n) => <Badge variant="secondary">{n.type}</Badge> },
            { key: 'createdAt', header: 'Date', render: (n) => new Date(n.createdAt).toLocaleString() },
            { key: 'actions', header: '', render: (n) => !n.read ? <Button size="sm" variant="ghost" onClick={() => markReadMutation.mutate(n.id)} isLoading={markReadMutation.isPending}>Mark Read</Button> : null },
          ]} />
        </Card>
      )}
    </div>
  );
};

export default Notifications;
