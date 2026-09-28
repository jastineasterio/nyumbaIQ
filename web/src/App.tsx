import { RouterProvider } from 'react-router-dom';
import router from './routes';
import InstallBanner from './components/ui/InstallBanner';

const App = () => {
  return (
    <>
      <RouterProvider router={router} />
      <InstallBanner />
    </>
  );
};

export default App;
