import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { kycApi } from '../api/kyc';
import type { KYCDocument, DocumentType } from '../types';
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

const Kyc = () => {
  const [isUploadModalOpen, setIsUploadModalOpen] = useState(false);
  const [selectedTenantId, setSelectedTenantId] = useState('');
  const [documentType, setDocumentType] = useState<DocumentType>('ID_CARD');
  const [file, setFile] = useState<File | null>(null);
  const [reviewingDoc, setReviewingDoc] = useState<KYCDocument | null>(null);
  const [reviewStatus, setReviewStatus] = useState<'APPROVED' | 'REJECTED'>('APPROVED');
  const [rejectionReason, setRejectionReason] = useState('');
  const [deleteDocId, setDeleteDocId] = useState<string | null>(null);
  const queryClient = useQueryClient();
  const { user } = useAuth();

  const { data: profile, isLoading: profileLoading } = useQuery({
    queryKey: ['kyc', 'profile', selectedTenantId],
    queryFn: () => kycApi.getProfile(selectedTenantId),
    enabled: !!selectedTenantId,
  });

  const { data: documents, isLoading: docsLoading } = useQuery({
    queryKey: ['kyc', 'documents', selectedTenantId],
    queryFn: () => kycApi.getDocuments(selectedTenantId, 0, 20),
    enabled: !!selectedTenantId,
  });

  const uploadMutation = useMutation({
    mutationFn: () => {
      if (!selectedTenantId || !file) throw new Error('Missing tenant ID or file');
      return kycApi.uploadDocument(selectedTenantId, { documentType, file });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['kyc', 'documents', selectedTenantId] });
      setIsUploadModalOpen(false);
      setFile(null);
      setDocumentType('ID_CARD');
      toast.success('Document uploaded successfully');
    },
    onError: () => toast.error('Failed to upload document'),
  });

  const reviewMutation = useMutation({
    mutationFn: ({ id, status, reason }: { id: string; status: 'APPROVED' | 'REJECTED'; reason?: string }) =>
      kycApi.reviewDocument(id, status, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['kyc', 'documents', selectedTenantId] });
      setReviewingDoc(null);
      setRejectionReason('');
      toast.success('Document reviewed successfully');
    },
    onError: () => toast.error('Failed to review document'),
  });

  const deleteMutation = useMutation({
    mutationFn: kycApi.deleteDocument,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['kyc', 'documents', selectedTenantId] });
      setDeleteDocId(null);
      toast.success('Document deleted successfully');
    },
    onError: () => toast.error('Failed to delete document'),
  });

  const handleUpload = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    uploadMutation.mutate();
  };

  const handleReview = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!reviewingDoc) return;
    reviewMutation.mutate({
      id: reviewingDoc.id,
      status: reviewStatus,
      reason: reviewStatus === 'REJECTED' ? rejectionReason : undefined,
    });
  };

  const documentTypes: { value: DocumentType; label: string }[] = [
    { value: 'ID_CARD', label: 'ID Card' },
    { value: 'PASSPORT', label: 'Passport' },
    { value: 'DRIVERS_LICENSE', label: "Driver's License" },
    { value: 'PROOF_OF_ADDRESS', label: 'Proof of Address' },
    { value: 'EMPLOYMENT_LETTER', label: 'Employment Letter' },
    { value: 'BANK_STATEMENT', label: 'Bank Statement' },
    { value: 'OTHER', label: 'Other' },
  ];

  return (
    <div>
      <PageHeader
        title="KYC"
        description="Know Your Customer verification"
        action={
          <Button onClick={() => setIsUploadModalOpen(true)}>Upload Document</Button>
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          {profileLoading ? (
            <Skeleton height={300} />
          ) : profile ? (
            <Card className="mb-6">
              <h3 className="text-lg font-semibold text-slate-900 mb-4">KYC Profile</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-slate-600">Full Name</p>
                  <p className="font-medium text-slate-900">{profile.fullName}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Email</p>
                  <p className="font-medium text-slate-900">{profile.email}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Phone</p>
                  <p className="font-medium text-slate-900">{profile.phone}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Status</p>
                  <Badge variant={profile.status === 'APPROVED' ? 'success' : profile.status === 'REJECTED' ? 'danger' : 'warning'}>
                    {profile.status}
                  </Badge>
                </div>
              </div>
            </Card>
          ) : (
            <Card className="mb-6">
              <p className="text-sm text-slate-600">Select a tenant to view their KYC profile</p>
            </Card>
          )}

          <Card>
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Documents</h3>
            {docsLoading ? (
              <TableSkeleton rows={5} cols={4} />
            ) : documents?.content.length === 0 ? (
              <EmptyState title="No documents" description="Upload documents for verification" />
            ) : (
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-200">
                    <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Type</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">File</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-slate-900">Status</th>
                    <th className="px-4 py-3 text-right text-sm font-semibold text-slate-900">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {documents?.content.map((doc: KYCDocument) => (
                    <tr key={doc.id} className="border-b border-slate-100 hover:bg-slate-50">
                      <td className="px-4 py-4 text-sm text-slate-700">{doc.documentType.replace(/_/g, ' ')}</td>
                      <td className="px-4 py-4 text-sm text-slate-700">{doc.fileName}</td>
                      <td className="px-4 py-4">
                        <Badge variant={doc.status === 'APPROVED' ? 'success' : doc.status === 'REJECTED' ? 'danger' : 'warning'}>
                          {doc.status}
                        </Badge>
                      </td>
                      <td className="px-4 py-4 text-right">
                        <Button size="sm" variant="ghost" onClick={() => { setReviewingDoc(doc); setReviewStatus(doc.status as any); }}>
                          Review
                        </Button>
                        <Button size="sm" variant="ghost" className="text-red-600" onClick={() => setDeleteDocId(doc.id)}>Delete</Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </Card>
        </div>

        <div>
          <Card>
            <h3 className="text-lg font-semibold text-slate-900 mb-4">Quick Actions</h3>
            <div className="space-y-3">
              <Button className="w-full" onClick={() => setIsUploadModalOpen(true)}>
                Upload Document
              </Button>
            </div>
          </Card>
        </div>
      </div>

      <Modal open={isUploadModalOpen} onClose={() => setIsUploadModalOpen(false)} title="Upload Document">
        <form onSubmit={handleUpload} className="space-y-4">
          <Select
            label="Document Type"
            value={documentType}
            onChange={(e) => setDocumentType(e.target.value as DocumentType)}
          >
            {documentTypes.map((dt) => (
              <option key={dt.value} value={dt.value}>{dt.label}</option>
            ))}
          </Select>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">File</label>
            <input
              type="file"
              accept=".pdf,.jpg,.jpeg,.png"
              onChange={(e) => setFile(e.target.files?.[0] || null)}
              className="w-full px-4 py-2.5 rounded-xl border-2 border-slate-200 text-slate-900"
              required
            />
          </div>
          <div className="flex gap-3 pt-4">
            <Button type="button" variant="secondary" onClick={() => setIsUploadModalOpen(false)}>Cancel</Button>
            <Button type="submit" isLoading={uploadMutation.isPending}>Upload</Button>
          </div>
        </form>
      </Modal>

      <Modal open={!!reviewingDoc} onClose={() => setReviewingDoc(null)} title="Review Document">
        <form onSubmit={handleReview} className="space-y-4">
          <Select
            label="Status"
            value={reviewStatus}
            onChange={(e) => setReviewStatus(e.target.value as 'APPROVED' | 'REJECTED')}
          >
            <option value="APPROVED">Approve</option>
            <option value="REJECTED">Reject</option>
          </Select>
          {reviewStatus === 'REJECTED' && (
            <Input
              label="Rejection Reason"
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
              required
            />
          )}
          <div className="flex gap-3 pt-4">
            <Button type="button" variant="secondary" onClick={() => setReviewingDoc(null)}>Cancel</Button>
            <Button type="submit" isLoading={reviewMutation.isPending}>Submit</Button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteDocId}
        onClose={() => setDeleteDocId(null)}
        onConfirm={() => deleteDocId && deleteMutation.mutate(deleteDocId)}
        title="Delete Document"
        message="Are you sure? This action cannot be undone."
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};

export default Kyc;





