import { NavLink } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

interface SidebarContentProps {
  links: { to: string; label: string; icon: string }[];
  onClose?: () => void;
}

export const SidebarContent = ({ links, onClose }: SidebarContentProps) => {
  const { user, logout } = useAuth();

  return (
    <>
      <div className="flex items-center gap-2 px-6 h-16 border-b border-slate-200">
        <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center">
          <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
          </svg>
        </div>
        <span className="text-lg font-bold text-slate-900">NyumbaIQ</span>
      </div>
      <div className="flex-1 overflow-y-auto p-4">
        <nav className="space-y-1">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-3 rounded-xl text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-primary-50 text-primary-700'
                    : 'text-slate-600 hover:bg-slate-100'
                }`
              }
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={link.icon} />
              </svg>
              {link.label}
            </NavLink>
          ))}
        </nav>
      </div>
      <div className="p-4 border-t border-slate-200">
        <button
          onClick={() => {
            logout();
          }}
          className="flex items-center gap-3 px-3 py-3 rounded-xl text-sm font-medium text-red-600 hover:bg-red-50 transition-colors w-full"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          Logout
        </button>
      </div>
    </>
  );
};

interface SidebarProps {
  links: { to: string; label: string; icon: string }[];
  onClose?: () => void;
}

const Sidebar = ({ links, onClose }: SidebarProps) => {
  return (
    <aside className="hidden lg:flex lg:flex-col lg:fixed lg:inset-y-0 lg:w-[var(--sidebar-width)] lg:z-50 lg:h-screen lg:overflow-hidden">
      <div className="flex flex-col flex-1 bg-white border-r border-slate-200 h-full">
        <SidebarContent links={links} onClose={onClose} />
      </div>
    </aside>
  );
};

export default Sidebar;
