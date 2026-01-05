import React, { useState, useEffect } from 'react'; // Ajout de useEffect
import { 
  Box, Typography, TextField, Button, Checkbox, 
  FormControlLabel, Paper, Link, InputAdornment, IconButton,
  CircularProgress, Alert // Ajout de composants pour le feedback
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux'; // Hooks Redux
import { loginUser } from '../../features/LoginSlice'; 
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import image1 from '../../images/cx.jpg';

const Login = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  
  // Lecture de l'état depuis le slice
  const { loading, error, token } = useSelector((state) => state.auth);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState(''); // Ajout de l'état pour le mot de passe
  const [showPassword, setShowPassword] = useState(false);

  // Redirection automatique si le login réussit
  useEffect(() => {
    if (token) {
      navigate('/confirmation');
    }
  }, [token, navigate]);

  const handleLogin = (e) => {
    e.preventDefault();
    // Appel de l'action asynchrone du slice
    dispatch(loginUser({ email, password }));
  };

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center', 
      bgcolor: '#f8fafc',
      p: { xs: 2, md: 4 }
    }}>
      <Paper elevation={24} sx={{ 
        display: 'flex', 
        width: '100%', 
        maxWidth: 1000, 
        minHeight: { xs: 'auto', lg: 650 }, 
        borderRadius: { xs: 4, md: 6 }, 
        overflow: 'hidden',
        flexDirection: { xs: 'column', lg: 'row' }
      }}>
        
        {/* Section Image */}
        <Box sx={{ 
          width: { xs: '100%', lg: '50%' }, 
          display: { xs: 'none', lg: 'block' },
          position: 'relative'
        }}>
          <Box
            component="img"
            src={image1}
            alt="Authentication background"
            sx={{ width: '100%', height: '100%', objectFit: 'cover' }}
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
            <Typography variant="h4" sx={{ fontWeight: 900, color: '#1e293b', letterSpacing: -1 }}>
              Bienvenue
            </Typography>
            <Typography variant="body2" sx={{ color: '#64748b', mt: 1 }}>
              Connectez-vous pour gérer vos commandes
            </Typography>
          </Box>

          {/* Affichage de l'erreur si elle existe */}
          {error && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleLogin}>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: { xs: 2, md: 3 } }}>
              <TextField
                fullWidth
                label="Adresse Email"
                variant="outlined"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="chef@gusto.com"
                InputProps={{ sx: { borderRadius: 3 } }}
                disabled={loading}
              />

              <TextField
                fullWidth
                label="Mot de passe"
                type={showPassword ? 'text' : 'password'}
                variant="outlined"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
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

              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 1 }}>
                <FormControlLabel
                  control={<Checkbox size="small" sx={{ color: '#6366f1' }} />}
                  label={<Typography variant="caption" sx={{ fontWeight: 600 }}>Rester connecté</Typography>}
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
                disabled={loading} // Désactivé pendant le chargement
                sx={{ 
                  py: 1.8, 
                  borderRadius: 3, 
                  bgcolor: '#6366f1',
                  textTransform: 'none',
                  fontWeight: 700,
                  '&:hover': { bgcolor: '#4f46e5' }
                }}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : 'Se connecter'}
              </Button>
            </Box>
          </form>

          <Typography variant="caption" sx={{ mt: 'auto', textAlign: 'center', color: '#94a3b8', pt: 2 }}>
            © 2026 Gusto System — Développé par Moko Yvan
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
};

export default Login;