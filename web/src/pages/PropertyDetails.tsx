import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { propertiesApi, buildingsApi } from '../api';
import type { Property } from '../types';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import Skeleton from '../components/ui/Skeleton';
import { useAuth } from '../hooks/useAuth';
import { useRole } from '../hooks/useRole';

const PropertyDetails = () => {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const { isOwner, isManager } = useRole(user?.role);

  const { data: property, isLoading: propertyLoading, error: propertyError } = useQuery<Property>({
    queryKey: ['property', id],
    queryFn: () => propertiesApi.getById(id!),
    enabled: !!id,
  });

  const { data: buildings } = useQuery({
    queryKey: ['buildings', id],
    queryFn: () => buildingsApi.getByProperty(id!),
    enabled: !!id,
  });

  if (propertyLoading) {
    return (
      <div>
        <PageHeader title="Property Details" />
        <div className="space-y-4">
          <Skeleton height={200} />
          <Skeleton height={300} />
        </div>
      </div>
    );
  }

  if (propertyError || !property) {
    return (
      <div>
        <PageHeader title="Property Details" />
        <div className="text-center py-12">
          <p className="text-red-600">Property not found</p>
          <Link to="/properties">
            <Button className="mt-4">Back to Properties</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div>
      <PageHeader
        title={property.name}
        description={property.description}
        breadcrumbs={[
          { label: 'Properties', href: '/properties' },
          { label: property.name },
        ]}
        action={
          (isOwner || isManager) && (
            <div className="flex gap-2">
              <Link to={`/buildings?propertyId=${property.id}`}>
                <Button>View Buildings</Button>
              </Link>
            </div>
          )
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Property Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-slate-600">Address</p>
                <p className="font-medium text-slate-900">{property.address}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Location</p>
                <p className="font-medium text-slate-900">{property.city}, {property.state}, {property.country}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Status</p>
                <Badge variant={property.status === 'ACTIVE' ? 'success' : 'warning'}>{property.status}</Badge>
              </div>
              <div>
                <p className="text-sm text-slate-600">Total Buildings</p>
                <p className="font-medium text-slate-900">{property.totalBuildings || 0}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Total Units</p>
                <p className="font-medium text-slate-900">{property.totalUnits || 0}</p>
              </div>
            </div>
          </Card>
        </div>

        <div>
          <Card>
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Quick Stats</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-slate-600">Buildings</span>
                <span className="font-semibold text-slate-900">{property.totalBuildings || 0}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-slate-600">Total Units</span>
                <span className="font-semibold text-slate-900">{property.totalUnits || 0}</span>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default PropertyDetails;

