import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Paper, TextField, Button,
  Avatar, Grid2 as Grid, Divider, IconButton, InputAdornment,
  Stack, Alert, Fade, CircularProgress, Card, CardContent
} from '@mui/material';
import {
  User, Mail, Phone, Lock, Eye, EyeOff,
  Save, Key, ShieldCheck, Camera, BadgeCheck
} from 'lucide-react';
import apiClient from '../services/apiClient';
import { toast } from 'react-hot-toast';
import PageHeader from '../widget/PageHeader';
import { motion } from 'framer-motion';

const ProfilePage = () => {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [userData, setUserData] = useState(null);
  const [showPassword, setShowPassword] = useState(false);

  // États pour les formulaires
  const [profileForm, setProfileForm] = useState({
    nom: '', prenom: '', telephone: ''
  });
  const [passwordForm, setPasswordForm] = useState({
    oldPassword: '', newPassword: '', confirmPassword: ''
  });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await apiClient.get('/users/me');
      setUserData(response.data);
      setProfileForm({
        nom: response.data.nom || '',
        prenom: response.data.prenom || '',
        telephone: response.data.telephone || ''
      });
    } catch (error) {
      toast.error('Erreur lors de la récupération du profil');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const response = await apiClient.put('/users/me', profileForm);
      setUserData(response.data);
      toast.success('Profil mis à jour avec succès');
    } catch (error) {
      toast.error(error.response?.data?.error || 'Erreur lors de la mise à jour');
    } finally {
      setSaving(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      return toast.error('Les mots de passe ne correspondent pas');
    }
    setSaving(true);
    try {
      await apiClient.put(`/users/${userData.id}/change-password`, {
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      });
      toast.success('Mot de passe modifié avec succès');
      setPasswordForm({ oldPassword: '', newPassword: '', confirmPassword: '' });
    } catch (error) {
      toast.error(error.response?.data?.error || 'Erreur lors du changement de mot de passe');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return (
    <Box sx={{ display: 'flex', height: '100vh', justifyContent: 'center', alignItems: 'center' }}>
      <CircularProgress sx={{ color: '#6366f1' }} />
    </Box>
  );

  return (
    <Box className="animate-in fade-in duration-500 pb-12">
      <PageHeader
        icon={User}
        title="Mon Profil"
        subtitle="Gérez vos informations personnelles et votre sécurité"
      />

      <Box sx={{ px: { xs: 2, md: 6 }, mt: -4 }}>
        <Grid container spacing={4}>
          {/* CARTE RÉSUMÉ & AVATAR */}
          <Grid size={{ xs: 12, lg: 4 }}>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5 }}
            >
              <Card sx={{ borderRadius: '32px', boxShadow: '0 10px 40px rgba(0,0,0,0.03)', border: '1px solid #f1f5f9', overflow: 'hidden' }}>
                <Box sx={{ height: 120, bgcolor: '#6366f1', position: 'relative' }}>
                  <Box sx={{ position: 'absolute', bottom: -40, left: '50%', transform: 'translateX(-50%)' }}>
                    <Box sx={{ position: 'relative' }}>
                      <Avatar
                        sx={{
                          width: 100, height: 100, border: '6px solid #fff',
                          bgcolor: '#f1f5f9', color: '#6366f1', fontWeight: 900, fontSize: '2rem',
                          boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                        }}
                      >
                        {userData?.prenom?.[0]}{userData?.nom?.[0]}
                      </Avatar>
                      <IconButton
                        size="small"
                        sx={{
                          position: 'absolute', bottom: 5, right: 5,
                          bgcolor: '#1e293b', color: '#fff', '&:hover': { bgcolor: '#000' }
                        }}
                      >
                        <Camera size={14} />
                      </IconButton>
                    </Box>
                  </Box>
                </Box>

                <CardContent sx={{ pt: 7, textAlign: 'center', pb: 4 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, mb: 0.5 }}>
                    <Typography variant="h5" sx={{ fontWeight: 900, color: '#1e293b' }}>
                      {userData?.prenom} {userData?.nom}
                    </Typography>
                    <BadgeCheck size={20} className="text-indigo-500" />
                  </Box>
                  <Typography variant="body2" sx={{ color: '#64748b', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '1px' }}>
                    {userData?.role?.replace('ROLE_', '')}
                  </Typography>

                  <Divider sx={{ my: 3, borderStyle: 'dashed' }} />

                  <Stack spacing={2} sx={{ textAlign: 'left', px: 2 }}>
                    <InfoItem icon={<Mail size={16} />} label="Email" value={userData?.email} />
                    <InfoItem icon={<Phone size={16} />} label="Tél" value={userData?.telephone || 'Non renseigné'} />
                  </Stack>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>

          {/* FORMULAIRES DE MODIFICATION */}
          <Grid size={{ xs: 12, lg: 8 }}>
            <Stack spacing={4}>
              {/* ÉDITION INFORMATIONS */}
              <motion.div
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.5, delay: 0.1 }}
              >
                <Paper sx={{ p: 4, borderRadius: '32px', boxShadow: '0 4px 24px rgba(0,0,0,0.02)', border: '1px solid #f1f5f9' }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
                    <Box sx={{ p: 1.5, bgcolor: '#eef2ff', color: '#6366f1', borderRadius: '16px' }}>
                      <User size={24} />
                    </Box>
                    <Box>
                      <Typography variant="h6" sx={{ fontWeight: 900, color: '#1e293b' }}>Informations Personnelles</Typography>
                      <Typography variant="caption" sx={{ color: '#94a3b8', fontWeight: 600 }}>Mettez à jour vos coordonnées principales</Typography>
                    </Box>
                  </Box>

                  <form onSubmit={handleUpdateProfile}>
                    <Grid container spacing={3}>
                      <Grid size={{ xs: 12, md: 6 }}>
                        <CustomInput
                          label="Prénom"
                          value={profileForm.prenom}
                          onChange={(e) => setProfileForm({ ...profileForm, prenom: e.target.value })}
                        />
                      </Grid>
                      <Grid size={{ xs: 12, md: 6 }}>
                        <CustomInput
                          label="Nom"
                          value={profileForm.nom}
                          onChange={(e) => setProfileForm({ ...profileForm, nom: e.target.value })}
                        />
                      </Grid>
                      <Grid size={{ xs: 12, md: 6 }}>
                        <CustomInput
                          label="Téléphone"
                          value={profileForm.telephone}
                          onChange={(e) => setProfileForm({ ...profileForm, telephone: e.target.value })}
                        />
                      </Grid>
                      <Grid size={{ xs: 12, md: 6 }}>
                        <CustomInput
                          label="Email (Fixe)"
                          value={userData?.email}
                          disabled
                        />
                      </Grid>
                    </Grid>
                    <Box sx={{ mt: 4, display: 'flex', justifyContent: 'flex-end' }}>
                      <Button
                        type="submit"
                        variant="contained"
                        disabled={saving}
                        startIcon={saving ? <CircularProgress size={20} color="inherit" /> : <Save size={18} />}
                        sx={primaryBtnStyles}
                      >
                        Enregistrer les modifications
                      </Button>
                    </Box>
                  </form>
                </Paper>
              </motion.div>

              {/* ÉDITION SÉCURITÉ */}
              <motion.div
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.5, delay: 0.2 }}
              >
                <Paper sx={{ p: 4, borderRadius: '32px', boxShadow: '0 4px 24px rgba(0,0,0,0.02)', border: '1px solid #f1f5f9' }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 4 }}>
                    <Box sx={{ p: 1.5, bgcolor: '#fff1f2', color: '#f43f5e', borderRadius: '16px' }}>
                      <ShieldCheck size={24} />
                    </Box>
                    <Box>
                      <Typography variant="h6" sx={{ fontWeight: 900, color: '#1e293b' }}>Sécurité</Typography>
                      <Typography variant="caption" sx={{ color: '#94a3b8', fontWeight: 600 }}>Gérez votre mot de passe et vos accès</Typography>
                    </Box>
                  </Box>

                  <form onSubmit={handleChangePassword}>
                    <Grid container spacing={3}>
                      <Grid size={{ xs: 12 }}>
                        <CustomInput
                          label="Ancien mot de passe"
                          type="password"
                          value={passwordForm.oldPassword}
                          onChange={(e) => setPasswordForm({ ...passwordForm, oldPassword: e.target.value })}
                        />
                      </Grid>
                      <Grid size={{ xs: 12, md: 6 }}>
                        <CustomInput
                          label="Nouveau mot de passe"
                          type="password"
                          value={passwordForm.newPassword}
                          onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                        />
                      </Grid>
                      <Grid size={{ xs: 12, md: 6 }}>
                        <CustomInput
                          label="Confirmer le nouveau"
                          type="password"
                          value={passwordForm.confirmPassword}
                          onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                        />
                      </Grid>
                    </Grid>
                    <Box sx={{ mt: 4, display: 'flex', justifyContent: 'flex-end' }}>
                      <Button
                        type="submit"
                        variant="contained"
                        disabled={saving}
                        startIcon={saving ? <CircularProgress size={20} color="inherit" /> : <Key size={18} />}
                        sx={secondaryBtnStyles}
                      >
                        Mettre à jour le mot de passe
                      </Button>
                    </Box>
                  </form>
                </Paper>
              </motion.div>
            </Stack>
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
};

