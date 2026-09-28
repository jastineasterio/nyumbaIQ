import { useQuery } from '@tanstack/react-query';
import { smartAccessApi } from '../api/smartAccess';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Badge from '../components/ui/Badge';
import ErrorState from '../components/ui/ErrorState';
import { TableSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';

const AccessStatus = () => {
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['smart-locks'], queryFn: () => smartAccessApi.getLocks(0, 100), staleTime: 30000 });

  if (error) return <div><PageHeader title="Access Status" /><ErrorState onRetry={() => refetch()} /></div>;

  const locks = data?.content ?? [];

  const statusVariant = (status: string) => {
    switch (status) {
      case 'ONLINE': return 'success';
      case 'OFFLINE': return 'danger';
      case 'LOCKED': return 'info';
      case 'UNLOCKED': return 'warning';
      default: return 'secondary';
    }
  };

  return (
    <div>
      <PageHeader title="Access Status" description="Your smart lock access" />
      {isLoading ? <TableSkeleton rows={5} cols={4} /> : locks.length === 0 ? (
        <EmptyState title="No locks assigned" description="You do not have access to any smart locks yet." />
      ) : (
        <Card padding="none">
          <Table data={locks} keyExtractor={(l) => l.id} columns={[
            { key: 'name', header: 'Lock Name' },
            { key: 'propertyName', header: 'Property', render: (l) => l.propertyName ?? l.propertyId },
            { key: 'unitName', header: 'Unit', render: (l) => l.unitName ?? l.unitId },
            { key: 'status', header: 'Status', render: (l) => <Badge variant={statusVariant(l.status)}>{l.status}</Badge> },
          ]} />
        </Card>
      )}
    </div>
  );
};

export default AccessStatus;
