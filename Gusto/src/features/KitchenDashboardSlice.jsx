import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apiClient from '../services/apiClient';

// Récupérer les commandes pour la cuisine
export const fetchKitchenOrders = createAsyncThunk('kitchen/fetchAll', async (_, { rejectWithValue }) => {
  try {
    const response = await apiClient.get('/commandes');
    // Filtrage pour la cuisine
    return response.data.filter(order => order.statut !== 'PAYEE' && order.statut !== 'ANNULEE');
  } catch (error) {
    return rejectWithValue(error.response?.data || "Erreur de connexion");
  }
});

// Mettre à jour le statut (ex: EN_PREPARATION -> PRETE)
export const updateKitchenStatus = createAsyncThunk(
  'kitchen/updateStatus',
  async ({ id, nouveauStatut }, { rejectWithValue }) => {
    try {
      // Le backend attend le statut en @RequestParam pour @PatchMapping("/{id}/statut")
      const response = await apiClient.patch(`/commandes/${id}/statut`, null, {
        params: { statut: nouveauStatut }
      });
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data || "Erreur lors de la mise à jour");
    }
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