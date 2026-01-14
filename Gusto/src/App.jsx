import { Route, Routes, Navigate } from 'react-router-dom';
import { RestaurantProvider } from './context/RestaurantContext';
import Login from './pages/Auth/Login';
import Dashboard from './pages/Dashboard';
import Users from './pages/Users';
import Confirmation from './pages/Auth/Confirmation';
import PointOfSale from './pages/POS';
import PaymentInterface from './pages/PaymentInterface';
import ProtectedRoute from './widget/ProtectedRoute';
import ProfilePage from './pages/ProfilePage';
import ServeurDashboard from './pages/serveur/ServerDashboard';
import KitchenDashboard from './pages/kitchen/KitchenDashboard';

// Manager Pages
import TablesManagement from './pages/manager/TablesManagement';
import MenuManagement from './pages/manager/MenuManagement';
import StockManagement from './pages/manager/StockManagement';
import Reports from './pages/manager/Reports';
import RestaurantSettings from './pages/manager/RestaurantSettings';
import PromotionsManagement from './pages/manager/PromotionsManagement';
import ReservationsManagement from './pages/manager/ReservationsManagement';

// Kitchen Pages
import RecipesManagement from './pages/kitchen/RecipesManagement';
import KitchenInventory from './pages/kitchen/KitchenInventory';

// Serveur Pages
import FloorPlan from './pages/serveur/FloorPlan';
import OrderHistory from './pages/serveur/OrderHistory';

// Caisse Pages
import CaisseView from './pages/caisse/CaisseView';

// Test Pages
import ImageUploadTest from './pages/test/ImageUploadTest';

// Common Pages
import Help from './pages/common/Help';
import NotificationsCenter from './pages/common/NotificationsCenter';
import NotFound from './pages/common/NotFound';

import ManagerLayout from './widget/ManagerLayout';

function App() {
  return (
    <RestaurantProvider>
      <Routes>
      {/* Routes publiques */}
      <Route path='/login' element={<Login />} />
      <Route path='/confirmation' element={<Confirmation />} />
      <Route path='/' element={<Navigate to="/login" />} />
      <Route path="/POS" element={<PointOfSale />} />
      {/* Route POS déplacée dans la section protégée */}

      {/* --- ROUTES AUTHENTIFIÉES AVEC SIDEBAR (UNITÉ DE DESIGN) --- */}
      <Route element={<ProtectedRoute allowedRoles={['ROLE_MANAGER', 'ROLE_SERVEUR', 'ROLE_CUISINIER', 'ROLE_CAISSIER', 'admin', 'manager', 'staff']} />}>
        <Route element={<ManagerLayout />}>
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/help" element={<Help />} />
          <Route path="/notifications" element={<NotificationsCenter />} />

          {/* --- ROUTES MANAGER --- */}
          <Route element={<ProtectedRoute allowedRoles={['ROLE_MANAGER', 'admin', 'manager']} />}>
            <Route path='/dashboard' element={<Dashboard />} />
            <Route path='/users' element={<Users />} />
            <Route path='/manager/tables' element={<TablesManagement />} />
            <Route path='/manager/menu' element={<MenuManagement />} />
            <Route path='/manager/stock' element={<StockManagement />} />
            <Route path='/manager/reports' element={<Reports />} />
            <Route path='/manager/settings' element={<RestaurantSettings />} />
            <Route path='/manager/promotions' element={<PromotionsManagement />} />
            <Route path='/manager/reservations' element={<ReservationsManagement />} />
          </Route>

          {/* --- ROUTES SERVEUR --- */}
          <Route element={<ProtectedRoute allowedRoles={['ROLE_SERVEUR', 'staff']} />}>
            <Route path='/server' element={<ServeurDashboard />} />
            <Route path='/serveur/floorplan' element={<FloorPlan />} />
            <Route path='/serveur/history' element={<OrderHistory />} />
          </Route>

          {/* --- ROUTES CUISINIER --- */}
          <Route element={<ProtectedRoute allowedRoles={['ROLE_CUISINIER', 'staff']} />}>
            <Route path='/kitchen' element={<KitchenDashboard />} />
            <Route path='/kitchen/recipes' element={<RecipesManagement />} />
            <Route path='/kitchen/inventory' element={<KitchenInventory />} />
          </Route>

          {/* --- ROUTES CAISSIER (DANS LE LAYOUT) --- */}
          <Route element={<ProtectedRoute allowedRoles={['ROLE_CAISSIER', 'ROLE_MANAGER', 'staff']} />}>
            <Route path='/payment' element={<PaymentInterface />} />
            <Route path='/caisse' element={<CaisseView />} />
          </Route>

          {/* --- TEST ROUTES --- */}
          <Route path='/test/image-upload' element={<ImageUploadTest />} />
        </Route>
      </Route>

      {/* Fallback */}
      <Route path='*' element={<NotFound />} />
      </Routes>
    </RestaurantProvider>
  );
}

export default App;