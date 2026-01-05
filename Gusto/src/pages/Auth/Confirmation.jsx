import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { verifyTwoFactor } from '../../features/LoginSlice'; // Ajustez le chemin
import { 
  Box, Typography, Button, Avatar, Paper, Grid, 
  IconButton, Divider, Zoom, CircularProgress, Alert 
} from '@mui/material';
import BackspaceOutlinedIcon from '@mui/icons-material/BackspaceOutlined';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
// ... vos autres imports d'icônes

const Confirmation = () => {
    const [pin, setPin] = useState('');
    const dispatch = useDispatch();
    const navigate = useNavigate();

    // On récupère les infos du store Redux
    const { loading, error, tempUsername, token } = useSelector((state) => state.auth);

    const {user} = useSelector((state)=> state.auth)

    localStorage.setItem('role',user?.role);

    // Redirection automatique une fois le token reçu
    useEffect(() => {
        if (token) {
            navigate('/dashboard');
        }
    }, [token, navigate]);

    const handleNumberClick = (num) => {
        if (pin.length < 4) setPin(prev => prev + num);
    };

    const handleDelete = () => {
        setPin(prev => prev.slice(0, -1));
    };

    // Fonction de validation
    const handleVerify = () => {
        if (pin.length === 4) {
            dispatch(verifyTwoFactor({ 
                username: tempUsername, // Le nom stocké lors du login
                code: pin 
            }));
        }
    };

    return (
        <Box sx={{ /* ... vos styles existants ... */ }}>
            <Zoom in={true}>
                <Paper elevation={0} sx={{ /* ... vos styles ... */ }}>
                    
                    {/* SECTION GAUCHE : IDENTITÉ DYNAMIQUE */}
                    <Box sx={{ flex: 0.9, p: 10, display: 'flex', flexDirection: 'column', alignItems: 'center', background: 'white' }}>
                        <Avatar 
                            // Vous pouvez adapter l'image selon l'utilisateur si besoin
                            src={`https://ui-avatars.com/api/?name=${tempUsername}&background=6366f1&color=fff`} 
                            sx={{ width: 150, height: 150, mb: 4 }} 
                        />
                        <Typography variant="h3" sx={{ fontWeight: 900, mb: 1 }}>
                            {tempUsername || "Utilisateur"}
                        </Typography>
                        {/* ... reste de la section gauche ... */}
                    </Box>

                    {/* SECTION DROITE : INTERFACE DE SAISIE */}
                    <Box sx={{ flex: 1.1, p: 8, bgcolor: '#f8fafc', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                        
                        {/* Affichage des erreurs du backend */}
                        {error && (
                            <Alert severity="error" sx={{ mb: 2, borderRadius: 3 }}>
                                {error}
                            </Alert>
                        )}

                        <Box sx={{ textAlign: 'center', mb: 6 }}>
                            <Typography variant="h4" sx={{ fontWeight: 800, mb: 1.5 }}>
                                Validation SMS
                            </Typography>
                            <Typography variant="body1" sx={{ color: '#64748b' }}>
                                Entrez le code envoyé au téléphone associé au compte de <strong>{tempUsername}</strong>.
                            </Typography>
                        </Box>

                        {/* INDICATEURS PIN */}
                        <Box sx={{ display: 'flex', gap: 3, mb: 8 }}>
                            {[...Array(4)].map((_, i) => (
                                <Box key={i} sx={{
                                    width: 20, height: 20, borderRadius: '6px',
                                    bgcolor: i < pin.length ? '#6366f1' : 'white',
                                    border: '2px solid',
                                    borderColor: i < pin.length ? '#6366f1' : '#e2e8f0',
                                    transition: 'all 0.3s',
                                    transform: i < pin.length ? 'rotate(45deg)' : 'none',
                                }} />
                            ))}
                        </Box>

                        {/* PAVÉ NUMÉRIQUE */}
                        <Grid container spacing={3} sx={{ maxWidth: 380 }}>
                            {[1, 2, 3, 4, 5, 6, 7, 8, 9, 'empty', 0, 'delete'].map((btn, idx) => (
                                <Grid item xs={4} key={idx}>
                                    {btn === 'empty' ? null : btn === 'delete' ? (
                                        <IconButton onClick={handleDelete} sx={{ width: '100%', height: 80 }}>
                                            <BackspaceOutlinedIcon sx={{ fontSize: 35 }} />
                                        </IconButton>
                                    ) : (
                                        <Button
                                            fullWidth
                                            onClick={() => handleNumberClick(btn.toString())}
                                            sx={{ height: 80, borderRadius: 5, bgcolor: 'white', fontWeight: 800, fontSize: '1.75rem' }}
                                        >
                                            {btn}
                                        </Button>
                                    )}
                                </Grid>
                            ))}
                        </Grid>

                        {/* BOUTON DE VALIDATION */}
                        <Button
                            onClick={handleVerify}
                            disabled={pin.length !== 4 || loading}
                            variant="contained"
                            sx={{ mt: 8, width: '100%', maxWidth: 380, py: 2.5, borderRadius: 5 }}
                        >
                            {loading ? <CircularProgress size={24} color="inherit" /> : "Valider le code"}
                        </Button>
                    </Box>
                </Paper>
            </Zoom>
        </Box>
    );
};

export default Confirmation;