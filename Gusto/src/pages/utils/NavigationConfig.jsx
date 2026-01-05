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
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Gestion Utilisateurs',
    path: '/users',
    icon: <PeopleIcon />,
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Cuisine',
    path: '/kitchen',
    icon: <KitchenIcon />,
    roles: ['ROLE_CUISINIER']
  },
  {
    title: 'Service Salle',
    path: '/server',
    icon: <RestaurantMenuIcon />,
    roles: ['ROLE_SERVEUR']
  },
  {
    title: 'Rapports',
    path: '/reports',
    icon: <AssessmentIcon />,
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Paramètres',
    path: '/settings',
    icon: <SettingsIcon />,
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Deconnexion',
    path: '/login',
    icon: <Logout />,
    roles: ['ROLE_MANAGER','ROLE_CUISINIER','ROLE_SERVEUR']
  },
];