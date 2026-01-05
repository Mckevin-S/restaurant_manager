import React from 'react';
import { Box, useTheme, useMediaQuery, CssBaseline } from '@mui/material';
import SideBar from '../../widget/SideBar'; 
import KitchenDashboard from './KitchenDashboard';

const drawerWidth = 260;

const KitchenPage = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  
  // Rôle utilisateur extrait de votre profil Moko Yvan
  const userRole = localStorage.getItem('role')

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: '#0f172a' }}>
      <CssBaseline />
      
      {/* Sidebar de navigation */}
      <SideBar userRole={userRole} />

      {/* Zone de contenu principale */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: { xs: 2, md: 4 },
          width: { md: `calc(100% - ${drawerWidth}px)` },
          // On évite que le contenu touche le bord de la sidebar
          mt: isMobile ? 8 : 0, 
          transition: 'all 0.3s ease-in-out',
        }}
      >
        <KitchenDashboard />
      </Box>
    </Box>
  );
};

export default KitchenPage;