import React, { useState } from 'react';
import { 
  Box, Typography, Grid, Paper, Button, IconButton, 
  Tabs, Tab, Badge, Divider, InputBase, Avatar
} from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';

// --- DONNÉES SIMULÉES (Inspirées de vos fichiers) ---
const CATEGORIES = ['Starters', 'Mains', 'Desserts', 'Drinks'];
const CURRENT_USER = {
  name: "Moko Yvan", // 
  role: "DÉVELOPPEUR FULL-STACK", // 
  location: "Bonaberi-Douala" // [cite: 8]
};

const MENU_ITEMS = [
  { id: 1, name: 'Artisan Coffee', category: 'Drinks', price: 5, image: 'https://images.unsplash.com/photo-1509042239860-f550ce710b93' },
  { id: 2, name: 'Signature Cocktail', category: 'Drinks', price: 14, image: 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b' },
  { id: 3, name: 'Wagyu Burger', category: 'Mains', price: 24, image: 'https://images.unsplash.com/photo-1586190848861-99aa4a171e90' },
  { id: 4, name: 'Lobster Linguine', category: 'Mains', price: 32, image: 'https://images.unsplash.com/photo-1551183053-bf91a1d81141' },
];

const PointOfSale = () => {
  const [activeCategory, setActiveCategory] = useState(3); // 'Drinks' par défaut
  const [selectedTable, setSelectedTable] = useState(1);
  const [cart, setCart] = useState([
    { id: 3, name: 'Wagyu Burger', price: 24, quantity: 1, status: 'New' },
    { id: 4, name: 'Lobster Linguine', price: 32, quantity: 1, status: 'New' },
    { id: 1, name: 'Artisan Coffee', price: 5, quantity: 1, status: 'New' },
  ]);

  const subtotal = cart.reduce((acc, item) => acc + (item.price * item.quantity), 0);

  const updateQuantity = (id, delta) => {
    setCart(prev => prev.map(item => 
      item.id === id ? { ...item, quantity: Math.max(1, item.quantity + delta) } : item
    ));
  };

  const removeItem = (id) => {
    setCart(prev => prev.filter(item => item.id !== id));
  };

  return (
    <Box sx={{ display: 'flex', height: '100vh', bgcolor: '#f8fafc', overflow: 'hidden' }}>
      
      {/* SIDEBAR GAUCHE : TABLES */}
      <Paper elevation={0} sx={{ width: 140, borderRight: '1px solid #e2e8f0', p: 2, display: 'flex', flexDirection: 'column' }}>
        <Typography variant="overline" sx={{ fontWeight: 800, color: '#94a3b8', mb: 2 }}>TABLES</Typography>
        <Grid container spacing={1.5}>
          {[1, 2, 3, 4, 5, 6, 'A', 'B'].map((table) => (
            <Grid item xs={12} key={table}>
              <Button
                fullWidth
                onClick={() => setSelectedTable(table)}
                sx={{
                  height: 80,
                  flexDirection: 'column',
                  borderRadius: 3,
                  border: '2px solid',
                  borderColor: selectedTable === table ? '#10b981' : '#f1f5f9',
                  bgcolor: selectedTable === table ? '#ecfdf5' : 'white',
                  color: selectedTable === table ? '#065f46' : '#64748b',
                  '&:hover': { bgcolor: '#f8fafc' }
                }}
              >
                <Typography variant="h6" sx={{ fontWeight: 800 }}>{table}</Typography>
                <Typography variant="caption" sx={{ fontSize: 10 }}>👤 {table === 'A' || table === 'B' ? 4 : 2}</Typography>
              </Button>
            </Grid>
          ))}
        </Grid>
      </Paper>

      {/* SECTION CENTRALE : MENU */}
      <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column', p: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
          <Box>
            <Typography variant="h5" sx={{ fontWeight: 800, color: '#1e293b' }}>Point of Sale</Typography>
            <Typography variant="body2" sx={{ color: '#94a3b8' }}>Select a table to start ordering</Typography>
          </Box>
          <Paper sx={{ p: '2px 12px', display: 'flex', alignItems: 'center', width: 300, borderRadius: 2, bgcolor: '#f1f5f9' }} elevation={0}>
            <SearchIcon sx={{ color: '#94a3b8', mr: 1 }} />
            <InputBase placeholder="Search menu..." sx={{ flex: 1 }} />
          </Paper>
        </Box>

        <Tabs 
          value={activeCategory} 
          onChange={(e, v) => setActiveCategory(v)}
          sx={{ mb: 4, '& .MuiTabs-indicator': { display: 'none' } }}
        >
          {CATEGORIES.map((cat, i) => (
            <Tab 
              key={cat} 
              label={cat} 
              sx={{ 
                textTransform: 'none', fontWeight: 600, borderRadius: 5, mr: 2, minWidth: 100,
                bgcolor: activeCategory === i ? '#1e293b' : 'white',
                color: activeCategory === i ? 'white !important' : '#64748b',
                border: '1px solid #e2e8f0'
              }} 
            />
          ))}
        </Tabs>

        <Grid container spacing={3}>
          <AnimatePresence mode='popLayout'>
            {MENU_ITEMS.filter(item => item.category === CATEGORIES[activeCategory]).map((item) => (
              <Grid item xs={12} sm={6} md={4} key={item.id}>
                <motion.div layout initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.9 }}>
                  <Paper sx={{ borderRadius: 4, overflow: 'hidden', border: '1px solid #f1f5f9' }}>
                    <Box sx={{ height: 160, backgroundImage: `url(${item.image})`, backgroundSize: 'cover', backgroundPosition: 'center' }} />
                    <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                      <Box>
                        <Typography variant="subtitle1" sx={{ fontWeight: 700 }}>{item.name}</Typography>
                        <Typography variant="caption" sx={{ color: '#94a3b8' }}>{item.category}</Typography>
                        <Typography variant="h6" sx={{ color: '#10b981', fontWeight: 800, mt: 1 }}>${item.price.toFixed(2)}</Typography>
                      </Box>
                      <IconButton sx={{ bgcolor: '#f1f5f9', color: '#1e293b' }}>
                        <AddIcon fontSize="small" />
                      </IconButton>
                    </Box>
                  </Paper>
                </motion.div>
              </Grid>
            ))}
          </AnimatePresence>
        </Grid>
      </Box>

      {/* PANEL DROIT : PANIER */}
      <Paper elevation={0} sx={{ width: 400, borderLeft: '1px solid #e2e8f0', display: 'flex', flexDirection: 'column', bgcolor: 'white' }}>
        <Box sx={{ p: 3, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Typography variant="h6" sx={{ fontWeight: 800 }}>Order for Table {selectedTable}</Typography>
          <Avatar src={CURRENT_USER.avatar} sx={{ width: 32, height: 32 }} />
        </Box>

        <Box sx={{ flex: 1, overflowY: 'auto', p: 2 }}>
          <AnimatePresence>
            {cart.map((item) => (
              <motion.div key={item.id} initial={{ x: 20, opacity: 0 }} animate={{ x: 0, opacity: 1 }} exit={{ x: -20, opacity: 0 }} transition={{ type: 'spring', damping: 20 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2, p: 1.5, borderRadius: 3, border: '1px solid #f1f5f9' }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', bgcolor: '#f8fafc', borderRadius: 2, mr: 2 }}>
                    <IconButton size="small" onClick={() => updateQuantity(item.id, -1)}><RemoveIcon fontSize="inherit" /></IconButton>
                    <Typography sx={{ mx: 1, fontWeight: 700 }}>{item.quantity}</Typography>
                    <IconButton size="small" onClick={() => updateQuantity(item.id, 1)}><AddIcon fontSize="inherit" /></IconButton>
                  </Box>
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>{item.name}</Typography>
                    <Typography variant="caption" sx={{ color: '#10b981', fontWeight: 600 }}>{item.status}</Typography>
                  </Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 800, mx: 2 }}>${(item.price * item.quantity).toFixed(2)}</Typography>
                  <IconButton size="small" color="error" onClick={() => removeItem(item.id)}><DeleteOutlineIcon fontSize="small" /></IconButton>
                </Box>
              </motion.div>
            ))}
          </AnimatePresence>
        </Box>

        <Box sx={{ p: 3, bgcolor: '#f8fafc', borderTop: '1px solid #e2e8f0' }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
            <Typography variant="body2" sx={{ color: '#94a3b8' }}>Subtotal</Typography>
            <Typography variant="body2" sx={{ fontWeight: 700 }}>${subtotal.toFixed(2)}</Typography>
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
            <Typography variant="h6" sx={{ fontWeight: 800 }}>Total</Typography>
            <Typography variant="h5" sx={{ fontWeight: 900 }}>${subtotal.toFixed(2)}</Typography>
          </Box>
          <Button 
            fullWidth 
            variant="contained" 
            endIcon={<ChevronRightIcon />}
            sx={{ 
              py: 2, borderRadius: 3, bgcolor: '#10b981', fontWeight: 700, textTransform: 'none', fontSize: '1rem',
              '&:hover': { bgcolor: '#059669' }
            }}
          >
            Send to Kitchen
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default PointOfSale;