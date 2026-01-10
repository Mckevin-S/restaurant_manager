import React, { useState } from 'react';
import { 
  Box, Typography, Paper, TextField, Button, 
  Avatar, Grid, Divider, IconButton, InputAdornment, 
  Stack, Alert, Fade 
} from '@mui/material';
import { useSelector } from 'react-redux';
import PhotoCameraIcon from '@mui/icons-material/PhotoCamera';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import SaveIcon from '@mui/icons-material/Save';

const ProfilePage = () => {
  // On récupère les infos de l'utilisateur depuis le store Redux (auth)
  const { user } = useSelector((state) => state.auth);
  
  const [showPassword, setShowPassword] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Logique d'appel API pour mettre à jour le profil ici
    setSuccess(true);
    setTimeout(() => setSuccess(false), 3000);
  };

  return (
    <Box sx={{ p: { xs: 2, md: 4 }, maxWidth: 800, margin: 'auto' }}>
      <Typography variant="h4" sx={{ fontWeight: 900, mb: 4, color: '#fff' }}>
        Mon Profil
      </Typography>

      {success && (
        <Fade in={success}>
          <Alert severity="success" sx={{ mb: 3, borderRadius: '12px' }}>
            Profil mis à jour avec succès !
          </Alert>
        </Fade>
      )}

      <Grid container spacing={4}>
        {/* Colonne Gauche : Avatar */}
        <Grid item xs={12} md={4} sx={{ textAlign: 'center' }}>
          <Paper elevation={0} sx={{ p: 4, borderRadius: '24px', bgcolor: '#1e293b', border: '1px solid rgba(255,255,255,0.05)' }}>
            <Box sx={{ position: 'relative', display: 'inline-block' }}>
              <Avatar
                sx={{ 
                  width: 120, 
                  height: 120, 
                  fontSize: '3rem', 
                  bgcolor: '#4caf50',
                  boxShadow: '0 0 20px rgba(76, 175, 80, 0.3)'
                }}
              >
                {user?.nom?.charAt(0) || 'U'}
              </Avatar>
              <IconButton 
                sx={{ 
                  position: 'absolute', bottom: 0, right: 0, 
                  bgcolor: '#121212', color: '#4caf50',
                  '&:hover': { bgcolor: '#000' },
                  border: '2px solid #1e293b'
                }}
              >
                <PhotoCameraIcon fontSize="small" />
              </IconButton>
            </Box>
            <Typography variant="h6" sx={{ mt: 2, color: '#fff', fontWeight: 700 }}>
              {user?.nom || 'Utilisateur'}
            </Typography>
            <Typography variant="body2" sx={{ color: '#4caf50', fontWeight: 600, textTransform: 'uppercase' }}>
              {user?.role || 'Rôle'}
            </Typography>
          </Paper>
        </Grid>

        {/* Colonne Droite : Formulaire */}
        <Grid item xs={12} md={8}>
          <Paper elevation={0} sx={{ p: 4, borderRadius: '24px', bgcolor: '#1e293b', border: '1px solid rgba(255,255,255,0.05)' }}>
            <form onSubmit={handleSubmit}>
              <Typography variant="subtitle1" sx={{ color: '#fff', mb: 3, fontWeight: 700 }}>
                Informations Personnelles
              </Typography>
              
              <Stack spacing={3}>
                <TextField
                  fullWidth
                  label="Nom complet"
                  defaultValue={user?.nom}
                  variant="outlined"
                  sx={inputStyles}
                />
                
                <TextField
                  fullWidth
                  label="Email"
                  defaultValue={user?.email}
                  disabled
                  variant="outlined"
                  sx={inputStyles}
                  helperText="L'email ne peut pas être modifié"
                />

                <Divider sx={{ my: 2, borderColor: 'rgba(255,255,255,0.05)' }} />

                <Typography variant="subtitle1" sx={{ color: '#fff', fontWeight: 700 }}>
                  Sécurité
                </Typography>

                <TextField
                  fullWidth
                  label="Nouveau mot de passe"
                  type={showPassword ? 'text' : 'password'}
                  variant="outlined"
                  sx={inputStyles}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton onClick={() => setShowPassword(!showPassword)} sx={{ color: 'rgba(255,255,255,0.5)' }}>
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />

                <Button 
                  type="submit"
                  variant="contained" 
                  startIcon={<SaveIcon />}
                  sx={{ 
                    mt: 2, 
                    bgcolor: '#4caf50', 
                    py: 1.5, 
                    borderRadius: '12px',
                    fontWeight: 700,
                    textTransform: 'none',
                    '&:hover': { bgcolor: '#388e3c' }
                  }}
                >
                  Enregistrer les modifications
                </Button>
              </Stack>
            </form>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

// Styles personnalisés pour les champs de saisie (Dark Mode)
const inputStyles = {
  '& label': { color: 'rgba(255,255,255,0.5)' },
  '& label.Mui-focused': { color: '#4caf50' },
  '& .MuiOutlinedInput-root': {
    color: '#fff',
    '& fieldset': { borderColor: 'rgba(255,255,255,0.1)' },
    '&:hover fieldset': { borderColor: 'rgba(255,255,255,0.3)' },
    '&.Mui-focused fieldset': { borderColor: '#4caf50' },
    borderRadius: '12px'
  },
  '& .MuiFormHelperText-root': { color: 'rgba(255,255,255,0.3)' }
};

export default ProfilePage;