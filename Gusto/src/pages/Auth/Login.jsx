import React, { useState } from 'react';
import { 
  Box, 
  Typography, 
  TextField, 
  Button, 
  Checkbox, 
  FormControlLabel, 
  Paper, 
  Link,
  InputAdornment,
  IconButton
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import image1 from '../../images/cx.jpg';

const Login = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const handleLogin = (e) => {
    e.preventDefault();
    
    let userRole = ''; 
    if (email.includes('cuisinier')) userRole = 'cuisinier';
    if (email.includes('admin')) userRole = 'admin';
    if (email.includes('server')) userRole = 'serveur';

    
    localStorage.setItem('user', userRole);

    navigate('/confirmation');
  };

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center', 
      bgcolor: '#f8fafc',
      p: { xs: 2, md: 4 } // Padding adaptatif
    }}>
      <Paper elevation={24} sx={{ 
        display: 'flex', 
        width: '100%', 
        maxWidth: 1000, 
        // Hauteur automatique sur mobile, fixe sur desktop
        minHeight: { xs: 'auto', lg: 650 }, 
        borderRadius: { xs: 4, md: 6 }, 
        overflow: 'hidden',
        flexDirection: { xs: 'column', lg: 'row' } // Empilement sur mobile
      }}>
        
        {/* Section Image */}
        <Box sx={{ 
          width: { xs: '100%', lg: '50%' }, 
          display: { xs: 'none', lg: 'block' }, // Caché sur mobile pour focus sur le form
          position: 'relative',
          height: { xs: 200, lg: 'auto' }
        }}>
          <Box
            component="img"
            src={image1}
            alt="Authentication background"
            sx={{ 
              width: '100%', 
              height: '100%', 
              objectFit: 'cover' // Correction de objectCover
            }}
          />
        </Box>

        {/* Section Formulaire */}
        <Box sx={{ 
          width: { xs: '100%', lg: '50%' }, 
          p: { xs: 3, sm: 6, lg: 8 }, 
          display: 'flex', 
          flexDirection: 'column', 
          justifyContent: 'center',
          bgcolor: 'white'
        }}>
          <Box sx={{ mb: { xs: 3, md: 5 } }}>
            <Typography variant="h4" sx={{ 
              fontWeight: 900, 
              color: '#1e293b', 
              letterSpacing: -1,
              fontSize: { xs: '1.75rem', md: '2.125rem' } // Taille de police adaptative
            }}>
              Bienvenue
            </Typography>
            <Typography variant="body2" sx={{ color: '#64748b', mt: 1 }}>
              Connectez-vous pour gérer vos commandes
            </Typography>
          </Box>

          <form onSubmit={handleLogin}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: { xs: 2, md: 3 } }}>
              <TextField
                fullWidth
                label="Adresse Email"
                variant="outlined"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="chef@gusto.com"
                InputProps={{ sx: { borderRadius: 3 } }}
              />

              <TextField
                fullWidth
                label="Mot de passe"
                type={showPassword ? 'text' : 'password'}
                variant="outlined"
                required
                InputProps={{
                  sx: { borderRadius: 3 },
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <Box sx={{ 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'space-between',
                flexWrap: 'wrap',
                gap: 1
              }}>
                <FormControlLabel
                  control={<Checkbox size="small" sx={{ color: '#6366f1', '&.Mui-checked': { color: '#6366f1' } }} />}
                  label={<Typography variant="caption" sx={{ fontWeight: 600, color: '#64748b' }}>Rester connecté</Typography>}
                />
                <Link href="#" underline="none" sx={{ fontSize: '0.75rem', fontWeight: 700, color: '#6366f1' }}>
                  Oublié ?
                </Link>
              </Box>

              <Button
                type="submit"
                fullWidth
                variant="contained"
                disableElevation
                sx={{ 
                  py: 1.8, 
                  borderRadius: 3, 
                  bgcolor: '#6366f1',
                  textTransform: 'none',
                  fontWeight: 700,
                  fontSize: '1rem',
                  '&:hover': { bgcolor: '#4f46e5' },
                  mt: 1
                }}
              >
                Se connecter
              </Button>
            </Box>
          </form>

          <Typography variant="caption" sx={{ 
            mt: { xs: 4, lg: 'auto' }, 
            textAlign: 'center', 
            color: '#94a3b8', 
            pt: 2 
          }}>
            © 2026 Gusto System — Développé par Moko Yvan
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
};

export default Login;