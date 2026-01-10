import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const BASE_URL = 'http://localhost:3006/api/Auth';

// 1. Étape de connexion initiale
export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await axios.post(`${BASE_URL}/login`, {
        email: credentials.email,
        motDePasse: credentials.password // Correction du nom du champ ici
      });
      return response.data; // Retourne { status: "PENDING_2FA", username: "..." }
    } catch (error) {
      return rejectWithValue(error.response?.data || "Identifiants invalides");
    }
  }
);

// 2. Étape de validation du code SMS
export const verifyTwoFactor = createAsyncThunk(
  'auth/verifyTwoFactor',
  async ({ username, code }, { rejectWithValue }) => {
    try {
      const response = await axios.post(`${BASE_URL}/verify-2fa`, {
        username: username,
        code: code
      });
      
      // Ici on reçoit enfin le token !
      localStorage.setItem('userToken', response.data.token);
      return response.data; // Contient { token, username, role }
    } catch (error) {
      return rejectWithValue(error.response?.data || "Code incorrect ou expiré");
    }
  }
);

const LoginSlice = createSlice({
  name: 'auth',
  initialState: {
    user: null,
    role: null,
    token: localStorage.getItem('userToken') || null,
    step: 'LOGIN', // LOGIN ou '2FA'
    loading: false,
    error: null,
    tempUsername: null, // Pour garder le nom entre les deux étapes
  },
 reducers: {
    logout: (state) => {
        state.user = null;
        state.token = null;
        state.step = 'LOGIN';
        state.tempUsername = null;
        state.error = null;
        localStorage.removeItem('userToken');
    },
    resetAuthError: (state) => {
        state.error = null;
    }
},
  extraReducers: (builder) => {
    builder
      // Gestion Login
      .addCase(loginUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.loading = false;
        state.step = '2FA'; // On passe à l'écran du code SMS
        state.tempUsername = action.payload.username;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Gestion 2FA
      .addCase(verifyTwoFactor.pending, (state) => {
        state.loading = true;
      })
      .addCase(verifyTwoFactor.fulfilled, (state, action) => {
        state.loading = false;
        state.token = action.payload.token;
        state.user = { nom: action.payload.username, role: action.payload.role };
        state.step = 'COMPLETED';
      })
      .addCase(verifyTwoFactor.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { logout } = LoginSlice.actions;
export default LoginSlice.reducer;