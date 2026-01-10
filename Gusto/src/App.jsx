import { Route, Routes, Navigate } from 'react-router-dom';
import Login from './pages/Auth/Login';
import Dashboard from './pages/Dashboard';
import Users from './pages/Users';
import Confirmation from './pages/Auth/Confirmation';
import PointOfSale from './pages/POS';
import KitchenDashboard from './pages/kitchen/Kitchen';
import ServeurPage from './pages/serveur/ServeurPage';
import PaymentInterface from './pages/PaymentInterface';
import ProtectedRoute from './widget/ProtectedRoute';
import ProfilePage from './pages/ProfilePage';

function App() {
  return (
    <Routes>
      {/* Routes publiques */}
      <Route path='/login' element={<Login />} />
      <Route path='/confirmation' element={<Confirmation />} />
      <Route path='/' element={<Navigate to="/login" />} />
      <Route path="/POS" element={<PointOfSale/>}/>

      {/* --- ROUTES ACCESSIBLES À TOUS LES CONNECTÉS --- */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_MANAGER', 'ROLE_SERVEUR', 'ROLE_CUISINIER']} />}>
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/POS" element={<PointOfSale/>}/>
      </Route>

      {/* --- ROUTES MANAGER --- */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_MANAGER']} />}>
        <Route path='/dashboard' element={<Dashboard />}>
          <Route index element={<div>Bienvenue sur le Dashboard</div>} />
        </Route>
        <Route path='/users' element={<Users />} />
      </Route>

      {/* --- ROUTES SERVEUR --- */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_SERVEUR']} />}>
        <Route path='/server' element={<ServeurPage/>}/>
        <Route path='/payment' element={<PaymentInterface/>}/>
      </Route>

      {/* --- ROUTES CUISINIER --- */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_CUISINIER']} />}>
        <Route path='/kitchen' element={<KitchenDashboard/>}/>
      </Route>

      {/* Fallback */}
      <Route path='*' element={<Navigate to="/login" />} />
    </Routes>
  );
}

export default App;