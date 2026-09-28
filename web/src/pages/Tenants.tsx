import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { tenantsApi, propertiesApi } from '../api';
import type { Tenant, TenantFormData } from '../types';
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
import { useRole } from '../hooks/useRole';

const Tenants = () => {
  const [page, setPage] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTenant, setEditingTenant] = useState<Tenant | null>(null);
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const { isOwner, isManager, isTenant } = useRole(user?.role);

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['tenants', page, search],
    queryFn: () => tenantsApi.getAll(page, 20),
  });

  const { data: propertiesData } = useQuery({
    queryKey: ['properties'],
    queryFn: () => propertiesApi.getAll(0, 100),
  });

  const createMutation = useMutation({
    mutationFn: tenantsApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tenants'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setIsModalOpen(false);
      toast.success('Tenant created successfully');
    },
    onError: () => toast.error('Failed to create tenant'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<TenantFormData> }) =>
      tenantsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tenants'] });
      setIsModalOpen(false);
      setEditingTenant(null);
      toast.success('Tenant updated successfully');
    },
    onError: () => toast.error('Failed to update tenant'),
  });

  const deleteMutation = useMutation({
    mutationFn: tenantsApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tenants'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setDeleteId(null);
      toast.success('Tenant deleted successfully');
    },
    onError: () => toast.error('Failed to delete tenant'),
  });

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const formDataObj: TenantFormData = {
      propertyId: formData.get('propertyId') as string,
      unitId: (formData.get('unitId') as string) || undefined,
      firstName: formData.get('firstName') as string,
      lastName: formData.get('lastName') as string,
      email: formData.get('email') as string,
      phone: formData.get('phone') as string,
      idNumber: (formData.get('idNumber') as string) || undefined,
      status: (formData.get('status') as any) || 'ACTIVE',
      leaseStartDate: (formData.get('leaseStartDate') as string) || undefined,
      leaseEndDate: (formData.get('leaseEndDate') as string) || undefined,
      emergencyContactName: (formData.get('emergencyContactName') as string) || undefined,
      emergencyContactPhone: (formData.get('emergencyContactPhone') as string) || undefined,
    };

    if (editingTenant) {
      updateMutation.mutate({ id: editingTenant.id, data: formDataObj });
    } else {
      createMutation.mutate(formDataObj);
    }
  };

  const tenants = data?.content || [];

  const filteredTenants = tenants.filter((t: Tenant) =>
    t.firstName.toLowerCase().includes(search.toLowerCase()) ||
    t.lastName.toLowerCase().includes(search.toLowerCase()) ||
    t.email.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <PageHeader
        title="Tenants"
        description="Manage tenant information"
        action={
          !isTenant && (
            <Button onClick={() => { setEditingTenant(null); setIsModalOpen(true); }}>
              Add Tenant
            </Button>
          )
        }
      />

      <div className="mb-6">
        <Input
          placeholder="Search tenants..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && (
        <div className="text-center py-12">
          <p className="text-red-600 mb-4">Failed to load tenants</p>
          <button onClick={() => refetch()} className="text-primary-600 hover:underline">Try again</button>
        </div>
      )}

      {isLoading ? (
        <TableSkeleton rows={10} cols={6} />
      ) : filteredTenants.length === 0 ? (
        <EmptyState
          title="No tenants found"
          description="Add your first tenant"
          action={
            !isTenant && (
              <Button onClick={() => { setEditingTenant(null); setIsModalOpen(true); }}>
                Add Tenant
              </Button>
            )
          }
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
                {!isTenant && (
                  <th className="px-4 py-3 text-right text-sm font-semibold text-slate-900">Actions</th>
                )}
              </tr>
            </thead>
            <tbody>
              {filteredTenants.map((tenant: Tenant) => (
                <tr key={tenant.id} className="border-b border-slate-100 hover:bg-slate-50">
                  <td className="px-4 py-4 text-sm font-medium text-slate-900">{tenant.firstName} {tenant.lastName}</td>
                  <td className="px-4 py-4 text-sm text-slate-700">{tenant.email}</td>
                  <td className="px-4 py-4 text-sm text-slate-700">{tenant.phone}</td>
                  <td className="px-4 py-4">
                    <Badge variant={tenant.status === 'ACTIVE' ? 'success' : tenant.status === 'BLACKLISTED' ? 'danger' : 'warning'}>
                      {tenant.status}
                    </Badge>
                  </td>
                  {!isTenant && (
                    <td className="px-4 py-4 text-right">
                      <Button size="sm" variant="ghost" onClick={() => { setEditingTenant(tenant); setIsModalOpen(true); }}>Edit</Button>
                      <Button size="sm" variant="ghost" className="text-red-600" onClick={() => setDeleteId(tenant.id)}>Delete</Button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}

      <Modal open={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingTenant(null); }} title={editingTenant ? 'Edit Tenant' : 'Add Tenant'} size="lg">
        <form onSubmit={handleSubmit} className="space-y-4">
          <Select name="propertyId" label="Property" required defaultValue={editingTenant?.propertyId}>
            <option value="">Select Property</option>
            {propertiesData?.content.map((p: any) => (
              <option key={p.id} value={p.id}>{p.name}</option>
            ))}
          </Select>
          <div className="grid grid-cols-2 gap-4">
            <Input name="firstName" label="First Name" defaultValue={editingTenant?.firstName} required />
            <Input name="lastName" label="Last Name" defaultValue={editingTenant?.lastName} required />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Input name="email" label="Email" type="email" defaultValue={editingTenant?.email} required />
            <Input name="phone" label="Phone" defaultValue={editingTenant?.phone} required />
          </div>
          <Input name="idNumber" label="ID Number" defaultValue={editingTenant?.idNumber} />
          <Select
            name="status"
            label="Status"
            defaultValue={editingTenant?.status || 'ACTIVE'}
            options={[
              { value: 'ACTIVE', label: 'Active' },
              { value: 'INACTIVE', label: 'Inactive' },
              { value: 'BLACKLISTED', label: 'Blacklisted' },
            ]}
          />
          <div className="grid grid-cols-2 gap-4">
            <Input name="leaseStartDate" label="Lease Start Date" type="date" defaultValue={editingTenant?.leaseStartDate} />
            <Input name="leaseEndDate" label="Lease End Date" type="date" defaultValue={editingTenant?.leaseEndDate} />
          </div>
          <Input name="emergencyContactName" label="Emergency Contact Name" defaultValue={editingTenant?.emergencyContactName} />
          <Input name="emergencyContactPhone" label="Emergency Contact Phone" defaultValue={editingTenant?.emergencyContactPhone} />
          <div className="flex gap-3 pt-4">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingTenant(null); }}>Cancel</Button>
            <Button type="submit" isLoading={createMutation.isPending || updateMutation.isPending}>
              {editingTenant ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={() => { if (deleteId) { deleteMutation.mutate(deleteId); } }}
        title="Delete Tenant"
        message="Are you sure? This will also remove all associated data."
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};

export default Tenants;





