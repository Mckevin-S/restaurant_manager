import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Button, Paper, Stack, IconButton,
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, MenuItem, Chip, CircularProgress,
  Grid2 as Grid
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SideBar from '../widget/SideBar';
import apiClient from '../services/apiClient';
import { toast } from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';

const Users = () => {
  const navigate = useNavigate();
  const userRole = localStorage.getItem('role');

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [openModal, setOpenModal] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [formData, setFormData] = useState({
    nom: '',
    prenom: '',
    email: '',
    motDePasse: '',
    role: 'ROLE_SERVEUR',
    telephone: ''
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await apiClient.get('/users');
      setUsers(response.data);
    } catch (error) {
      console.error('Erreur chargement utilisateurs:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (user = null) => {
    if (user) {
      setSelectedUser(user);
      setFormData({
        nom: user.nom || '',
        prenom: user.prenom || '',
        email: user.email || '',
        motDePasse: '', // Ne pas pré-remplir le MDP en édition
        role: user.role || 'ROLE_SERVEUR',
        telephone: user.telephone || ''
      });
    } else {
      setSelectedUser(null);
      setFormData({
        nom: '',
        prenom: '',
        email: '',
        motDePasse: '',
        role: 'ROLE_SERVEUR',
        telephone: ''
      });
    }
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    setSelectedUser(null);
  };

  const handleSaveUser = async () => {
    try {
      if (selectedUser) {
        // Update user
        const updateData = { ...formData };
        if (!updateData.motDePasse) delete updateData.motDePasse; // Ne pas envoyer si vide
        await apiClient.put(`/users/${selectedUser.id}`, updateData);
        toast.success('Utilisateur mis à jour');
      } else {
        // Create user
        await apiClient.post('/users', formData);
        toast.success('Utilisateur créé avec succès');
      }
      fetchUsers();
      handleCloseModal();
    } catch (error) {
      console.error('Erreur sauvegarde utilisateur:', error);
    }
  };

  const handleDeleteUser = async () => {
    try {
      await apiClient.delete(`/users/${selectedUser.id}`);
      toast.success('Utilisateur supprimé');
      fetchUsers();
      setOpenDeleteDialog(false);
      setSelectedUser(null);
    } catch (error) {
      console.error('Erreur suppression utilisateur:', error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('userToken');
    localStorage.removeItem('role');
    navigate('/login');
  };

  const columns = [
    { field: 'id', headerName: 'ID', width: 70 },
    {
      field: 'fullName',
      headerName: 'Nom complet',
      width: 200,
      valueGetter: (params, row) => `${row.prenom || ''} ${row.nom || ''}`,
    },
    { field: 'email', headerName: 'Email', width: 200 },
    {
      field: 'role',
      headerName: 'Rôle',
      width: 150,
      renderCell: (params) => {
        const role = params.value;
        let color = 'default';
        if (role === 'ROLE_MANAGER') color = 'primary';
        if (role === 'ROLE_CUISINIER') color = 'warning';
        if (role === 'ROLE_SERVEUR') color = 'success';

        return <Chip label={role.replace('ROLE_', '')} color={color} size="small" variant="outlined" />;
      }
    },
    { field: 'telephone', headerName: 'Téléphone', width: 150 },
    {
      field: 'actions',
      headerName: 'Actions',
      width: 120,
      sortable: false,
      renderCell: (params) => (
        <Stack direction="row" spacing={1}>
          <IconButton size="small" color="primary" onClick={() => handleOpenModal(params.row)}>
            <EditIcon fontSize="small" />
          </IconButton>
          <IconButton size="small" color="error" onClick={() => { setSelectedUser(params.row); setOpenDeleteDialog(true); }}>
            <DeleteIcon fontSize="small" />
          </IconButton>
        </Stack>
      ),
    },
  ];

  return (
    <Box sx={{ display: 'flex', bgcolor: '#F8F9FA', minHeight: '100vh', width: "100%" }}>
      <SideBar userRole={userRole} onLogout={handleLogout} />

      <Box component="main" sx={{ flexGrow: 1, p: 4, width: "100%", overflowX: 'hidden' }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4, alignItems: 'center' }}>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#1A1C1E' }}>Utilisateurs</Typography>
            <Typography variant="body2" color="text.secondary">Gérez les accès et les rôles du personnel</Typography>
          </Box>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => handleOpenModal()}
            sx={{ borderRadius: 2, textTransform: 'none', bgcolor: '#1A1C1E' }}
          >
            Nouvel Utilisateur
          </Button>
        </Box>

        <Paper sx={{ width: '100%', borderRadius: 4, overflow: 'hidden', boxShadow: 'none', border: '1px solid #E0E0E0' }}>
          <DataGrid
            rows={users}
            columns={columns}
            loading={loading}
            initialState={{
              pagination: { paginationModel: { pageSize: 10 } },
            }}
            pageSizeOptions={[10, 25, 50]}
            disableRowSelectionOnClick
            autoHeight
            sx={{
              border: 'none',
              '& .MuiDataGrid-cell:focus': { outline: 'none' },
            }}
          />
        </Paper>

        {/* Modal Ajout/Modif */}
        <Dialog open={openModal} onClose={handleCloseModal} fullWidth maxWidth="sm">
          <DialogTitle sx={{ fontWeight: 'bold' }}>
            {selectedUser ? 'Modifier Utilisateur' : 'Nouvel Utilisateur'}
          </DialogTitle>
          <DialogContent>
            <Stack spacing={3} sx={{ mt: 1 }}>
              <Grid container spacing={2}>
                <Grid size={{ xs: 6 }}>
                  <TextField
                    label="Prénom"
                    fullWidth
                    value={formData.prenom}
                    onChange={(e) => setFormData({ ...formData, prenom: e.target.value })}
                  />
                </Grid>
                <Grid size={{ xs: 6 }}>
                  <TextField
                    label="Nom"
                    fullWidth
                    value={formData.nom}
                    onChange={(e) => setFormData({ ...formData, nom: e.target.value })}
                  />
                </Grid>
              </Grid>
              <TextField
                label="Email"
                fullWidth
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />
              <TextField
                label="Mot de passe"
                fullWidth
                type="password"
                placeholder={selectedUser ? "Laisser vide pour ne pas modifier" : ""}
                value={formData.motDePasse}
                onChange={(e) => setFormData({ ...formData, motDePasse: e.target.value })}
              />
              <TextField
                select
                label="Rôle"
                fullWidth
                value={formData.role}
                onChange={(e) => setFormData({ ...formData, role: e.target.value })}
              >
                <MenuItem value="ROLE_SERVEUR">Serveur</MenuItem>
                <MenuItem value="ROLE_CUISINIER">Cuisinier</MenuItem>
                <MenuItem value="ROLE_MANAGER">Manager</MenuItem>
              </TextField>
              <TextField
                label="Téléphone"
                fullWidth
                value={formData.telephone}
                onChange={(e) => setFormData({ ...formData, telephone: e.target.value })}
              />
            </Stack>
          </DialogContent>
          <DialogActions sx={{ p: 3 }}>
            <Button onClick={handleCloseModal} color="inherit">Annuler</Button>
            <Button onClick={handleSaveUser} variant="contained" sx={{ bgcolor: '#1A1C1E', borderRadius: 2 }}>
              Sauvegarder
            </Button>
          </DialogActions>
        </Dialog>

        {/* Dialog Suppression */}
        <Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)}>
          <DialogTitle>Confirmer la suppression</DialogTitle>
          <DialogContent>
            Voulez-vous vraiment supprimer l'utilisateur {selectedUser?.prenom} {selectedUser?.nom} ? Cette action est irréversible.
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => setOpenDeleteDialog(false)} color="inherit">Annuler</Button>
            <Button onClick={handleDeleteUser} color="error" variant="contained">Supprimer</Button>
          </DialogActions>
        </Dialog>

      </Box>
    </Box>
  );
};

export default Users;