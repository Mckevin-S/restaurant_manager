import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const api = 'http://localhost:3006/api/Auth/login'

// Action asynchrone pour gérer l'appel API
export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async (credentials, { rejectWithValue }) => {
    try {
      // Remplacez l'URL par celle de votre backend
      const response = await axios.post(api, {
          email: credentials.email,
          password: credentials.password
        }, 
        {
          headers: {
            'Content-Type': 'application/json',
          }
        });
      
      // Stockage du token pour rester connecté après un refresh
      localStorage.setItem('userToken', response.data.token);
      
      return response.data; // Contient généralement { user, token }
    } catch (error) {
      // Récupère le message d'erreur du backend ou un message par défaut
      return rejectWithValue(error.response?.data?.message || "Erreur de connexion");
    }
  }
);

const LoginSlice = createSlice({
  name: 'auth',
  initialState: {
    user: null,
    token: localStorage.getItem('userToken') || null,
    loading: false,
    error: null,
  },
  reducers: {
    // Ici, on ne met rien car on n'a pas besoin d'actions synchrones (comme logout)
  },
  extraReducers: (builder) => {
    builder
      // 1. Requête en cours
      .addCase(loginUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      // 2. Requête réussie
      .addCase(loginUser.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload.user;
        state.token = action.payload.token;
      })
      // 3. Requête échouée
      .addCase(loginUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export default LoginSlice.reducer;