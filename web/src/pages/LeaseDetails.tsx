import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { leasesApi } from '../api/leases';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import ErrorState from '../components/ui/ErrorState';
import { TableSkeleton } from '../components/ui/Skeleton';
import type { Lease } from '../types';

const LeaseDetails = () => {
  const { id } = useParams();
  const queryClient = useQueryClient();
  const [isRenewOpen, setIsRenewOpen] = useState(false);
  const [endDate, setEndDate] = useState('');
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['lease', id], queryFn: () => leasesApi.getById(id!), enabled: !!id });
  const renewMutation = useMutation({ mutationFn: (endDate: string) => leasesApi.renew(id!, endDate), onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['lease', id] }); setIsRenewOpen(false); } });
  const approveMutation = useMutation({ mutationFn: () => leasesApi.approve(id!), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['lease', id] }) });
  const terminateMutation = useMutation({ mutationFn: () => leasesApi.terminate(id!), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['lease', id] }) });

  if (isLoading) return <div><PageHeader title="Lease Details" /><TableSkeleton rows={1} cols={2} /></div>;
  if (error) return <div><PageHeader title="Lease Details" /><ErrorState onRetry={() => refetch()} /></div>;
  if (!data) return <div><PageHeader title="Lease Details" /><p>Lease not found</p></div>;

  const statusVariant = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'PENDING': return 'warning';
      case 'TERMINATED': return 'danger';
      case 'EXPIRED': return 'info';
      default: return 'secondary';
    }
  };

  return (
    <div>
      <PageHeader title={`Lease ${data.leaseNumber}`} description={data.propertyName ?? data.propertyId} breadcrumbs={[{ label: 'Leases', href: '/leases' }, { label: data.leaseNumber }]} />
      <Card>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <p className="text-sm text-slate-500">Tenant</p>
            <p className="font-medium text-slate-900">{data.tenantName ?? data.tenantId}</p>
          </div>
          <div>
            <p className="text-sm text-slate-500">Unit</p>
            <p className="font-medium text-slate-900">{data.unitName ?? data.unitId}</p>
          </div>
          <div>
            <p className="text-sm text-slate-500">Start Date</p>
            <p className="font-medium text-slate-900">{new Date(data.startDate).toLocaleDateString()}</p>
          </div>
          <div>
            <p className="text-sm text-slate-500">End Date</p>
            <p className="font-medium text-slate-900">{new Date(data.endDate).toLocaleDateString()}</p>
          </div>
          <div>
            <p className="text-sm text-slate-500">Rent Amount</p>
            <p className="font-medium text-slate-900">${data.rentAmount.toLocaleString()}</p>
          </div>
          <div>
            <p className="text-sm text-slate-500">Deposit Amount</p>
            <p className="font-medium text-slate-900">${data.depositAmount.toLocaleString()}</p>
          </div>
          <div>
            <p className="text-sm text-slate-500">Status</p>
            <Badge variant={statusVariant(data.status)}>{data.status}</Badge>
          </div>
        </div>
        <div className="mt-6 flex gap-3">
          {data.status === 'PENDING' && <Button onClick={() => approveMutation.mutate()} isLoading={approveMutation.isPending}>Approve Lease</Button>}
          {data.status === 'ACTIVE' && <Button onClick={() => setIsRenewOpen(true)}>Renew Lease</Button>}
          {data.status === 'ACTIVE' && <Button variant="danger" onClick={() => terminateMutation.mutate()} isLoading={terminateMutation.isPending}>Terminate Lease</Button>}
        </div>
      </Card>
      <Modal open={isRenewOpen} onClose={() => setIsRenewOpen(false)} title="Renew Lease">
        <div className="space-y-4">
          <Input label="New End Date" type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsRenewOpen(false)}>Cancel</Button>
            <Button onClick={() => renewMutation.mutate(endDate)} isLoading={renewMutation.isPending}>Renew</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default LeaseDetails;
