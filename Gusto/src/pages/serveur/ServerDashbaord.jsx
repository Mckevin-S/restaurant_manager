import React, { useState } from 'react';
import { Box, Typography, Paper, Grid, Button, Divider, Chip, Badge } from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import PaymentsIcon from '@mui/icons-material/Payments';
import RestaurantIcon from '@mui/icons-material/Restaurant';

const INITIAL_TABLES = [
  { id: 1, name: 'Table 1', time: '07:15 PM', item: 'Signature Cocktail', total: '14.00', status: 'READY TO SERVE' },
  { id: 2, name: 'Table 2', time: '04:21 PM', item: 'Wagyu Burger', total: '24.00', status: 'SERVED' },
  { id: 4, name: 'Table 4', time: '07:16 PM', item: 'Tiramisu', total: '10.00', status: 'PENDING' }
];

const ServeurDashboard = () => {
  const [tables, setTables] = useState(INITIAL_TABLES);

  const getStatusColor = (status) => {
    switch (status) {
      case 'READY TO SERVE': return { border: '#10b981', bg: '#ecfdf5', text: '#10b981' };
      case 'SERVED': return { border: '#3b82f6', bg: '#eff6ff', text: '#3b82f6' };
      default: return { border: '#e2e8f0', bg: '#f8fafc', text: '#64748b' };
    }
  };

  const renderTableCard = (table) => {
    const colors = getStatusColor(table.status);
    
    return (
      <Grid item xs={12} sm={6} lg={4} key={table.id}>
        <motion.div layout initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <Paper elevation={0} sx={{ 
            p: 2.5, borderRadius: 4, bgcolor: 'white', 
            border: '2px solid', borderColor: colors.border,
            boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)'
          }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <Box sx={{ 
                  width: 40, height: 40, borderRadius: 2, bgcolor: '#f1f5f9', 
                  display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 800
                }}>
                  {table.id}
                </Box>
                <Box>
                  <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>{table.name}</Typography>
                  <Typography variant="caption" sx={{ color: '#64748b' }}>🕒 {table.time}</Typography>
                </Box>
              </Box>
              <Chip 
                label={table.status} 
                size="small" 
                sx={{ bgcolor: colors.bg, color: colors.text, fontWeight: 800, fontSize: '0.65rem' }} 
              />
            </Box>

            <Box sx={{ minHeight: 80, mb: 2 }}>
              <Typography variant="body2" sx={{ fontWeight: 600, color: '#1e293b', mb: 1 }}>
                1x {table.item}
              </Typography>
              <Divider sx={{ my: 1.5, borderStyle: 'dashed' }} />
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography variant="body2" sx={{ color: '#64748b' }}>Total</Typography>
                <Typography variant="body2" sx={{ fontWeight: 800 }}>${table.total}</Typography>
              </Box>
            </Box>

            {table.status === 'READY TO SERVE' && (
              <Button 
                fullWidth variant="contained" disableElevation
                startIcon={<CheckCircleIcon />}
                sx={{ bgcolor: '#10b981', '&:hover': { bgcolor: '#059669' }, borderRadius: 3, textTransform: 'none', fontWeight: 700 }}
              >
                Mark as Served
              </Button>
            )}

            {table.status === 'SERVED' && (
              <Button 
                fullWidth variant="contained" disableElevation
                startIcon={<PaymentsIcon />}
                sx={{ bgcolor: '#3b82f6', '&:hover': { bgcolor: '#2563eb' }, borderRadius: 3, textTransform: 'none', fontWeight: 700 }}
              >
                Process Payment
              </Button>
            )}

            {table.status === 'PENDING' && (
              <Button 
                fullWidth variant="outlined" disabled
                startIcon={<RestaurantIcon />}
                sx={{ borderRadius: 3, textTransform: 'none', fontWeight: 700, borderStyle: 'dashed' }}
              >
                Kitchen Preparing...
              </Button>
            )}
          </Paper>
        </motion.div>
      </Grid>
    );
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 900, color: '#1e293b' }}>Service Dashboard</Typography>
          <Typography variant="body2" sx={{ color: '#64748b' }}>Monitor tables and serve food</Typography>
        </Box>
        
        <Badge badgeContent={1} color="error" overlap="circular">
          <Button 
            variant="contained" 
            startIcon={<NotificationsActiveIcon />}
            sx={{ bgcolor: '#ecfdf5', color: '#10b981', '&:hover': { bgcolor: '#d1fae5' }, borderRadius: 2, textTransform: 'none', fontWeight: 700, boxShadow: 'none' }}
          >
            1 Order Ready
          </Button>
        </Badge>
      </Box>

      <Grid container spacing={3}>
        {tables.map(renderTableCard)}
      </Grid>
    </Box>
  );
};

export default ServeurDashboard;