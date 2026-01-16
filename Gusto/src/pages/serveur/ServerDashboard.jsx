import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchCommandes, updateStatutCommande } from '../../features/ServeurDashboardSlice';
import websocketService from '../../services/websocketService';
import { Box, Typography, Paper, Button, Divider, Chip, Stack, CircularProgress, Grid, Avatar } from '@mui/material';
import { AnimatePresence ,motion } from 'framer-motion';

// Icônes
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import RestaurantIcon from '@mui/icons-material/Restaurant';
import AccessTimeIcon from '@mui/icons-material/AccessTime';

const ServeurDashboard = () => {
  const dispatch = useDispatch();
  const { commandes = [] } = useSelector((state) => state.serveur);

  useEffect(() => {
    // 1. Premier chargement
    dispatch(fetchCommandes());

    // 2. Connexion WebSocket pour notifications temps réel
    websocketService.connect(
      () => {
        // Écouter si un plat est prêt (Notif Cuisine -> Salle)
        websocketService.subscribe('/topic/salle/prete', () => {
          // Rafraîchir la liste ou mettre à jour l'état local
          dispatch(fetchCommandes());
          // Toast ou son ici
        });

        // Écouter les mises à jour de prix (Addition)
        websocketService.subscribe('/topic/serveurs/addition', () => {
          dispatch(fetchCommandes());
        });
      },
      (err) => console.error(err)
    );

    return () => {
      websocketService.disconnect();
    };
  }, [dispatch]);

  const getStatusConfig = (statut) => {
    const configs = {
      'PRETE': { color: '#10b981', bg: '#ecfdf5', label: 'À SERVIR', pulse: true },
      'SERVIE': { color: '#6366f1', bg: '#eef2ff', label: 'EN CAISSE', pulse: false },
      'EN_PREPARATION': { color: '#f59e0b', bg: '#fffbeb', label: 'CUISINE', pulse: false },
      'PAYEE': { color: '#3b82f6', bg: '#eff6ff', label: 'TERMINÉE', pulse: false },
      'EN_ATTENTE': { color: '#ef4444', bg: '#fef2f2', label: 'NOUVEAU', pulse: true }
    };
    return configs[statut] || { color: '#64748b', bg: '#f1f5f9', label: statut };
  };

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 5 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 900, color: '#1e293b' }}>Tableau de Service</Typography>
          <Typography color="text.secondary">Gérez les commandes en temps réel</Typography>
        </Box>

        {/* Badge d'alerte pour les commandes PRÊTES */}
        <Button
          variant="contained"
          startIcon={<NotificationsActiveIcon />}
          sx={{
            bgcolor: '#10b981', borderRadius: 3, px: 3, py: 1.5, fontWeight: 800,
            '&:hover': { bgcolor: '#059669' }
          }}
        >
          {commandes.filter(c => c.statut === 'PRETE').length} Plats prêts
        </Button>
      </Stack>

      <Grid container spacing={3}>
        <AnimatePresence>
          {commandes.map((cmd) => {
            const config = getStatusConfig(cmd.statut);
            return (
              <Grid item xs={12} md={6} lg={4} key={cmd.id}>
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, scale: 0.5 }}
                >
                  <Paper elevation={0} sx={{ borderRadius: 5, border: `2px solid ${config.bg}`, overflow: 'hidden', position: 'relative' }}>

                    {/* Header de la carte */}
                    <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center', bgcolor: config.bg }}>
                      <Stack direction="row" spacing={2} alignItems="center">
                        <Avatar sx={{ bgcolor: '#fff', color: '#1e293b', fontWeight: 900, border: '1px solid #e2e8f0' }}>
                          {cmd.table?.numero}
                        </Avatar>
                        <Box>
                          <Typography variant="subtitle1" sx={{ fontWeight: 900 }}>Table {cmd.table?.numero}</Typography>
                          <Stack direction="row" alignItems="center" spacing={0.5}>
                            <AccessTimeIcon sx={{ fontSize: 14, color: '#64748b' }} />
                            <Typography variant="caption" color="text.secondary">
                              {new Date(cmd.dateHeureCommande).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            </Typography>
                          </Stack>
                        </Box>
                      </Stack>
                      <Chip
                        label={config.label}
                        sx={{ bgcolor: '#fff', color: config.color, fontWeight: 900, fontSize: '0.7rem' }}
                        variant="outlined"
                      />
                    </Box>

                    {/* Contenu de la commande */}
                    <Box sx={{ p: 3 }}>
                      <Stack spacing={1} sx={{ mb: 2 }}>
                        {cmd.lignesCommande?.map((ligne, idx) => (
                          <Stack key={idx} direction="row" justifyContent="space-between">
                            <Typography variant="body2" sx={{ fontWeight: 600 }}>
                              <span style={{ color: '#10b981' }}>{ligne.quantite}x</span> {ligne.platNom || "Plat"}
                            </Typography>
                          </Stack>
                        ))}
                      </Stack>

                      <Divider sx={{ my: 2, borderStyle: 'dashed' }} />

                      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 3 }}>
                        <Typography variant="caption" color="text.secondary">Total à régler</Typography>
                        <Typography variant="h6" sx={{ fontWeight: 900 }}>{cmd.totalTtc?.toLocaleString()} FCFA</Typography>
                      </Stack>

                      {/* Actions Dynamiques */}
                      {cmd.statut === 'EN_ATTENTE' && (
                        <Button
                          fullWidth variant="contained"
                          onClick={() => dispatch(updateStatutCommande({ id: cmd.id, nouveauStatut: 'EN_PREPARATION' }))}
                          sx={{ bgcolor: '#1e293b', borderRadius: 3, py: 1.5, fontWeight: 800, textTransform: 'none' }}
                        >
                          Envoyer en Cuisine
                        </Button>
                      )}

                      {cmd.statut === 'PRETE' && (
                        <Button
                          fullWidth variant="contained"
                          startIcon={<CheckCircleIcon />}
                          onClick={() => dispatch(updateStatutCommande({ id: cmd.id, nouveauStatut: 'SERVIE' }))}
                          sx={{
                            bgcolor: '#10b981', borderRadius: 3, py: 1.5, fontWeight: 800, textTransform: 'none',
                            animation: config.pulse ? 'pulse 2s infinite' : 'none'
                          }}
                        >
                          Marquer comme Servi
                        </Button>
                      )}

                      {cmd.statut === 'SERVIE' && (
                        <Button fullWidth disabled sx={{ borderRadius: 3, py: 1.5, color: '#6366f1' }}>
                          <CheckCircleIcon sx={{ mr: 1 }} /> Envoyé à l'encaissement
                        </Button>
                      )}

                      {cmd.statut === 'EN_PREPARATION' && (
                        <Button fullWidth disabled sx={{ borderRadius: 3, py: 1.5 }}>
                          <CircularProgress size={20} sx={{ mr: 1 }} /> En préparation...
                        </Button>
                      )}
                    </Box>
                  </Paper>
                </motion.div>
              </Grid>
            );
          })}
        </AnimatePresence>
      </Grid>

      {/* CSS pour l'animation d'urgence */}
      <style>{`
        @keyframes pulse {
          0% { transform: scale(1); }
          50% { transform: scale(1.02); box-shadow: 0 0 20px rgba(16,185,129,0.4); }
          100% { transform: scale(1); }
        }
      `}</style>
    </Box>
  );
};

export default ServeurDashboard;