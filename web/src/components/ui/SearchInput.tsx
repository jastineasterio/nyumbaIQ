import { InputHTMLAttributes, forwardRef } from 'react';

interface SearchInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

const SearchInput = forwardRef<HTMLInputElement, SearchInputProps>(
  ({ label, className = '', ...props }, ref) => {
    return (
      <div className="relative">
        {label && (
          <label className="block text-sm font-medium text-slate-700 mb-1.5">
            {label}
          </label>
        )}
        <div className="relative">
          <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            ref={ref}
            type="search"
            className={`w-full pl-10 pr-4 py-2.5 rounded-xl border-2 border-slate-200 text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent transition-colors ${className}`}
            {...props}
          />
        </div>
      </div>
    );
  }
);

SearchInput.displayName = 'SearchInput';

export default SearchInput;
