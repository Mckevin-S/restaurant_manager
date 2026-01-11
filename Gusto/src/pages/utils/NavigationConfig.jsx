import DashboardIcon from '@mui/icons-material/Dashboard';
import PeopleIcon from '@mui/icons-material/People';
import SettingsIcon from '@mui/icons-material/Settings';
import AssessmentIcon from '@mui/icons-material/Assessment';
import KitchenIcon from '@mui/icons-material/Kitchen';
import RestaurantMenuIcon from '@mui/icons-material/RestaurantMenu';
import TableBarIcon from '@mui/icons-material/TableBar';
import InventoryIcon from '@mui/icons-material/Inventory';
import RestaurantIcon from '@mui/icons-material/Restaurant';

export const NAVIGATION = [
  {
    title: 'Tableau de bord',
    path: '/dashboard',
    icon: <DashboardIcon />,
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Gestion Tables',
    path: '/manager/tables',
    icon: <TableBarIcon />,
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Gestion Menu',
    path: '/manager/menu',
    icon: <RestaurantIcon />,
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Gestion Stock',
    path: '/manager/stock',
    icon: <InventoryIcon />,
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Utilisateurs',
    path: '/users',
    icon: <PeopleIcon />,
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Rapports',
    path: '/manager/reports',
    icon: <AssessmentIcon />,
    roles: ['ROLE_MANAGER']
  },
  {
    title: 'Paramètres',
    path: '/manager/settings',
    icon: <SettingsIcon />,
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
  }
];