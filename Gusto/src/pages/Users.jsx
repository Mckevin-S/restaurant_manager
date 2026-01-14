import React, { useState, useEffect, useContext } from 'react';
import {
  Box, Typography, Button, Paper, Stack, IconButton,
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, MenuItem, Chip, CircularProgress,
  Grid2 as Grid, Avatar, InputAdornment, Card, CardContent
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import EmailIcon from '@mui/icons-material/Email';
import PhoneIcon from '@mui/icons-material/Phone';
import BadgeIcon from '@mui/icons-material/Badge';
import SideBar from '../widget/SideBar';
import apiClient from '../services/apiClient';
import RestaurantContext from '../context/RestaurantContext';
import { toast } from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';

const Users = () => {
  const navigate = useNavigate();
  const userRole = localStorage.getItem('role');

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [openModal, setOpenModal] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [formData, setFormData] = useState({
    nom: '', prenom: '', email: '', motDePasse: '', role: 'SERVEUR', telephone: ''
  });

  const { restaurantInfo } = useContext(RestaurantContext);

  useEffect(() => { fetchUsers(); }, []);

  const fetchUsers = async () => {
    try {
      const response = await apiClient.get('/users');
      setUsers(response.data);
    } catch (error) {
      toast.error('Erreur de chargement');
    } finally { setLoading(false); }
  };

  const filteredUsers = users.filter(u => 
    `${u.prenom} ${u.nom}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getRoleStyle = (role) => {
    switch (role) {
      case 'MANAGER': return { color: '#3b82f6', label: 'Manager' };
      case 'CUISINIER': return { color: '#f59e0b', label: 'Cuisine' };
      default: return { color: '#10b981', label: 'Serveur' };
    }
  };

  const handleOpenModal = (user = null) => {
    if (user) {
      setSelectedUser(user);
      setFormData({ ...user, motDePasse: '' });
    } else {
      setSelectedUser(null);
      setFormData({ nom: '', prenom: '', email: '', motDePasse: '', role: 'SERVEUR', telephone: '' });
    }
    setOpenModal(true);
  };

  const handleSaveUser = async () => {
    try {
      if (selectedUser) {
        const { id, ...updateData } = formData;
        if (!updateData.motDePasse) delete updateData.motDePasse;
        await apiClient.put(`/users/${selectedUser.id}`, updateData);
        toast.success('Profil mis à jour');
      } else {
        await apiClient.post('/users', formData);
        toast.success('Utilisateur créé');
      }
      fetchUsers();
      setOpenModal(false);
    } catch (e) { toast.error('Erreur de sauvegarde'); }
  };

  if (loading) return (
    <Box sx={{ display: 'flex', height: '100vh', justifyContent: 'center', alignItems: 'center' }}>
      <CircularProgress />
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', bgcolor: '#F8F9FA', minHeight: '100vh' }}>
      {/* <SideBar userRole={userRole} onLogout={() => navigate('/login')} /> */}

      <Box component="main" sx={{ flexGrow: 1, p: { xs: 2, md: 4 }, width: "100%" }}>
        
        {/* HEADER */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4, alignItems: 'flex-start' }}>
          <Box>
              <Typography variant="h4" sx={{ fontWeight: 800, color: '#1A1C1E' }}>{`Équipe ${ restaurantInfo?.nom || 'du Restaurant'}`}</Typography>
              <Typography variant="body2" color="text.secondary">Gérez les membres de votre personnel et leurs permissions</Typography>
            </Box>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => handleOpenModal()}
            sx={{ borderRadius: 3, px: 3, py: 1, textTransform: 'none', bgcolor: '#1A1C1E', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}
          >
            Ajouter un membre
          </Button>
        </Box>

        {/* QUICK STATS */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <StatMiniCard label="Total Personnel" value={users.length} icon={<BadgeIcon color="primary" />} />
          <StatMiniCard label="Managers" value={users.filter(u => u.role === 'MANAGER').length} icon={<BadgeIcon sx={{ color: '#3b82f6' }} />} />
          <StatMiniCard label="Cuisiniers" value={users.filter(u => u.role === 'CUISINIER').length} icon={<BadgeIcon sx={{ color: '#f59e0b' }} />} />
          <StatMiniCard label="Serveurs" value={users.filter(u => u.role === 'SERVEUR').length} icon={<BadgeIcon sx={{ color: '#10b981' }} />} />
        </Grid>

        {/* SEARCH BAR */}
        <TextField
          fullWidth
          placeholder="Rechercher par nom, email..."
          variant="outlined"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          sx={{ mb: 4, bgcolor: 'white', borderRadius: 3, '& .MuiOutlinedInput-notchedOutline': { border: 'none' }, boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}
          InputProps={{
            startAdornment: <InputAdornment position="start"><SearchIcon color="disabled" /></InputAdornment>,
          }}
        />

        {/* USER GRID */}
        <Grid container spacing={3}>
          {filteredUsers.map((user) => (
            <Grid size={{ xs: 12, sm: 6, lg: 4 }} key={user.id}>
              <UserCard 
                user={user} 
                style={getRoleStyle(user.role)} 
                onEdit={() => handleOpenModal(user)} 
                onDelete={() => { setSelectedUser(user); setOpenDeleteDialog(true); }}
              />
            </Grid>
          ))}
        </Grid>

        {/* MODALS (Identiques mais avec un style plus propre) */}
        {/* ... Gardez vos composants Dialog mais assurez-vous qu'ils utilisent les mêmes borderRadius: 4 ... */}
        <Dialog open={openModal} onClose={() => setOpenModal(false)} fullWidth maxWidth="sm" PaperProps={{ sx: { borderRadius: 4 } }}>
            <DialogTitle sx={{ fontWeight: 800, pt: 3 }}>{selectedUser ? 'Modifier le profil' : 'Nouveau membre'}</DialogTitle>
            <DialogContent>
                <Stack spacing={2} sx={{ mt: 2 }}>
                    <Stack direction="row" spacing={2}>
                        <TextField fullWidth label="Prénom" value={formData.prenom} onChange={(e) => setFormData({...formData, prenom: e.target.value})} />
                        <TextField fullWidth label="Nom" value={formData.nom} onChange={(e) => setFormData({...formData, nom: e.target.value})} />
                    </Stack>
                    <TextField fullWidth label="Email" type="email" value={formData.email} onChange={(e) => setFormData({...formData, email: e.target.value})} />
                    <TextField fullWidth label="Téléphone" value={formData.telephone} onChange={(e) => setFormData({...formData, telephone: e.target.value})} />
                    <TextField select fullWidth label="Rôle" value={formData.role} onChange={(e) => setFormData({...formData, role: e.target.value})}>
                        <MenuItem value="SERVEUR">Serveur</MenuItem>
                        <MenuItem value="CUISINIER">Cuisinier</MenuItem>
                        <MenuItem value="MANAGER">Manager</MenuItem>
                    </TextField>
                    {!selectedUser && <TextField fullWidth label="Mot de passe" type="password" onChange={(e) => setFormData({...formData, motDePasse: e.target.value})} />}
                </Stack>
            </DialogContent>
            <DialogActions sx={{ p: 3 }}>
                <Button onClick={() => setOpenModal(false)} sx={{ color: '#757575' }}>Annuler</Button>
                <Button onClick={handleSaveUser} variant="contained" sx={{ bgcolor: '#1A1C1E', borderRadius: 2, px: 4 }}>Enregistrer</Button>
            </DialogActions>
        </Dialog>

        {/* Delete Confirmation */}
        <Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)} PaperProps={{ sx: { borderRadius: 3 } }}>
          <DialogTitle>Supprimer {selectedUser?.prenom} ?</DialogTitle>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => setOpenDeleteDialog(false)}>Annuler</Button>
            <Button onClick={async () => {
                await apiClient.delete(`/users/${selectedUser.id}`);
                fetchUsers();
                setOpenDeleteDialog(false);
                toast.success('Utilisateur supprimé');
            }} color="error" variant="contained">Supprimer</Button>
          </DialogActions>
        </Dialog>

      </Box>
    </Box>
  );
};

// --- SUBCOMPONENTS ---

const StatMiniCard = ({ label, value, icon }) => (
  <Grid size={{ xs: 6, md: 3 }}>
    <Paper sx={{ p: 2, borderRadius: 4, display: 'flex', alignItems: 'center', gap: 2, border: '1px solid #E0E0E0', boxShadow: 'none' }}>
      <Avatar sx={{ bgcolor: '#F8F9FA' }}>{icon}</Avatar>
      <Box>
        <Typography variant="h6" sx={{ fontWeight: 800, lineHeight: 1 }}>{value}</Typography>
        <Typography variant="caption" color="text.secondary">{label}</Typography>
      </Box>
    </Paper>
  </Grid>
);

const UserCard = ({ user, style, onEdit, onDelete }) => (
  <Card sx={{ borderRadius: 4, border: '1px solid #E0E0E0', boxShadow: 'none', transition: '0.3s', '&:hover': { boxShadow: '0 8px 24px rgba(0,0,0,0.05)' } }}>
    <CardContent sx={{ p: 3 }}>
      <Stack direction="row" justifyContent="space-between" alignItems="flex-start" sx={{ mb: 2 }}>
        <Avatar sx={{ width: 56, height: 56, bgcolor: style.color + '20', color: style.color, fontWeight: 'bold', fontSize: '1.2rem' }}>
          {user.prenom[0]}{user.nom[0]}
        </Avatar>
        <Chip 
            label={style.label} 
            size="small" 
            sx={{ bgcolor: style.color + '15', color: style.color, fontWeight: 'bold', borderRadius: 1 }} 
        />
      </Stack>
      
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 0.5 }}>{user.prenom} {user.nom}</Typography>
      
      <Stack spacing={1} sx={{ mb: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <EmailIcon sx={{ fontSize: 16, color: 'text.disabled' }} />
          <Typography variant="body2" color="text.secondary">{user.email}</Typography>
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <PhoneIcon sx={{ fontSize: 16, color: 'text.disabled' }} />
          <Typography variant="body2" color="text.secondary">{user.telephone || 'Non renseigné'}</Typography>
        </Box>
      </Stack>

      <Box sx={{ display: 'flex', borderTop: '1px dashed #E0E0E0', pt: 2, mt: 1, gap: 1 }}>
        <Button startIcon={<EditIcon />} onClick={onEdit} fullWidth sx={{ color: '#1A1C1E', textTransform: 'none', fontWeight: 600 }}>Éditer</Button>
        <IconButton onClick={onDelete} color="error" sx={{ bgcolor: '#fff5f5' }}><DeleteIcon fontSize="small" /></IconButton>
      </Box>
    </CardContent>
  </Card>
);

export default Users;