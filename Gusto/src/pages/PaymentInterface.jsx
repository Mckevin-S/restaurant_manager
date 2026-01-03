import React, { useState } from 'react';
import { 
  Box, Typography, Paper, Grid, Button, Divider, 
  IconButton, TextField, InputAdornment, Container, alpha
} from '@mui/material';
import PaymentsIcon from '@mui/icons-material/Payments';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import SmartphoneIcon from '@mui/icons-material/Smartphone';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

const PaymentInterface = () => {
  const [method, setMethod] = useState('cash');
  const [receivedAmount, setReceivedAmount] = useState('');
  
  const subtotal = 48.00;
  const tax = 9.12;
  const total = subtotal + tax;
  const change = receivedAmount > total ? receivedAmount - total : 0;

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      background: 'linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)',
      py: { xs: 2, md: 6 } 
    }}>
      <Container maxWidth="lg">
        
        {/* Header - Plus épuré */}
        <Box sx={{ mb: 5, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <IconButton sx={{ bgcolor: 'white', '&:hover': { bgcolor: '#f1f5f9' }, boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
              <ArrowBackIosNewIcon fontSize="small" sx={{ color: '#64748b' }} />
            </IconButton>
            <Box>
              <Typography variant="h4" sx={{ fontWeight: 900, color: '#0f172a', letterSpacing: '-0.02em' }}>
                Règlement
              </Typography>
              <Typography variant="body2" sx={{ color: '#64748b', fontWeight: 500 }}>
                Commande #ORD-2026-882 • Table 04
              </Typography>
            </Box>
          </Box>
        </Box>

        <Grid container spacing={4}>
          {/* GAUCHE : Configuration du paiement */}
          <Grid item xs={12} md={7.5}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
              
              <Paper sx={{ p: 4, borderRadius: 8, boxShadow: '0 20px 50px rgba(0,0,0,0.04)', border: '1px solid rgba(255,255,255,0.8)' }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 3, color: '#1e293b', display: 'flex', alignItems: 'center', gap: 1 }}>
                  Mode de règlement
                </Typography>

                <Grid container spacing={2}>
                  {[
                    { id: 'cash', label: 'Espèces', icon: <PaymentsIcon fontSize="large" />, color: '#10b981' },
                    { id: 'card', label: 'Carte Bancaire', icon: <CreditCardIcon fontSize="large" />, color: '#3b82f6' },
                    { id: 'momo', label: 'Mobile Money', icon: <SmartphoneIcon fontSize="large" />, color: '#f59e0b' }
                  ].map((item) => (
                    <Grid item xs={4} key={item.id}>
                      <Button
                        fullWidth
                        onClick={() => setMethod(item.id)}
                        sx={{ 
                          flexDirection: 'column', py: 3, borderRadius: 6,
                          border: '2px solid',
                          borderColor: method === item.id ? item.color : '#f1f5f9',
                          bgcolor: method === item.id ? alpha(item.color, 0.05) : 'white',
                          color: method === item.id ? item.color : '#64748b',
                          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                          '&:hover': { borderColor: item.color, bgcolor: alpha(item.color, 0.02) },
                          transform: method === item.id ? 'scale(1.02)' : 'scale(1)',
                        }}
                      >
                        {item.icon}
                        <Typography variant="caption" sx={{ mt: 1.5, fontWeight: 800, textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                          {item.label}
                        </Typography>
                        {method === item.id && <CheckCircleIcon sx={{ position: 'absolute', top: 10, right: 10, fontSize: 18 }} />}
                      </Button>
                    </Grid>
                  ))}
                </Grid>

                <Divider sx={{ my: 4, opacity: 0.6 }} />

                <Box sx={{ p: 3, bgcolor: '#f8fafc', borderRadius: 6, border: '1px solid #e2e8f0' }}>
                  <Typography variant="subtitle2" sx={{ fontWeight: 800, mb: 2, color: '#475569' }}>
                    {method === 'cash' ? "Calcul de la monnaie" : "Validation transaction"}
                  </Typography>
                  <Grid container spacing={3} alignItems="center">
                    <Grid item xs={12} sm={6}>
                      <TextField
                        fullWidth
                        label="Montant encaissé"
                        type="number"
                        value={receivedAmount}
                        onChange={(e) => setReceivedAmount(e.target.value)}
                        InputProps={{
                          startAdornment: <InputAdornment position="start">$</InputAdornment>,
                          sx: { borderRadius: 4, height: 64, bgcolor: 'white', fontSize: '1.2rem', fontWeight: 700 }
                        }}
                      />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <Box sx={{ textAlign: { sm: 'right' } }}>
                        <Typography variant="caption" sx={{ color: '#64748b', fontWeight: 700, display: 'block' }}>
                          MONNAIE À RENDRE
                        </Typography>
                        <Typography variant="h4" sx={{ fontWeight: 900, color: change > 0 ? '#10b981' : '#cbd5e1' }}>
                          ${change.toFixed(2)}
                        </Typography>
                      </Box>
                    </Grid>
                  </Grid>
                </Box>
              </Paper>
            </Box>
          </Grid>

          {/* DROITE : Récapitulatif Style Facture */}
          <Grid item xs={12} md={4.5}>
            <Paper sx={{ 
              p: 0, borderRadius: 8, overflow: 'hidden', 
              boxShadow: '0 30px 60px rgba(15, 23, 42, 0.15)',
              bgcolor: '#1e293b', color: 'white' 
            }}>
              {/* Top Banner */}
              <Box sx={{ p: 4, bgcolor: '#334155', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                <Typography variant="h6" sx={{ fontWeight: 800, display: 'flex', alignItems: 'center', gap: 1.5 }}>
                  <ReceiptLongIcon sx={{ color: '#818cf8' }} /> Ticket de caisse
                </Typography>
              </Box>

              <Box sx={{ p: 4 }}>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography sx={{ color: '#94a3b8', fontWeight: 500 }}>Sous-total</Typography>
                    <Typography sx={{ fontWeight: 600 }}>${subtotal.toFixed(2)}</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography sx={{ color: '#94a3b8', fontWeight: 500 }}>TVA (19.2%)</Typography>
                    <Typography sx={{ fontWeight: 600 }}>${tax.toFixed(2)}</Typography>
                  </Box>
                  
                  <Box sx={{ py: 2, my: 1, borderTop: '2px dashed rgba(255,255,255,0.1)', borderBottom: '2px dashed rgba(255,255,255,0.1)' }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Typography variant="h5" sx={{ fontWeight: 900, letterSpacing: -1 }}>TOTAL DÛ</Typography>
                      <Typography variant="h4" sx={{ fontWeight: 900, color: '#818cf8' }}>
                        ${total.toFixed(2)}
                      </Typography>
                    </Box>
                  </Box>

                  <Box sx={{ mb: 2 }}>
                    <Typography variant="caption" sx={{ color: '#64748b', fontWeight: 800, letterSpacing: '0.1em', display: 'block', mb: 1.5 }}>
                      DÉTAILS DES ARTICLES
                    </Typography>
                    {[
                      { n: 'Signature Cocktail', p: '14.00' },
                      { n: 'Wagyu Burger Special', p: '34.00' }
                    ].map((item, idx) => (
                      <Box key={idx} sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                        <Typography variant="body2" sx={{ color: '#cbd5e1' }}>1x {item.n}</Typography>
                        <Typography variant="body2" sx={{ fontWeight: 600 }}>${item.p}</Typography>
                      </Box>
                    ))}
                  </Box>

                  <Button
                    fullWidth
                    variant="contained"
                    size="large"
                    sx={{ 
                      mt: 2, py: 2.5, borderRadius: 5, 
                      bgcolor: '#6366f1', 
                      fontWeight: 900,
                      textTransform: 'uppercase', 
                      letterSpacing: '0.1em',
                      fontSize: '0.95rem',
                      boxShadow: '0 12px 24px rgba(99, 102, 241, 0.4)',
                      '&:hover': { bgcolor: '#4f46e5', transform: 'translateY(-2px)' },
                      transition: 'all 0.2s'
                    }}
                  >
                    Valider l'encaissement
                  </Button>

                  <Typography variant="caption" sx={{ textAlign: 'center', color: '#475569', mt: 2, fontWeight: 500 }}>
                    Un reçu numérique sera généré automatiquement après validation.
                  </Typography>
                </Box>
              </Box>
            </Paper>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
};

export default PaymentInterface;