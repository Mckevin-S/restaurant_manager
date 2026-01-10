import React, { useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { 
  Drawer, List, ListItem, ListItemButton, 
  ListItemIcon, ListItemText, Toolbar, Divider, 
  Typography, Box, IconButton, useMediaQuery, useTheme 
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
// Nouvelles icônes
import PersonOutlineIcon from '@mui/icons-material/PersonOutline';
import LogoutIcon from '@mui/icons-material/Logout';
import { NAVIGATION } from '../pages/utils/NavigationConfig';

const drawerWidth = 260;

export default function SideBar({ userRole, onLogout }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const theme = useTheme();
  const location = useLocation();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const handleDrawerToggle = () => setMobileOpen(!mobileOpen);

  const drawerContent = (
    <Box sx={{ 
      height: '100%', 
      backgroundColor: '#121212', 
      color: '#fff', 
      display: 'flex', 
      flexDirection: 'column' 
    }}>
      {/* HEADER LOGO */}
      <Toolbar sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
        <Typography variant="h5" sx={{ fontWeight: 900, color: '#4caf50', letterSpacing: '1px' }}>
          GUS<span style={{ color: '#fff' }}>TO</span>
        </Typography>
      </Toolbar>

      {/* NAVIGATION PRINCIPALE (Prend l'espace disponible) */}
      <Box sx={{ flexGrow: 1, px: 2 }}>
        <List>
          {NAVIGATION.filter(item => item.roles.includes(userRole)).map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <ListItem key={item.title} disablePadding sx={{ mb: 1 }}>
                <ListItemButton 
                  component={NavLink} 
                  to={item.path}
                  onClick={() => isMobile && setMobileOpen(false)}
                  sx={{
                    borderRadius: '12px',
                    backgroundColor: isActive ? 'rgba(76, 175, 80, 0.15)' : 'transparent',
                    color: isActive ? '#4caf50' : 'rgba(255,255,255,0.7)',
                    '&:hover': { backgroundColor: 'rgba(255,255,255,0.05)' }
                  }}
                >
                  <ListItemIcon sx={{ color: isActive ? '#4caf50' : 'rgba(255,255,255,0.5)', minWidth: '40px' }}>
                    {item.icon}
                  </ListItemIcon>
                  <ListItemText primary={item.title} primaryTypographyProps={{ fontSize: '0.9rem', fontWeight: isActive ? 700 : 500 }} />
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      </Box>

      {/* BAS DE LA SIDEBAR : PROFIL PUIS DECONNEXION */}
      <Box sx={{ px: 2, pb: 4 }}>
        <Divider sx={{ backgroundColor: 'rgba(255,255,255,0.05)', mb: 2 }} />
        
        <List>
          {/* MON PROFIL (Remplace l'ancien login de déconnexion) */}
          <ListItem disablePadding sx={{ mb: 1 }}>
            <ListItemButton 
              component={NavLink} 
              to="/profile"
              onClick={() => isMobile && setMobileOpen(false)}
              sx={{
                borderRadius: '12px',
                color: location.pathname === '/profile' ? '#4caf50' : 'rgba(255,255,255,0.7)',
                '&:hover': { backgroundColor: 'rgba(255,255,255,0.05)' }
              }}
            >
              <ListItemIcon sx={{ color: location.pathname === '/profile' ? '#4caf50' : 'rgba(255,255,255,0.5)', minWidth: '40px' }}>
                <PersonOutlineIcon />
              </ListItemIcon>
              <ListItemText primary="Mon Profil" primaryTypographyProps={{ fontSize: '0.9rem' }} />
            </ListItemButton>
          </ListItem>

          {/* DÉCONNEXION (Tout en bas) */}
          <ListItem disablePadding>
            <ListItemButton 
              onClick={onLogout}
              sx={{
                borderRadius: '12px',
                color: '#ff5252',
                '&:hover': { backgroundColor: 'rgba(255, 82, 82, 0.1)' }
              }}
            >
              <ListItemIcon sx={{ color: '#ff5252', minWidth: '40px' }}>
                <LogoutIcon />
              </ListItemIcon>
              <ListItemText primary="Déconnexion" primaryTypographyProps={{ fontSize: '0.9rem', fontWeight: 600 }} />
            </ListItemButton>
          </ListItem>
        </List>
      </Box>
    </Box>
  );

  return (
    <Box component="nav" sx={{ width: { md: drawerWidth }, flexShrink: { md: 0 } }}>
      <Drawer
        variant={isMobile ? "temporary" : "permanent"}
        open={isMobile ? mobileOpen : true}
        onClose={handleDrawerToggle}
        ModalProps={{ keepMounted: true }}
        sx={{
          '& .MuiDrawer-paper': { 
            boxSizing: 'border-box', 
            width: drawerWidth, 
            backgroundColor: '#121212', 
            borderRight: '1px solid rgba(255,255,255,0.05)' 
          },
        }}
      >
        {drawerContent}
      </Drawer>
    </Box>
  );
}