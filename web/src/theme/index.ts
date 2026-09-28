import { colors, spacing, borderRadius, shadows } from './colors';

export const theme = {
  colors,
  spacing,
  borderRadius,
  shadows,
  breakpoints: {
    sm: '640px',
    md: '768px',
    lg: '1024px',
    xl: '1280px',
  },
  sidebar: {
    width: '280px',
    collapsedWidth: '80px',
  },
  header: {
    height: '64px',
  },
};

export default theme;
