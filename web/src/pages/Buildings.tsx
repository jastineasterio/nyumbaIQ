import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link, useSearchParams } from 'react-router-dom';
import { buildingsApi, propertiesApi } from '../api';
import type { Building, BuildingFormData } from '../types';
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
import { useAuth } from '../hooks/useAuth';
import { useRole } from '../hooks/useRole';

const Buildings = () => {
  const [page, setPage] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingBuilding, setEditingBuilding] = useState<Building | null>(null);
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const [searchParams] = useSearchParams();
  const propertyId = searchParams.get('propertyId');
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const { isOwner, isManager } = useRole(user?.role);

  const { data: propertiesData } = useQuery({
    queryKey: ['properties'],
    queryFn: () => propertiesApi.getAll(0, 100),
  });

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['buildings', propertyId, page],
    queryFn: () => buildingsApi.getByProperty(propertyId!, page, 20),
    enabled: !!propertyId,
  });

  const createMutation = useMutation({
    mutationFn: (data: BuildingFormData) => buildingsApi.create(data.propertyId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['buildings'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setIsModalOpen(false);
      toast.success('Building created successfully');
    },
    onError: () => toast.error('Failed to create building'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<BuildingFormData> }) =>
      buildingsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['buildings'] });
      setIsModalOpen(false);
      setEditingBuilding(null);
      toast.success('Building updated successfully');
    },
    onError: () => toast.error('Failed to update building'),
  });

  const deleteMutation = useMutation({
    mutationFn: buildingsApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['buildings'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setDeleteId(null);
      toast.success('Building deleted successfully');
    },
    onError: () => toast.error('Failed to delete building'),
  });

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const formDataObj: BuildingFormData = {
      propertyId: formData.get('propertyId') as string,
      name: formData.get('name') as string,
      description: (formData.get('description') as string) || '',
      status: (formData.get('status') as 'ACTIVE' | 'INACTIVE' | 'UNDER_MAINTENANCE') || 'ACTIVE',
    };

    if (editingBuilding) {
      updateMutation.mutate({ id: editingBuilding.id, data: formDataObj });
    } else {
      createMutation.mutate(formDataObj);
    }
  };

  const buildings = data?.content || [];

  return (
    <div>
      <PageHeader
        title="Buildings"
        description="Manage buildings across properties"
        action={
          (isOwner || isManager) && (
            <Button onClick={() => { setEditingBuilding(null); setIsModalOpen(true); }}>
              Add Building
            </Button>
          )
        }
      />

      {!propertyId && (
        <Card className="mb-6">
          <p className="text-sm text-slate-600 mb-2">Select a property to view its buildings</p>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {propertiesData?.content.map((p: any) => (
              <Link key={p.id} to={`/buildings?propertyId=${p.id}`}>
                <Card className="hover:shadow-md transition-shadow">
                  <h3 className="font-semibold text-slate-900">{p.name}</h3>
                  <p className="text-sm text-slate-600 mt-1">{p.address}</p>
                </Card>
              </Link>
            ))}
          </div>
        </Card>
      )}

      {propertyId && (
        <>
          {error && (
            <div className="text-center py-12">
              <p className="text-red-600 mb-4">Failed to load buildings</p>
              <button onClick={() => refetch()} className="text-primary-600 hover:underline">Try again</button>
            </div>
          )}

          {isLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {Array.from({ length: 6 }).map((_, i) => (
                <CardSkeleton key={i} />
              ))}
            </div>
          ) : buildings.length === 0 ? (
            <EmptyState
              title="No buildings found"
              description="Add your first building to this property"
              action={
                (isOwner || isManager) && (
                  <Button onClick={() => { setEditingBuilding(null); setIsModalOpen(true); }}>
                    Add Building
                  </Button>
                )
              }
            />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {buildings.map((building: Building) => (
                <Card key={building.id} className="hover:shadow-lg transition-shadow">
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <h3 className="text-lg font-semibold text-slate-900 mb-1">{building.name}</h3>
                      <p className="text-sm text-slate-600">{building.description || 'No description'}</p>
                    </div>
                    <Badge variant={building.status === 'ACTIVE' ? 'success' : 'warning'}>{building.status}</Badge>
                  </div>
                  <div className="flex items-center justify-between pt-4 border-t border-slate-100">
                    <span className="text-sm text-slate-600">{building.floorsCount} floors</span>
                    <div className="flex gap-2">
                      <Link to={`/floors?buildingId=${building.id}`}>
                        <Button size="sm" variant="secondary">View Floors</Button>
                      </Link>
                      {(isOwner || isManager) && (
                        <>
                          <Button size="sm" variant="ghost" onClick={() => { setEditingBuilding(building); setIsModalOpen(true); }}>Edit</Button>
                          <Button size="sm" variant="ghost" className="text-red-600" onClick={() => setDeleteId(building.id)}>Delete</Button>
                        </>
                      )}
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </>
      )}

      <Modal open={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingBuilding(null); }} title={editingBuilding ? 'Edit Building' : 'Add Building'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <Select name="propertyId" label="Property" required defaultValue={editingBuilding?.propertyId || propertyId || ''}>
            <option value="">Select Property</option>
            {propertiesData?.content.map((p: any) => (
              <option key={p.id} value={p.id}>{p.name}</option>
            ))}
          </Select>
          <Input name="name" label="Building Name" defaultValue={editingBuilding?.name} required />
          <Input name="description" label="Description" defaultValue={editingBuilding?.description} />
          <Select
            name="status"
            label="Status"
            defaultValue={editingBuilding?.status || 'ACTIVE'}
            options={[
              { value: 'ACTIVE', label: 'Active' },
              { value: 'INACTIVE', label: 'Inactive' },
              { value: 'UNDER_MAINTENANCE', label: 'Under Maintenance' },
            ]}
          />
          <div className="flex gap-3 pt-4">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingBuilding(null); }}>Cancel</Button>
            <Button type="submit" isLoading={createMutation.isPending || updateMutation.isPending}>
              {editingBuilding ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={() => { if (deleteId) { deleteMutation.mutate(deleteId); } }}
        title="Delete Building"
        message="Are you sure? This will also delete all associated floors and units."
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};

export default Buildings;





