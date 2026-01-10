import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const API_URL = 'http://localhost:3006/api/commandes';

// Configuration du header avec le token JWT
const getAuthHeader = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
});

// Récupérer les commandes pour la cuisine
export const fetchKitchenOrders = createAsyncThunk('kitchen/fetchAll', async (_, { rejectWithValue }) => {
  try {
    const token = localStorage.getItem('token');
    
    // Si pas de token, on ne tente même pas la requête
    if (!token) return rejectWithValue("Pas de token trouvé");

    const response = await axios.get(API_URL, {
      headers: { 
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    // Filtrage pour la cuisine
    return response.data.filter(order => order.statut !== 'PAYEE' && order.statut !== 'ANNULEE');
  } catch (error) {
    return rejectWithValue(error.response?.data || "Erreur de connexion");
  }
});

// Mettre à jour le statut (ex: EN_PREPARATION -> PRETE)
export const updateKitchenStatus = createAsyncThunk(
  'kitchen/updateStatus',
  async ({ id, nouveauStatut }) => {
    const response = await axios.patch(
      `${API_URL}/${id}/statut?statut=${nouveauStatut}`,
      {},
      getAuthHeader()
    );
    return response.data;
  }
);

const kitchenSlice = createSlice({
  name: 'kitchen',
  initialState: { orders: [], loading: false, error: null },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchKitchenOrders.pending, (state) => {
        if (state.orders.length === 0) state.loading = true;
      })
      .addCase(fetchKitchenOrders.fulfilled, (state, action) => {
        state.loading = false;
        state.orders = action.payload;
      })
      .addCase(fetchKitchenOrders.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(updateKitchenStatus.fulfilled, (state, action) => {
        const index = state.orders.findIndex(o => o.id === action.payload.id);
        if (index !== -1) {
          state.orders[index] = action.payload;
        }
      });
  },
});

export default kitchenSlice.reducer;