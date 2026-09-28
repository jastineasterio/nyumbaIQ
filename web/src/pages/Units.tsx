import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link, useSearchParams } from 'react-router-dom';
import { unitsApi, floorsApi, buildingsApi, propertiesApi } from '../api';
import type { Unit, UnitFormData } from '../types';
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

const Units = () => {
  const [page, setPage] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingUnit, setEditingUnit] = useState<Unit | null>(null);
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState('');
  const [searchParams] = useSearchParams();
  const floorId = searchParams.get('floorId');
  const propertyId = searchParams.get('propertyId');
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const { isOwner, isManager } = useRole(user?.role);

  const { data: floorsData } = useQuery({
    queryKey: ['floors'],
    queryFn: async () => {
      const response = await fetch('/api/v1/floors?page=0&size=100');
      if (!response.ok) throw new Error('Failed to fetch floors');
      return response.json();
    },
    enabled: !floorId,
  });

  const { data: propertiesData } = useQuery({
    queryKey: ['properties'],
    queryFn: () => propertiesApi.getAll(0, 100),
  });

  const { data: buildingsData } = useQuery({
    queryKey: ['buildings'],
    queryFn: () => buildingsApi.getAll(),
    enabled: !!propertyId,
  });

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['units', floorId, propertyId, statusFilter, page],
    queryFn: () => {
      if (floorId) {
        return unitsApi.getByFloor(floorId, page, 20);
      }
      return unitsApi.getAll({ propertyId: propertyId || undefined, status: statusFilter || undefined, page, size: 20 });
    },
  });

  const createMutation = useMutation({
    mutationFn: (data: UnitFormData) => unitsApi.create(data.floorId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['units'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setIsModalOpen(false);
      toast.success('Unit created successfully');
    },
    onError: () => toast.error('Failed to create unit'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<UnitFormData> }) =>
      unitsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['units'] });
      setIsModalOpen(false);
      setEditingUnit(null);
      toast.success('Unit updated successfully');
    },
    onError: () => toast.error('Failed to update unit'),
  });

  const deleteMutation = useMutation({
    mutationFn: unitsApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['units'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      setDeleteId(null);
      toast.success('Unit deleted successfully');
    },
    onError: () => toast.error('Failed to delete unit'),
  });

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const formDataObj: UnitFormData = {
      propertyId: formData.get('propertyId') as string,
      buildingId: formData.get('buildingId') as string,
      floorId: formData.get('floorId') as string,
      unitNumber: formData.get('unitNumber') as string,
      unitType: formData.get('unitType') as any,
      bedrooms: parseInt(formData.get('bedrooms') as string, 10),
      bathrooms: parseInt(formData.get('bathrooms') as string, 10),
      squareFeet: formData.get('squareFeet') ? parseInt(formData.get('squareFeet') as string, 10) : undefined,
      monthlyRent: parseFloat(formData.get('monthlyRent') as string),
      securityDeposit: parseFloat(formData.get('securityDeposit') as string),
      description: (formData.get('description') as string) || '',
      amenities: (formData.get('amenities') as string)?.split(',').map((a: string) => a.trim()).filter(Boolean) || [],
    };

    if (editingUnit) {
      updateMutation.mutate({ id: editingUnit.id, data: formDataObj });
    } else {
      createMutation.mutate(formDataObj);
    }
  };

  const units = data?.content || [];

  return (
    <div>
      <PageHeader
        title="Units"
        description="Manage units across floors"
        action={
          (isOwner || isManager) && (
            <Button onClick={() => { setEditingUnit(null); setIsModalOpen(true); }}>
              Add Unit
            </Button>
          )
        }
      />

      {!floorId && !propertyId && (
        <Card className="mb-6">
          <p className="text-sm text-slate-600 mb-2">Select a floor or property to view units</p>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {floorsData?.content.map((f: any) => (
              <Link key={f.id} to={`/units?floorId=${f.id}`}>
                <Card className="hover:shadow-md transition-shadow">
                  <h3 className="font-semibold text-slate-900">Floor {f.level}</h3>
                  <p className="text-sm text-slate-600 mt-1">{f.unitsCount} units</p>
                </Card>
              </Link>
            ))}
          </div>
        </Card>
      )}

      {(floorId || propertyId) && (
        <>
          {error && (
            <div className="text-center py-12">
              <p className="text-red-600 mb-4">Failed to load units</p>
              <button onClick={() => refetch()} className="text-primary-600 hover:underline">Try again</button>
            </div>
          )}

          {isLoading ? (
            <TableSkeleton rows={10} cols={6} />
          ) : units.length === 0 ? (
            <EmptyState
              title="No units found"
              description="Add your first unit"
              action={
                (isOwner || isManager) && (
                  <Button onClick={() => { setEditingUnit(null); setIsModalOpen(true); }}>
                    Add Unit
                  </Button>
                )
              }
            />
          ) : (
            <Card>
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-200">
                    <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Unit Number</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Type</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Bedrooms</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Rent</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Status</th>
                    {(isOwner || isManager) && (
                      <th className="px-4 py-3 text-right text-sm font-semibold text-slate-900">Actions</th>
                    )}
                  </tr>
                </thead>
                <tbody>
                  {units.map((unit: Unit) => (
                    <tr key={unit.id} className="border-b border-slate-100 hover:bg-slate-50">
                      <td className="px-4 py-4 text-sm font-medium text-slate-900">{unit.unitNumber}</td>
                      <td className="px-4 py-4 text-sm text-slate-700">{unit.unitType.replace(/_/g, ' ')}</td>
                      <td className="px-4 py-4 text-sm text-slate-700">{unit.bedrooms}</td>
                      <td className="px-4 py-4 text-sm text-slate-700">${unit.monthlyRent.toLocaleString()}</td>
                      <td className="px-4 py-4">
                        <Badge variant={unit.status === 'OCCUPIED' ? 'success' : unit.status === 'VACANT' ? 'info' : 'warning'}>
                          {unit.status}
                        </Badge>
                      </td>
                      {(isOwner || isManager) && (
                        <td className="px-4 py-4 text-right">
                          <Button size="sm" variant="ghost" onClick={() => { setEditingUnit(unit); setIsModalOpen(true); }}>Edit</Button>
                          <Button size="sm" variant="ghost" className="text-red-600" onClick={() => setDeleteId(unit.id)}>Delete</Button>
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </Card>
          )}
        </>
      )}

      <Modal open={isModalOpen} onClose={() => { setIsModalOpen(false); setEditingUnit(null); }} title={editingUnit ? 'Edit Unit' : 'Add Unit'} size="lg">
        <form onSubmit={handleSubmit} className="space-y-4">
          <Select name="propertyId" label="Property" required defaultValue={editingUnit?.propertyId || propertyId || undefined}>
            <option value="">Select Property</option>
            {propertiesData?.content.map((p: any) => (
              <option key={p.id} value={p.id}>{p.name}</option>
            ))}
          </Select>
          <Select name="buildingId" label="Building" required defaultValue={editingUnit?.buildingId}>
            <option value="">Select Building</option>
            {buildingsData?.content.map((b: any) => (
              <option key={b.id} value={b.id}>{b.name}</option>
            ))}
          </Select>
          <Select name="floorId" label="Floor" required defaultValue={editingUnit?.floorId}>
            <option value="">Select Floor</option>
            {floorsData?.content.map((f: any) => (
              <option key={f.id} value={f.id}>Floor {f.level}</option>
            ))}
          </Select>
          <div className="grid grid-cols-2 gap-4">
            <Input name="unitNumber" label="Unit Number" defaultValue={editingUnit?.unitNumber} required />
            <Select name="unitType" label="Type" defaultValue={editingUnit?.unitType}>
              <option value="STUDIO">Studio</option>
              <option value="ONE_BEDROOM">One Bedroom</option>
              <option value="TWO_BEDROOM">Two Bedroom</option>
              <option value="THREE_BEDROOM">Three Bedroom</option>
              <option value="PENTHOUSE">Penthouse</option>
              <option value="COMMERCIAL">Commercial</option>
            </Select>
          </div>
          <div className="grid grid-cols-3 gap-4">
            <Input name="bedrooms" label="Bedrooms" type="number" defaultValue={editingUnit?.bedrooms} required />
            <Input name="bathrooms" label="Bathrooms" type="number" defaultValue={editingUnit?.bathrooms} required />
            <Input name="squareFeet" label="Square Feet" type="number" defaultValue={editingUnit?.squareFeet} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Input name="monthlyRent" label="Monthly Rent" type="number" step="0.01" defaultValue={editingUnit?.monthlyRent} required />
            <Input name="securityDeposit" label="Security Deposit" type="number" step="0.01" defaultValue={editingUnit?.securityDeposit} required />
          </div>
          <Input name="description" label="Description" defaultValue={editingUnit?.description} />
          <Input name="amenities" label="Amenities (comma separated)" defaultValue={editingUnit?.amenities?.join(', ')} />
          <Select
            name="status"
            label="Status"
            defaultValue={editingUnit?.status || 'VACANT'}
            options={[
              { value: 'VACANT', label: 'Vacant' },
              { value: 'OCCUPIED', label: 'Occupied' },
              { value: 'RESERVED', label: 'Reserved' },
              { value: 'UNDER_MAINTENANCE', label: 'Under Maintenance' },
            ]}
          />
          <div className="flex gap-3 pt-4">
            <Button type="button" variant="secondary" onClick={() => { setIsModalOpen(false); setEditingUnit(null); }}>Cancel</Button>
            <Button type="submit" isLoading={createMutation.isPending || updateMutation.isPending}>
              {editingUnit ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteId}
        onClose={() => setDeleteId(null)}
        onConfirm={() => { if (deleteId) { deleteMutation.mutate(deleteId); } }}
        title="Delete Unit"
        message="Are you sure? This action cannot be undone."
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};

export default Units;





