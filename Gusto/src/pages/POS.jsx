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
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import LocalFireDepartmentIcon from '@mui/icons-material/LocalFireDepartment';

import { getPlats, getCategories, getTables, createCommande } from '../services/api';
import { formatStatut } from '../utils/formatters';
import { getImageUrl, createImageErrorHandler } from '../utils/imageUtils';
import { getImageDisplayUrl } from '../utils/imageStorage';

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
        if (tableRes.data?.length > 0) setSelectedTable(tableRes.data[0]);
      } catch (error) {
        setNotification({ open: true, message: 'Erreur réseau lors du chargement', severity: 'error' });
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
      const matchesCategory = !currentCategory || itemCatId === currentCategory.id;
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
      item.id === id ? { ...item, quantity: Math.max(0, item.quantity + delta) } : item
    ).filter(item => item.quantity > 0));
  };

  const subtotal = useMemo(() => cart.reduce((acc, item) => acc + (item.prix * item.quantity), 0), [cart]);

  // CORRECTION ERREUR 400 : Payload structuré pour le backend
  const [orderType, setOrderType] = useState('SUR_PLACE'); // 'SUR_PLACE' or 'A_EMPORTER'

  // ... (useEffects and other logic remain same)

  const handleOrderSubmit = async () => {
    if (orderType === 'SUR_PLACE' && !selectedTable) return setNotification({ open: true, message: 'Sélectionnez une table', severity: 'warning' });
    if (cart.length === 0) return setNotification({ open: true, message: 'Le panier est vide', severity: 'warning' });

    try {
      const orderPayload = {
        tableId: orderType === 'SUR_PLACE' ? selectedTable.id : null,
        statut: 'EN_ATTENTE',
        typeCommande: orderType,
        lignesCommande: cart.map(item => ({
          plat: item.id, // Changé de platId à plat pour correspondre au DTO
          quantite: item.quantity,
          prixUnitaire: item.prix
        }))
      };

      await createCommande(orderPayload);
      setNotification({ open: true, message: 'Commande envoyée avec succès !', severity: 'success' });
      setCart([]);
    } catch (e) {
      const errorDetail = e.response?.data?.message || 'Erreur lors de l\'envoi';
      setNotification({ open: true, message: errorDetail, severity: 'error' });
    }
  };

  return (
    <Box sx={{ display: 'flex', height: '100vh', bgcolor: '#f4f7f9', overflow: 'hidden' }}>

      {/* SIDEBAR TABLES */}
      <Box sx={{ width: 80, bgcolor: '#fff', borderRight: '1px solid #eef2f6', display: 'flex', flexDirection: 'column', alignItems: 'center', py: 3, gap: 2 }}>
        <Avatar sx={{ bgcolor: '#10b981', mb: 2 }}><RestaurantMenuIcon /></Avatar>
        {loading ? [1, 2, 3].map(i => <Skeleton key={i} variant="rectangular" width={50} height={50} sx={{ borderRadius: 2 }} />) :
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
        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 4 }}>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 900, color: '#1e293b' }}>
              Gusto<span style={{ color: '#10b981' }}>.</span>
            </Typography>
            <Typography variant="body2" sx={{ color: '#94a3b8' }}>
              {selectedTable ? `Commande Table ${selectedTable.numero}` : 'Sélectionnez une table'}
            </Typography>
          </Box>
          <Paper elevation={0} sx={{ p: '10px 20px', display: 'flex', alignItems: 'center', width: 300, borderRadius: 3, border: '1px solid #eef2f6' }}>
            <SearchIcon sx={{ color: '#cbd5e1', mr: 1 }} />
            <InputBase
              placeholder="Rechercher..."
              sx={{ flex: 1 }}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </Paper>
        </Stack>

        {loading ?
          <Stack direction="row" spacing={2} sx={{ mb: 4 }}>
            {[1, 2, 3, 4].map((i) => (
              <Skeleton key={i} variant="rectangular" width={100} height={40} sx={{ borderRadius: 2 }} />
            ))}
          </Stack>
          :
          <Tabs
            value={activeCategoryIndex}
            onChange={(e, v) => setActiveCategoryIndex(v)}
            variant="scrollable"
            scrollButtons={false}
            TabIndicatorProps={{ style: { display: 'none' } }}
            sx={{ mb: 4 }}
          >
            {categories.map((cat, i) => (
              <Tab
                key={cat.id}
                label={cat.nom}
                sx={{
                  textTransform: 'none',
                  fontWeight: 700,
                  borderRadius: '10px',
                  mr: 2,
                  minHeight: 40,
                  bgcolor: activeCategoryIndex === i ? '#10b981' : '#fff',
                  color: activeCategoryIndex === i ? '#fff !important' : '#64748b',
                  border: '1px solid #eef2f6'
                }}
              />
            ))}
          </Tabs>
        }

        {/* GRID PLATS */}
        <Grid container spacing={3}>
          {loading ? [1, 2, 3, 4, 5, 6].map(i => (
            <Grid item xs={12} sm={6} md={4} key={i}><Skeleton variant="rectangular" height={200} sx={{ borderRadius: 4 }} /></Grid>
          )) : (
            <AnimatePresence mode='popLayout'>
              {filteredMenu.map((item) => (
                <Grid item xs={12} sm={6} md={4} key={item.id}>
                  <motion.div layout initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
                    <Paper
                      onClick={() => addToCart(item)}
                      sx={{
                        borderRadius: 4, overflow: 'hidden', cursor: 'pointer', border: '1px solid #eef2f6',
                        '&:hover': { transform: 'translateY(-5px)', boxShadow: '0 10px 20px rgba(0,0,0,0.05)' }
                      }}
                    >
                      {item.photoUrl ? (
                        <Box
                          component="img"
                          src={getImageDisplayUrl(item.photoUrl)}
                          alt={item.nom}
                          onError={createImageErrorHandler()}
                          sx={{ height: 140, width: '100%', objectFit: 'cover', display: 'block' }}
                        />
                      ) : (
                        <Box sx={{ height: 140, bgcolor: '#f1f5f9', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                          <RestaurantMenuIcon sx={{ fontSize: 40, color: '#cbd5e1' }} />
                        </Box>
                      )}
                      <Box sx={{ p: 2 }}>
                        <Typography fontWeight={800}>{item.nom}</Typography>
                        <Typography variant="h6" color="primary" fontWeight={900}>{item.prix.toLocaleString()} {CURRENCY}</Typography>
                      </Box>
                    </Paper>
                  </motion.div>
                </Grid>
              ))}
            </AnimatePresence>
          )}
        </Grid>
      </Box>

      <Box sx={{ width: 380, bgcolor: '#fff', borderLeft: '1px solid #eef2f6', display: 'flex', flexDirection: 'column' }}>
        <Box sx={{ p: 3, borderBottom: '1px solid #f8fafc', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6" fontWeight={900}>Panier</Typography>
          <Stack direction="row" spacing={1} sx={{ bgcolor: '#f1f5f9', p: 0.5, borderRadius: 3 }}>
            <Button
              size="small"
              onClick={() => setOrderType('SUR_PLACE')}
              sx={{
                borderRadius: 2.5, px: 2, textTransform: 'none', fontWeight: 800,
                bgcolor: orderType === 'SUR_PLACE' ? '#fff' : 'transparent',
                color: orderType === 'SUR_PLACE' ? '#10b981' : '#64748b',
                boxShadow: orderType === 'SUR_PLACE' ? '0 2px 8px rgba(0,0,0,0.05)' : 'none',
                '&:hover': { bgcolor: orderType === 'SUR_PLACE' ? '#fff' : 'rgba(0,0,0,0.02)' }
              }}
            >
              Sur Place
            </Button>
            <Button
              size="small"
              onClick={() => setOrderType('A_EMPORTER')}
              sx={{
                borderRadius: 2.5, px: 2, textTransform: 'none', fontWeight: 800,
                bgcolor: orderType === 'A_EMPORTER' ? '#fff' : 'transparent',
                color: orderType === 'A_EMPORTER' ? '#10b981' : '#64748b',
                boxShadow: orderType === 'A_EMPORTER' ? '0 2px 8px rgba(0,0,0,0.05)' : 'none',
                '&:hover': { bgcolor: orderType === 'A_EMPORTER' ? '#fff' : 'rgba(0,0,0,0.02)' }
              }}
            >
              À Emporter
            </Button>
          </Stack>
        </Box>

        <Box sx={{ flex: 1, overflowY: 'auto', p: 2 }}>
          {cart.map((item) => (
            <Box key={item.id} sx={{ display: 'flex', alignItems: 'center', mb: 2, p: 1.5, bgcolor: '#f8fafc', borderRadius: 3 }}>
              <Box sx={{ flex: 1 }}>
                <Typography variant="body2" fontWeight={800}>{item.nom}</Typography>
                <Typography variant="caption" color="primary">{item.prix.toLocaleString()} {CURRENCY}</Typography>
              </Box>
              <Stack direction="row" alignItems="center" spacing={1}>
                <IconButton size="small" onClick={() => updateQuantity(item.id, -1)}><RemoveIcon fontSize="small" /></IconButton>
                <Typography fontWeight={900}>{item.quantity}</Typography>
                <IconButton size="small" onClick={() => updateQuantity(item.id, 1)}><AddIcon fontSize="small" /></IconButton>
              </Stack>
            </Box>
          ))}
        </Box>

        <Box sx={{ p: 3, borderTop: '1px solid #eef2f6' }}>
          <Stack direction="row" justifyContent="space-between" sx={{ mb: 2 }}>
            <Typography variant="h5" fontWeight={900}>Total</Typography>
            <Typography variant="h5" fontWeight={900} color="#10b981">{subtotal.toLocaleString()} {CURRENCY}</Typography>
          </Stack>
          <Button
            variant="contained"
            disabled={cart.length === 0}
            onClick={handleOrderSubmit}
            sx={{ width: '100%', py: 1.5, borderRadius: 3, bgcolor: '#1e293b', fontWeight: 800, textTransform: 'none' }}
          >
            Valider la commande
          </Button>
        </Box>
      </Box>

      <Snackbar open={notification.open} autoHideDuration={4000} onClose={() => setNotification({ ...notification, open: false })}>
        <Alert severity={notification.severity} variant="filled" sx={{ borderRadius: 2 }}>{notification.message}</Alert>
      </Snackbar>
    </Box>
  );
};

export default PointOfSale;