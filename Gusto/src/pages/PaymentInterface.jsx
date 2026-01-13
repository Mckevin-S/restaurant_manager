import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Paper, Grid, Button, Divider,
  IconButton, TextField, InputAdornment, Container, alpha,
  List, ListItem, ListItemText, ListItemAvatar, Avatar, CircularProgress, Stack, Fade, Chip,
  Dialog, DialogTitle, DialogContent, DialogActions
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
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import PrintIcon from '@mui/icons-material/Print';
import RefreshIcon from '@mui/icons-material/Refresh';
import PersonIcon from '@mui/icons-material/Person';
import PhoneIcon from '@mui/icons-material/Phone';
import NotesIcon from '@mui/icons-material/Notes';

const PaymentInterface = () => {
  const [commandes, setCommandes] = useState([]);
  const [selectedCommande, setSelectedCommande] = useState(null);
  const [loading, setLoading] = useState(true);
  const [method, setMethod] = useState('cash');
  const [receivedAmount, setReceivedAmount] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);
  const [paymentSuccess, setPaymentSuccess] = useState(false);
  const [lastPaidCommande, setLastPaidCommande] = useState(null);

  // Client Info Modal State
  const [clientModalOpen, setClientModalOpen] = useState(false);
  const [clientInfo, setClientInfo] = useState({ name: '', phone: '', note: '' });

  useEffect(() => {
    fetchCommandes();
  }, []);

  const fetchCommandes = async () => {
    setLoading(true);
    try {
      const response = await getCommandesServies();
      setCommandes(response.data);
    } catch (error) {
      toast.error("Erreur commande");
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
      console.error(error);
      toast.error("Erreur lors de la validation du paiement");
    } finally {
      setIsProcessing(false);
    }
  };

  const handleOpenPrintModal = () => {
    setClientInfo({ name: '', phone: '', note: '' }); // Reset or keep previous? Resetting is safer often.
    setClientModalOpen(true);
  };

  const handleConfirmPrint = () => {
    setClientModalOpen(false);
    setTimeout(() => {
      window.print();
    }, 300); // Small delay to allow modal to close completely
  };

  const total = selectedCommande ? selectedCommande.totalTtc : 0;
  const change = (receivedAmount && total) ? Math.max(0, parseFloat(receivedAmount) - total) : 0;

  // --- STYLES ---
  const glassEffect = {
    background: 'rgba(255, 255, 255, 0.95)',
    backdropFilter: 'blur(10px)',
    boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.1)',
    border: '1px solid rgba(255, 255, 255, 0.18)',
    borderRadius: 6
  };

  const interactiveCard = {
    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
    '&:hover': {
      transform: 'translateY(-4px)',
      boxShadow: '0 12px 24px rgba(0,0,0,0.15)'
    }
  };

  if (loading && commandes.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', minHeight: '80vh' }}>
        <CircularProgress sx={{ color: '#6366f1' }} />
      </Box>
    );
  }

  return (
    <Box sx={{
      minHeight: '100%',
      p: { xs: 2, md: 4 },
      background: 'linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%)'
    }}>
      <Container maxWidth="xl" disableGutters>
        <Grid container spacing={3}>

          {/* COLONNE GAUCHE : LISTE DES COMMANDES */}
          <Grid item xs={12} md={4} lg={3}>
            <Fade in={true} timeout={500}>
              <Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                  <Typography variant="h5" sx={{ fontWeight: 800, color: '#1e293b', letterSpacing: '-0.5px' }}>
                    En attente <Chip label={commandes.length} color="primary" size="small" sx={{ ml: 1, fontWeight: 700 }} />
                  </Typography>
                  <IconButton onClick={fetchCommandes} size="small" sx={{ bgcolor: 'white' }}><RefreshIcon /></IconButton>
                </Box>

                <Paper sx={{ ...glassEffect, height: 'calc(100vh - 180px)', overflowY: 'auto', bgcolor: 'rgba(255,255,255,0.7)', p: 0 }}>
                  <List sx={{ p: 2 }}>
                    {commandes.length === 0 ? (
                      <Box sx={{ p: 4, textAlign: 'center', opacity: 0.6, mt: 10 }}>
                        <ReceiptLongIcon sx={{ fontSize: 64, color: '#cbd5e1', mb: 2 }} />
                        <Typography variant="body1" color="text.secondary" fontWeight={600}>Aucune commande prête.</Typography>
                        <Typography variant="caption" color="text.secondary">Les commandes servies apparaîtront ici.</Typography>
                      </Box>
                    ) : (
                      commandes.map((cmd) => (
                        <ListItem
                          key={cmd.id}
                          button
                          onClick={() => setSelectedCommande(cmd)}
                          selected={selectedCommande?.id === cmd.id}
                          sx={{
                            mb: 2,
                            borderRadius: 4,
                            bgcolor: selectedCommande?.id === cmd.id ? '#fff' : 'rgba(255,255,255,0.6)',
                            border: selectedCommande?.id === cmd.id ? '2px solid #6366f1' : '1px solid transparent',
                            boxShadow: selectedCommande?.id === cmd.id ? '0 10px 25px rgba(99, 102, 241, 0.2)' : 'none',
                            transition: 'all 0.2s ease',
                            flexDirection: 'column',
                            alignItems: 'flex-start',
                            p: 2,
                            '&:hover': { bgcolor: '#fff', transform: 'scale(1.02)', zIndex: 1 }
                          }}
                        >
                          <Box sx={{ display: 'flex', width: '100%', justifyContent: 'space-between', mb: 1 }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                              <Avatar sx={{
                                bgcolor: selectedCommande?.id === cmd.id ? '#6366f1' : '#e0e7ff',
                                color: selectedCommande?.id === cmd.id ? 'white' : '#4338ca',
                                borderRadius: 3, width: 48, height: 48
                              }}>
                                <TableBarIcon />
                              </Avatar>
                              <Box>
                                <Typography fontWeight={800} color="#1e293b" fontSize="1.1rem">Table {cmd.table?.numero}</Typography>
                                <Typography variant="caption" color="#64748b" sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                  <AccessTimeIcon sx={{ fontSize: 12 }} /> {new Date(cmd.dateHeureCommande).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                </Typography>
                              </Box>
                            </Box>
                            <Chip label={`#${cmd.id}`} size="small" sx={{ height: 24, fontSize: '0.7rem', fontWeight: 800, bgcolor: '#f1f5f9', color: '#64748b' }} />
                          </Box>

                          <Divider sx={{ width: '100%', my: 1.5, opacity: 0.5 }} />

                          <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center' }}>
                            <Typography variant="caption" fontWeight={600} color="#94a3b8">{cmd.lignesCommande?.length || 0} articles</Typography>
                            <Typography variant="h6" sx={{ fontWeight: 900, color: '#6366f1' }}>
                              {cmd.totalTtc.toLocaleString()} <span style={{ fontSize: '0.6em', textTransform: 'uppercase' }}>Fcfa</span>
                            </Typography>
                          </Box>
                        </ListItem>
                      ))
                    )}
                  </List>
                </Paper>
              </Box>
            </Fade>
          </Grid>

          {/* COLONNE CENTRALE : DÉTAILS & PAIEMENT */}
          <Grid item xs={12} md={selectedCommande ? 5 : 8} lg={selectedCommande ? 5.5 : 9}>
            <Fade in={true} timeout={700}>
              <Box>
                {selectedCommande ? (
                  <Box sx={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 140px)' }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <IconButton onClick={() => setSelectedCommande(null)} sx={{ bgcolor: 'white', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
                          <ArrowBackIosNewIcon fontSize="small" />
                        </IconButton>
                        <Box>
                          <Typography variant="h5" sx={{ fontWeight: 900, color: '#1e293b' }}>Règlement</Typography>
                          <Typography variant="body2" color="text.secondary">Commande #{selectedCommande.id}</Typography>
                        </Box>
                      </Box>
                      <Chip label="À ENCAISSER" color="warning" sx={{ fontWeight: 800, borderRadius: 2 }} />
                    </Box>

                    <Paper sx={{ ...glassEffect, p: 4, flex: 1, display: 'flex', flexDirection: 'column', gap: 4, overflowY: 'auto' }}>
                      <Box>
                        <Typography variant="subtitle2" sx={{ color: '#64748b', fontWeight: 800, mb: 2, textTransform: 'uppercase', letterSpacing: '0.5px', fontSize: '0.8rem' }}>
                          Moyen de paiement
                        </Typography>
                        <Grid container spacing={2}>
                          {[
                            { id: 'cash', label: 'Espèces', icon: <PaymentsIcon sx={{ fontSize: 32 }} />, color: '#10b981', gradient: 'linear-gradient(135deg, #10b981 0%, #059669 100%)' },
                            { id: 'card', label: 'Carte Bancaire', icon: <CreditCardIcon sx={{ fontSize: 32 }} />, color: '#3b82f6', gradient: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)' },
                            { id: 'momo', label: 'Mobile Money', icon: <SmartphoneIcon sx={{ fontSize: 32 }} />, color: '#f59e0b', gradient: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)' }
                          ].map((item) => (
                            <Grid item xs={4} key={item.id}>
                              <Button
                                fullWidth onClick={() => setMethod(item.id)}
                                sx={{
                                  ...interactiveCard,
                                  flexDirection: 'column', py: 3.5, borderRadius: 4,
                                  background: method === item.id ? item.gradient : 'white',
                                  color: method === item.id ? '#fff' : '#64748b',
                                  border: method === item.id ? 'none' : '1px solid #f1f5f9',
                                  boxShadow: method === item.id ? `0 10px 25px ${alpha(item.color, 0.4)}` : '0 4px 12px rgba(0,0,0,0.03)',
                                  '&:hover': {
                                    background: method === item.id ? item.gradient : '#f8fafc',
                                    transform: 'translateY(-4px)',
                                    boxShadow: '0 12px 24px rgba(0,0,0,0.1)'
                                  }
                                }}
                              >
                                {item.icon}
                                <Typography variant="caption" sx={{ mt: 1.5, fontWeight: 800, fontSize: '0.85rem' }}>{item.label}</Typography>
                              </Button>
                            </Grid>
                          ))}
                        </Grid>
                      </Box>

                      <Divider flexItem sx={{ borderStyle: 'dashed' }} />

                      <Box sx={{ flex: 1 }}>
                        <Typography variant="subtitle2" sx={{ color: '#64748b', fontWeight: 800, mb: 2, textTransform: 'uppercase', letterSpacing: '0.5px', fontSize: '0.8rem' }}>
                          Saisie du montant
                        </Typography>
                        <Box sx={{ p: 4, bgcolor: '#f8fafc', borderRadius: 5, border: '1px solid #e2e8f0', boxShadow: 'inset 0 2px 4px rgba(0,0,0,0.02)' }}>
                          <Grid container spacing={4} alignItems="center">
                            <Grid item xs={12} sm={6}>
                              <TextField
                                fullWidth label="Montant Reçu" type="number"
                                autoFocus
                                value={receivedAmount} onChange={(e) => setReceivedAmount(e.target.value)}
                                sx={{
                                  '& .MuiOutlinedInput-root': {
                                    borderRadius: 3, bgcolor: 'white', fontWeight: 800, fontSize: '1.5rem',
                                    height: 70,
                                    boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
                                    '& fieldset': { borderColor: 'transparent' },
                                    '&:hover fieldset': { borderColor: '#cbd5e1' },
                                    '&.Mui-focused fieldset': { borderColor: '#6366f1', borderWidth: 2 }
                                  },
                                  '& .MuiInputLabel-root': { fontWeight: 600 }
                                }}
                                InputProps={{
                                  startAdornment: <InputAdornment position="start"><Typography fontWeight={700} color="text.secondary">FCFA</Typography></InputAdornment>,
                                }}
                              />
                            </Grid>
                            <Grid item xs={12} sm={6}>
                              <Box sx={{ textAlign: { sm: 'right' } }}>
                                <Typography variant="caption" sx={{ fontWeight: 800, color: '#94a3b8', letterSpacing: '1px', textTransform: 'uppercase' }}>Monnaie à rendre</Typography>
                                <Typography variant="h3" sx={{ fontWeight: 900, color: change > 0 ? '#10b981' : '#cbd5e1', mt: 0.5, letterSpacing: '-1px' }}>
                                  {change.toLocaleString()}
                                </Typography>
                              </Box>
                            </Grid>
                          </Grid>
                        </Box>
                      </Box>

                    </Paper>
                  </Box>
                ) : (
                  <Paper sx={{ ...glassEffect, height: 'calc(100vh - 60px)', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', textAlign: 'center', p: 4, bgcolor: 'rgba(255,255,255,0.5)' }}>
                    <Box sx={{
                      width: 140, height: 140, borderRadius: '50%', bgcolor: 'white',
                      display: 'flex', alignItems: 'center', justifyContent: 'center', mb: 4,
                      boxShadow: '0 20px 40px rgba(99, 102, 241, 0.15)'
                    }}>
                      <PaymentsIcon sx={{ fontSize: 70, color: '#6366f1' }} />
                    </Box>
                    <Typography variant="h3" fontWeight={900} color="#1e293b" gutterBottom>Caisse Enregistreuse</Typography>
                    <Typography variant="h6" color="text.secondary" maxWidth={500} sx={{ fontWeight: 500, lineHeight: 1.6 }}>
                      Bienvenue sur votre interface de caisse. <br />
                      Sélectionnez une commande dans la liste de gauche pour procéder à l'encaissement.
                    </Typography>
                  </Paper>
                )}
              </Box>
            </Fade>
          </Grid>

          {/* COLONNE DROITE : TICKET */}
          <Grid item xs={false} md={selectedCommande ? 3 : false} lg={selectedCommande ? 3.5 : false} sx={{ display: selectedCommande ? 'block' : 'none' }}>
            <Fade in={!!selectedCommande} timeout={900}>
              <Box sx={{ height: 'calc(100vh - 80px)', position: 'relative' }}>
                {paymentSuccess && lastPaidCommande ? (
                  <Paper sx={{ p: 4, borderRadius: 6, textAlign: 'center', height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', bgcolor: 'white', boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)' }}>
                    <Box sx={{ mb: 'auto', mt: 'auto' }}>
                      <Box sx={{ position: 'relative', display: 'inline-flex', mb: 3 }}>
                        <Box sx={{ position: 'absolute', inset: -10, borderRadius: '50%', bgcolor: alpha('#10b981', 0.2) }} />
                        <CheckCircleIcon sx={{ fontSize: 100, color: '#10b981', position: 'relative' }} />
                      </Box>

                      <Typography variant="h4" fontWeight={900} gutterBottom sx={{ color: '#1e293b' }}>Paiement Validé !</Typography>
                      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
                        La commande est clôturée et la table est libérée.
                      </Typography>
                    </Box>

                    <Stack spacing={2} sx={{ width: '100%', mt: 4 }}>
                      <Button
                        fullWidth variant="contained" size="large"
                        onClick={handleOpenPrintModal}
                        startIcon={<PrintIcon />}
                        sx={{ py: 2, borderRadius: 4, bgcolor: '#1e293b', fontWeight: 800, boxShadow: '0 10px 20px rgba(30, 41, 59, 0.3)' }}
                      >
                        IMPRIMER LE REÇU
                      </Button>
                      <Button
                        fullWidth variant="outlined" size="large"
                        onClick={() => { setPaymentSuccess(false); setLastPaidCommande(null); }}
                        sx={{ py: 2, borderRadius: 4, fontWeight: 800, border: '2px solid #e2e8f0', color: '#64748b' }}
                      >
                        RETOUR À L'ACCUEIL
                      </Button>
                    </Stack>
                  </Paper>
                ) : (
                  <Paper sx={{
                    height: '100%', borderRadius: 6, overflow: 'hidden',
                    bgcolor: '#1e293b', color: 'white',
                    display: 'flex', flexDirection: 'column',
                    boxShadow: '0 30px 60px -12px rgba(15, 23, 42, 0.4)'
                  }}>
                    <Box sx={{ p: 4, bgcolor: '#0f172a', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                      <Typography variant="subtitle2" sx={{ color: '#94a3b8', letterSpacing: '2px', textTransform: 'uppercase', fontSize: '0.7rem', fontWeight: 700, mb: 1 }}>Aperçu du ticket</Typography>
                      <Typography variant="h5" sx={{ fontWeight: 800 }}>Table <span style={{ color: '#818cf8' }}>{selectedCommande?.table?.numero}</span></Typography>
                      <Typography variant="body2" sx={{ opacity: 0.6 }}>Commande #{selectedCommande?.id}</Typography>
                    </Box>

                    <Box sx={{ p: 4, flex: 1, overflowY: 'auto' }}>
                      <Stack spacing={2.5}>
                        {selectedCommande?.lignesCommande?.map((ligne, i) => (
                          <Box key={i} sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <Box sx={{ flex: 1 }}>
                              <Typography variant="body1" fontWeight={600} sx={{ color: '#f8fafc' }}>{ligne.platNom || 'Plat inconnu'}</Typography>
                              <Typography variant="caption" sx={{ color: '#94a3b8', fontWeight: 500 }}>
                                {ligne.quantite} x {ligne.prixUnitaire?.toLocaleString()}
                              </Typography>
                            </Box>
                            <Typography variant="body1" fontWeight={700}>{(ligne.quantite * ligne.prixUnitaire).toLocaleString()}</Typography>
                          </Box>
                        ))}
                      </Stack>

                      <Divider sx={{ my: 4, borderColor: 'rgba(255,255,255,0.1)', borderStyle: 'dashed' }} />

                      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography variant="body2" color="#94a3b8">Sous-total</Typography>
                        <Typography variant="body2" fontWeight={600}>{selectedCommande?.totalTtc.toLocaleString()}</Typography>
                      </Box>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography variant="body2" color="#94a3b8">TVA (included)</Typography>
                        <Typography variant="body2" fontWeight={600}>-</Typography>
                      </Box>

                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 4, pt: 4, borderTop: '2px solid rgba(255,255,255,0.1)' }}>
                        <Typography variant="h5" fontWeight={900}>TOTAL</Typography>
                        <Typography variant="h4" fontWeight={900} sx={{ color: '#818cf8' }}>{selectedCommande?.totalTtc.toLocaleString()}</Typography>
                      </Box>
                    </Box>

                    <Box sx={{ p: 3, bgcolor: '#0f172a' }}>
                      <Button
                        fullWidth variant="contained" size="large"
                        onClick={handleValidation}
                        disabled={isProcessing || !receivedAmount || parseFloat(receivedAmount) < total}
                        sx={{
                          py: 2.5, borderRadius: 4,
                          bgcolor: '#6366f1', color: 'white', fontWeight: 900, fontSize: '1.rem',
                          boxShadow: '0 0 20px rgba(99, 102, 241, 0.4)',
                          '&:hover': { bgcolor: '#4f46e5', boxShadow: '0 0 30px rgba(99, 102, 241, 0.6)' },
                          '&:disabled': { bgcolor: '#334155', color: '#64748b', boxShadow: 'none' }
                        }}
                      >
                        {isProcessing ? <CircularProgress size={24} color="inherit" /> : 'VALIDER ET ENCAISSER'}
                      </Button>
                    </Box>
                  </Paper>
                )}
              </Box>
            </Fade>
          </Grid>

        </Grid>
      </Container>


      {/* --- MODAL CLIENT --- */}
      <Dialog
        open={clientModalOpen}
        onClose={() => setClientModalOpen(false)}
        maxWidth="sm"
        fullWidth
        PaperProps={{
          sx: { borderRadius: 4, p: 2 }
        }}
      >
        <DialogTitle sx={{ fontWeight: 900, fontSize: '1.5rem', pb: 1 }}>
          Informations Client
          <Typography variant="body2" color="text.secondary">Ajouter les détails du client sur la facture (optionnel)</Typography>
        </DialogTitle>
        <DialogContent sx={{ mt: 1 }}>
          <Grid container spacing={3} sx={{ mt: 0.5 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Nom du client"
                InputProps={{
                  startAdornment: <InputAdornment position="start"><PersonIcon color="action" /></InputAdornment>,
                }}
                value={clientInfo.name}
                onChange={(e) => setClientInfo({ ...clientInfo, name: e.target.value })}
                variant="outlined"
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 3 } }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Téléphone"
                InputProps={{
                  startAdornment: <InputAdornment position="start"><PhoneIcon color="action" /></InputAdornment>,
                }}
                value={clientInfo.phone}
                onChange={(e) => setClientInfo({ ...clientInfo, phone: e.target.value })}
                variant="outlined"
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 3 } }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={3}
                label="Note / Adresse"
                InputProps={{
                  startAdornment: <InputAdornment position="start"><NotesIcon color="action" sx={{ mt: 1 }} /></InputAdornment>,
                }}
                value={clientInfo.note}
                onChange={(e) => setClientInfo({ ...clientInfo, note: e.target.value })}
                variant="outlined"
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 3 } }}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions sx={{ p: 3, pt: 0 }}>
          <Button onClick={() => setClientModalOpen(false)} sx={{ fontWeight: 700, px: 3, borderRadius: 3, color: '#64748b' }}>
            Annuler
          </Button>
          <Button
            variant="contained"
            onClick={handleConfirmPrint}
            startIcon={<PrintIcon />}
            sx={{ fontWeight: 800, px: 4, py: 1.5, borderRadius: 3, bgcolor: '#1e293b' }}
          >
            CONFIRMER ET IMPRIMER
          </Button>
        </DialogActions>
      </Dialog>


      {/* ZONE D'IMPRESSION (80mm) */}
      <Box className="displayPrint-container" sx={{ display: 'none', displayPrint: 'block', width: '80mm', p: 0, color: 'black', fontFamily: "'Courier New', Courier, monospace" }}>
        <Box sx={{ textAlign: 'center', mb: 2, pb: 2, borderBottom: '2px dashed black' }}>
          <Typography variant="h5" fontWeight={900} sx={{ textTransform: 'uppercase', fontSize: '1.4rem' }}>GUSTO RESTAURANT</Typography>
          <Typography variant="body2" sx={{ fontSize: '0.9rem' }}>Cocody Riviera, Abidjan</Typography>
          <Typography variant="body2" sx={{ fontSize: '0.9rem' }}>Tel: +225 07 07 07 07 07</Typography>
          <Typography variant="caption" sx={{ mt: 1, display: 'block' }}>RC: 123456789 • CC: 987654321</Typography>
        </Box>

        {lastPaidCommande && (
          <Box>
            <Box sx={{ mb: 2 }}>
              <Grid container>
                <Grid item xs={6}><Typography variant="body2" fontWeight={700}>Facture N°:</Typography></Grid>
                <Grid item xs={6} textAlign="right"><Typography variant="body2">#{lastPaidCommande.id}-{new Date().getFullYear()}</Typography></Grid>

                <Grid item xs={6}><Typography variant="body2" fontWeight={700}>Date:</Typography></Grid>
                <Grid item xs={6} textAlign="right"><Typography variant="body2">{new Date().toLocaleString('fr-FR')}</Typography></Grid>

                <Grid item xs={6}><Typography variant="body2" fontWeight={700}>Table:</Typography></Grid>
                <Grid item xs={6} textAlign="right"><Typography variant="body2">{lastPaidCommande.table?.numero}</Typography></Grid>

                <Grid item xs={6}><Typography variant="body2" fontWeight={700}>Caissier:</Typography></Grid>
                <Grid item xs={6} textAlign="right"><Typography variant="body2">Admin</Typography></Grid>
              </Grid>
            </Box>

            {/* INFO CLIENT SI PRÉSENT */}
            {(clientInfo.name || clientInfo.phone) && (
              <Box sx={{ mb: 2, p: 1, border: '1px solid black', borderRadius: 1 }}>
                <Typography variant="caption" fontWeight={900} sx={{ textDecoration: 'underline' }}>CLIENT</Typography>
                {clientInfo.name && <Typography variant="body2" fontWeight={700}>{clientInfo.name}</Typography>}
                {clientInfo.phone && <Typography variant="body2">Tél: {clientInfo.phone}</Typography>}
                {clientInfo.note && <Typography variant="caption" sx={{ display: 'block', mt: 0.5, fontStyle: 'italic' }}>Note: {clientInfo.note}</Typography>}
              </Box>
            )}

            <Divider sx={{ borderStyle: 'solid', borderColor: 'black', mb: 1 }} />

            {/* TICKET ITEMS HEADER */}
            <Grid container sx={{ mb: 0.5 }}>
              <Grid item xs={7}><Typography variant="caption" fontWeight={900}>DESIGNATION</Typography></Grid>
              <Grid item xs={2} textAlign="center"><Typography variant="caption" fontWeight={900}>QTE</Typography></Grid>
              <Grid item xs={3} textAlign="right"><Typography variant="caption" fontWeight={900}>TOTAL</Typography></Grid>
            </Grid>

            {lastPaidCommande.lignesCommande?.map((ligne, i) => (
              <Grid container key={i} sx={{ mb: 0.5 }}>
                <Grid item xs={7}><Typography variant="body2" sx={{ fontSize: '0.85rem' }}>{ligne.platNom}</Typography></Grid>
                <Grid item xs={2} textAlign="center"><Typography variant="body2" sx={{ fontSize: '0.85rem' }}>{ligne.quantite}</Typography></Grid>
                <Grid item xs={3} textAlign="right"><Typography variant="body2" sx={{ fontSize: '0.85rem' }}>{(ligne.quantite * ligne.prixUnitaire).toLocaleString()}</Typography></Grid>
              </Grid>
            ))}

            <Divider sx={{ borderStyle: 'solid', borderColor: 'black', my: 1 }} />

            <Grid container sx={{ mb: 0.5 }}>
              <Grid item xs={6}><Typography variant="body2" fontWeight={700} sx={{ fontSize: '1rem' }}>NET À PAYER</Typography></Grid>
              <Grid item xs={6} textAlign="right"><Typography variant="body2" fontWeight={900} sx={{ fontSize: '1.2rem' }}>{lastPaidCommande.totalTtc.toLocaleString()} F</Typography></Grid>
            </Grid>

            <Grid container sx={{ mb: 2 }}>
              <Grid item xs={6}><Typography variant="caption">Espèces</Typography></Grid>
              <Grid item xs={6} textAlign="right"><Typography variant="caption">{(receivedAmount || lastPaidCommande.totalTtc).toLocaleString()} F</Typography></Grid>
              {receivedAmount && (
                <>
                  <Grid item xs={6}><Typography variant="caption">Rendu</Typography></Grid>
                  <Grid item xs={6} textAlign="right"><Typography variant="caption">{(receivedAmount - lastPaidCommande.totalTtc).toLocaleString()} F</Typography></Grid>
                </>
              )}
            </Grid>

            <Box sx={{ textAlign: 'center', mt: 4 }}>
              <Typography variant="caption" sx={{ display: 'block' }}>Les marchandises vendues ne sont ni reprises ni échangées.</Typography>
              <Typography variant="body2" fontWeight={900} sx={{ mt: 1 }}>MERCI DE VOTRE VISITE !</Typography>
              <Typography variant="caption">Gusto Restaurant System</Typography>
            </Box>
          </Box>
        )}
      </Box>

      <style>
        {`
          @media print {
            @page { margin: 0; size: 80mm auto; }
            body { visibility: hidden; }
            .displayPrint-container { 
              visibility: visible; 
              position: absolute; 
              left: 0; 
              top: 0; 
              width: 100%; 
              background: white;
            }
            .displayPrint-container * { visibility: visible; }
          }
        `}
      </style>
    </Box>
  );
};

export default PaymentInterface;