// Composants utilitaires locaux
const InfoItem = ({ icon, label, value }) => (
  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
    <Box sx={{ color: '#94a3b8' }}>{icon}</Box>
    <Box>
      <Typography variant="caption" sx={{ color: '#94a3b8', fontWeight: 700, display: 'block' }}>{label}</Typography>
      <Typography variant="body2" sx={{ color: '#1e293b', fontWeight: 700 }}>{value}</Typography>
    </Box>
  </Box>
);

const CustomInput = ({ label, type = 'text', value, onChange, disabled }) => {
  const [show, setShow] = useState(false);
  const isPassword = type === 'password';

  return (
    <Box sx={{ spaceY: 1 }}>
      <Typography variant="caption" sx={{ ml: 1, fontWeight: 800, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
        {label}
      </Typography>
      <TextField
        fullWidth
        type={isPassword ? (show ? 'text' : 'password') : type}
        value={value}
        onChange={onChange}
        disabled={disabled}
        variant="outlined"
        InputProps={{
          endAdornment: isPassword && (
            <InputAdornment position="end">
              <IconButton onClick={() => setShow(!show)} size="small" sx={{ color: '#94a3b8' }}>
                {show ? <EyeOff size={18} /> : <Eye size={18} />}
              </IconButton>
            </InputAdornment>
          ),
          sx: {
            borderRadius: '16px',
            bgcolor: disabled ? '#f8fafc' : '#fff',
            fontWeight: 700,
            color: '#1e293b',
            '& fieldset': { borderColor: '#e2e8f0' },
            '&:hover fieldset': { borderColor: '#cbd5e1' },
            '&.Mui-focused fieldset': { borderColor: '#6366f1', borderWidth: '2px' },
            transition: 'all 0.2s',
          }
        }}
        sx={{ mt: 0.5 }}
      />
    </Box>
  );
};

const primaryBtnStyles = {
  bgcolor: '#1e293b',
  color: '#fff',
  px: 4, py: 1.5,
  borderRadius: '16px',
  fontWeight: 800,
  textTransform: 'none',
  boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
  '&:hover': { bgcolor: '#0f172a', transform: 'translateY(-1px)' },
  '&:active': { transform: 'scale(0.98)' },
  transition: 'all 0.2s'
};

const secondaryBtnStyles = {
  bgcolor: '#fff',
  color: '#1e293b',
  border: '2px solid #e2e8f0',
  px: 4, py: 1.5,
  borderRadius: '16px',
  fontWeight: 800,
  textTransform: 'none',
  boxShadow: 'none',
  '&:hover': { bgcolor: '#f8fafc', borderColor: '#cbd5e1' },
  '&:active': { transform: 'scale(0.98)' },
  transition: 'all 0.2s'
};

export default ProfilePage;
