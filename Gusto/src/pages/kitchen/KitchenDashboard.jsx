import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchKitchenOrders, updateKitchenStatus } from '../../features/KitchenDashboardSlice';
import { 
  Box, Typography, Paper, Grid, Button, Divider, 
  useTheme, useMediaQuery, Chip, Stack, CircularProgress 
} from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';

// Icons
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';

// --- SOUS-COMPOSANTS (Définis en premier pour éviter les ReferenceError) ---

const StatCard = ({ label, value, color }) => (
  <Paper sx={{ flex: 1, px: 3, py: 1.5, bgcolor: '#1e293b', borderRadius: 4, textAlign: 'center', minWidth: 100 }}>
    <Typography variant="caption" sx={{ color, fontWeight: 800, fontSize: '0.65rem' }}>{label}</Typography>
    <Typography variant="h5" sx={{ color: 'white', fontWeight: 900 }}>{value}</Typography>
  </Paper>
);

const SectionHeader = ({ label, count, color }) => (
  <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 3, ml: 1 }}>
    <Typography variant="overline" sx={{ color, fontWeight: 900, fontSize: '0.75rem', letterSpacing: 1 }}>
      {label}
    </Typography>
    <Chip label={count} size="small" sx={{ bgcolor: color, color: '#1e293b', fontWeight: 900, height: 20 }} />
  </Stack>
);

// --- COMPOSANT PRINCIPAL ---

