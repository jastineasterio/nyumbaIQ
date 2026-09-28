import { useEffect } from 'react';

interface MobileDrawerProps {
  open: boolean;
  onClose: () => void;
  children: React.ReactNode;
}

const MobileDrawer = ({ open, onClose, children }: MobileDrawerProps) => {
  useEffect(() => {
    if (open) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [open]);

  if (!open) return null;

  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-50 lg:hidden" onClick={onClose} />
      <div className="fixed inset-y-0 left-0 w-72 bg-white z-50 shadow-xl lg:hidden">
        {children}
      </div>
    </>
  );
};

export default MobileDrawer;

