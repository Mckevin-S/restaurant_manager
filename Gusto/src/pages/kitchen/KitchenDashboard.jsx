import React, { useState } from 'react';
import { Box, Typography, Paper, Grid, Button, Divider, useTheme, useMediaQuery } from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';

const INITIAL_ORDERS = [
  { id: '#2848', table: 'Table 1', items: ['Lava Cake'], time: '2M AGO', status: 'pending' },
  { id: '#6581', table: 'Table 4', items: ['Tiramisu'], time: '1M AGO', status: 'cooking' }
];

const KitchenDashboard = () => {
  const [orders, setOrders] = useState(INITIAL_ORDERS);
  const theme = useTheme();
  
  // Hooks pour détecter la taille de l'écran
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isTablet = useMediaQuery(theme.breakpoints.down('md'));

  const updateStatus = (orderId, newStatus) => {
    setOrders(prev => prev.map(order => 
      order.id === orderId ? { ...order, status: newStatus } : order
    ));
  };

  const renderOrderCard = (order) => (
    <motion.div
      layout
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.8 }}
      key={order.id}
    >
      <Paper elevation={0} sx={{ 
        p: 2.5, mb: 2, borderRadius: 4, bgcolor: '#1e293b', 
        border: '1px solid', borderColor: order.status === 'cooking' ? '#f59e0b' : 'rgba(255,255,255,0.05)',
        boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)'
      }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
          <Box>
            <Typography variant="caption" sx={{ color: '#94a3b8', fontWeight: 700 }}>COMMANDE {order.id}</Typography>
            <Typography variant="h5" sx={{ color: 'white', fontWeight: 800 }}>{order.table}</Typography>
          </Box>
          <Box sx={{ textAlign: 'right' }}>
            <Typography variant="caption" sx={{ color: '#6366f1', fontWeight: 800, bgcolor: 'rgba(99, 102, 241, 0.1)', px: 1, borderRadius: 1 }}>
              {order.time}
            </Typography>
          </Box>
        </Box>

        <Divider sx={{ borderColor: 'rgba(255,255,255,0.05)', mb: 2 }} />

        <Box sx={{ minHeight: 60, mb: 3 }}>
          {order.items.map((item, idx) => (
            <Box key={idx} sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 1 }}>
              <Box sx={{ width: 22, height: 22, borderRadius: 1, bgcolor: '#334155', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Typography variant="caption" sx={{ color: 'white', fontWeight: 700 }}>1</Typography>
              </Box>
              <Typography sx={{ color: '#cbd5e1', fontWeight: 500 }}>{item}</Typography>
            </Box>
          ))}
        </Box>

        <Button 
          fullWidth variant="contained" 
          startIcon={order.status === 'pending' ? <LocalFireDepartmentIcon /> : <CheckCircleOutlineIcon />}
          onClick={() => updateStatus(order.id, order.status === 'pending' ? 'cooking' : 'ready')}
          sx={{ 
            bgcolor: order.status === 'pending' ? '#f59e0b' : '#10b981', 
            '&:hover': { bgcolor: order.status === 'pending' ? '#d97706' : '#059669' },
            borderRadius: 3, textTransform: 'none', fontWeight: 700, py: 1.2
          }}
        >
          {order.status === 'pending' ? 'Commencer' : 'Terminer'}
        </Button>
      </Paper>
    </motion.div>
  );

  return (
    <Box sx={{ p: { xs: 1, sm: 2 } }}>
      {/* Header Responsive : Colonne sur mobile, Ligne sur Desktop */}
      <Box sx={{ 
        display: 'flex', 
        flexDirection: isMobile ? 'column' : 'row',
        justifyContent: 'space-between', 
        alignItems: isMobile ? 'flex-start' : 'center', 
        gap: 2,
        mb: 6 
      }}>
        <Box>
          <Typography variant={isMobile ? "h5" : "h4"} sx={{ color: 'white', fontWeight: 900, letterSpacing: -1 }}>
            Kitchen System
          </Typography>
          <Typography variant="body2" sx={{ color: '#4caf50', fontWeight: 600 }}>
            Cuisinier : Moko Yvan (En ligne)
          </Typography>
        </Box>
        
        <Box sx={{ display: 'flex', gap: 2, width: isMobile ? '100%' : 'auto' }}>
            <Paper sx={{ flex: 1, px: 2, py: 1, bgcolor: '#1e293b', border: '1px solid rgba(255,255,255,0.05)', borderRadius: 3, textAlign: 'center' }}>
                <Typography variant="caption" sx={{ color: '#94a3b8', fontWeight: 700 }}>ATTENTE</Typography>
                <Typography variant="h6" sx={{ color: 'white', fontWeight: 800 }}>{orders.filter(o => o.status === 'pending').length}</Typography>
            </Paper>
            <Paper sx={{ flex: 1, px: 2, py: 1, bgcolor: '#1e293b', border: '1px solid rgba(255,255,255,0.05)', borderRadius: 3, textAlign: 'center' }}>
                <Typography variant="caption" sx={{ color: '#f59e0b', fontWeight: 700 }}>CUISSON</Typography>
                <Typography variant="h6" sx={{ color: 'white', fontWeight: 800 }}>{orders.filter(o => o.status === 'cooking').length}</Typography>
            </Paper>
        </Box>
      </Box>

      {/* Grille Responsive : 1 colonne (mobile), 2 colonnes (tablette), 3 colonnes (desktop) */}
      <Grid container spacing={3}>
        <Grid item xs={12} sm={6} md={4}>
          <Typography variant="overline" sx={{ color: '#94a3b8', fontWeight: 800, mb: 2, display: 'block', ml: 1 }}>QUEUE</Typography>
          <AnimatePresence>{orders.filter(o => o.status === 'pending').map(renderOrderCard)}</AnimatePresence>
        </Grid>
        
        <Grid item xs={12} sm={6} md={4}>
          <Typography variant="overline" sx={{ color: '#f59e0b', fontWeight: 800, mb: 2, display: 'block', ml: 1 }}>PRÉPARATION</Typography>
          <AnimatePresence>{orders.filter(o => o.status === 'cooking').map(renderOrderCard)}</AnimatePresence>
        </Grid>
        
        <Grid item xs={12} md={4}>
          <Typography variant="overline" sx={{ color: '#10b981', fontWeight: 800, mb: 2, display: 'block', ml: 1 }}>TERMINÉ</Typography>
          <Box sx={{ display: 'flex', flexDirection: isTablet && !isMobile ? 'row' : 'column', flexWrap: 'wrap', gap: 2 }}>
            <AnimatePresence>
                {orders.filter(o => o.status === 'ready').map(order => (
                <motion.div 
                    key={order.id} 
                    initial={{ opacity: 0 }} 
                    animate={{ opacity: 0.4 }}
                    style={{ width: isTablet && !isMobile ? '48%' : '100%' }}
                >
                    <Paper sx={{ p: 2, bgcolor: 'transparent', border: '1px dashed #334155', borderRadius: 3 }}>
                    <Typography variant="subtitle2" sx={{ color: '#10b981', fontWeight: 700 }}>{order.id} - {order.table}</Typography>
                    </Paper>
                </motion.div>
                ))}
            </AnimatePresence>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
};

export default KitchenDashboard;