const KitchenDashboard = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isTablet = useMediaQuery(theme.breakpoints.down('md'));
  
  const dispatch = useDispatch();
  // Sécurité sur le useSelector
  const { orders = [], loading = false, error = null } = useSelector((state) => state.kitchen || {});

  useEffect(() => {
    dispatch(fetchKitchenOrders());
    // Polling toutes les 7 secondes
    const interval = setInterval(() => dispatch(fetchKitchenOrders()), 30000);
    return () => clearInterval(interval);
  }, [dispatch]);

  const handleStatusChange = (orderId, currentStatus) => {
    let nextStatus = '';
    if (currentStatus === 'EN_ATTENTE') nextStatus = 'EN_PREPARATION';
    else if (currentStatus === 'EN_PREPARATION') nextStatus = 'PRETE';
    
    if (nextStatus) {
      dispatch(updateKitchenStatus({ id: orderId, nouveauStatut: nextStatus }));
    }
  };

  const getMinutesAgo = (timeString) => {
    const diff = Math.floor((Date.now() - new Date(timeString).getTime()) / 60000);
    return diff > 0 ? `${diff}M AGO` : 'JUST NOW';
  };

  const renderOrderCard = (order) => (
    <motion.div
      layout
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, scale: 0.9 }}
      key={order.id}
    >
      <Paper elevation={0} sx={{ 
        p: 2.5, mb: 2, borderRadius: 4, bgcolor: '#1e293b', 
        border: '1px solid', 
        borderColor: order.statut === 'EN_PREPARATION' ? '#f59e0b' : 'rgba(255,255,255,0.05)',
      }}>
        <Stack direction="row" justifyContent="space-between" alignItems="flex-start" sx={{ mb: 2 }}>
          <Box>
            <Typography variant="caption" sx={{ color: '#94a3b8', fontWeight: 700 }}>
              ID: #{order.id}
            </Typography>
            <Typography variant="h5" sx={{ color: 'white', fontWeight: 900 }}>
              Table {order.table?.numero || '?'}
            </Typography>
          </Box>
          <Chip 
            icon={<AccessTimeIcon sx={{ fontSize: '14px !important', color: '#6366f1 !important' }} />}
            label={getMinutesAgo(order.dateHeureCommande)} 
            sx={{ bgcolor: 'rgba(99, 102, 241, 0.1)', color: '#6366f1', fontWeight: 800 }}
          />
        </Stack>

        <Divider sx={{ borderColor: 'rgba(255,255,255,0.05)', mb: 2 }} />

        <Box sx={{ minHeight: 60, mb: 3 }}>
          {order.lignesCommande?.map((ligne, idx) => (
            <Stack key={idx} direction="row" spacing={2} alignItems="center" sx={{ mb: 1 }}>
              <Box sx={{ width: 24, height: 24, borderRadius: 1, bgcolor: '#334155', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Typography variant="caption" sx={{ color: 'white', fontWeight: 700 }}>{ligne.quantite}</Typography>
              </Box>
              <Typography sx={{ color: '#cbd5e1', fontWeight: 600 }}>{ligne.platNom}</Typography>
            </Stack>
          ))}
        </Box>

        <Button 
          fullWidth variant="contained" 
          startIcon={order.statut === 'EN_ATTENTE' ? <LocalFireDepartmentIcon /> : <CheckCircleOutlineIcon />}
          onClick={() => handleStatusChange(order.id, order.statut)}
          sx={{ 
            bgcolor: order.statut === 'EN_ATTENTE' ? '#f59e0b' : '#10b981', 
            borderRadius: 3, fontWeight: 800, py: 1.5,
            '&:hover': { bgcolor: order.statut === 'EN_ATTENTE' ? '#d97706' : '#059669' }
          }}
        >
          {order.statut === 'EN_ATTENTE' ? 'Commencer' : 'Terminer'}
        </Button>
      </Paper>
    </motion.div>
  );

  if (loading && orders.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', bgcolor: '#0f172a' }}>
        <CircularProgress color="success" />
      </Box>
    );
  }

  return (
    <Box sx={{ p: { xs: 2, md: 4 }, bgcolor: '#0f172a', minHeight: '100vh' }}>
      {/* Header */}
      <Stack direction={isMobile ? "column" : "row"} justifyContent="space-between" spacing={3} sx={{ mb: 6 }}>
        <Box>
          <Typography variant={isMobile ? "h4" : "h3"} sx={{ color: 'white', fontWeight: 900 }}>
            Kitchen <span style={{ color: '#4caf50' }}>Ops</span>
          </Typography>
          <Typography variant="body2" sx={{ color: '#94a3b8' }}>Suivi des préparations en temps réel</Typography>
        </Box>
        
        <Stack direction="row" spacing={2}>
          <StatCard label="EN ATTENTE" value={orders.filter(o => o.statut === 'EN_ATTENTE').length} color="#94a3b8" />
          <StatCard label="EN CUISSON" value={orders.filter(o => o.statut === 'EN_PREPARATION').length} color="#f59e0b" />
        </Stack>
      </Stack>

      <Grid container spacing={4}>
        <Grid item xs={12} sm={6} md={4}>
          <SectionHeader label="QUEUE" count={orders.filter(o => o.statut === 'EN_ATTENTE').length} color="#94a3b8" />
          <AnimatePresence>{orders.filter(o => o.statut === 'EN_ATTENTE').map(renderOrderCard)}</AnimatePresence>
        </Grid>
        
        <Grid item xs={12} sm={6} md={4}>
          <SectionHeader label="PRÉPARATION" count={orders.filter(o => o.statut === 'EN_PREPARATION').length} color="#f59e0b" />
          <AnimatePresence>{orders.filter(o => o.statut === 'EN_PREPARATION').map(renderOrderCard)}</AnimatePresence>
        </Grid>
        
        <Grid item xs={12} md={4}>
          <SectionHeader label="TERMINÉ" count={orders.filter(o => o.statut === 'PRETE').length} color="#10b981" />
          <AnimatePresence>
            {orders.filter(o => o.statut === 'PRETE').map(order => (
              <motion.div key={order.id} initial={{ opacity: 0 }} animate={{ opacity: 0.6 }}>
                <Paper sx={{ p: 2, mb: 2, bgcolor: 'rgba(16, 185, 129, 0.05)', border: '1px dashed #10b981' }}>
                  <Typography variant="subtitle2" sx={{ color: '#10b981', fontWeight: 800 }}>
                    #{order.id} • Table {order.table?.numero}
                  </Typography>
                </Paper>
              </motion.div>
            ))}
          </AnimatePresence>
        </Grid>
      </Grid>
    </Box>
  );
};

export default KitchenDashboard;