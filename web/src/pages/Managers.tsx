import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { managersApi, propertiesApi } from '../api';
import type { Manager, ManagerFormData } from '../types';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import Select from '../components/ui/Select';
import Badge from '../components/ui/Badge';
import Skeleton, { TableSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import toast from 'react-hot-toast';
import { useAuth } from '../hooks/useAuth';

const Managers = () => {
  const [page, setPage] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingManager, setEditingManager] = useState<Manager | null>(null);
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const [assignModalOpen, setAssignModalOpen] = useState(false);
  const [selectedManager, setSelectedManager] = useState<Manager | null>(null);
  const [selectedPropertyId, setSelectedPropertyId] = useState('');
  const queryClient = useQueryClient();
  const { user } = useAuth();

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['managers', page],
    queryFn: () => managersApi.getAll(page, 20),
  });

  const { data: propertiesData } = useQuery({
    queryKey: ['properties'],
    queryFn: () => propertiesApi.getAll(0, 100),
  });

  const createMutation = useMutation({
    mutationFn: managersApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['managers'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setIsModalOpen(false);
      toast.success('Manager created successfully');
    },
    onError: () => toast.error('Failed to create manager'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<ManagerFormData> }) =>
      managersApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['managers'] });
      setIsModalOpen(false);
      setEditingManager(null);
      toast.success('Manager updated successfully');
    },
    onError: () => toast.error('Failed to update manager'),
  });

  const deleteMutation = useMutation({
    mutationFn: managersApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['managers'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setDeleteId(null);
      toast.success('Manager deleted successfully');
    },
    onError: () => toast.error('Failed to delete manager'),
  });

  const assignMutation = useMutation({
    mutationFn: ({ managerId, propertyId }: { managerId: string; propertyId: string }) =>
      managersApi.assignProperty(managerId, propertyId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['managers'] });
      setAssignModalOpen(false);
      setSelectedManager(null);
      setSelectedPropertyId('');
      toast.success('Property assigned successfully');
    },
    onError: () => toast.error('Failed to assign property'),
  });

  const unassignMutation = useMutation({
    mutationFn: ({ managerId, assignmentId }: { managerId: string; assignmentId: string }) =>
      managersApi.unassignProperty(managerId, assignmentId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['managers'] });
      toast.success('Property unassigned successfully');
    },
    onError: () => toast.error('Failed to unassign property'),
  });

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const formDataObj: ManagerFormData = {
      firstName: formData.get('firstName') as string,
      lastName: formData.get('lastName') as string,
      email: formData.get('email') as string,
      phone: formData.get('phone') as string,
      status: (formData.get('status') as any) || 'ACTIVE',
    };

    if (editingManager) {
      updateMutation.mutate({ id: editingManager.id, data: formDataObj });
    } else {
      createMutation.mutate(formDataObj);
    }
  };

  const managers = data?.content || [];

  return (
    <div>
      <PageHeader
        title="Managers"
        description="Manage property managers"
        action={
          <Button onClick={() => { setEditingManager(null); setIsModalOpen(true); }}>
            Add Manager
          </Button>
        }
      />

      {error && (
        <div className="text-center py-12">
          <p className="text-red-600 mb-4">Failed to load managers</p>
          <button onClick={() => refetch()} className="text-primary-600 hover:underline">Try again</button>
        </div>
      )}

      {isLoading ? (
        <TableSkeleton rows={10} cols={5} />
      ) : managers.length === 0 ? (
        <EmptyState
          title="No managers found"
          description="Add your first manager"
          action={<Button onClick={() => { setEditingManager(null); setIsModalOpen(true); }}>Add Manager</Button>}
        />
      ) : (
        <Card>
          <table className="w-full">
            <thead>
              <tr className="border-b border-slate-200">
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Name</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Email</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Phone</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Status</th>
                <th className="px-4 py-3 text-right text-sm font-semibold text-slate-900">Actions</th>
              </tr>
            </thead>
            <tbody>
              {managers.map((manager: Manager) => (
                <tr key={manager.id} className="border-b border-slate-100 hover:bg-slate-50">
                  <td className="px-4 py-4 text-sm font-medium text-slate-900">{manager.firstName} {manager.lastName}</td>
                  <td className="px-4 py-4 text-sm text-slate-700">{manager.email}</td>
                  <td className="px-4 py-4 text-sm text-slate-700">{manager.phone}</td>
                  <td className="px-4 py-4">
                    <Badge variant={manager.status === 'ACTIVE' ? 'success' : 'warning'}>{manager.status}</Badge>
                  </td>
                  <td className="px-4 py-4 text-right">
                    <Button size="sm" variant="ghost" onClick={() => { setSelectedManager(manager); setAssignModalOpen(true); }}>Assign</Button>
                    <Button size="sm" variant="ghost" onClick={() => { setEditingManager(manager); setIsModalOpen(true); }}>Edit</Button>
                    <Button size="sm" variant="ghost" className="text-red-600" onClick={() => setDeleteId(manager.id)}>Delete</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}

      <Modal open={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingManager(null); }} title={editingManager ? 'Edit Manager' : 'Add Manager'} size="lg">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input name="firstName" label="First Name" defaultValue={editingManager?.firstName} required />
            <Input name="lastName" label="Last Name" defaultValue={editingManager?.lastName} required />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Input name="email" label="Email" type="email" defaultValue={editingManager?.email} required />
            <Input name="phone" label="Phone" defaultValue={editingManager?.phone} required />
          </div>
          <Select
            name="status"
            label="Status"
            defaultValue={editingManager?.status || 'ACTIVE'}
            options={[
              { value: 'ACTIVE', label: 'Active' },
              { value: 'INACTIVE', label: 'Inactive' },
              { value: 'SUSPENDED', label: 'Suspended' },
            ]}
          />
          <div className="flex gap-3 pt-4">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingManager(null); }}>Cancel</Button>
            <Button type="submit" isLoading={createMutation.isPending || updateMutation.isPending}>
              {editingManager ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>

      <Modal open={assignModalOpen} onClose={() => { setAssignModalOpen(false); setSelectedManager(null); setSelectedPropertyId(''); }} title="Assign Property">
        <div className="space-y-4">
          <p className="text-sm text-slate-600">Assign a property to <strong>{selectedManager?.firstName} {selectedManager?.lastName}</strong></p>
          <Select
            label="Property"
            value={selectedPropertyId}
            onChange={(e) => setSelectedPropertyId(e.target.value)}
          >
            <option value="">Select Property</option>
            {propertiesData?.content.map((p: any) => (
              <option key={p.id} value={p.id}>{p.name}</option>
            ))}
          </Select>
          <div className="flex gap-3 pt-4">
            <Button variant="secondary" onClick={() => { setAssignModalOpen(false); setSelectedManager(null); setSelectedPropertyId(''); }}>Cancel</Button>
            <Button
              onClick={() => selectedManager && selectedPropertyId && assignMutation.mutate({ managerId: selectedManager.id, propertyId: selectedPropertyId })}
              disabled={!selectedPropertyId}
              isLoading={assignMutation.isPending}
            >
              Assign
            </Button>
          </div>
        </div>
      </Modal>

      <ConfirmDialog
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={() => { if (deleteId) { deleteMutation.mutate(deleteId); } }}
        title="Delete Manager"
        message="Are you sure? This action cannot be undone."
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};

export default Managers;





