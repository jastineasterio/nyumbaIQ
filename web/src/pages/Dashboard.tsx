import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { dashboardApi } from '../api/dashboard';
import { useAuth } from '../hooks/useAuth';
import { useRole } from '../hooks/useRole';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Skeleton, { CardSkeleton } from '../components/ui/Skeleton';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import { Link } from 'react-router-dom';
import type { DashboardStats } from '../types';

const Dashboard = () => {
  const { user } = useAuth();
  const { role } = useRole(user?.role);
  const navigate = useNavigate();
  const { data: stats, isLoading, error, refetch } = useQuery<DashboardStats>({
    queryKey: ['dashboard', 'stats'],
    queryFn: dashboardApi.getStats,
  });

  const statCards = [
    { title: 'Total Properties', value: stats?.totalProperties ?? 0, href: '/properties', color: 'from-primary-500 to-primary-600' },
    { title: 'Total Buildings', value: stats?.totalBuildings ?? 0, href: '/buildings', color: 'from-accent-500 to-accent-600' },
    { title: 'Total Floors', value: stats?.totalFloors ?? 0, href: '/floors', color: 'from-violet-500 to-violet-600' },
    { title: 'Total Units', value: stats?.totalUnits ?? 0, href: '/units', color: 'from-emerald-500 to-emerald-600' },
    { title: 'Occupied Units', value: stats?.occupiedUnits ?? 0, href: '/units', color: 'from-green-500 to-green-600' },
    { title: 'Vacant Units', value: stats?.vacantUnits ?? 0, href: '/units', color: 'from-orange-500 to-orange-600' },
    { title: 'Total Managers', value: stats?.totalManagers ?? 0, href: '/managers', color: 'from-pink-500 to-pink-600' },
    { title: 'Total Tenants', value: stats?.totalTenants ?? 0, href: '/tenants', color: 'from-blue-500 to-blue-600' },
  ];

  if (error) {
    return (
      <div>
        <PageHeader title="Dashboard" description="Welcome back!" />
        <div className="text-center py-12">
          <p className="text-red-600 mb-4">Failed to load dashboard stats</p>
          <button onClick={() => refetch()} className="text-primary-600 hover:underline">
            Try again
          </button>
        </div>
      </div>
    );
  }

  if (role === 'TENANT') {
    return (
      <div>
        <PageHeader title="Dashboard" description={`Welcome back, ${user?.firstName}!`} />
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card>
            <p className="text-sm text-slate-500">Rent Balance</p>
            <p className="text-2xl font-bold text-slate-900">$0.00</p>
            <Badge variant="warning">Due Soon</Badge>
          </Card>
          <Card>
            <p className="text-sm text-slate-500">Due Date</p>
            <p className="text-2xl font-bold text-slate-900">-</p>
            <p className="text-sm text-slate-500 mt-2">No active lease</p>
          </Card>
          <Card>
            <p className="text-sm text-slate-500">Lease Status</p>
            <p className="text-2xl font-bold text-slate-900">-</p>
          </Card>
          <Card>
            <p className="text-sm text-slate-500 mb-3">Quick Actions</p>
            <div className="flex flex-col gap-2">
              <Button size="sm" onClick={() => navigate('/my-billing')}>Pay Rent</Button>
              <Button size="sm" variant="secondary" onClick={() => navigate('/my-statement')}>View Statement</Button>
              <Button size="sm" variant="secondary" onClick={() => navigate('/maintenance')}>Maintenance</Button>
              <Button size="sm" variant="secondary" onClick={() => navigate('/access-status')}>Access Status</Button>
            </div>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div>
      <PageHeader title="Dashboard" description={`Welcome back, ${user?.firstName}!`} />

      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <CardSkeleton key={i} />
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {statCards.map((card) => (
            <Link key={card.title} to={card.href}>
              <Card className="hover:shadow-lg transition-shadow">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-sm font-medium text-slate-600 mb-1">{card.title}</p>
                    <p className="text-3xl font-bold text-slate-900">{card.value}</p>
                  </div>
                  <div className={`w-10 h-10 rounded-xl bg-gradient-to-br ${card.color} flex items-center justify-center`}>
                    <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                    </svg>
                  </div>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default Dashboard;


