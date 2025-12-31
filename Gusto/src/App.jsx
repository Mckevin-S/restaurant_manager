import { Route, Routes, Navigate } from 'react-router-dom';
import Login from './pages/Auth/Login';
import Dashboard from './pages/Dashboard';
import Users from './pages/Users';
// import Users from './pages/Users'; // Assure-toi d'importer tes composants
// import HomeStats from './pages/HomeStats'; // Composant contenant tes graphiques actuels

function App() {
  return (
    <Routes>
      {/* Route publique */}
      <Route path='/login' element={<Login />} />
      
      {/* Routes protégées (Dashboard) */}
      <Route path='/dashboard' element={<Dashboard />}>
        {/* Route par défaut quand on arrive sur /dashboard */}
        <Route index element={<Dashboard />} /> 
        
        {/* Route pour /dashboard/users */}
      </Route>
        <Route path='/users' element={<Users />} />

      {/* Redirection automatique */}
      <Route path='/' element={<Navigate to="/dashboard" />} />
    </Routes>
  );
}

export default App;