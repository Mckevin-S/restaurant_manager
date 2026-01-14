import React, { useEffect, useState } from 'react';
import { Box, Typography, Paper, Button, Grid, Chip, Stack, CircularProgress, Avatar } from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import apiClient from '../../services/apiClient';
import { toast } from 'react-hot-toast';

// Icônes
import MonetizationOnIcon from '@mui/icons-material/MonetizationOn';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import AccessTimeIcon from '@mui/icons-material/AccessTime';

const CaisseView = () => {
  const [commandes, setCommandes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState(null);

  useEffect(() => {
    loadCommandes();
    // Recharger chaque 10 secondes
    const interval = setInterval(loadCommandes, 10000);
    return () => clearInterval(interval);
  }, []);

  const loadCommandes = async () => {
    try {
      const response = await apiClient.get('/commandes');
      // Filtrer pour la caisse: SERVIE uniquement (en attente de paiement)
      const filtered = response.data.filter(cmd => cmd.statut === 'SERVIE');
      setCommandes(filtered);
    } catch (error) {
      console.error('Erreur chargement commandes', error);
      toast.error('Erreur lors du chargement des commandes');
    } finally {
      setLoading(false);
    }
  };

  const handleEncaisser = async (commandeId) => {
    setUpdatingId(commandeId);
    try {
      await apiClient.patch(`/commandes/${commandeId}/statut`, null, {
        params: { statut: 'PAYEE' }
      });
      toast.success('Commande encaissée');
      // Retirer de la liste
      setCommandes(prev => prev.filter(c => c.id !== commandeId));
    } catch (error) {
      console.error('Erreur encaissement', error);
      toast.error('Erreur lors de l\'encaissement');
    } finally {
      setUpdatingId(null);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 4, bgcolor: '#f8fafc', minHeight: '100vh' }}>
      {/* Header */}
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 5 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 900, color: '#1e293b', display: 'flex', alignItems: 'center', gap: 2 }}>
            <MonetizationOnIcon sx={{ fontSize: 32, color: '#10b981' }} />
            Encaissement
          </Typography>
          <Typography color="text.secondary">
            {commandes.length} commande(s) en attente de paiement
          </Typography>
        </Box>

        <Chip 
          label={`${commandes.length} à encaisser`}
          color="success"
          sx={{ fontWeight: 900, height: 40, fontSize: '1rem' }}
        />
      </Stack>

      {/* Liste des commandes */}
      <Grid container spacing={3}>
        <AnimatePresence>
          {commandes.length > 0 ? (
            commandes.map((cmd) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={cmd.id}>
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, scale: 0.95 }}
                  layout
                >
                  <Paper 
                    sx={{ 
                      p: 3, 
                      borderRadius: 3, 
                      border: '2px solid #10b981',
                      bgcolor: '#f0fdf4',
                      height: '100%',
                      display: 'flex',
                      flexDirection: 'column',
                      justifyContent: 'space-between'
                    }}
                  >
                    {/* Table & Heure */}
                    <Box>
                      <Stack direction="row" alignItems="center" spacing={2} sx={{ mb: 3 }}>
                        <Avatar sx={{ bgcolor: '#10b981', color: 'white', fontWeight: 900 }}>
                          {cmd.table?.numero || 'X'}
                        </Avatar>
                        <Box flex={1}>
                          <Typography variant="h6" sx={{ fontWeight: 900 }}>
                            Table {cmd.table?.numero || 'N/A'}
                          </Typography>
                          <Stack direction="row" alignItems="center" spacing={0.5}>
                            <AccessTimeIcon sx={{ fontSize: 14 }} />
                            <Typography variant="caption" color="text.secondary">
                              {new Date(cmd.dateCreation).toLocaleTimeString()}
                            </Typography>
                          </Stack>
                        </Box>
                      </Stack>

                      {/* Total */}
                      <Paper sx={{ p: 2, bgcolor: 'white', mb: 2, textAlign: 'center', borderRadius: 2 }}>
                        <Typography variant="caption" color="text.secondary">
                          MONTANT TOTAL
                        </Typography>
                        <Typography 
                          variant="h5" 
                          sx={{ fontWeight: 900, color: '#10b981', mt: 0.5 }}
                        >
                          {cmd.montantTotal?.toLocaleString('fr-FR', { 
                            style: 'currency', 
                            currency: 'XAF' 
                          }) || 'N/A'}
                        </Typography>
                      </Paper>

                      {/* Détails */}
                      <Stack spacing={1} sx={{ mb: 2 }}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem' }}>
                          <Typography color="text.secondary">Plats:</Typography>
                          <Typography sx={{ fontWeight: 600 }}>
                            {cmd.lignes?.length || 0} article(s)
                          </Typography>
                        </Box>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem' }}>
                          <Typography color="text.secondary">Statut:</Typography>
                          <Chip 
                            label="À PAYER"
                            size="small"
                            sx={{ 
                              bgcolor: '#10b981', 
                              color: 'white', 
                              fontWeight: 800,
                              height: 20
                            }}
                          />
                        </Box>
                      </Stack>
                    </Box>

                    {/* Bouton Encaisser */}
                    <Button
                      variant="contained"
                      fullWidth
                      onClick={() => handleEncaisser(cmd.id)}
                      disabled={updatingId === cmd.id}
                      startIcon={updatingId === cmd.id ? <CircularProgress size={20} /> : <CheckCircleIcon />}
                      sx={{
                        bgcolor: '#10b981',
                        color: 'white',
                        fontWeight: 900,
                        py: 1.5,
                        '&:hover': { bgcolor: '#059669' },
                        '&:disabled': { opacity: 0.7 }
                      }}
                    >
                      {updatingId === cmd.id ? 'Encaissement...' : 'ENCAISSER'}
                    </Button>
                  </Paper>
                </motion.div>
              </Grid>
            ))
          ) : (
            <Grid item xs={12}>
              <Paper sx={{ p: 5, textAlign: 'center', bgcolor: '#f1f5f9', borderRadius: 3 }}>
                <CheckCircleIcon sx={{ fontSize: 60, color: '#10b981', mb: 2 }} />
                <Typography variant="h6" sx={{ fontWeight: 900, color: '#1e293b' }}>
                  Aucune commande en attente
                </Typography>
                <Typography color="text.secondary">
                  Toutes les commandes ont été encaissées
                </Typography>
              </Paper>
            </Grid>
          )}
        </AnimatePresence>
      </Grid>
    </Box>
  );
};

export default CaisseView;
