import { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { usersApi, authApi } from '../api';
import type { User, UpdateUserRequest, ChangePasswordRequest } from '../types';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import Badge from '../components/ui/Badge';
import Skeleton from '../components/ui/Skeleton';
import toast from 'react-hot-toast';
import { useAuth } from '../hooks/useAuth';

const Profile = () => {
  const { user: authUser } = useAuth();
  const queryClient = useQueryClient();
  const [isEditing, setIsEditing] = useState(false);
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    middleName: '',
    lastName: '',
    email: '',
    phone: '',
  });
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const { data: user, isLoading, refetch } = useQuery<User>({
    queryKey: ['user'],
    queryFn: usersApi.getMe,
  });

  const updateMutation = useMutation({
    mutationFn: (data: UpdateUserRequest) => usersApi.updateMe(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['user'] });
      setIsEditing(false);
      toast.success('Profile updated successfully');
    },
    onError: () => toast.error('Failed to update profile'),
  });

  const changePasswordMutation = useMutation({
    mutationFn: (data: ChangePasswordRequest) => authApi.changePassword(data),
    onSuccess: () => {
      setShowPasswordForm(false);
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
      toast.success('Password changed successfully');
    },
    onError: () => toast.error('Failed to change password'),
  });

  useEffect(() => {
    if (user && !isEditing) {
      setFormData({
        firstName: user.firstName,
        middleName: user.middleName || '',
        lastName: user.lastName,
        email: user.email,
        phone: user.phone,
      });
    }
  }, [user, isEditing]);

  if (isLoading) {
    return (
      <div>
        <PageHeader title="Profile" description="Manage your account" />
        <Skeleton height={400} />
      </div>
    );
  }

  if (!user) {
    return (
      <div>
        <PageHeader title="Profile" />
        <p className="text-red-600">Failed to load profile</p>
      </div>
    );
  }

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    updateMutation.mutate(formData);
  };

  const handlePasswordSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      toast.error('Passwords do not match');
      return;
    }
    changePasswordMutation.mutate({
      currentPassword: passwordData.currentPassword,
      newPassword: passwordData.newPassword,
    });
  };

  return (
    <div>
      <PageHeader title="Profile" description="Manage your account settings" />

      <div className="max-w-2xl space-y-6">
        <Card>
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-slate-900">Personal Information</h3>
            {!isEditing && (
              <Button variant="secondary" onClick={() => setIsEditing(true)}>
                Edit
              </Button>
            )}
          </div>

          {isEditing ? (
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <Input
                  label="First Name"
                  value={formData.firstName}
                  onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  required
                />
                <Input
                  label="Middle Name"
                  value={formData.middleName}
                  onChange={(e) => setFormData({ ...formData, middleName: e.target.value })}
                />
              </div>
              <Input
                label="Last Name"
                value={formData.lastName}
                onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                required
              />
              <Input
                label="Email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                required
              />
              <Input
                label="Phone"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                required
              />
              <div className="flex gap-3 pt-4">
                <Button type="button" variant="secondary" onClick={() => setIsEditing(false)}>Cancel</Button>
                <Button type="submit" isLoading={updateMutation.isPending}>Save Changes</Button>
              </div>
            </form>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <p className="text-sm text-slate-600">First Name</p>
                <p className="font-medium text-slate-900">{user.firstName}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Middle Name</p>
                <p className="font-medium text-slate-900">{user.middleName || '-'}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Last Name</p>
                <p className="font-medium text-slate-900">{user.lastName}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Email</p>
                <p className="font-medium text-slate-900">{user.email}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Phone</p>
                <p className="font-medium text-slate-900">{user.phone}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Username</p>
                <p className="font-medium text-slate-900">{user.username}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Role</p>
                <Badge variant="primary">{user.role}</Badge>
              </div>
              <div>
                <p className="text-sm text-slate-600">Status</p>
                <Badge variant={user.status === 'ACTIVE' ? 'success' : 'warning'}>{user.status}</Badge>
              </div>
              <div>
                <p className="text-sm text-slate-600">Member Since</p>
                <p className="font-medium text-slate-900">{new Date(user.createdAt).toLocaleDateString()}</p>
              </div>
              <div>
                <p className="text-sm text-slate-600">Last Login</p>
                <p className="font-medium text-slate-900">
                  {user.lastLogin ? new Date(user.lastLogin).toLocaleString() : '-'}
                </p>
              </div>
            </div>
          )}
        </Card>

        <Card>
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-slate-900">Change Password</h3>
            {!showPasswordForm && (
              <Button variant="secondary" onClick={() => setShowPasswordForm(true)}>
                Change Password
              </Button>
            )}
          </div>

          {showPasswordForm && (
            <form onSubmit={handlePasswordSubmit} className="space-y-4">
              <Input
                label="Current Password"
                type="password"
                value={passwordData.currentPassword}
                onChange={(e) => setPasswordData({ ...passwordData, currentPassword: e.target.value })}
                required
              />
              <Input
                label="New Password"
                type="password"
                value={passwordData.newPassword}
                onChange={(e) => setPasswordData({ ...passwordData, newPassword: e.target.value })}
                required
              />
              <Input
                label="Confirm New Password"
                type="password"
                value={passwordData.confirmPassword}
                onChange={(e) => setPasswordData({ ...passwordData, confirmPassword: e.target.value })}
                required
              />
              <div className="flex gap-3 pt-4">
                <Button type="button" variant="secondary" onClick={() => setShowPasswordForm(false)}>Cancel</Button>
                <Button type="submit" isLoading={changePasswordMutation.isPending}>Change Password</Button>
              </div>
            </form>
          )}
        </Card>
      </div>
    </div>
  );
};

export default Profile;


