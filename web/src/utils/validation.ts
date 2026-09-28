export const validators = {
  required: (value: string) => {
    if (!value.trim()) return 'This field is required';
    return undefined;
  },
  email: (value: string) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(value)) return 'Please enter a valid email address';
    return undefined;
  },
  minLength: (min: number) => (value: string) => {
    if (value.length < min) return `Must be at least ${min} characters`;
    return undefined;
  },
  maxLength: (max: number) => (value: string) => {
    if (value.length > max) return `Must be no more than ${max} characters`;
    return undefined;
  },
  phone: (value: string) => {
    const phoneRegex = /^\+?[\d\s-()]+$/;
    if (!phoneRegex.test(value)) return 'Please enter a valid phone number';
    return undefined;
  },
  number: (value: string) => {
    if (isNaN(Number(value))) return 'Please enter a valid number';
    return undefined;
  },
  positiveNumber: (value: string) => {
    const num = Number(value);
    if (isNaN(num) || num <= 0) return 'Please enter a positive number';
    return undefined;
  },
};

export const validate = (values: Record<string, string>, rules: Record<string, ((value: string) => string | undefined)[]>): Record<string, string> => {
  const errors: Record<string, string> = {};
  for (const [key, validatorsList] of Object.entries(rules)) {
    const value = values[key] || '';
    for (const validator of validatorsList) {
      const error = validator(value);
      if (error) {
        errors[key] = error;
        break;
      }
    }
  }
  return errors;
};
