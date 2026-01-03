import DashboardIcon from '@mui/icons-material/Dashboard';
import PeopleIcon from '@mui/icons-material/People';
import SettingsIcon from '@mui/icons-material/Settings';
import AssessmentIcon from '@mui/icons-material/Assessment';
import KitchenIcon from '@mui/icons-material/Kitchen';
import RestaurantMenuIcon from '@mui/icons-material/RestaurantMenu';
import { Logout } from '@mui/icons-material';

export const NAVIGATION = [
  {
    title: 'Tableau de bord',
    path: '/dashboard',
    icon: <DashboardIcon />,
    roles: ['admin', 'manager']
  },
  {
    title: 'Gestion Utilisateurs',
    path: '/users',
    icon: <PeopleIcon />,
    roles: ['admin']
  },
  {
    title: 'Cuisine',
    path: '/kitchen',
    icon: <KitchenIcon />,
    roles: ['cuisinier']
  },
  {
    title: 'Service Salle',
    path: '/server',
    icon: <RestaurantMenuIcon />,
    roles: ['serveur']
  },
  {
    title: 'Rapports',
    path: '/reports',
    icon: <AssessmentIcon />,
    roles: ['admin', 'manager']
  },
  {
    title: 'Paramètres',
    path: '/settings',
    icon: <SettingsIcon />,
    roles: ['admin', 'manager', 'serveur', 'cuisinier']
  },
  {
    title: 'Deconnexion',
    path: '/login',
    icon: <Logout />,
    roles: ['admin', 'manager', 'serveur', 'cuisinier']
  },
];