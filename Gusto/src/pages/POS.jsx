import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { 
  Box, Typography, Grid, Paper, Button, IconButton, 
  Tabs, Tab, InputBase, Avatar, Stack, Skeleton, Alert, Snackbar, Badge
} from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';

// Icons
import RestaurantMenuIcon from '@mui/icons-material/RestaurantMenu';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';

import { getPlats, getCategories, getTables, createCommande } from '../services/api';

const CURRENCY = 'FCFA';

const PointOfSale = () => {
  const [categories, setCategories] = useState([]);
  const [plats, setPlats] = useState([]);
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeCategoryIndex, setActiveCategoryIndex] = useState(0);
  const [selectedTable, setSelectedTable] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [cart, setCart] = useState([]);
  const [notification, setNotification] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    const loadInitialData = async () => {
      try {
        const [catRes, platRes, tableRes] = await Promise.all([
          getCategories(),
          getPlats(),
          getTables()
        ]);
        setCategories(catRes.data || []);
        setPlats(platRes.data || []);
        setTables(tableRes.data || []);
        if (tableRes.data?.length > 0) setSelectedTable(tableRes.data[0]);
      } catch (error) {
        setNotification({ open: true, message: 'Erreur réseau', severity: 'error' });
      } finally {
        setLoading(false);
      }
    };
    loadInitialData();
  }, []);

  const filteredMenu = useMemo(() => {
    if (!Array.isArray(plats) || plats.length === 0) return [];
    const currentCategory = categories[activeCategoryIndex];
    
    return plats.filter(item => {
      const itemCatId = (item.category && typeof item.category === 'object') ? item.category.id : item.category;
      const matchesCategory = !currentCategory || itemCatId == currentCategory.id;
      const matchesSearch = item.nom?.toLowerCase().includes(searchQuery.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }, [activeCategoryIndex, searchQuery, plats, categories]);

  const addToCart = useCallback((product) => {
    if (!product.disponibilite) return;
    setCart(prev => {
      const existing = prev.find(item => item.id === product.id);
      if (existing) return prev.map(item => item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item);
      return [...prev, { ...product, quantity: 1 }];
    });
  }, []);

  const updateQuantity = (id, delta) => {
    setCart(prev => prev.map(item => 
      item.id === id ? { ...item, quantity: Math.max(1, item.quantity + delta) } : item
    ).filter(item => item.quantity > 0));
  };

  const subtotal = useMemo(() => cart.reduce((acc, item) => acc + (item.prix * item.quantity), 0), [cart]);

  const handleOrderSubmit = async () => {
    if (!selectedTable) return setNotification({ open: true, message: 'Sélectionnez une table', severity: 'warning' });
    try {
      const orderPayload = {
        table: selectedTable,
        dateHeureCommande: new Date().toISOString(),
        statut: 'EN_ATTENTE',
        lignesCommande: cart.map(item => ({ platId: item.id, quantite: item.quantity, prixUnitaire: item.prix }))
      };
      await createCommande(orderPayload);
      setNotification({ open: true, message: 'Commande envoyée !', severity: 'success' });
      setCart([]);
    } catch (e) { setNotification({ open: true, message: 'Erreur envoi', severity: 'error' }); }
  };

  return (
    <Box sx={{ display: 'flex', height: '100vh', bgcolor: '#f4f7f9', overflow: 'hidden', fontFamily: "'Inter', sans-serif" }}>
      
      {/* SIDEBAR TABLES - Ultra compacte */}
      <Box sx={{ width: 80, bgcolor: '#fff', borderRight: '1px solid #eef2f6', display: 'flex', flexDirection: 'column', alignItems: 'center', py: 3, gap: 2 }}>
        <Avatar sx={{ bgcolor: '#10b981', mb: 2 }}><RestaurantMenuIcon /></Avatar>
        {loading ? [1,2,3].map(i => <Skeleton key={i} variant="rounded" width={50} height={50} />) : 
          tables.map((t) => (
            <IconButton 
              key={t.id} 
              onClick={() => setSelectedTable(t)}
              sx={{ 
                width: 55, height: 55, borderRadius: 2, 
                bgcolor: selectedTable?.id === t.id ? '#1e293b' : '#f8fafc',
                color: selectedTable?.id === t.id ? '#fff' : '#64748b',
                transition: '0.3s', '&:hover': { bgcolor: '#1e293b', color: '#fff' }
              }}
            >
              <Typography variant="button" sx={{ fontWeight: 900 }}>{t.numero}</Typography>
            </IconButton>
          ))
        }
      </Box>

      {/* MAIN CONTENT */}
      <Box sx={{ flex: 1, p: 4, overflowY: 'auto' }}>
        <Stack direction="row" justifyContent="space-between" alignItems="flex-end" sx={{ mb: 5 }}>
          <Box>
            <Typography variant="h3" sx={{ fontWeight: 900, letterSpacing: '-1px', color: '#1e293b' }}>
              Gusto<span style={{ color: '#10b981' }}>.</span>
            </Typography>
            <Typography variant="body1" sx={{ color: '#94a3b8', fontWeight: 500 }}>
              Table {selectedTable?.numero || '...'} • Cuisine Ouverte
            </Typography>
          </Box>
          <Paper elevation={0} sx={{ p: '12px 20px', display: 'flex', alignItems: 'center', width: 350, borderRadius: 4, bgcolor: '#fff', border: '1px solid #eef2f6' }}>
            <SearchIcon sx={{ color: '#cbd5e1', mr: 1 }} />
            <InputBase placeholder="Rechercher un plat..." fullWidth value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} />
          </Paper>
        </Stack>

        {/* CATEGORIES */}
        <Tabs 
          value={activeCategoryIndex} 
          onChange={(e, v) => setActiveCategoryIndex(v)}
          variant="scrollable"
          scrollButtons={false}
          sx={{ mb: 4, '& .MuiTabs-indicator': { display: 'none' } }}
        >
          {loading ? [1,2,3,4].map(i => <Skeleton key={i} variant="text" width={100} height={40} sx={{ mr: 2 }} />) :
            categories.map((cat, i) => (
              <Tab 
                key={cat.id} 
                label={cat.nom} 
                sx={{ 
                  textTransform: 'none', fontWeight: 700, borderRadius: '12px', mr: 2, minHeight: 45,
                  bgcolor: activeCategoryIndex === i ? '#10b981' : '#fff',
                  color: activeCategoryIndex === i ? '#fff !important' : '#64748b',
                  boxShadow: activeCategoryIndex === i ? '0 10px 20px -5px rgba(16,185,129,0.4)' : 'none',
                  border: '1px solid #eef2f6'
                }} 
              />
            ))
          }
        </Tabs>

        {/* GRID PLATS */}
        <Grid container spacing={3}>
          {loading ? [1,2,3,4,5,6].map(i => (
            <Grid item xs={12} sm={6} md={4} key={i}><Skeleton variant="rectangular" height={250} sx={{ borderRadius: 4 }} /></Grid>
          )) : (
            <AnimatePresence mode='popLayout'>
              {filteredMenu.map((item) => (
                <Grid item xs={12} sm={6} md={4} key={item.id}>
                  <motion.div layout initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.9 }}>
                    <Paper 
                      onClick={() => addToCart(item)}
                      sx={{ 
                        borderRadius: 5, overflow: 'hidden', cursor: 'pointer', border: '1px solid #eef2f6', elevation: 0,
                        transition: '0.3s', '&:hover': { transform: 'translateY(-8px)', boxShadow: '0 20px 30px -10px rgba(0,0,0,0.05)' }
                      }}
                    >
                      <Box sx={{ height: 160, position: 'relative', backgroundImage: `url(${item.photoUrl})`, backgroundSize: 'cover', backgroundPosition: 'center' }}>
                        {item.populaire && (
                          <Box sx={{ position: 'absolute', top: 15, left: 15, bgcolor: '#ff4757', color: '#fff', px: 1.5, py: 0.5, borderRadius: 2, display: 'flex', alignItems: 'center', gap: 0.5 }}>
                            <LocalFireDepartmentIcon fontSize="small" /> <Typography variant="caption" sx={{ fontWeight: 800 }}>POPULAIRE</Typography>
                          </Box>
                        )}
                      </Box>
                      <Box sx={{ p: 2.5 }}>
                        <Typography variant="h6" sx={{ fontWeight: 800, mb: 0.5 }}>{item.nom}</Typography>
                        <Typography variant="body2" sx={{ color: '#94a3b8', height: 40, overflow: 'hidden', mb: 2 }}>{item.description}</Typography>
                        <Stack direction="row" justifyContent="space-between" alignItems="center">
                          <Typography variant="h6" sx={{ color: '#1e293b', fontWeight: 900 }}>{item.prix.toLocaleString()} {CURRENCY}</Typography>
                          <IconButton size="small" sx={{ bgcolor: '#f4f7f9' }}><AddIcon /></IconButton>
                        </Stack>
                      </Box>
                    </Paper>
                  </motion.div>
                </Grid>
              ))}
            </AnimatePresence>
          )}
        </Grid>
      </Box>

      {/* PANIER DROIT - Design "Glass" */}
      <Box sx={{ width: 400, bgcolor: '#fff', borderLeft: '1px solid #eef2f6', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ p: 4, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Typography variant="h5" sx={{ fontWeight: 900 }}>Panier</Typography>
          <Badge badgeContent={cart.length} color="success"><ReceiptLongIcon color="action" /></Badge>
        </Box>

        <Box sx={{ flex: 1, overflowY: 'auto', px: 3 }}>
          {cart.length === 0 ? (
            <Stack alignItems="center" sx={{ mt: 10, opacity: 0.3 }}>
              <Avatar sx={{ width: 80, height: 80, bgcolor: '#f4f7f9', mb: 2 }}><RestaurantMenuIcon sx={{ fontSize: 40, color: '#64748b' }} /></Avatar>
              <Typography variant="h6" sx={{ fontWeight: 700 }}>C'est vide ici...</Typography>
            </Stack>
          ) : (
            cart.map((item) => (
              <motion.div key={item.id} layout initial={{ x: 20 }} animate={{ x: 0 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 3, p: 2, bgcolor: '#f8fafc', borderRadius: 4 }}>
                  <Avatar src={item.photoUrl} variant="rounded" sx={{ width: 50, height: 50, borderRadius: 2 }} />
                  <Box sx={{ flex: 1, ml: 2 }}>
                    <Typography variant="body2" sx={{ fontWeight: 800 }}>{item.nom}</Typography>
                    <Typography variant="caption" sx={{ color: '#10b981', fontWeight: 700 }}>{item.prix.toLocaleString()} {CURRENCY}</Typography>
                  </Box>
                  <Stack direction="row" alignItems="center" spacing={1}>
                    <IconButton size="small" onClick={() => updateQuantity(item.id, -1)} sx={{ bgcolor: '#fff' }}><RemoveIcon fontSize="small" /></IconButton>
                    <Typography sx={{ fontWeight: 900, minWidth: 20, textAlign: 'center' }}>{item.quantity}</Typography>
                    <IconButton size="small" onClick={() => updateQuantity(item.id, 1)} sx={{ bgcolor: '#fff' }}><AddIcon fontSize="small" /></IconButton>
                  </Stack>
                </Box>
              </motion.div>
            ))
          )}
        </Box>

        <Box sx={{ p: 4, borderTop: '1px solid #eef2f6' }}>
          <Stack spacing={1} sx={{ mb: 3 }}>
            <Stack direction="row" justifyContent="space-between"><Typography color="text.secondary">Sous-total</Typography><Typography fontWeight={700}>{subtotal.toLocaleString()} {CURRENCY}</Typography></Stack>
            <Stack direction="row" justifyContent="space-between"><Typography variant="h5" fontWeight={900}>Total</Typography><Typography variant="h5" fontWeight={900} color="#10b981">{subtotal.toLocaleString()} {CURRENCY}</Typography></Stack>
          </Stack>
          <Button 
            fullWidth variant="contained" 
            disabled={cart.length === 0}
            onClick={handleOrderSubmit}
            sx={{ py: 2, borderRadius: 4, bgcolor: '#1e293b', fontSize: '1rem', fontWeight: 800, textTransform: 'none', boxShadow: '0 10px 20px -5px rgba(30,41,59,0.3)' }}
          >
            Passer la commande
          </Button>
        </Box>
      </Box>

      <Snackbar open={notification.open} autoHideDuration={3000} onClose={() => setNotification({ ...notification, open: false })}>
        <Alert severity={notification.severity} variant="filled" sx={{ borderRadius: 3 }}>{notification.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default PointOfSale;