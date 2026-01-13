import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Paper, Grid, Button, Divider,
  IconButton, TextField, InputAdornment, Container, alpha,
  List, ListItem, ListItemText, ListItemAvatar, Avatar, CircularProgress, Stack
} from '@mui/material';
import { getCommandesServies, updateStatut } from '../services/api';
import { toast } from 'react-hot-toast';
import PaymentsIcon from '@mui/icons-material/Payments';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import SmartphoneIcon from '@mui/icons-material/Smartphone';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import TableBarIcon from '@mui/icons-material/TableBar';

const PaymentInterface = () => {
  const [commandes, setCommandes] = useState([]);
  const [selectedCommande, setSelectedCommande] = useState(null);
  const [loading, setLoading] = useState(true);
  const [method, setMethod] = useState('cash');
  const [receivedAmount, setReceivedAmount] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);
  const [paymentSuccess, setPaymentSuccess] = useState(false);
  const [lastPaidCommande, setLastPaidCommande] = useState(null);

  useEffect(() => {
    fetchCommandes();
  }, []);

  const fetchCommandes = async () => {
    setLoading(true);
    try {
      const response = await getCommandesServies();
      setCommandes(response.data);
    } catch (error) {
      toast.error("Erreur lors de la récupération des commandes");
    } finally {
      setLoading(false);
    }
  };

  const handleValidation = async () => {
    if (!selectedCommande) return toast.error("Veuillez sélectionner une commande");

    setIsProcessing(true);
    try {
      await updateStatut(selectedCommande.id, 'PAYEE');
      toast.success(`Encaissement validé pour la Table ${selectedCommande.table?.numero || 'N/A'}`);
      setLastPaidCommande(selectedCommande);
      setPaymentSuccess(true);
      setSelectedCommande(null);
      setReceivedAmount('');
      fetchCommandes();
    } catch (error) {
      toast.error("Erreur lors de la validation du paiement");
    } finally {
      setIsProcessing(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  const total = selectedCommande ? selectedCommande.totalTtc : 0;
  const change = (receivedAmount && total) ? Math.max(0, parseFloat(receivedAmount) - total) : 0;

  if (loading && commandes.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', bgcolor: '#f8fafc' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ minHeight: '100vh', background: 'linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)', py: { xs: 2, md: 6 } }}>
      <Container maxWidth="xl">
        <Grid container spacing={4}>
          {/* GAUCHE : LISTE DES COMMANDES À PAYER */}
          <Grid item xs={12} md={3}>
            <Typography variant="h6" sx={{ fontWeight: 900, mb: 3, color: '#0f172a' }}>Commandes à encaisser</Typography>
            <Paper sx={{ borderRadius: 6, overflow: 'hidden', boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
              <List sx={{ p: 0 }}>
                {commandes.length === 0 ? (
                  <Box sx={{ p: 4, textAlign: 'center' }}>
                    <Typography variant="body2" color="text.secondary">Aucune commande servie en attente de paiement.</Typography>
                  </Box>
                ) : (
                  commandes.map((cmd) => (
                    <ListItem
                      key={cmd.id}
                      button
                      onClick={() => setSelectedCommande(cmd)}
                      selected={selectedCommande?.id === cmd.id}
                      sx={{
                        borderBottom: '1px solid #f1f5f9',
                        '&.Mui-selected': { bgcolor: alpha('#6366f1', 0.1), '&:hover': { bgcolor: alpha('#6366f1', 0.15) } },
                        py: 2
                      }}
                    >
                      <ListItemAvatar>
                        <Avatar sx={{ bgcolor: selectedCommande?.id === cmd.id ? '#6366f1' : '#f1f5f9', color: selectedCommande?.id === cmd.id ? 'white' : '#64748b' }}>
                          <TableBarIcon />
                        </Avatar>
                      </ListItemAvatar>
                      <ListItemText
                        primary={`Table ${cmd.table?.numero || 'N/A'}`}
                        secondary={`${cmd.totalTtc.toLocaleString()} FCFA`}
                        primaryTypographyProps={{ fontWeight: 800 }}
                        secondaryTypographyProps={{ fontWeight: 700, color: '#6366f1' }}
                      />
                    </ListItem>
                  ))
                )}
              </List>
            </Paper>
          </Grid>

          {/* MILIEU : CONFIGURATION DU PAIEMENT */}
          <Grid item xs={12} md={selectedCommande ? 5.5 : 9}>
            {selectedCommande ? (
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 1 }}>
                  <IconButton onClick={() => setSelectedCommande(null)} sx={{ bgcolor: 'white', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
                    <ArrowBackIosNewIcon fontSize="small" />
                  </IconButton>
                  <Typography variant="h5" sx={{ fontWeight: 900 }}>Détails du règlement</Typography>
                </Box>

                <Paper sx={{ p: 4, borderRadius: 8, boxShadow: '0 20px 50px rgba(0,0,0,0.04)' }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 3 }}>Mode de règlement</Typography>
                  <Grid container spacing={2}>
                    {[
                      { id: 'cash', label: 'Espèces', icon: <PaymentsIcon fontSize="large" />, color: '#10b981' },
                      { id: 'card', label: 'Carte', icon: <CreditCardIcon fontSize="large" />, color: '#3b82f6' },
                      { id: 'momo', label: 'Mobile', icon: <SmartphoneIcon fontSize="large" />, color: '#f59e0b' }
                    ].map((item) => (
                      <Grid item xs={4} key={item.id}>
                        <Button
                          fullWidth onClick={() => setMethod(item.id)}
                          sx={{
                            flexDirection: 'column', py: 3, borderRadius: 6, border: '2px solid',
                            borderColor: method === item.id ? item.color : '#f1f5f9',
                            bgcolor: method === item.id ? alpha(item.color, 0.05) : 'white',
                            color: method === item.id ? item.color : '#64748b'
                          }}
                        >
                          {item.icon}
                          <Typography variant="caption" sx={{ mt: 1.5, fontWeight: 800 }}>{item.label}</Typography>
                        </Button>
                      </Grid>
                    ))}
                  </Grid>

                  <Divider sx={{ my: 4 }} />

                  <Box sx={{ p: 3, bgcolor: '#f8fafc', borderRadius: 6, border: '1px solid #e2e8f0' }}>
                    <Grid container spacing={3} alignItems="center">
                      <Grid item xs={12} sm={6}>
                        <TextField
                          fullWidth label="Montant encaissé" type="number"
                          value={receivedAmount} onChange={(e) => setReceivedAmount(e.target.value)}
                          InputProps={{
                            startAdornment: <InputAdornment position="start">FCFA</InputAdornment>,
                            sx: { borderRadius: 4, height: 64, bgcolor: 'white', fontWeight: 700 }
                          }}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6}>
                        <Box sx={{ textAlign: { sm: 'right' } }}>
                          <Typography variant="caption" sx={{ fontWeight: 700, color: '#64748b' }}>MONNAIE À RENDRE</Typography>
                          <Typography variant="h4" sx={{ fontWeight: 900, color: change > 0 ? '#10b981' : '#cbd5e1' }}>
                            {change.toLocaleString()}
                          </Typography>
                        </Box>
                      </Grid>
                    </Grid>
                  </Box>
                </Paper>
              </Box>
            ) : (
              <Box sx={{ display: 'flex', height: '60vh', justifyContent: 'center', alignItems: 'center', opacity: 0.5 }}>
                <Typography variant="h5" fontWeight={700}>Sélectionnez une commande pour procéder au règlement</Typography>
              </Box>
            )}
          </Grid>

          {/* DROITE : TICKET OU SUCCÈS */}
          <Grid item xs={12} md={3.5}>
            {paymentSuccess && lastPaidCommande ? (
              <Paper sx={{ p: 4, borderRadius: 8, textAlign: 'center', boxShadow: '0 20px 50px rgba(0,0,0,0.08)', bgcolor: 'white' }}>
                <CheckCircleIcon sx={{ fontSize: 80, color: '#10b981', mb: 2 }} />
                <Typography variant="h5" fontWeight={900} gutterBottom>Paiement Réussi !</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
                  La commande de la Table {lastPaidCommande.table?.numero} a été clôturée avec succès.
                </Typography>
                <Stack spacing={2}>
                  <Button
                    fullWidth variant="contained"
                    onClick={handlePrint}
                    startIcon={<ReceiptLongIcon />}
                    sx={{ py: 1.5, borderRadius: 3, bgcolor: '#1e293b', fontWeight: 800 }}
                  >
                    IMPRIMER LE REÇU
                  </Button>
                  <Button
                    fullWidth variant="outlined"
                    onClick={() => { setPaymentSuccess(false); setLastPaidCommande(null); }}
                    sx={{ py: 1.5, borderRadius: 3, fontWeight: 800 }}
                  >
                    NOUVEL ENCAISSEMENT
                  </Button>
                </Stack>
              </Paper>
            ) : selectedCommande ? (
              <Paper sx={{ borderRadius: 8, overflow: 'hidden', boxShadow: '0 30px 60px rgba(15, 23, 42, 0.15)', bgcolor: '#1e293b', color: 'white' }}>
                <Box sx={{ p: 3, bgcolor: '#334155' }}>
                  <Typography variant="h6" sx={{ fontWeight: 800, display: 'flex', alignItems: 'center', gap: 1.5 }}>
                    <ReceiptLongIcon sx={{ color: '#818cf8' }} /> Ticket #{selectedCommande.id}
                  </Typography>
                </Box>
                <Box sx={{ p: 3 }}>
                  <Stack spacing={2}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                      <Typography color="#94a3b8">Table</Typography>
                      <Typography fontWeight={600}>{selectedCommande.table?.numero}</Typography>
                    </Box>
                    <Divider sx={{ borderColor: 'rgba(255,255,255,0.1)' }} />
                    <Box sx={{ maxHeight: 200, overflowY: 'auto', pr: 1 }}>
                      {selectedCommande.lignesCommande?.map((ligne, i) => (
                        <Box key={i} sx={{ display: 'flex', justifyContent: 'space-between', mb: 1.5 }}>
                          <Typography variant="body2" color="#cbd5e1" sx={{ flex: 1, mr: 1 }}>{ligne.quantite}x {ligne.platNom || 'Article'}</Typography>
                          <Typography variant="body2">{(ligne.quantite * ligne.prixUnitaire).toLocaleString()} FCFA</Typography>
                        </Box>
                      ))}
                    </Box>
                    <Divider sx={{ borderColor: 'rgba(255,255,255,0.1)', borderStyle: 'dashed' }} />
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', py: 2 }}>
                      <Typography variant="h5" fontWeight={900}>TOTAL</Typography>
                      <Typography variant="h5" fontWeight={900} color="#818cf8">{selectedCommande.totalTtc.toLocaleString()} FCFA</Typography>
                    </Box>
                    <Button
                      fullWidth variant="contained" size="large"
                      onClick={handleValidation}
                      disabled={isProcessing || !receivedAmount || parseFloat(receivedAmount) < total}
                      sx={{
                        py: 2, borderRadius: 4, bgcolor: '#6366f1', fontWeight: 900,
                        '&:hover': { bgcolor: '#4f46e5' }
                      }}
                    >
                      {isProcessing ? <CircularProgress size={24} color="inherit" /> : 'VALIDER LE PAIEMENT'}
                    </Button>
                  </Stack>
                </Box>
              </Paper>
            ) : (
              <Box sx={{ border: '2px dashed #cbd5e1', borderRadius: 8, height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', p: 4, textAlign: 'center' }}>
                <Typography variant="body1" color="text.secondary" fontWeight={600}>
                  En attente de sélection d'une commande
                </Typography>
              </Box>
            )}
          </Grid>
        </Grid>
      </Container>

      {/* ZONE INVISIBLE POUR L'IMPRESSION */}
      <Box sx={{ display: 'none', displayPrint: 'block', p: 4, color: 'black', width: '80mm' }}>
        <Typography variant="h5" align="center" fontWeight={900}>GUSTO RESTAURANT</Typography>
        <Typography variant="body2" align="center" sx={{ mb: 2 }}>Reçu de Paiement</Typography>
        <Divider sx={{ my: 1, borderColor: 'black' }} />
        {lastPaidCommande && (
          <>
            <Typography variant="body2">Ticket: #{lastPaidCommande.id}</Typography>
            <Typography variant="body2">Table: {lastPaidCommande.table?.numero}</Typography>
            <Typography variant="body2">Date: {new Date().toLocaleString()}</Typography>
            <Divider sx={{ my: 1, borderColor: 'black' }} />
            {lastPaidCommande.lignesCommande?.map((ligne, i) => (
              <Box key={i} sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <Typography variant="body2">{ligne.quantite}x {ligne.platNom || 'Article'}</Typography>
                <Typography variant="body2">{(ligne.quantite * ligne.prixUnitaire).toLocaleString()}</Typography>
              </Box>
            ))}
            <Divider sx={{ my: 1, borderColor: 'black', borderStyle: 'dashed' }} />
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography variant="h6" fontWeight={900}>TOTAL</Typography>
              <Typography variant="h6" fontWeight={900}>{lastPaidCommande.totalTtc.toLocaleString()} FCFA</Typography>
            </Box>
            <Typography variant="body2" align="center" sx={{ mt: 4 }}>Merci de votre visite !</Typography>
          </>
        )}
      </Box>

      <style>
        {`
          @media print {
            body * { visibility: hidden; }
            [class*="displayPrint"] { visibility: visible; position: absolute; left: 0; top: 0; }
            header, footer, aside, nav, button { display: none !important; }
          }
        `}
      </style>

    </Box>
  );
};

export default PaymentInterface;
