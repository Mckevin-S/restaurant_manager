import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const API_URL = "http://localhost:3006/api/commandes";

export const fetchCommandes = createAsyncThunk(
  'serveur/fetchCommandes',
  async (_, { getState, rejectWithValue }) => {
    try {
      const token = getState().auth.token;
      const response = await axios.get(API_URL, {
        headers: { Authorization: `Bearer ${token}` }
      });
      // On s'assure de retourner un tableau
      return Array.isArray(response.data) ? response.data : [];
    } catch (error) {
      return rejectWithValue(error.response?.data || "Erreur serveur");
    }
  }
);

export const updateStatutCommande = createAsyncThunk(
  'serveur/updateStatut',
  async ({ id, nouveauStatut }, { getState, rejectWithValue }) => {
    try {
      const token = getState().auth.token;
      // CORRECTION URL & PARAM : Correspondance avec @PatchMapping("/{id}/statut")
      const response = await axios.patch(
        `${API_URL}/${id}/statut`,
        { statut: nouveauStatut },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      // On retourne l'id et le nouveau statut pour mettre à jour le store
      return { id, nouveauStatut };
    } catch (error) {
      return rejectWithValue("Erreur lors du changement de statut");
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