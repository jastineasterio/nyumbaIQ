import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { propertiesApi } from '../api/properties';
import type { Property, PropertyFormData } from '../types';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import Select from '../components/ui/Select';
import Badge from '../components/ui/Badge';
import Skeleton, { CardSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';
import ConfirmDialog from '../components/ui/ConfirmDialog';
import toast from 'react-hot-toast';

const Properties = () => {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProperty, setEditingProperty] = useState<Property | null>(null);
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const queryClient = useQueryClient();

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['properties', page],
    queryFn: () => propertiesApi.getAll(page, 20),
  });

  const createMutation = useMutation({
    mutationFn: propertiesApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['properties'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setIsModalOpen(false);
      toast.success('Property created successfully');
    },
    onError: () => {
      toast.error('Failed to create property');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<PropertyFormData> }) =>
      propertiesApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['properties'] });
      setIsModalOpen(false);
      setEditingProperty(null);
      toast.success('Property updated successfully');
    },
    onError: () => {
      toast.error('Failed to update property');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: propertiesApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['properties'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setDeleteId(null);
      toast.success('Property deleted successfully');
    },
    onError: () => {
      toast.error('Failed to delete property');
    },
  });

  const filteredProperties = data?.content.filter((p: Property) =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    p.address.toLowerCase().includes(search.toLowerCase())
  ) || [];

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const formDataObj: PropertyFormData = {
      name: formData.get('name') as string,
      description: (formData.get('description') as string) || '',
      address: formData.get('address') as string,
      city: formData.get('city') as string,
      state: formData.get('state') as string,
      country: formData.get('country') as string,
      postalCode: (formData.get('postalCode') as string) || '',
      status: (formData.get('status') as 'ACTIVE' | 'INACTIVE' | 'UNDER_MAINTENANCE') || 'ACTIVE',
    };

    if (editingProperty) {
      updateMutation.mutate({ id: editingProperty.id, data: formDataObj });
    } else {
      createMutation.mutate(formDataObj);
    }
  };

  const openCreateModal = () => {
    setEditingProperty(null);
    setIsModalOpen(true);
  };

  const openEditModal = (property: Property) => {
    setEditingProperty(property);
    setIsModalOpen(true);
  };

  return (
    <div>
      <PageHeader
        title="Properties"
        description="Manage your properties"
        action={
          <Button onClick={openCreateModal}>Add Property</Button>
        }
      />

      <div className="mb-6">
        <Input
          placeholder="Search properties..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && (
        <div className="text-center py-12">
          <p className="text-red-600 mb-4">Failed to load properties</p>
          <button onClick={() => refetch()} className="text-primary-600 hover:underline">
            Try again
          </button>
        </div>
      )}

      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {Array.from({ length: 6 }).map((_, i) => (
            <CardSkeleton key={i} />
          ))}
        </div>
      ) : filteredProperties.length === 0 ? (
        <EmptyState
          title="No properties found"
          description="Get started by creating your first property"
          action={<Button onClick={openCreateModal}>Add Property</Button>}
        />
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredProperties.map((property) => (
              <Card key={property.id} className="hover:shadow-lg transition-shadow">
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <h3 className="text-lg font-semibold text-slate-900 mb-1">{property.name}</h3>
                    <p className="text-sm text-slate-600 line-clamp-2">{property.address}</p>
                  </div>
                  <Badge variant={property.status === 'ACTIVE' ? 'success' : 'warning'}>
                    {property.status}
                  </Badge>
                </div>
                <div className="flex items-center gap-4 text-sm text-slate-600 mb-4">
                  <span>{property.city}, {property.state}</span>
                </div>
                <div className="flex items-center justify-between pt-4 border-t border-slate-100">
                  <div className="flex gap-2">
                    <Link to={`/properties/${property.id}`}>
                      <Button size="sm" variant="secondary">View</Button>
                    </Link>
                    <Button size="sm" variant="ghost" onClick={() => openEditModal(property)}>Edit</Button>
                  </div>
                  <Button size="sm" variant="ghost" className="text-red-600" onClick={() => setDeleteId(property.id)}>
                    Delete
                  </Button>
                </div>
              </Card>
            ))}
          </div>

          {data && data.totalPages > 1 && (
            <div className="flex items-center justify-between mt-6">
              <Button
                variant="secondary"
                disabled={page === 0}
                onClick={() => setPage(p => p - 1)}
              >
                Previous
              </Button>
              <span className="text-sm text-slate-600">
                Page {page + 1} of {data.totalPages}
              </span>
              <Button
                variant="secondary"
                disabled={page >= data.totalPages - 1}
                onClick={() => setPage(p => p + 1)}
              >
                Next
              </Button>
            </div>
          )}
        </>
      )}

      <Modal open={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingProperty(null); }} title={editingProperty ? 'Edit Property' : 'Create Property'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input name="name" label="Property Name" defaultValue={editingProperty?.name} required />
          <Input name="description" label="Description" defaultValue={editingProperty?.description} />
          <Input name="address" label="Address" defaultValue={editingProperty?.address} required />
          <div className="grid grid-cols-2 gap-4">
            <Input name="city" label="City" defaultValue={editingProperty?.city} required />
            <Input name="state" label="State" defaultValue={editingProperty?.state} required />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Input name="country" label="Country" defaultValue={editingProperty?.country} required />
            <Input name="postalCode" label="Postal Code" defaultValue={editingProperty?.postalCode} />
          </div>
          <Select
            name="status"
            label="Status"
            defaultValue={editingProperty?.status || 'ACTIVE'}
            options={[
              { value: 'ACTIVE', label: 'Active' },
              { value: 'INACTIVE', label: 'Inactive' },
              { value: 'UNDER_MAINTENANCE', label: 'Under Maintenance' },
            ]}
          />
          <div className="flex gap-3 pt-4">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingProperty(null); }}>
              Cancel
            </Button>
            <Button type="submit" isLoading={createMutation.isPending || updateMutation.isPending}>
              {editingProperty ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={() => { if (deleteId) { deleteMutation.mutate(deleteId); } }}
        title="Delete Property"
        message="Are you sure you want to delete this property? This action cannot be undone."
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};

export default Properties;




