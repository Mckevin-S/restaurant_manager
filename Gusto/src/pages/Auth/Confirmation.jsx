import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { verifyTwoFactor } from '../../features/LoginSlice';
import { 
  Box, 
  Typography, 
  Button, 
  Avatar, 
  Paper, 
  Grid, 
  IconButton, 
  Zoom, 
  CircularProgress, 
  Alert 
} from '@mui/material';
import BackspaceOutlinedIcon from '@mui/icons-material/BackspaceOutlined';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';

const Confirmation = () => {
    const [pin, setPin] = useState('');
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { loading, error, tempUsername, token, user, step } = useSelector((state) => state.auth);

    // --- LOGIQUE DE VALIDATION ---
    const handleVerify = useCallback(() => {
        if (pin.length === 4 && !loading) {
            dispatch(verifyTwoFactor({ 
                username: tempUsername, 
                code: pin 
            }));
        }
    }, [pin, loading, tempUsername, dispatch]);

    const handleDelete = useCallback(() => {
        setPin(prev => prev.slice(0, -1));
    }, []);

    const handleNumberInput = useCallback((num) => {
        setPin(prev => (prev.length < 4 ? prev + num : prev));
    }, []);

    // --- GESTION DU CLAVIER PHYSIQUE ---
    useEffect(() => {
        const handleKeyDown = (event) => {
            // Chiffres 0-9
            if (/^[0-9]$/.test(event.key)) {
                handleNumberInput(event.key);
            } 
            // Effacer
            else if (event.key === 'Backspace') {
                handleDelete();
            } 
            // Valider
            else if (event.key === 'Enter' && pin.length === 4) {
                handleVerify();
            }
        };

        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, [pin, handleNumberInput, handleDelete, handleVerify]);

    // --- REDIRECTIONS ---
    useEffect(() => {
        if (step === 'COMPLETED' && token) {
            localStorage.setItem('role', user?.role);
            navigate('/dashboard', { replace: true });
        }
        if (!tempUsername && step !== 'COMPLETED') {
            navigate('/login', { replace: true });
        }
    }, [step, token, tempUsername, navigate, user]);

    return (
        <Box sx={{ 
            minHeight: '100vh', display: 'flex', alignItems: 'center', 
            justifyContent: 'center', bgcolor: '#0f172a', p: 2 
        }}>
            <Zoom in={true}>
                <Paper elevation={0} sx={{ 
                    display: 'flex', width: '100%', maxWidth: '900px', 
                    minHeight: '600px', borderRadius: '32px', overflow: 'hidden',
                    flexDirection: { xs: 'column', md: 'row' }
                }}>
                    
                    {/* SECTION GAUCHE : INFOS UTILISATEUR */}
                    <Box sx={{ 
                        flex: 1, p: { xs: 4, md: 8 }, display: 'flex', 
                        flexDirection: 'column', bgcolor: 'white' 
                    }}>
                        <Typography variant="h4" sx={{ fontWeight: 800, color: '#1e293b', mb: 1 }}>
                            Vérification
                        </Typography>
                        <Typography variant="body1" sx={{ color: '#64748b', mb: 6 }}>
                            Utilisez votre code PIN pour valider la connexion.
                        </Typography>

                        <Box sx={{
                            display: 'flex', alignItems: 'center', p: 2.5,
                            borderRadius: '20px', border: '2px solid #6366f1',
                            bgcolor: '#f8faff', width: '100%'
                        }}>
                            <Avatar 
                                src={`https://ui-avatars.com/api/?name=${tempUsername}&background=6366f1&color=fff`} 
                                sx={{ width: 60, height: 60, mr: 2, borderRadius: '14px' }} 
                            />
                            <Box>
                                <Typography sx={{ fontWeight: 800, color: '#1e293b', fontSize: '1.1rem' }}>
                                    {tempUsername || "Utilisateur"}
                                </Typography>
                                <Typography variant="caption" sx={{ fontWeight: 700, color: '#94a3b8', textTransform: 'uppercase' }}>
                                    {user?.role || "Accès Sécurisé"}
                                </Typography>
                            </Box>
                        </Box>
                    </Box>

                    {/* SECTION DROITE : INTERFACE PIN */}
                    <Box sx={{ 
                        flex: 1, p: 6, display: 'flex', flexDirection: 'column', 
                        alignItems: 'center', justifyContent: 'center', bgcolor: '#fcfdfe' 
                    }}>
                        
                        {/* INDICATEURS VISUELS */}
                        <Box sx={{ display: 'flex', gap: 2.5, mb: 6 }}>
                            {[...Array(4)].map((_, i) => (
                                <Box key={i} sx={{
                                    width: 16, height: 16, borderRadius: '4px',
                                    bgcolor: i < pin.length ? '#6366f1' : '#e2e8f0',
                                    transform: i < pin.length ? 'rotate(45deg) scale(1.1)' : 'none',
                                    transition: 'all 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275)',
                                    boxShadow: i < pin.length ? '0 4px 12px rgba(99, 102, 241, 0.3)' : 'none'
                                }} />
                            ))}
                        </Box>

                        {error && (
                            <Alert severity="error" sx={{ mb: 3, width: '100%', borderRadius: '12px' }}>
                                {error}
                            </Alert>
                        )}

                        {/* PAVÉ NUMÉRIQUE VIRTUEL */}
                        <Grid container spacing={2} sx={{ maxWidth: 300 }}>
                            {[1, 2, 3, 4, 5, 6, 7, 8, 9, null, 0, 'delete'].map((btn, idx) => (
                                <Grid item xs={4} key={idx} sx={{ display: 'flex', justifyContent: 'center' }}>
                                    {btn === null ? (
                                        <Box sx={{ width: 75 }} />
                                    ) : btn === 'delete' ? (
                                        <IconButton onClick={handleDelete} sx={{ width: 75, height: 65 }}>
                                            <BackspaceOutlinedIcon sx={{ color: '#94a3b8' }} />
                                        </IconButton>
                                    ) : (
                                        <Button
                                            onClick={() => handleNumberInput(btn.toString())}
                                            sx={{ 
                                                width: 75, height: 65, borderRadius: '18px', 
                                                bgcolor: '#f1f5f9', color: '#1e293b',
                                                fontSize: '1.5rem', fontWeight: 800,
                                                '&:hover': { bgcolor: '#e2e8f0' }
                                            }}
                                        >
                                            {btn}
                                        </Button>
                                    )}
                                </Grid>
                            ))}
                        </Grid>

                        <Button
                            fullWidth
                            onClick={handleVerify}
                            disabled={pin.length !== 4 || loading}
                            variant="contained"
                            sx={{ 
                                mt: 6, maxWidth: 300, py: 2, borderRadius: '16px',
                                bgcolor: '#1e293b', color: 'white', fontWeight: 800,
                                textTransform: 'none', fontSize: '1.1rem',
                                boxShadow: '0 10px 15px -3px rgba(30, 41, 59, 0.2)',
                                '&:hover': { bgcolor: '#0f172a' }
                            }}
                        >
                            {loading ? (
                                <CircularProgress size={24} color="inherit" />
                            ) : (
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                    Continuer <ChevronRightIcon />
                                </Box>
                            )}
                        </Button>
                    </Box>
                </Paper>
            </Zoom>
        </Box>
    );
};

export default Confirmation;