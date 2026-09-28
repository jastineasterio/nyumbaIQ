import { useInstallPrompt } from '../../hooks/useInstallPrompt';

const InstallBanner = () => {
  const { isVisible, promptInstall, dismiss } = useInstallPrompt();

  if (!isVisible) return null;

  return (
    <div className="fixed bottom-4 left-4 right-4 md:left-auto md:right-6 md:w-96 bg-slate-900 text-white rounded-xl shadow-2xl p-4 z-50 flex items-start gap-3">
      <div className="flex-1">
        <p className="font-semibold text-sm">Install NyumbaIQ</p>
        <p className="text-xs text-slate-300 mt-1">Add to home screen for a faster experience</p>
      </div>
      <div className="flex flex-col gap-1">
        <button
          onClick={promptInstall}
          className="text-xs bg-cyan-500 hover:bg-cyan-400 text-white px-3 py-1.5 rounded-lg font-medium transition-colors"
        >
          Install
        </button>
        <button
          onClick={dismiss}
          className="text-xs text-slate-400 hover:text-white px-1 transition-colors"
        >
          Later
        </button>
      </div>
    </div>
  );
};

export default InstallBanner;
