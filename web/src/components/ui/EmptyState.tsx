interface EmptyStateProps {
  title: string;
  description?: string;
  icon?: React.ReactNode;
  action?: React.ReactNode;
}

const EmptyState = ({ title, description, icon, action }: EmptyStateProps) => {
  return (
    <div className="text-center py-12">
      {icon && (
        <div className="mx-auto w-16 h-16 mb-4 text-slate-300">
          {icon}
        </div>
      )}
      <h3 className="text-lg font-semibold text-slate-900 mb-2">{title}</h3>
      {description && <p className="text-sm text-slate-500 mb-6 max-w-sm mx-auto">{description}</p>}
      {action && <div className="mt-6">{action}</div>}
    </div>
  );
};

export default EmptyState;
