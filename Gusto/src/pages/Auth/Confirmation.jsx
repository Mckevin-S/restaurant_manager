import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { 
  Box, Typography, Button, Avatar, Paper, Grid, 
  IconButton, Divider, Fade, Zoom 
} from '@mui/material';
import BackspaceOutlinedIcon from '@mui/icons-material/BackspaceOutlined';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';

// Simulation de l'utilisateur (à remplacer par votre contexte Auth)
const currentUser = { 
  name: 'Moko Yvan', 
  role: 'DÉVELOPPEUR FULL-STACK', 
  image: 'https://i.pravatar.cc/150?u=yvan' 
};

const Confirmation = () => {
    const [pin, setPin] = useState('');

    const handleNumberClick = (num) => {
        if (pin.length < 4) setPin(prev => prev + num);
    };

    const handleDelete = () => {
        setPin(prev => prev.slice(0, -1));
    };

    

    return (
        <Box sx={{ 
            minHeight: '100vh', 
            background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            p: 3 
        }}>
            <Zoom in={true} style={{ transitionDelay: '100ms' }}>
                <Paper elevation={0} sx={{ 
                    display: 'flex', 
                    flexDirection: { xs: 'column', md: 'row' },
                    width: '100%', 
                    maxWidth: 1050, 
                    borderRadius: 10, 
                    overflow: 'hidden',
                    minHeight: 650,
                    boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)'
                }}>
                    
                    {/* SECTION GAUCHE : IDENTITÉ VISUELLE */}
                    <Box sx={{ 
                        flex: 0.9,
                        p: { xs: 6, md: 10 }, 
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center',
                        background: 'white',
                        position: 'relative'
                    }}>
                        <Box sx={{ position: 'relative' }}>
                            <Avatar 
                                src={currentUser.image} 
                                sx={{ 
                                    width: 150, height: 150, mb: 4, 
                                    boxShadow: '0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04)',
                                    border: '6px solid #f1f5f9'
                                }} 
                            />
                            <Box sx={{ 
                                position: 'absolute', bottom: 40, right: 10,
                                bgcolor: '#10b981', color: 'white', borderRadius: '50%',
                                p: 0.5, display: 'flex', border: '4px solid white'
                            }}>
                                <VerifiedUserIcon sx={{ fontSize: 20 }} />
                            </Box>
                        </Box>

                        <Typography variant="h3" sx={{ fontWeight: 900, color: '#0f172a', mb: 1, letterSpacing: '-0.02em' }}>
                            {currentUser.name}
                        </Typography>
                        <Typography variant="subtitle1" sx={{ color: '#6366f1', mb: 4, fontWeight: 700, letterSpacing: 2 }}>
                            {currentUser.role}
                        </Typography>
                        
                        <Divider sx={{ width: '40%', mb: 4, borderBottomWidth: 2, borderColor: '#f1f5f9' }} />
                        
                        <Typography variant="body2" sx={{ color: '#94a3b8', fontStyle: 'italic' }}>
                            Authentification biométrique sécurisée active
                        </Typography>
                    </Box>

                    {/* SECTION DROITE : INTERFACE DE SAISIE */}
                    <Box sx={{ 
                        flex: 1.1, 
                        p: { xs: 4, md: 8 }, 
                        bgcolor: '#f8fafc', // Gris bleuté très clair pro
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}>
                        <Box sx={{ textAlign: 'center', mb: 6 }}>
                            <Typography variant="h4" sx={{ fontWeight: 800, color: '#1e293b', mb: 1.5 }}>
                                Saisissez votre PIN
                            </Typography>
                            <Typography variant="body1" sx={{ color: '#64748b', maxWidth: 300, mx: 'auto' }}>
                                Entrez votre code d'accès à 4 chiffres pour déverrouiller le tableau de bord.
                            </Typography>
                        </Box>

                        {/* INDICATEURS PIN MODERNISÉS */}
                        <Box sx={{ display: 'flex', gap: 3, mb: 8 }}>
                            {[...Array(4)].map((_, i) => (
                                <Box
                                    key={i}
                                    sx={{
                                        width: 20, height: 20, borderRadius: '6px',
                                        bgcolor: i < pin.length ? '#6366f1' : 'white',
                                        border: '2px solid',
                                        borderColor: i < pin.length ? '#6366f1' : '#e2e8f0',
                                        transition: 'all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275)',
                                        transform: i < pin.length ? 'rotate(45deg) scale(1.1)' : 'rotate(0deg)',
                                        boxShadow: i < pin.length ? '0 0 20px rgba(99, 102, 241, 0.4)' : 'none'
                                    }}
                                />
                            ))}
                        </Box>

                        {/* PAVÉ NUMÉRIQUE ÉPURÉ */}
                        <Grid container spacing={3} sx={{ maxWidth: 380 }}>
                            {[1, 2, 3, 4, 5, 6, 7, 8, 9].map((num) => (
                                <Grid item xs={4} key={num}>
                                    <Button
                                        fullWidth
                                        onClick={() => handleNumberClick(num.toString())}
                                        sx={{
                                            height: 80, borderRadius: 5,
                                            bgcolor: 'white', color: '#1e293b',
                                            fontWeight: 800, fontSize: '1.75rem',
                                            boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)',
                                            '&:hover': { bgcolor: '#eff6ff', transform: 'translateY(-4px)', boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)' },
                                            transition: 'all 0.2s'
                                        }}
                                    >
                                        {num}
                                    </Button>
                                </Grid>
                            ))}
                            <Grid item xs={4}></Grid>
                            <Grid item xs={4}>
                                <Button
                                    fullWidth
                                    onClick={() => handleNumberClick('0')}
                                    sx={{
                                        height: 80, borderRadius: 5,
                                        bgcolor: 'white', color: '#1e293b',
                                        fontWeight: 800, fontSize: '1.75rem',
                                        boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)',
                                        '&:hover': { bgcolor: '#eff6ff', transform: 'translateY(-4px)' }
                                    }}
                                >
                                    0
                                </Button>
                            </Grid>
                            <Grid item xs={4} sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                <IconButton 
                                    onClick={handleDelete} 
                                    sx={{ 
                                        color: '#94a3b8', 
                                        '&:hover': { color: '#ef4444', bgcolor: '#fee2e2' },
                                        transition: '0.3s'
                                    }}
                                >
                                    <BackspaceOutlinedIcon sx={{ fontSize: 35 }} />
                                </IconButton>
                            </Grid>
                        </Grid>

                        {/* CTA DE CONNEXION */}
                        <Button
                            component={Link}
                            to="/dashboard"
                            disabled={pin.length !== 4}
                            variant="contained"
                            sx={{
                                mt: 8, width: '100%', maxWidth: 380, py: 2.5,
                                borderRadius: 5, fontSize: '1.1rem', fontWeight: 800,
                                textTransform: 'none', letterSpacing: 1,
                                bgcolor: '#6366f1',
                                boxShadow: '0 20px 25px -5px rgba(99, 102, 241, 0.3)',
                                '&:hover': { bgcolor: '#4f46e5', transform: 'scale(1.02)' },
                                '&.Mui-disabled': { bgcolor: '#e2e8f0', color: '#94a3b8' },
                                transition: 'all 0.3s'
                            }}
                            endIcon={<ChevronRightIcon sx={{ fontSize: 28 }} />}
                           
                        >
                            Accéder au Dashboard
                        </Button>
                    </Box>
                </Paper>
            </Zoom>
        </Box>
    );
};

export default Confirmation;