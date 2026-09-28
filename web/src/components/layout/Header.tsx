import { useAuth } from '../../hooks/useAuth';
import { useNavigate } from 'react-router-dom';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <header className="hidden lg:flex items-center justify-between h-16 px-8 border-b border-slate-200 bg-white sticky top-0 z-40">
      <div className="flex-1" />
      <div className="flex items-center gap-4">
        <div className="text-right">
          <p className="text-sm font-medium text-slate-900">
            {user?.firstName} {user?.lastName}
          </p>
          <p className="text-xs text-slate-500 capitalize">{user?.role}</p>
        </div>
        <div className="w-8 h-8 rounded-full bg-gradient-to-br from-primary-500 to-accent-500 flex items-center justify-center text-white text-sm font-medium">
          {user?.firstName?.[0]}
          {user?.lastName?.[0]}
        </div>
        <button
          onClick={handleLogout}
          className="text-sm text-slate-600 hover:text-slate-900"
        >
          Logout
        </button>
      </div>
    </header>
  );
};

export default Header;


