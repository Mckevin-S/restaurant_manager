// import { useSelector } from 'react-redux';
// import { Navigate, Outlet, useLocation } from 'react-router-dom';

// const ProtectedRoute = ({ allowedRoles }) => {
//   const { token, user, tempUsername } = useSelector((state) => state.auth);
//   const location = useLocation();

//   // 1. Si on essaie d'aller sur /confirmation
//   if (location.pathname === '/confirmation') {
//     // On n'autorise que si le premier login a réussi (tempUsername présent)
//     return tempUsername ? <Outlet /> : <Navigate to="/login" replace />;
//   }

//   // 2. Pour toutes les autres routes protégées (Dashboard, POS, etc.)
//   if (!token) {
//     return <Navigate to="/login" replace />;
//   }

//   // 3. Vérification des rôles une fois le token présent
//   if (allowedRoles && !allowedRoles.includes(user?.role)) {
//     return <Navigate to="/login" replace />; 
//   }

//   return <Outlet />;
// };

// export default ProtectedRoute;

import { useSelector } from 'react-redux';
import { Navigate, Outlet, useLocation } from 'react-router-dom';

const ProtectedRoute = ({ allowedRoles }) => {
  const { token, user, tempUsername } = useSelector((state) => state.auth);
  const location = useLocation();

  // 1. GESTION DE LA PHASE DE CONFIRMATION (AVANT TOKEN)
  if (location.pathname === '/confirmation') {
    // Si déjà loggé, on quitte cette page
    return tempUsername ? <Outlet /> : <Navigate to="/login" replace />;
  }

  // 2. GESTION DE LA SÉCURITÉ (SANS TOKEN)
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // 3. LOGIQUE D'AIGUILLAGE AUTOMATIQUE
  // Si l'utilisateur est sur la racine "/" ou sur une page non autorisée,
  // on le redirige vers son interface métier
  const handleRedirect = () => {
    switch (user?.role) {
      case 'ROLE_MANAGER':  return '/dashboard';
      case 'ROLE_SERVEUR':  return '/server';
      case 'ROLE_CUISINIER': return '/kitchen';
      default: return '/login';
    }
  };

  // Si l'utilisateur essaie d'accéder à une page qui n'est pas pour son rôle
  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    return <Navigate to={handleRedirect()} replace />;
  }

  // Si on est sur la route racine "/", on redirige vers l'accueil métier
  if (location.pathname === '/') {
    return <Navigate to={handleRedirect()} replace />;
  }

  return <Outlet />;
};

export default ProtectedRoute;