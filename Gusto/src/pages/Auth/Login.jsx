import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, TextField, Button, Checkbox, 
  FormControlLabel, Paper, Link, InputAdornment, IconButton,
  CircularProgress, Alert, Stack 
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { loginUser } from '../../features/LoginSlice'; 
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import image1 from '../../images/cx.jpg';

const Login = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  
  const { loading, error, step } = useSelector((state) => state.auth);
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);

useEffect(() => {
  if (step === '2FA') {
    navigate('/confirmation');
  }
}, [step, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleLogin = (e) => {
    e.preventDefault();
    dispatch(loginUser(formData));
  };

  return (
    <Box sx={{ 
      minHeight: '100dvh', // Utilisation de dvh pour mobile (évite les bugs de barre d'adresse)
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center', 
      bgcolor: '#f1f5f9',
      p: { xs: 1, sm: 2, md: 4 } 
    }}>
      <Paper elevation={0} sx={{ 
        display: 'flex', 
        width: '100%', 
        maxWidth: { lg: '1000px', md: '800px' }, 
        minHeight: { xs: 'auto', md: '600px' }, 
        borderRadius: { xs: 4, md: 8 }, 
        overflow: 'hidden',
        flexDirection: { xs: 'column', md: 'row' },
        boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)'
      }}>
        
        {/* SECTION IMAGE - Optimisée pour le chargement */}
        <Box sx={{ 
          width: { xs: '100%', md: '50%' }, 
          height: { xs: '200px', md: 'auto' }, // Visible sur mobile mais plus petit
          position: 'relative',
          bgcolor: '#e2e8f0' // Placeholder pendant le chargement
        }}>
          <Box
            component="img"
            src={image1}
            loading="lazy" // Optimisation navigateur
            alt="Gusto Kitchen"
            sx={{ 
              width: '100%', 
              height: '100%', 
              objectFit: 'cover',
              willChange: 'transform'
            }}
          />
          <Box sx={{
            position: 'absolute', top: 0, left: 0, width: '100%', height: '100%',
            background: 'linear-gradient(to bottom, rgba(99, 102, 241, 0.05), rgba(15, 23, 42, 0.4))'
          }} />
        </Box>

        {/* SECTION FORMULAIRE - Responsive et Rapide */}
        <Box sx={{ 
          width: { xs: '100%', md: '50%' }, 
          p: { xs: 3, sm: 5, lg: 7 }, 
          display: 'flex', 
          flexDirection: 'column',
          bgcolor: 'white',
          opacity: loading ? 0.7 : 1, // Feedback visuel de chargement
          transition: 'opacity 0.2s ease'
        }}>
          
          <Stack spacing={1} sx={{ mb: { xs: 4, md: 6 } }}>
            <Typography variant="h4" sx={{ 
              fontWeight: 900, 
              color: '#1e293b', 
              fontSize: { xs: '1.75rem', md: '2.125rem' },
              letterSpacing: '-0.025em'
            }}>
              Bienvenue
            </Typography>
            <Typography variant="body2" sx={{ color: '#64748b', fontSize: '1rem' }}>
              Accédez à votre tableau de bord <strong>Gusto</strong>
            </Typography>
          </Stack>

          {error && (
            <Alert severity="error" variant="outlined" sx={{ mb: 3, borderRadius: 3, fontWeight: 500 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleLogin} style={{ width: '100%' }}>
            <Stack spacing={2.5}>
              <TextField
                fullWidth
                label="Email"
                name="email"
                type="email"
                required
                value={formData.email}
                onChange={handleChange}
                disabled={loading}
                autoComplete="email"
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 3 } }}
              />

              <TextField
                fullWidth
                label="Mot de passe"
                name="password"
                type={showPassword ? 'text' : 'password'}
                required
                value={formData.password}
                onChange={handleChange}
                disabled={loading}
                autoComplete="current-password"
                sx={{ '& .MuiOutlinedInput-root': { borderRadius: 3 } }}
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton onClick={() => setShowPassword(!showPassword)} edge="end" size="small">
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 1 }}>
                <FormControlLabel
                  control={<Checkbox size="small" sx={{ color: '#6366f1' }} />}
                  label={<Typography sx={{ fontSize: '0.875rem', fontWeight: 500 }}>Rester connecté</Typography>}
                />
                <Link href="#" underline="hover" sx={{ fontSize: '0.875rem', fontWeight: 700, color: '#6366f1' }}>
                  Oublié ?
                </Link>
              </Box>

              <Button
                type="submit"
                fullWidth
                variant="contained"
                disabled={loading}
                sx={{ 
                  py: 2, 
                  borderRadius: 3, 
                  bgcolor: '#1e293b', // Couleur plus pro (Slate 900)
                  fontSize: '1rem',
                  textTransform: 'none',
                  fontWeight: 700,
                  transition: 'all 0.2s',
                  '&:hover': { bgcolor: '#0f172a', transform: 'translateY(-1px)' },
                  '&:active': { transform: 'translateY(0)' }
                }}
              >
                {loading ? <CircularProgress size={24} sx={{ color: 'white' }} /> : 'Se connecter'}
              </Button>
            </Stack>
          </form>

          <Box sx={{ mt: 'auto', pt: 4, textAlign: 'center' }}>
            <Typography variant="caption" sx={{ color: '#94a3b8', fontWeight: 500 }}>
              © 2026 Gusto System
            </Typography>
          </Box>
          
        </Box>
      </Paper>
    </Box>
  );
};

export default Login;