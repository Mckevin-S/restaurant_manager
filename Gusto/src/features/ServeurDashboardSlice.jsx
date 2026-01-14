import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apiClient from '../services/apiClient';

export const fetchCommandes = createAsyncThunk(
  'serveur/fetchCommandes',
  async (_, { rejectWithValue }) => {
    try {
      const response = await apiClient.get('/commandes');
      // Filtrer pour le serveur: PRETE (à servir) et SERVIE (en attente de paiement)
      const filtered = Array.isArray(response.data) 
        ? response.data.filter(cmd => cmd.statut === 'PRETE' || cmd.statut === 'SERVIE')
        : [];
      return filtered;
    } catch (error) {
      return rejectWithValue(error.response?.data || "Erreur serveur");
    }
  }
);

export const updateStatutCommande = createAsyncThunk(
  'serveur/updateStatut',
  async ({ id, nouveauStatut }, { rejectWithValue }) => {
    try {
      // CORRECTION URL & PARAM : Correspondance avec @PatchMapping("/{id}/statut")
      const response = await apiClient.patch(`/commandes/${id}/statut`, null, {
        params: { statut: nouveauStatut }
      });
      // On retourne l'id et le nouveau statut pour mettre à jour le store
      return { id, nouveauStatut };
    } catch (error) {
      return rejectWithValue(error.response?.data || "Erreur lors du changement de statut");
    }
  }
);


const serveurSlice = createSlice({
  name: 'serveur',
  initialState: {
    commandes: [],
    loading: false,
    error: null
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCommandes.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCommandes.fulfilled, (state, action) => {
        state.loading = false;
        state.commandes = action.payload;
      })
      .addCase(fetchCommandes.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      /* --- CORRECTION ICI --- */
      .addCase(updateStatutCommande.fulfilled, (state, action) => {
        // Dans createAsyncThunk, les données retournées sont dans action.payload
        const { id, nouveauStatut } = action.payload;
        const cmd = state.commandes.find(c => c.id === id);
        if (cmd) {
          cmd.statut = nouveauStatut; // On met à jour le champ statut
        }
      });
  }
});

export default serveurSlice.reducer;