import { Route, Routes, Navigate } from 'react-router-dom';
import Login from './pages/Auth/Login';
import Dashboard from './pages/Dashboard';
import Users from './pages/Users';
import Confirmation from './pages/Auth/Confirmation';
import PointOfSale from './pages/POS';
import KitchenDashboard from './pages/kitchen/Kitchen';
import ServeurPage from './pages/serveur/ServeurPage';
import PaymentInterface from './pages/PaymentInterface';

function App() {
  return (
    <Routes>
      {/* Route par défaut : Redirige vers Confirmation ou Login au démarrage */}
      <Route path='/' element={<Navigate to="/POS" />} />

      {/* Routes d'authentification */}
      <Route path='/login' element={<Login />} />
      <Route path='/confirmation' element={<Confirmation />} />
      <Route path="/POS" element={<PointOfSale/>}/>

      {/* Routes principales */}
      <Route path='/dashboard' element={<Dashboard />}>
        {/* Ici, on ne remet pas Dashboard en element, on met le contenu de la page d'accueil du dashboard */}
        <Route index element={<div>Bienvenue sur le Dashboard</div>} />
      </Route>
        <Route path='/users' element={<Users />} />
        <Route path='/kitchen' element={<KitchenDashboard/>}/>
        <Route path='/server' element={<ServeurPage/>}/>
        <Route path='/payment' element={<PaymentInterface/>}/>

      {/* Fallback : si l'URL n'existe pas, retour à la confirmation */}
      <Route path='*' element={<Navigate to="/confirmation" />} />
    </Routes>
  );
}

export default App;

