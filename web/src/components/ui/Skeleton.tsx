interface SkeletonProps {
  className?: string;
  variant?: 'text' | 'circular' | 'rectangular';
  width?: string | number;
  height?: string | number;
}

const Skeleton = ({ className = '', variant = 'rectangular', width, height }: SkeletonProps) => {
  const baseStyles = 'animate-pulse bg-slate-200';
  const variants = {
    text: 'rounded',
    circular: 'rounded-full',
    rectangular: 'rounded-lg',
  };

  const style: React.CSSProperties = {};
  if (width) style.width = typeof width === 'number' ? `${width}px` : width;
  if (height) style.height = typeof height === 'number' ? `${height}px` : height;

  return (
    <div
      className={`${baseStyles} ${variants[variant]} ${className}`}
      style={style}
    />
  );
};

export const TableSkeleton = ({ rows = 5, cols = 5 }: { rows?: number; cols?: number }) => (
  <div className="overflow-x-auto">
    <table className="w-full">
      <thead>
        <tr className="border-b border-slate-200">
          {Array.from({ length: cols }).map((_, i) => (
            <th key={i} className="px-4 py-3">
              <Skeleton variant="text" width="60%" height={16} />
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {Array.from({ length: rows }).map((_, rowIndex) => (
          <tr key={rowIndex} className="border-b border-slate-100">
            {Array.from({ length: cols }).map((_, colIndex) => (
              <td key={colIndex} className="px-4 py-4">
                <Skeleton variant="text" height={14} />
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);

export const CardSkeleton = () => (
  <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
    <div className="space-y-4">
      <Skeleton variant="text" width="40%" height={24} />
      <Skeleton variant="text" width="80%" height={16} />
      <Skeleton variant="text" width="60%" height={16} />
      <div className="flex gap-2 pt-4">
        <Skeleton variant="rectangular" width={80} height={36} />
        <Skeleton variant="rectangular" width={80} height={36} />
      </div>
    </div>
  </div>
);

export default Skeleton;
