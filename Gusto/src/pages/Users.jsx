import React, { useState } from 'react';
import { 
  Box, Typography, Button, Paper, Stack, IconButton, 
  Dialog, DialogTitle, DialogContent, DialogActions, 
  TextField, MenuItem, Chip, Snackbar, Alert 
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SideBar from '../widget/SideBar';

const Users = () => {
  const userRole = 'admin'; // Simulé pour la sidebar

  // --- DONNÉES INITIALES ---
  const [users, setUsers] = useState([
    { id: 1, name: 'Admin Principal', email: 'admin@resto.com', role: 'admin', status: 'Active' },
    { id: 2, name: 'Sophie Serveuse', email: 'sophie@resto.com', role: 'staff', status: 'Active' },
    { id: 3, name: 'Marc Manager', email: 'marc@resto.com', role: 'manager', status: 'Inactive' },
  ]);

  // --- ÉTATS ---
  const [openModal, setOpenModal] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [formData, setFormData] = useState({ name: '', email: '', role: 'staff' });
  const [notification, setNotification] = useState({ open: false, message: '', severity: 'success' });

  // --- ACTIONS ---
  const showNotify = (message, severity = 'success') => {
    setNotification({ open: true, message, severity });
  };

  const handleOpenAdd = () => {
    setSelectedUser(null);
    setFormData({ name: '', email: '', role: 'staff' });
    setOpenModal(true);
  };

  const handleOpenEdit = (user) => {
    setSelectedUser(user);
    setFormData({ name: user.name, email: user.email, role: user.role });
    setOpenModal(true);
  };

  const handleSave = () => {
    if (selectedUser) {
      setUsers(users.map(u => u.id === selectedUser.id ? { ...u, ...formData } : u));
      showNotify('Utilisateur mis à jour !');
    } else {
      const newUser = { id: Date.now(), ...formData, status: 'Active' };
      setUsers([...users, newUser]);
      showNotify('Nouvel utilisateur ajouté !');
    }
    setOpenModal(false);
  };

  const handleDelete = () => {
    setUsers(users.filter(u => u.id !== selectedUser.id));
    setOpenDeleteDialog(false);
    showNotify('Utilisateur supprimé', 'error');
  };

  // --- COLONNES ---
  const columns = [
    { field: 'id', headerName: 'ID', width: 80 },
    { field: 'name', headerName: 'Nom', flex: 1, renderCell: (p) => <Typography sx={{ fontWeight: 500, mt: 1.5 }}>{p.value}</Typography> },
    { field: 'email', headerName: 'Email', flex: 1, renderCell: (p) => <Typography sx={{ mt: 1.5 }}>{p.value}</Typography> },
    { field: 'role', headerName: 'Rôle', width: 120, renderCell: (p) => (
      <Typography sx={{ textTransform: 'capitalize', color: p.value === 'admin' ? '#4caf50' : '#2196f3', mt: 1.5, fontWeight: 'bold' }}>{p.value}</Typography>
    )},
    { field: 'status', headerName: 'Statut', width: 120, renderCell: (p) => (
      <Box sx={{ mt: 1 }}>
        <Chip 
          label={p.value} 
          size="small" 
          sx={{ bgcolor: p.value === 'Active' ? '#E8F5E9' : '#FFEBEE', color: p.value === 'Active' ? '#2E7D32' : '#D32F2F', fontWeight: 'bold' }} 
        />
      </Box>
    )},
    { field: 'actions', headerName: 'Actions', width: 100, sortable: false, renderCell: (p) => (
      <Stack direction="row" spacing={1} sx={{ mt: 0.5 }}>
        <IconButton size="small" onClick={() => handleOpenEdit(p.row)} color="primary"><EditIcon fontSize="small"/></IconButton>
        <IconButton size="small" onClick={() => { setSelectedUser(p.row); setOpenDeleteDialog(true); }} color="error"><DeleteIcon fontSize="small"/></IconButton>
      </Stack>
    )},
  ];

  return (
    <Box sx={{ display: 'flex', bgcolor: '#F8F9FA', minHeight: '100vh', width: "100%" }}>
      <SideBar userRole={userRole} />

      <Box component="main" sx={{ flexGrow: 1, p: 4, width: "100%", overflowX: 'hidden' }}>
        
        {/* HEADER */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4, alignItems: 'center' }}>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#1A1C1E' }}>Gestion des Utilisateurs</Typography>
            <Typography variant="body2" color="text.secondary">Gérez les membres de votre équipe et leurs permissions</Typography>
          </Box>
          <Button 
            variant="contained" 
            startIcon={<AddIcon />} 
            onClick={handleOpenAdd} 
            sx={{ bgcolor: '#4caf50', '&:hover': { bgcolor: '#388e3c' }, borderRadius: 2, textTransform: 'none', px: 3 }}
          >
            Ajouter un utilisateur
          </Button>
        </Box>

        {/* TABLEAU */}
        <Paper sx={{ width: '100%', borderRadius: 4, border: '1px solid #E0E0E0', boxShadow: 'none', overflow: 'hidden' }}>
          <DataGrid
            rows={users}
            columns={columns}
            autoHeight
            pageSize={5}
            rowsPerPageOptions={[5]}
            disableSelectionOnClick
            sx={{ 
              border: 'none', 
              '& .MuiDataGrid-columnHeaders': { bgcolor: '#F8F9FA', borderBottom: '1px solid #E0E0E0' },
              '& .MuiDataGrid-cell': { borderBottom: '1px solid #F0F0F0' }
            }}
          />
        </Paper>

        {/* MODAL AJOUT / EDIT */}
        <Dialog open={openModal} onClose={() => setOpenModal(false)} fullWidth maxWidth="xs" PaperProps={{ sx: { borderRadius: 3 } }}>
          <DialogTitle sx={{ fontWeight: 'bold' }}>{selectedUser ? "Modifier l'utilisateur" : "Nouvel utilisateur"}</DialogTitle>
          <DialogContent dividers sx={{ borderBottom: 'none' }}>
            <Stack spacing={2} sx={{ mt: 1 }}>
              <TextField label="Nom complet" fullWidth variant="outlined" value={formData.name} onChange={(e) => setFormData({...formData, name: e.target.value})} />
              <TextField label="Email" fullWidth variant="outlined" value={formData.email} onChange={(e) => setFormData({...formData, email: e.target.value})} />
              <TextField select label="Rôle" fullWidth value={formData.role} onChange={(e) => setFormData({...formData, role: e.target.value})}>
                <MenuItem value="admin">Administrateur</MenuItem>
                <MenuItem value="manager">Manager</MenuItem>
                <MenuItem value="staff">Personnel (Staff)</MenuItem>
              </TextField>
            </Stack>
          </DialogContent>
          <DialogActions sx={{ p: 3 }}>
            <Button onClick={() => setOpenModal(false)} sx={{ color: 'text.secondary' }}>Annuler</Button>
            <Button onClick={handleSave} variant="contained" sx={{ bgcolor: '#4caf50', borderRadius: 2 }}>Enregistrer</Button>
          </DialogActions>
        </Dialog>

        {/* DELETE DIALOG */}
        <Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)} PaperProps={{ sx: { borderRadius: 3 } }}>
          <DialogTitle sx={{ fontWeight: 'bold' }}>Confirmer la suppression</DialogTitle>
          <DialogContent>
            Êtes-vous sûr de vouloir supprimer <strong>{selectedUser?.name}</strong> ? Cette action est irréversible.
          </DialogContent>
          <DialogActions sx={{ p: 2 }}>
            <Button onClick={() => setOpenDeleteDialog(false)}>Annuler</Button>
            <Button onClick={handleDelete} color="error" variant="contained" sx={{ borderRadius: 2 }}>Supprimer</Button>
          </DialogActions>
        </Dialog>

        {/* NOTIFICATION */}
        <Snackbar 
          open={notification.open} 
          autoHideDuration={3000} 
          onClose={() => setNotification({ ...notification, open: false })}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        >
          <Alert severity={notification.severity} variant="filled" sx={{ borderRadius: 2 }}>{notification.message}</Alert>
        </Snackbar>

      </Box>
    </Box>
  );
};

export default Users;