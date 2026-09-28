import { createBrowserRouter, Navigate } from 'react-router-dom';
import AppLayout from '../layouts/AppLayout';
import AuthLayout from '../layouts/AuthLayout';
import Login from '../pages/Login';
import Dashboard from '../pages/Dashboard';
import Properties from '../pages/Properties';
import PropertyDetails from '../pages/PropertyDetails';
import Buildings from '../pages/Buildings';
import Floors from '../pages/Floors';
import Units from '../pages/Units';
import Tenants from '../pages/Tenants';
import Managers from '../pages/Managers';
import Kyc from '../pages/Kyc';
import AuditLogs from '../pages/AuditLogs';
import Profile from '../pages/Profile';
import NotFound from '../pages/NotFound';
import Leases from '../pages/Leases';
import LeaseDetails from '../pages/LeaseDetails';
import Billing from '../pages/Billing';
import Invoices from '../pages/Invoices';
import Payments from '../pages/Payments';
import Receipts from '../pages/Receipts';
import Statements from '../pages/Statements';
import Expenses from '../pages/Expenses';
import Maintenance from '../pages/Maintenance';
import Extensions from '../pages/Extensions';
import SmartAccess from '../pages/SmartAccess';
import Reports from '../pages/Reports';
import Notifications from '../pages/Notifications';
import AccessStatus from '../pages/AccessStatus';
import { useAuth } from '../hooks/useAuth';
import { useRole } from '../hooks/useRole';
import AiAssistant from '../pages/AiAssistant';

const ProtectedRoute = ({ children, allowedRoles }: { children: React.ReactNode; allowedRoles: string[] }) => {
  const { user, isAuthenticated, isLoading } = useAuth();
  const { role } = useRole(user?.role);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="w-12 h-12 border-4 border-primary-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!allowedRoles.includes(role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};

const router = createBrowserRouter([
  {
    path: '/login',
    element: <AuthLayout />,
    children: [
      {
        index: true,
        element: <Login />,
      },
    ],
  },
  {
    path: '/',
    element: (
      <ProtectedRoute allowedRoles={['OWNER', 'MANAGER', 'TENANT']}>
        <AppLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/dashboard" replace />,
      },
      {
        path: 'dashboard',
        element: <Dashboard />,
      },
      {
        path: 'properties',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Properties />
          </ProtectedRoute>
        ),
      },
      {
        path: 'properties/:id',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <PropertyDetails />
          </ProtectedRoute>
        ),
      },
      {
        path: 'buildings',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Buildings />
          </ProtectedRoute>
        ),
      },
      {
        path: 'floors',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Floors />
          </ProtectedRoute>
        ),
      },
      {
        path: 'units',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Units />
          </ProtectedRoute>
        ),
      },
      {
        path: 'tenants',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Tenants />
          </ProtectedRoute>
        ),
      },
      {
        path: 'managers',
        element: (
          <ProtectedRoute allowedRoles={['OWNER']}>
            <Managers />
          </ProtectedRoute>
        ),
      },
      {
        path: 'kyc',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER', 'TENANT']}>
            <Kyc />
          </ProtectedRoute>
        ),
      },
      {
        path: 'audit-logs',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <AuditLogs />
          </ProtectedRoute>
        ),
      },
      {
        path: 'profile',
        element: <Profile />,
      },
      {
        path: 'leases',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Leases />
          </ProtectedRoute>
        ),
      },
      {
        path: 'leases/:id',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <LeaseDetails />
          </ProtectedRoute>
        ),
      },
      {
        path: 'billing',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Billing />
          </ProtectedRoute>
        ),
      },
      {
        path: 'invoices',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Invoices />
          </ProtectedRoute>
        ),
      },
      {
        path: 'payments',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Payments />
          </ProtectedRoute>
        ),
      },
      {
        path: 'receipts',
        element: (
          <ProtectedRoute allowedRoles={['OWNER']}>
            <Receipts />
          </ProtectedRoute>
        ),
      },
      {
        path: 'statements',
        element: (
          <ProtectedRoute allowedRoles={['OWNER']}>
            <Statements />
          </ProtectedRoute>
        ),
      },
      {
        path: 'expenses',
        element: (
          <ProtectedRoute allowedRoles={['OWNER']}>
            <Expenses />
          </ProtectedRoute>
        ),
      },
      {
        path: 'maintenance',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER', 'TENANT']}>
            <Maintenance />
          </ProtectedRoute>
        ),
      },
      {
        path: 'extensions',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER', 'TENANT']}>
            <Extensions />
          </ProtectedRoute>
        ),
      },
      {
        path: 'smart-access',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <SmartAccess />
          </ProtectedRoute>
        ),
      },
      {
        path: 'reports',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER']}>
            <Reports />
          </ProtectedRoute>
        ),
      },
      {
        path: 'notifications',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER', 'TENANT']}>
            <Notifications />
          </ProtectedRoute>
        ),
      },
      {
        path: 'my-lease',
        element: (
          <ProtectedRoute allowedRoles={['TENANT']}>
            <Leases />
          </ProtectedRoute>
        ),
      },
      {
        path: 'my-billing',
        element: (
          <ProtectedRoute allowedRoles={['TENANT']}>
            <Billing />
          </ProtectedRoute>
        ),
      },
      {
        path: 'my-payments',
        element: (
          <ProtectedRoute allowedRoles={['TENANT']}>
            <Payments />
          </ProtectedRoute>
        ),
      },
      {
        path: 'my-receipts',
        element: (
          <ProtectedRoute allowedRoles={['TENANT']}>
            <Receipts />
          </ProtectedRoute>
        ),
      },
      {
        path: 'my-statement',
        element: (
          <ProtectedRoute allowedRoles={['TENANT']}>
            <Statements />
          </ProtectedRoute>
        ),
      },
      {
        path: 'access-status',
        element: (
          <ProtectedRoute allowedRoles={['TENANT']}>
            <AccessStatus />
          </ProtectedRoute>
        ),
      },
      {
        path: 'ai-assistant',
        element: (
          <ProtectedRoute allowedRoles={['OWNER', 'MANAGER', 'TENANT']}>
            <AiAssistant />
          </ProtectedRoute>
        ),
      },
    ],
  },
  {
    path: '*',
    element: <NotFound />,
  },
]);

export default router;

