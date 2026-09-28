import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { expensesApi } from '../api/expenses';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import Select from '../components/ui/Select';
import ErrorState from '../components/ui/ErrorState';
import { TableSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';
import type { Expense, ExpenseFormData } from '../types';

const Expenses = () => {
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [form, setForm] = useState<ExpenseFormData>({ propertyId: '', category: '', description: '', amount: 0 });
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['expenses'], queryFn: () => expensesApi.getAll(0, 100), staleTime: 30000 });
  const createMutation = useMutation({ mutationFn: expensesApi.create, onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['expenses'] }); setIsModalOpen(false); } });
  const submitMutation = useMutation({ mutationFn: (id: string) => expensesApi.submit(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['expenses'] }) });
  const approveMutation = useMutation({ mutationFn: (id: string) => expensesApi.approve(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['expenses'] }) });
  const payMutation = useMutation({ mutationFn: (id: string) => expensesApi.pay(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['expenses'] }) });
  const cancelMutation = useMutation({ mutationFn: (id: string) => expensesApi.cancel(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['expenses'] }) });

  if (error) return <div><PageHeader title="Expenses" /><ErrorState onRetry={() => refetch()} /></div>;

  const expenses = data?.content ?? [];

  const statusVariant = (status: string) => {
    switch (status) {
      case 'PAID': return 'success';
      case 'APPROVED': return 'info';
      case 'SUBMITTED': return 'warning';
      case 'DRAFT': return 'secondary';
      case 'CANCELLED': return 'danger';
      default: return 'secondary';
    }
  };

  return (
    <div>
      <PageHeader title="Expenses" description="Track and approve expenses" action={<Button onClick={() => setIsModalOpen(true)}>New Expense</Button>} />
      {isLoading ? <TableSkeleton rows={5} cols={5} /> : expenses.length === 0 ? (
        <EmptyState title="No expenses" description="Create an expense entry to get started." action={<Button onClick={() => setIsModalOpen(true)}>New Expense</Button>} />
      ) : (
        <Card padding="none">
          <Table data={expenses} keyExtractor={(e) => e.id} columns={[
            { key: 'propertyName', header: 'Property', render: (e) => e.propertyName ?? e.propertyId },
            { key: 'category', header: 'Category' },
            { key: 'description', header: 'Description' },
            { key: 'amount', header: 'Amount', render: (e) => `$${e.amount.toLocaleString()}` },
            { key: 'status', header: 'Status', render: (e) => <Badge variant={statusVariant(e.status)}>{e.status}</Badge> },
            { key: 'actions', header: '', render: (e) => (
              <div className="flex gap-2">
                {e.status === 'DRAFT' && <Button size="sm" onClick={() => submitMutation.mutate(e.id)} isLoading={submitMutation.isPending}>Submit</Button>}
                {e.status === 'SUBMITTED' && <Button size="sm" onClick={() => approveMutation.mutate(e.id)} isLoading={approveMutation.isPending}>Approve</Button>}
                {e.status === 'APPROVED' && <Button size="sm" onClick={() => payMutation.mutate(e.id)} isLoading={payMutation.isPending}>Pay</Button>}
                {(e.status === 'DRAFT' || e.status === 'SUBMITTED') && <Button size="sm" variant="danger" onClick={() => cancelMutation.mutate(e.id)} isLoading={cancelMutation.isPending}>Cancel</Button>}
              </div>
            ) },
          ]} />
        </Card>
      )}
      <Modal open={isModalOpen} onClose={() => setIsModalOpen(false)} title="New Expense" size="lg">
        <div className="space-y-4">
          <Input label="Property ID" value={form.propertyId} onChange={(e) => setForm({ ...form, propertyId: e.target.value })} />
          <Input label="Category" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} />
          <Input label="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <Input label="Amount" type="number" value={form.amount} onChange={(e) => setForm({ ...form, amount: Number(e.target.value) })} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>Cancel</Button>
            <Button onClick={() => createMutation.mutate(form)} isLoading={createMutation.isPending}>Create Expense</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default Expenses;
