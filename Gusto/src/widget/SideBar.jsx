import React, { useState } from 'react';
import { 
  Drawer, List, ListItem, ListItemButton, 
  ListItemIcon, ListItemText, Toolbar, Divider, 
  Typography, Box, IconButton, useMediaQuery, useTheme 
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { NAVIGATION } from '../pages/utils/NavigationConfig';

const drawerWidth = 260;

export default function SideBar({ userRole }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const theme = useTheme();
  
  // Détecte si l'écran est petit (mobile/tablette)
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  // Contenu de la sidebar (extrait pour être réutilisé)
  const drawerContent = (
    <Box sx={{ height: '100%', backgroundColor: '#121212', color: '#fff' }}>
      <Toolbar sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
        <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#4caf50' }}>
          GUS<span style={{ color: '#fff' }}>TO</span>
        </Typography>
      </Toolbar>
      
      <Box sx={{ px: 3, pb: 2, textAlign: 'center' }}>
        <Typography variant="body2" sx={{ color: '#4caf50', fontWeight: '500' }}>
          {userRole?.toLowerCase()}
        </Typography>
      </Box>
      
      <Divider sx={{ backgroundColor: '#2d2d2d', mx: 2 }} />
      
      <List sx={{ mt: 2, px: 1 }}>
        {NAVIGATION.filter(item => item.roles.includes(userRole)).map((item) => (
          <ListItem key={item.title} disablePadding sx={{ mb: 0.5 }}>
            <ListItemButton 
              href={item.path}
              onClick={() => isMobile && setMobileOpen(false)} // Ferme la sidebar au clic sur mobile
              sx={{
                borderRadius: '8px',
                '&:hover': { backgroundColor: 'rgba(76, 175, 80, 0.08)' },
              }}
            >
              <ListItemIcon sx={{ color: '#4caf50', minWidth: '40px' }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.title} primaryTypographyProps={{ fontSize: '0.9rem' }} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Box>
  );

  return (
    <>
      {/* BOUTON MENU (Visible uniquement sur Mobile) */}
      {isMobile && (
        <IconButton
          color="inherit"
          aria-label="open drawer"
          edge="start"
          onClick={handleDrawerToggle}
          sx={{ position: 'fixed', top: 10, left: 20, zIndex: 1201, bgcolor: '#121212', color: '#4caf50', '&:hover': {bgcolor: '#121212'} }}
        >
          <MenuIcon />
        </IconButton>
      )}

      <Box component="nav" sx={{ width: { md: drawerWidth }, flexShrink: { md: 0 } }}>
        {/* VERSION MOBILE (Drawer qui glisse) */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }} // Meilleure performance sur mobile
          sx={{
            display: { xs: 'block', md: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, borderRight: 'none' },
          }}
        >
          {drawerContent}
        </Drawer>

        {/* VERSION DESKTOP (Drawer fixe) */}
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', md: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, backgroundColor: '#121212', borderRight: '1px solid #2d2d2d' },
          }}
          open
        >
          {drawerContent}
        </Drawer>
      </Box>
    </>
  );
}