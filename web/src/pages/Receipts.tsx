import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { receiptsApi } from '../api/receipts';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Button from '../components/ui/Button';
import ErrorState from '../components/ui/ErrorState';
import { TableSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';

const Receipts = () => {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['receipts'], queryFn: () => receiptsApi.getAll(0, 100), staleTime: 30000 });
  const downloadMutation = useMutation({ mutationFn: receiptsApi.download });

  if (error) return <div><PageHeader title="Receipts" /><ErrorState onRetry={() => refetch()} /></div>;

  const receipts = data?.content ?? [];

  const handleDownload = async (id: string) => {
    const result = await downloadMutation.mutateAsync(id);
    window.open(result.url, '_blank');
  };

  return (
    <div>
      <PageHeader title="Receipts" description="View and download receipts" />
      {isLoading ? <TableSkeleton rows={5} cols={5} /> : receipts.length === 0 ? (
        <EmptyState title="No receipts" description="Receipts will appear here after payments." />
      ) : (
        <Card padding="none">
          <Table data={receipts} keyExtractor={(r) => r.id} columns={[
            { key: 'receiptNumber', header: 'Receipt #' },
            { key: 'tenantName', header: 'Tenant', render: (r) => r.tenantName ?? r.tenantId },
            { key: 'amount', header: 'Amount', render: (r) => `$${r.amount.toLocaleString()}` },
            { key: 'method', header: 'Method' },
            { key: 'issuedAt', header: 'Issued', render: (r) => new Date(r.issuedAt).toLocaleDateString() },
            { key: 'actions', header: '', render: (r) => <Button size="sm" onClick={() => handleDownload(r.id)} isLoading={downloadMutation.isPending}>Download</Button> },
          ]} />
        </Card>
      )}
    </div>
  );
};

export default Receipts;
