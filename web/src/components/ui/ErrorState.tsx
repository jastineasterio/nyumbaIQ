import Button from './Button';

interface ErrorStateProps {
  title?: string;
  message?: string;
  onRetry?: () => void;
}

const ErrorState = ({ title = 'Something went wrong', message = 'Please try again later.', onRetry }: ErrorStateProps) => {
  return (
    <div className="text-center py-12">
      <div className="mx-auto w-16 h-16 mb-4 text-red-400">
        <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4.5c-.774-.833-1.732-.833-2.5 0L3.982 16.5c-.774.833.192 2.5 1.732 2.5z" />
        </svg>
      </div>
      <h3 className="text-lg font-semibold text-slate-900 mb-2">{title}</h3>
      <p className="text-sm text-slate-500 mb-6 max-w-sm mx-auto">{message}</p>
      {onRetry && (
        <Button onClick={onRetry}>Try Again</Button>
      )}
    </div>
  );
};

export default ErrorState;
