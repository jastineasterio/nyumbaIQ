import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { smartAccessApi } from '../api/smartAccess';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import Select from '../components/ui/Select';
import ErrorState from '../components/ui/ErrorState';
import { TableSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';
import type { SmartLock, CredentialFormData, AccessPolicyFormData } from '../types';

const SmartAccess = () => {
  const queryClient = useQueryClient();
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [selectedLockId, setSelectedLockId] = useState('');
  const [isCredentialOpen, setIsCredentialOpen] = useState(false);
  const [isPolicyOpen, setIsPolicyOpen] = useState(false);
  const [createForm, setCreateForm] = useState({ propertyId: '', unitId: '', name: '', model: '' });
  const [credentialForm, setCredentialForm] = useState<CredentialFormData>({ type: 'PASSWORD', name: '' });
  const [policyForm, setPolicyForm] = useState<AccessPolicyFormData>({ name: '', allowedDays: [], startTime: '', endTime: '', isActive: true });
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['smart-locks'], queryFn: () => smartAccessApi.getLocks(0, 100), staleTime: 30000 });
  const createMutation = useMutation({ mutationFn: smartAccessApi.createLock, onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['smart-locks'] }); setIsCreateOpen(false); } });
  const lockMutation = useMutation({ mutationFn: (id: string) => smartAccessApi.lock(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['smart-locks'] }) });
  const unlockMutation = useMutation({ mutationFn: (id: string) => smartAccessApi.unlock(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['smart-locks'] }) });
  const credentialMutation = useMutation({ mutationFn: (data: CredentialFormData) => smartAccessApi.addCredential(selectedLockId, data), onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['smart-locks'] }); setIsCredentialOpen(false); } });
  const policyMutation = useMutation({ mutationFn: (data: AccessPolicyFormData) => smartAccessApi.updateAccessPolicy(selectedLockId, data), onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['smart-locks'] }); setIsPolicyOpen(false); } });

  if (error) return <div><PageHeader title="Smart Access" /><ErrorState onRetry={() => refetch()} /></div>;

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
      <PageHeader title="Smart Access" description="Manage smart locks and access" action={<Button onClick={() => setIsCreateOpen(true)}>Add Lock</Button>} />
      {isLoading ? <TableSkeleton rows={5} cols={4} /> : locks.length === 0 ? (
        <EmptyState title="No smart locks" description="Add a smart lock to manage access control." action={<Button onClick={() => setIsCreateOpen(true)}>Add Lock</Button>} />
      ) : (
        <Card padding="none">
          <Table data={locks} keyExtractor={(l) => l.id} columns={[
            { key: 'name', header: 'Name' },
            { key: 'propertyName', header: 'Property', render: (l) => l.propertyName ?? l.propertyId },
            { key: 'unitName', header: 'Unit', render: (l) => l.unitName ?? l.unitId },
            { key: 'model', header: 'Model' },
            { key: 'status', header: 'Status', render: (l) => <Badge variant={statusVariant(l.status)}>{l.status}</Badge> },
            { key: 'actions', header: '', render: (l) => (
              <div className="flex gap-2">
                <Button size="sm" onClick={() => lockMutation.mutate(l.id)} isLoading={lockMutation.isPending}>Lock</Button>
                <Button size="sm" variant="secondary" onClick={() => unlockMutation.mutate(l.id)} isLoading={unlockMutation.isPending}>Unlock</Button>
                <Button size="sm" variant="ghost" onClick={() => { setSelectedLockId(l.id); setIsCredentialOpen(true); }}>Credentials</Button>
                <Button size="sm" variant="ghost" onClick={() => { setSelectedLockId(l.id); setIsPolicyOpen(true); }}>Policy</Button>
              </div>
            ) },
          ]} />
        </Card>
      )}
      <Modal open={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Add Smart Lock" size="lg">
        <div className="space-y-4">
          <Input label="Property ID" value={createForm.propertyId} onChange={(e) => setCreateForm({ ...createForm, propertyId: e.target.value })} />
          <Input label="Unit ID" value={createForm.unitId} onChange={(e) => setCreateForm({ ...createForm, unitId: e.target.value })} />
          <Input label="Name" value={createForm.name} onChange={(e) => setCreateForm({ ...createForm, name: e.target.value })} />
          <Input label="Model" value={createForm.model} onChange={(e) => setCreateForm({ ...createForm, model: e.target.value })} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsCreateOpen(false)}>Cancel</Button>
            <Button onClick={() => createMutation.mutate(createForm)} isLoading={createMutation.isPending}>Add Lock</Button>
          </div>
        </div>
      </Modal>
      <Modal open={isCredentialOpen} onClose={() => setIsCredentialOpen(false)} title="Add Credential">
        <div className="space-y-4">
          <Select label="Type" value={credentialForm.type} onChange={(e) => setCredentialForm({ ...credentialForm, type: e.target.value as CredentialFormData['type'] })} options={[
            { value: 'PASSWORD', label: 'Password' }, { value: 'CARD', label: 'Card' }, { value: 'FINGERPRINT', label: 'Fingerprint' }, { value: 'APP', label: 'App' },
          ]} />
          <Input label="Name" value={credentialForm.name} onChange={(e) => setCredentialForm({ ...credentialForm, name: e.target.value })} />
          <Input label="Expires At (optional)" type="date" value={credentialForm.expiresAt} onChange={(e) => setCredentialForm({ ...credentialForm, expiresAt: e.target.value })} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsCredentialOpen(false)}>Close</Button>
            <Button onClick={() => credentialMutation.mutate(credentialForm)} isLoading={credentialMutation.isPending}>Add</Button>
          </div>
        </div>
      </Modal>
      <Modal open={isPolicyOpen} onClose={() => setIsPolicyOpen(false)} title="Access Policy">
        <div className="space-y-4">
          <Input label="Policy Name" value={policyForm.name} onChange={(e) => setPolicyForm({ ...policyForm, name: e.target.value })} />
          <Input label="Start Time" type="time" value={policyForm.startTime} onChange={(e) => setPolicyForm({ ...policyForm, startTime: e.target.value })} />
          <Input label="End Time" type="time" value={policyForm.endTime} onChange={(e) => setPolicyForm({ ...policyForm, endTime: e.target.value })} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsPolicyOpen(false)}>Close</Button>
            <Button onClick={() => policyMutation.mutate(policyForm)} isLoading={policyMutation.isPending}>Save Policy</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default SmartAccess;
