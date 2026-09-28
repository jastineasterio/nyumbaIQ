import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { statementsApi } from '../api/statements';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import ErrorState from '../components/ui/ErrorState';
import { TableSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';
import type { Statement, StatementGenerateRequest } from '../types';

const Statements = () => {
  const queryClient = useQueryClient();
  const [isGenerateOpen, setIsGenerateOpen] = useState(false);
  const [form, setForm] = useState<StatementGenerateRequest>({ tenantId: '', periodStart: '', periodEnd: '' });
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['statements'], queryFn: () => statementsApi.getAll(0, 100), staleTime: 30000 });
  const generateMutation = useMutation({ mutationFn: statementsApi.generate, onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['statements'] }); setIsGenerateOpen(false); } });

  if (error) return <div><PageHeader title="Statements" /><ErrorState onRetry={() => refetch()} /></div>;

  const statements = data?.content ?? [];

  return (
    <div>
      <PageHeader title="Statements" description="Account statements" action={<Button onClick={() => setIsGenerateOpen(true)}>Generate Statement</Button>} />
      {isLoading ? <TableSkeleton rows={5} cols={5} /> : statements.length === 0 ? (
        <EmptyState title="No statements" description="Generate a statement to view account history." action={<Button onClick={() => setIsGenerateOpen(true)}>Generate Statement</Button>} />
      ) : (
        <Card padding="none">
          <Table data={statements} keyExtractor={(s) => s.id} columns={[
            { key: 'statementNumber', header: 'Statement #' },
            { key: 'tenantName', header: 'Tenant', render: (s) => s.tenantName ?? s.tenantId },
            { key: 'periodStart', header: 'Period Start' },
            { key: 'periodEnd', header: 'Period End' },
            { key: 'closingBalance', header: 'Closing Balance', render: (s) => `$${s.closingBalance.toLocaleString()}` },
          ]} />
        </Card>
      )}
      <Modal open={isGenerateOpen} onClose={() => setIsGenerateOpen(false)} title="Generate Statement" size="lg">
        <div className="space-y-4">
          <Input label="Tenant ID" value={form.tenantId} onChange={(e) => setForm({ ...form, tenantId: e.target.value })} />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Period Start" type="date" value={form.periodStart} onChange={(e) => setForm({ ...form, periodStart: e.target.value })} />
            <Input label="Period End" type="date" value={form.periodEnd} onChange={(e) => setForm({ ...form, periodEnd: e.target.value })} />
          </div>
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsGenerateOpen(false)}>Cancel</Button>
            <Button onClick={() => generateMutation.mutate(form)} isLoading={generateMutation.isPending}>Generate</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default Statements;
