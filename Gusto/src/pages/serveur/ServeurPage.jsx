import React from 'react';
import { Box, useTheme, useMediaQuery, CssBaseline } from '@mui/material';
import SideBar from '../../widget/SideBar';
import ServeurDashboard from './ServerDashboard';


const drawerWidth = 260;

const ServeurPage = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  // Rôle défini sur SERVEUR pour filtrer la navigation
  const userRole = localStorage.getItem('role');

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: '#f8fafc' }}>
      <CssBaseline />

      {/* Barre latérale de navigation */}
      <SideBar userRole={userRole} />

      {/* Contenu principal : Service Dashboard */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: { xs: 2, md: 3 },
          width: { md: `calc(100% - ${drawerWidth}px)` },
          //   ml: { md: `${drawerWidth}px` }, 
          mt: isMobile ? 8 : 0,
          transition: 'all 0.3s ease-in-out',
        }}
      >
        <ServeurDashboard />
      </Box>
    </Box>
  );
};

export default ServeurPage;