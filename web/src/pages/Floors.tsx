import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link, useSearchParams } from 'react-router-dom';
import { floorsApi, buildingsApi } from '../api';
import type { Floor, FloorFormData } from '../types';
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

const Floors = () => {
  const [page, setPage] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingFloor, setEditingFloor] = useState<Floor | null>(null);
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const [searchParams] = useSearchParams();
  const buildingId = searchParams.get('buildingId');
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const { isOwner, isManager } = useRole(user?.role);

  const { data: buildingsData } = useQuery({
    queryKey: ['buildings'],
    queryFn: () => buildingsApi.getByProperty(''),
    enabled: !buildingId,
  });

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['floors', buildingId, page],
    queryFn: () => floorsApi.getByBuilding(buildingId!, page, 20),
    enabled: !!buildingId,
  });

  const createMutation = useMutation({
    mutationFn: (data: FloorFormData) => floorsApi.create(data.buildingId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['floors'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setIsModalOpen(false);
      toast.success('Floor created successfully');
    },
    onError: () => toast.error('Failed to create floor'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<FloorFormData> }) =>
      floorsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['floors'] });
      setIsModalOpen(false);
      setEditingFloor(null);
      toast.success('Floor updated successfully');
    },
    onError: () => toast.error('Failed to update floor'),
  });

  const deleteMutation = useMutation({
    mutationFn: floorsApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['floors'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setDeleteId(null);
      toast.success('Floor deleted successfully');
    },
    onError: () => toast.error('Failed to delete floor'),
  });

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const formDataObj: FloorFormData = {
      buildingId: formData.get('buildingId') as string,
      propertyId: formData.get('propertyId') as string,
      level: parseInt(formData.get('level') as string, 10),
      name: (formData.get('name') as string) || undefined,
      status: (formData.get('status') as 'ACTIVE' | 'INACTIVE' | 'UNDER_MAINTENANCE') || 'ACTIVE',
    };

    if (editingFloor) {
      updateMutation.mutate({ id: editingFloor.id, data: formDataObj });
    } else {
      createMutation.mutate(formDataObj);
    }
  };

  const floors = data?.content || [];

  return (
    <div>
      <PageHeader
        title="Floors"
        description="Manage floors within buildings"
        action={
          (isOwner || isManager) && (
            <Button onClick={() => { setEditingFloor(null); setIsModalOpen(true); }}>
              Add Floor
            </Button>
          )
        }
      />

      {!buildingId && (
        <Card className="mb-6">
          <p className="text-sm text-slate-600 mb-2">Select a building to view its floors</p>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {buildingsData?.content.map((b: any) => (
              <Link key={b.id} to={`/floors?buildingId=${b.id}`}>
                <Card className="hover:shadow-md transition-shadow">
                  <h3 className="font-semibold text-slate-900">{b.name}</h3>
                  <p className="text-sm text-slate-600 mt-1">{b.floorsCount} floors</p>
                </Card>
              </Link>
            ))}
          </div>
        </Card>
      )}

      {buildingId && (
        <>
          {error && (
            <div className="text-center py-12">
              <p className="text-red-600 mb-4">Failed to load floors</p>
              <button onClick={() => refetch()} className="text-primary-600 hover:underline">Try again</button>
            </div>
          )}

          {isLoading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {Array.from({ length: 6 }).map((_, i) => (
                <CardSkeleton key={i} />
              ))}
            </div>
          ) : floors.length === 0 ? (
            <EmptyState
              title="No floors found"
              description="Add your first floor to this building"
              action={
                (isOwner || isManager) && (
                  <Button onClick={() => { setEditingFloor(null); setIsModalOpen(true); }}>
                    Add Floor
                  </Button>
                )
              }
            />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {floors.map((floor: Floor) => (
                <Card key={floor.id} className="hover:shadow-lg transition-shadow">
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <h3 className="text-lg font-semibold text-slate-900 mb-1">
                        {floor.name || `Floor ${floor.level}`}
                      </h3>
                      <p className="text-sm text-slate-600">Level {floor.level}</p>
                    </div>
                    <Badge variant={floor.status === 'ACTIVE' ? 'success' : 'warning'}>{floor.status}</Badge>
                  </div>
                  <div className="flex items-center justify-between pt-4 border-t border-slate-100">
                    <span className="text-sm text-slate-600">{floor.unitsCount} units</span>
                    <div className="flex gap-2">
                      <Link to={`/units?floorId=${floor.id}`}>
                        <Button size="sm" variant="secondary">View Units</Button>
                      </Link>
                      {(isOwner || isManager) && (
                        <>
                          <Button size="sm" variant="ghost" onClick={() => { setEditingFloor(floor); setIsModalOpen(true); }}>Edit</Button>
                          <Button size="sm" variant="ghost" className="text-red-600" onClick={() => setDeleteId(floor.id)}>Delete</Button>
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

      <Modal open={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingFloor(null); }} title={editingFloor ? 'Edit Floor' : 'Add Floor'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <Select name="buildingId" label="Building" required defaultValue={editingFloor?.buildingId || buildingId || ''}>
            <option value="">Select Building</option>
            {buildingsData?.content.map((b: any) => (
              <option key={b.id} value={b.id}>{b.name}</option>
            ))}
          </Select>
          <Input name="propertyId" label="Property ID" defaultValue={editingFloor?.propertyId} required />
          <Input name="level" label="Level" type="number" defaultValue={editingFloor?.level} required />
          <Input name="name" label="Name (optional)" defaultValue={editingFloor?.name} />
          <Select
            name="status"
            label="Status"
            defaultValue={editingFloor?.status || 'ACTIVE'}
            options={[
              { value: 'ACTIVE', label: 'Active' },
              { value: 'INACTIVE', label: 'Inactive' },
              { value: 'UNDER_MAINTENANCE', label: 'Under Maintenance' },
            ]}
          />
          <div className="flex gap-3 pt-4">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingFloor(null); }}>Cancel</Button>
            <Button type="submit" isLoading={createMutation.isPending || updateMutation.isPending}>
              {editingFloor ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={() => { if (deleteId) { deleteMutation.mutate(deleteId); } }}
        title="Delete Floor"
        message="Are you sure? This will also delete all associated units."
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};

export default Floors;





